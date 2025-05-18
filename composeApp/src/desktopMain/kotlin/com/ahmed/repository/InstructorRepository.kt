package com.ahmed.repository

import com.ahmed.model.DatabaseManager
import com.ahmed.model.Instructor
import com.ahmed.model.InstructorDTO
import com.ahmed.model.Instructors
import com.ahmed.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class InstructorRepository {
    private fun checkDatabaseConnection(): String? {
        return if (!DatabaseManager.isDatabaseReady()) {
            "Database is not connected or initialized"
        } else null
    }

    // Create
    suspend fun createInstructor(instructorDto: InstructorDTO): Pair<Instructor?, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(null, it)
        }

        try {
            val instructor = transaction {
                val newInstructor = Instructor.new {
                    this.firstName = instructorDto.firstName
                    this.lastName = instructorDto.lastName
                    this.email = instructorDto.email
                    this.department = instructorDto.department
                    this.hireDate = instructorDto.hireDate
                }
                
                // Validate the record before committing
                val validationErrors = newInstructor.validate()
                if (validationErrors.isNotEmpty()) {
                    throw IllegalArgumentException(validationErrors.joinToString(", "))
                }
                
                newInstructor
            }
            Logger.info("Instructor created successfully: ${instructor.firstName} ${instructor.lastName}")
            Pair(instructor, null)
        } catch (e: Exception) {
            val errorMessage = "Error creating instructor: ${e.message}"
            Logger.error(errorMessage)
            Pair(null, errorMessage)
        }
    }

    // Read
    suspend fun getInstructorById(id: Int): Pair<Instructor?, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(null, it)
        }

        try {
            val instructor = transaction {
                Instructor.findById(id)
            }
            if (instructor != null) {
                Logger.info("Instructor retrieved successfully: ID $id")
                Pair(instructor, null)
            } else {
                val errorMessage = "Instructor not found with ID: $id"
                Logger.warning(errorMessage)
                Pair(null, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error retrieving instructor: ${e.message}"
            Logger.error(errorMessage)
            Pair(null, errorMessage)
        }
    }

    suspend fun getAllInstructors(): Pair<List<Instructor>, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(emptyList(), it)
        }

        try {
            val instructors = transaction {
                Instructor.all().toList()
            }
            Logger.info("Retrieved ${instructors.size} instructors")
            Pair(instructors, null)
        } catch (e: Exception) {
            val errorMessage = "Error retrieving all instructors: ${e.message}"
            Logger.error(errorMessage)
            Pair(emptyList(), errorMessage)
        }
    }

    // Update
    suspend fun updateInstructor(
        instructorDto: InstructorDTO
    ): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        if (instructorDto.id == null) {
            return@withContext Pair(false, "Instructor ID is null")
        }

        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val instructor = Instructor.findById(instructorDto.id) ?: return@transaction false
                instructor.firstName = instructorDto.firstName
                instructor.lastName = instructorDto.lastName
                instructor.email = instructorDto.email
                instructor.department = instructorDto.department
                instructor.hireDate = instructorDto.hireDate
                
                // Validate before updating
                val validationErrors = instructor.validate()
                if (validationErrors.isNotEmpty()) {
                    throw IllegalArgumentException(validationErrors.joinToString(", "))
                }
                
                true
            }
            if (success) {
                Logger.info("Instructor updated successfully: ID ${instructorDto.id}")
                Pair(true, null)
            } else {
                val errorMessage = "Instructor not found with ID: ${instructorDto.id}"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error updating instructor: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }

    // Delete
    suspend fun deleteInstructor(id: Int): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val instructor = Instructor.findById(id) ?: return@transaction false
                instructor.delete()
                true
            }
            if (success) {
                Logger.info("Instructor deleted successfully: ID $id")
                Pair(true, null)
            } else {
                val errorMessage = "Instructor not found with ID: $id"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error deleting instructor: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }

    // Additional utility methods
    suspend fun searchInstructors(query: String): Pair<List<Instructor>, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(emptyList(), it)
        }

        try {
            val instructors = transaction {
                Instructor.find {
                    (Instructors.firstName.lowerCase() like "%${query.lowercase()}%") or
                            (Instructors.lastName.lowerCase() like "%${query.lowercase()}%") or
                            (Instructors.email.lowerCase() like "%${query.lowercase()}%") or
                            (Instructors.department.lowerCase() like "%${query.lowercase()}%")
                }.toList()
            }
            Logger.info("Search completed. Found ${instructors.size} instructors matching query: $query")
            Pair(instructors, null)
        } catch (e: Exception) {
            val errorMessage = "Error searching instructors: ${e.message}"
            Logger.error(errorMessage)
            Pair(emptyList(), errorMessage)
        }
    }

    suspend fun findInstructorsByDepartment(department: String): Pair<List<Instructor>, String?> =
        withContext(Dispatchers.IO) {
            checkDatabaseConnection()?.let {
                return@withContext Pair(emptyList(), it)
            }

            try {
                val instructors = transaction {
                    Instructor.find { Instructors.department eq department }.toList()
                }
                Logger.info("Retrieved ${instructors.size} instructors in department: $department")
                Pair(instructors, null)
            } catch (e: Exception) {
                val errorMessage = "Error finding instructors by department: ${e.message}"
                Logger.error(errorMessage)
                Pair(emptyList(), errorMessage)
            }
        }
}
