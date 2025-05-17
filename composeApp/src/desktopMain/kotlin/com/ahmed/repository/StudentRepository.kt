package com.ahmed.repository

import com.ahmed.model.DatabaseManager
import com.ahmed.model.Student
import com.ahmed.model.StudentDTO
import com.ahmed.model.StudentStatus
import com.ahmed.model.Students
import com.ahmed.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class StudentRepository {
    private fun checkDatabaseConnection(): String? {
        return if (!DatabaseManager.isDatabaseReady()) {
            "Database is not connected or initialized"
        } else null
    }

    val timeZone = TimeZone.currentSystemDefault()

    // Create
    suspend fun createStudent(studentDto: StudentDTO): Pair<Student?, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(null, it)
        }

        try {
            val student = transaction {
                Student.new {
                    this.firstName = studentDto.firstName
                    this.lastName = studentDto.lastName
                    this.email = studentDto.email
                    this.dateOfBirth = studentDto.dateOfBirth
                    this.enrollmentDate = studentDto.enrollmentDate
                    this.status = studentDto.status.value
                }
            }
            Logger.info("Student created successfully: ${student.firstName} ${student.lastName}")
            Pair(student, null)
        } catch (e: Exception) {
            val errorMessage = "Error creating student: ${e.message}"
            Logger.error(errorMessage)
            Pair(null, errorMessage)
        }
    }

    // Read
    suspend fun getStudentById(id: Int): Pair<Student?, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(null, it)
        }

        try {
            val student = transaction {
                Student.findById(id)
            }
            if (student != null) {
                Logger.info("Student retrieved successfully: ID $id")
                Pair(student, null)
            } else {
                val errorMessage = "Student not found with ID: $id"
                Logger.warning(errorMessage)
                Pair(null, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error retrieving student: ${e.message}"
            Logger.error(errorMessage)
            Pair(null, errorMessage)
        }
    }

    suspend fun getAllStudents(): Pair<List<Student>, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(emptyList(), it)
        }

        try {
            val students = transaction {
                Student.all().toList()
            }
            Logger.info("Retrieved ${students.size} students")
            Pair(students, null)
        } catch (e: Exception) {
            val errorMessage = "Error retrieving all students: ${e.message}"
            Logger.error(errorMessage)
            Pair(emptyList(), errorMessage)
        }
    }

    suspend fun findStudentsByStatus(status: StudentStatus): Pair<List<Student>, String?> =
        withContext(Dispatchers.IO) {
            checkDatabaseConnection()?.let {
                return@withContext Pair(emptyList(), it)
            }

            try {
                val students = transaction {
                    Student.find { Students.status eq status.value }.toList()
                }
                Logger.info("Retrieved ${students.size} students with status: $status")
                Pair(students, null)
            } catch (e: Exception) {
                val errorMessage = "Error finding students by status: ${e.message}"
                Logger.error(errorMessage)
                Pair(emptyList(), errorMessage)
            }
        }

    // Update
    suspend fun updateStudent(
        studentDto: StudentDTO
    ): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        if (studentDto.id == null) {
            return@withContext Pair(false, "Student ID is null")
        }

        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val student = Student.findById(studentDto.id) ?: return@transaction false
                student.firstName = studentDto.firstName
                student.lastName = studentDto.lastName
                student.email = studentDto.email
                student.status = studentDto.status.value
                true
            }
            if (success) {
                Logger.info("Student updated successfully: ID ${studentDto.id}")
                Pair(true, null)
            } else {
                val errorMessage = "Student not found with ID: ${studentDto.id}"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error updating student: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }

    // Delete
    suspend fun deleteStudent(id: Int): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val student = Student.findById(id) ?: return@transaction false
                student.delete()
                true
            }
            if (success) {
                Logger.info("Student deleted successfully: ID $id")
                Pair(true, null)
            } else {
                val errorMessage = "Student not found with ID: $id"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error deleting student: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }

    // Additional utility methods
    suspend fun searchStudents(query: String): Pair<List<Student>, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(emptyList(), it)
        }

        try {
            val students = transaction {
                Student.find {
                    (Students.firstName.lowerCase() like "%${query.lowercase()}%") or
                            (Students.lastName.lowerCase() like "%${query.lowercase()}%") or
                            (Students.email.lowerCase() like "%${query.lowercase()}%")
                }.toList()
            }
            Logger.info("Search completed. Found ${students.size} students matching query: $query")
            Pair(students, null)
        } catch (e: Exception) {
            val errorMessage = "Error searching students: ${e.message}"
            Logger.error(errorMessage)
            Pair(emptyList(), errorMessage)
        }
    }
}