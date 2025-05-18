package com.ahmed.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.ahmed.model.DatabaseEvent
import com.ahmed.model.InstructorDTO
import com.ahmed.model.toDTO
import com.ahmed.repository.InstructorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.ahmed.model.DatabaseManager

class InstructorViewModel(
    private val repository: InstructorRepository = InstructorRepository()
) : ViewModel() {
    private val _instructors = MutableStateFlow<List<InstructorDTO>>(emptyList())
    val instructors: StateFlow<List<InstructorDTO>> = _instructors.asStateFlow()

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
                    loadInstructors()
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
                            _instructors.value = emptyList()
                        } else {
                            _error.value = null // Clear any previous connection errors
                            loadInstructors()
                        }
                    }

                    is DatabaseEvent.InitializationCompleted -> {
                        if (event.success) {
                            _error.value = null
                            loadInstructors()
                        } else {
                            _error.value = event.message
                            _instructors.value = emptyList()
                        }
                    }

                    is DatabaseEvent.ResetCompleted -> {
                        if (event.success) {
                            _error.value = null
                            _instructors.value = emptyList() // Clear list before reloading
                            loadInstructors()
                        } else {
                            _error.value = event.message
                        }
                    }

                    is DatabaseEvent.DemoDataLoaded -> {
                        if (event.success) {
                            _error.value = null
                            loadInstructors()
                        } else {
                            _error.value = event.message
                        }
                    }

                    null -> {
                        // Initial state, check current connection status
                        if (DatabaseManager.isDatabaseReady()) {
                            loadInstructors()
                        }
                    }
                }
            }
        }
    }

    fun loadInstructors() {
        if (!DatabaseManager.isDatabaseReady()) {
            _error.value = "Database is not connected"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val (instructors, error) = repository.getAllInstructors()
                _instructors.value = instructors.map { it.toDTO() }
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Failed to load instructors: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createInstructor(instructorDto: InstructorDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (instructor, error) = repository.createInstructor(instructorDto)
                if (instructor != null) {
                    loadInstructors() // Reload the list after creation
                } else {
                    _error.value = error ?: "Failed to create instructor"
                }
            } catch (e: Exception) {
                _error.value = "Error creating instructor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateInstructor(instructorDto: InstructorDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.updateInstructor(instructorDto)
                if (success) {
                    loadInstructors() // Reload the list after update
                } else {
                    _error.value = error ?: "Failed to update instructor"
                }
            } catch (e: Exception) {
                _error.value = "Error updating instructor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteInstructor(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.deleteInstructor(id)
                if (success) {
                    loadInstructors() // Reload the list after deletion
                } else {
                    _error.value = error ?: "Failed to delete instructor"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting instructor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchInstructors(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (instructors, error) = repository.searchInstructors(query)
                _instructors.value = instructors.map { it.toDTO() }
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Error searching instructors: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
