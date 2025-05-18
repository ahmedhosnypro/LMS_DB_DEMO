package com.ahmed.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.ahmed.model.CourseDTO
import com.ahmed.model.DatabaseEvent
import com.ahmed.model.EnrollmentDTO
import com.ahmed.model.toDTO
import com.ahmed.repository.EnrollmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.ahmed.model.DatabaseManager
import com.ahmed.model.StudentDTO

class EnrollmentViewModel(
    val courseDto: CourseDTO?,
    private val repository: EnrollmentRepository = EnrollmentRepository()
) : ViewModel() {
    private val _students = MutableStateFlow<List<StudentDTO>>(emptyList())
    val students: StateFlow<List<StudentDTO>> = _students.asStateFlow()

    private val _unenrolledStudents = MutableStateFlow<List<StudentDTO>>(emptyList())
    val unenrolledStudents: StateFlow<List<StudentDTO>> = _unenrolledStudents.asStateFlow()
    private val _enrollments = MutableStateFlow<List<EnrollmentDTO>>(emptyList())
    val enrollments: StateFlow<List<EnrollmentDTO>> = _enrollments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var isFirstRun = true

    init {
        // Only load enrollments if we have a course
        if (courseDto != null) {
            viewModelScope.launch {
                DatabaseManager.connectionStatus.collect { isConnected ->
                    if (isFirstRun && isConnected) {
                        refresh()
                        isFirstRun = false
                    }
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
                            _enrollments.value = emptyList()
                        } else {
                            _error.value = null // Clear any previous connection errors
                            refresh()
                        }
                    }

                    is DatabaseEvent.InitializationCompleted -> {
                        if (event.success) {
                            _error.value = null
                            refresh()
                        } else {
                            _error.value = event.message
                            _enrollments.value = emptyList()
                        }
                    }

                    is DatabaseEvent.ResetCompleted -> {
                        if (event.success) {
                            _error.value = null
                            _enrollments.value = emptyList() // Clear list before reloading
                            refresh()
                        } else {
                            _error.value = event.message
                        }
                    }

                    is DatabaseEvent.DemoDataLoaded -> {
                        if (event.success) {
                            _error.value = null
                            refresh()
                        } else {
                            _error.value = event.message
                        }
                    }

                    null -> {
                        // Initial state, check current connection status
                        if (DatabaseManager.isDatabaseReady()) {
                            refresh()
                        }
                    }
                }
            }
        }
    }

    fun loadEnrollments() {
        if (!DatabaseManager.isDatabaseReady()) {
            _error.value = "Database is not connected"
            return
        }


        viewModelScope.launch(Dispatchers.IO) {
            if (courseDto?.id == null) {
                _error.value = "No course selected"
                return@launch
            }
            _isLoading.value = true
            try {
                val (enrollments, error) = repository.getEnrollmentsByCourseId(courseDto.id)
                _enrollments.value = enrollments
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Failed to load enrollments: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUnenrolledStudents() {
        if (!DatabaseManager.isDatabaseReady()) {
            _error.value = "Database is not connected"
            return
        }
        
        if (courseDto == null) {
            _error.value = "No course selected"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val courseId = courseDto.id
            if(courseId == null) {
                _error.value = "Course ID is null"
                return@launch
            }
            try {
                val (students, error) = repository.getUnenrolledStudentsForCourse(courseDto.id)
                _unenrolledStudents.value = students.map { it.toDTO() }
                _error.value = error
            } catch (e: Exception) {
                _error.value = "Failed to load unenrolled students: ${e.message}"
            }
        }
    }

    private fun refresh(){
        loadEnrollments()
        loadUnenrolledStudents()
    }

    fun createEnrollment(enrollmentDto: EnrollmentDTO) {
        if (courseDto == null) {
            _error.value = "No course selected"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (enrollment, error) = repository.createEnrollment(enrollmentDto)
                if (enrollment != null) {
                    refresh() // Reload the list after creation
                } else {
                    _error.value = error ?: "Failed to create enrollment"
                }
            } catch (e: Exception) {
                _error.value = "Error creating enrollment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateEnrollment(enrollmentDto: EnrollmentDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.updateEnrollment(enrollmentDto)
                if (success) {
                    refresh() // Reload the list after update
                } else {
                    _error.value = error ?: "Failed to update enrollment"
                }
            } catch (e: Exception) {
                _error.value = "Error updating enrollment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEnrollment(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (success, error) = repository.deleteEnrollment(id)
                if (success) {
                    refresh() // Reload the list after deletion
                } else {
                    _error.value = error ?: "Failed to delete enrollment"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting enrollment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun searchByStudentName(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (enrollments, error) = repository.getEnrollmentsByCourseId(courseDto?.id ?: return@launch)
                if (error != null) {
                    _error.value = error
                    return@launch
                }
                
                _enrollments.value = enrollments
                    .filter { enrollment ->
                        enrollment.student.let { student ->
                            val fullName = "${student.firstName} ${student.lastName}".lowercase()
                            fullName.contains(name.lowercase())
                        }
                    }
            } catch (e: Exception) {
                _error.value = "Error searching enrollments: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
