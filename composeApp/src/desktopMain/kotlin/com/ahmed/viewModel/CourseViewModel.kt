package com.ahmed.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.ahmed.model.DatabaseEvent
import com.ahmed.model.CourseDTO
import com.ahmed.model.toDTO
import com.ahmed.repository.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.ahmed.model.DatabaseManager

class CourseViewModel(
    private val repository: CourseRepository = CourseRepository()
) : ViewModel() {
    private val _courses = MutableStateFlow<List<CourseDTO>>(emptyList())
    val courses: StateFlow<List<CourseDTO>> = _courses.asStateFlow()

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
                    loadCourses()
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
                            _courses.value = emptyList()
                        } else {
                            _error.value = null
                            loadCourses()
                        }
                    }

                    is DatabaseEvent.InitializationCompleted -> {
                        if (event.success) {
                            _error.value = null
                            loadCourses()
                        } else {
                            _error.value = event.message
                            _courses.value = emptyList()
                        }
                    }

                    is DatabaseEvent.ResetCompleted -> {
                        if (event.success) {
                            _error.value = null
                            _courses.value = emptyList()
                            loadCourses()
                        } else {
                            _error.value = event.message
                        }
                    }

                    is DatabaseEvent.DemoDataLoaded -> {
                        if (event.success) {
                            _error.value = null
                            loadCourses()
                        } else {
                            _error.value = event.message
                        }
                    }

                    null -> {
                        if (DatabaseManager.isDatabaseReady()) {
                            loadCourses()
                        }
                    }
                }
            }
        }
    }

    fun loadCourses() {
        if (!DatabaseManager.isDatabaseReady()) {
            _error.value = "Database is not connected"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val (courses, error) = repository.getAllCourses()
                _courses.value = courses.map { it.toDTO() }
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Failed to load courses: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createCourse(courseDto: CourseDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (course, error) = repository.createCourse(courseDto)
                if (course != null) {
                    loadCourses()
                } else {
                    _error.value = error ?: "Failed to create course"
                }
            } catch (e: Exception) {
                _error.value = "Error creating course: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCourse(courseDto: CourseDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.updateCourse(courseDto)
                if (success) {
                    loadCourses()
                } else {
                    _error.value = error ?: "Failed to update course"
                }
            } catch (e: Exception) {
                _error.value = "Error updating course: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCourse(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.deleteCourse(id)
                if (success) {
                    loadCourses()
                } else {
                    _error.value = error ?: "Failed to delete course"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting course: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchCourses(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (courses, error) = repository.searchCourses(query)
                _courses.value = courses.map { it.toDTO() }
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Error searching courses: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
