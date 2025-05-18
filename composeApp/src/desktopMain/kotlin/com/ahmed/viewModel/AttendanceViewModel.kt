package com.ahmed.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.ahmed.model.DatabaseEvent
import com.ahmed.model.AttendanceDTO
import com.ahmed.model.toDTO
import com.ahmed.repository.AttendanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.ahmed.model.DatabaseManager

class AttendanceViewModel(
    private val repository: AttendanceRepository = AttendanceRepository()
) : ViewModel() {
    private val _attendanceRecords = MutableStateFlow<List<AttendanceDTO>>(emptyList())
    val attendanceRecords: StateFlow<List<AttendanceDTO>> = _attendanceRecords.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var isFirstRun = true

    init {
        // First run check based on connection status
        viewModelScope.launch {
            DatabaseManager.connectionStatus.collect { isConnected ->
                if (isFirstRun && isConnected) {
                    loadAttendance()
                    isFirstRun = false
                }
            }
        }

        viewModelScope.launch {
            // Monitor database events
            DatabaseManager.databaseEvent.collect { event ->
                when (event) {
                    is DatabaseEvent.ConnectionChanged -> {
                        if (!event.success) {
                            _error.value = event.message ?: "Database connection lost"
                            _attendanceRecords.value = emptyList()
                        } else {
                            _error.value = null
                            loadAttendance()
                        }
                    }

                    is DatabaseEvent.InitializationCompleted -> {
                        if (event.success) {
                            _error.value = null
                            loadAttendance()
                        } else {
                            _error.value = event.message
                            _attendanceRecords.value = emptyList()
                        }
                    }

                    is DatabaseEvent.ResetCompleted -> {
                        if (event.success) {
                            _error.value = null
                            _attendanceRecords.value = emptyList()
                            loadAttendance()
                        } else {
                            _error.value = event.message
                        }
                    }

                    is DatabaseEvent.DemoDataLoaded -> {
                        if (event.success) {
                            _error.value = null
                            loadAttendance()
                        } else {
                            _error.value = event.message
                        }
                    }

                    null -> {
                        if (DatabaseManager.isDatabaseReady()) {
                            loadAttendance()
                        }
                    }
                }
            }
        }
    }

    fun loadAttendance() {
        if (!DatabaseManager.isDatabaseReady()) {
            _error.value = "Database is not connected"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val (records, error) = repository.getAllAttendance()
                _attendanceRecords.value = records
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Failed to load attendance records: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createAttendance(attendanceDto: AttendanceDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (record, error) = repository.createAttendance(attendanceDto)
                if (record != null) {
                    loadAttendance()
                } else {
                    _error.value = error ?: "Failed to create attendance record"
                }
            } catch (e: Exception) {
                _error.value = "Error creating attendance record: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAttendance(attendanceDto: AttendanceDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.updateAttendance(attendanceDto)
                if (success) {
                    loadAttendance()
                } else {
                    _error.value = error ?: "Failed to update attendance record"
                }
            } catch (e: Exception) {
                _error.value = "Error updating attendance record: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAttendance(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.deleteAttendance(id)
                if (success) {
                    loadAttendance()
                } else {
                    _error.value = error ?: "Failed to delete attendance record"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting attendance record: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAttendanceByEnrollment(enrollmentId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (records, error) = repository.getAttendanceByEnrollmentId(enrollmentId)
                _attendanceRecords.value = records
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Error getting attendance records for enrollment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchAttendance(query: String) {
        if (!DatabaseManager.isDatabaseReady()) {
            _error.value = "Database is not connected"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val (records, error) = repository.getAllAttendance()
                if (records.isNotEmpty() && query.isNotEmpty()) {
                    val filteredRecords = records.filter { record ->
                        record.enrollmentId.toString().contains(query) ||
                        record.status.value.contains(query, ignoreCase = true) ||
                        record.date.toString().contains(query)
                    }
                    _attendanceRecords.value = filteredRecords
                } else {
                    _attendanceRecords.value = records
                }
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Failed to search attendance records: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
