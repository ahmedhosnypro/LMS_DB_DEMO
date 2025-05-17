package com.ahmed.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.ahmed.model.DatabaseEvent
import com.ahmed.model.StudentDTO
import com.ahmed.model.toDTO
import com.ahmed.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.ahmed.model.DatabaseManager

class StudentViewModel(
    private val repository: StudentRepository = StudentRepository()
) : ViewModel() {
    private val _students = MutableStateFlow<List<StudentDTO>>(emptyList())
    val students: StateFlow<List<StudentDTO>> = _students.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            // Monitor database events
            DatabaseManager.databaseEvent.collect { event ->
                when (event) {
                    is DatabaseEvent.ConnectionChanged -> {
                        if (!event.success) {
                            _error.value = event.message ?: "Database connection lost"
                            _students.value = emptyList()
                        } else {
                            _error.value = null // Clear any previous connection errors
                            loadStudents()
                        }
                    }

                    is DatabaseEvent.InitializationCompleted -> {
                        if (event.success) {
                            _error.value = null
                            loadStudents()
                        } else {
                            _error.value = event.message
                            _students.value = emptyList()
                        }
                    }

                    is DatabaseEvent.ResetCompleted -> {
                        if (event.success) {
                            _error.value = null
                            _students.value = emptyList() // Clear list before reloading
                            loadStudents()
                        } else {
                            _error.value = event.message
                        }
                    }

                    is DatabaseEvent.DemoDataLoaded -> {
                        if (event.success) {
                            _error.value = null
                            loadStudents()
                        } else {
                            _error.value = event.message
                        }
                    }

                    null -> {
                        // Initial state, check current connection status
                        if (DatabaseManager.isDatabaseReady()) {
                            loadStudents()
                        }
                    }
                }
            }
        }
    }

    fun loadStudents() {
        if (!DatabaseManager.isDatabaseReady()) {
            _error.value = "Database is not connected"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val (students, error) = repository.getAllStudents()
                _students.value = students.map { it.toDTO() }
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Failed to load students: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createStudent(studentDto: StudentDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (student, error) = repository.createStudent(studentDto)
                if (student != null) {
                    loadStudents() // Reload the list after creation
                } else {
                    _error.value = error ?: "Failed to create student"
                }
            } catch (e: Exception) {
                _error.value = "Error creating student: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStudent(studentDto: StudentDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.updateStudent(studentDto)
                if (success) {
                    loadStudents() // Reload the list after update
                } else {
                    _error.value = error ?: "Failed to update student"
                }
            } catch (e: Exception) {
                _error.value = "Error updating student: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteStudent(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.deleteStudent(id)
                if (success) {
                    loadStudents() // Reload the list after deletion
                } else {
                    _error.value = error ?: "Failed to delete student"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting student: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchStudents(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (students, error) = repository.searchStudents(query)
                _students.value = students.map { it.toDTO() }
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Error searching students: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}