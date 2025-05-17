package com.ahmed.repository

import com.ahmed.model.DatabaseManager
import com.ahmed.model.Course
import com.ahmed.model.CourseDTO
import com.ahmed.model.CourseStatus
import com.ahmed.model.Courses
import com.ahmed.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CourseRepository {
    private fun checkDatabaseConnection(): String? {
        return if (!DatabaseManager.isDatabaseReady()) {
            "Database is not connected or initialized"
        } else null
    }

    // Create
    suspend fun createCourse(courseDto: CourseDTO): Pair<Course?, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(null, it)
        }

        try {
            val course = transaction {
                Course.new {
                    this.courseCode = courseDto.courseCode
                    this.title = courseDto.title
                    this.description = courseDto.description
                    this.credits = courseDto.credits
                    this.instructorId = courseDto.instructorId?.let { id -> org.jetbrains.exposed.v1.core.dao.id.EntityID(id, com.ahmed.model.Instructors) }
                    this.maxStudents = courseDto.maxStudents
                    this.status = courseDto.status
                }
            }
            Logger.info("Course created successfully: ${course.courseCode} - ${course.title}")
            Pair(course, null)
        } catch (e: Exception) {
            val errorMessage = "Error creating course: ${e.message}"
            Logger.error(errorMessage)
            Pair(null, errorMessage)
        }
    }

    // Read
    suspend fun getCourseById(id: Int): Pair<Course?, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(null, it)
        }

        try {
            val course = transaction {
                Course.findById(id)
            }
            if (course != null) {
                Logger.info("Course retrieved successfully: ID $id")
                Pair(course, null)
            } else {
                val errorMessage = "Course not found with ID: $id"
                Logger.warning(errorMessage)
                Pair(null, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error retrieving course: ${e.message}"
            Logger.error(errorMessage)
            Pair(null, errorMessage)
        }
    }

    suspend fun getAllCourses(): Pair<List<Course>, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(emptyList(), it)
        }

        try {
            val courses = transaction {
                Course.all().toList()
            }
            Logger.info("Retrieved ${courses.size} courses")
            Pair(courses, null)
        } catch (e: Exception) {
            val errorMessage = "Error retrieving all courses: ${e.message}"
            Logger.error(errorMessage)
            Pair(emptyList(), errorMessage)
        }
    }

    suspend fun findCoursesByStatus(status: CourseStatus): Pair<List<Course>, String?> =
        withContext(Dispatchers.IO) {
            checkDatabaseConnection()?.let {
                return@withContext Pair(emptyList(), it)
            }

            try {
                val courses = transaction {
                    Course.find { Courses.status eq status.value }.toList()
                }
                Logger.info("Retrieved ${courses.size} courses with status: $status")
                Pair(courses, null)
            } catch (e: Exception) {
                val errorMessage = "Error finding courses by status: ${e.message}"
                Logger.error(errorMessage)
                Pair(emptyList(), errorMessage)
            }
        }

    // Update
    suspend fun updateCourse(courseDto: CourseDTO): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val course = Course.findById(courseDto.id) ?: return@transaction false
                course.courseCode = courseDto.courseCode
                course.title = courseDto.title
                course.description = courseDto.description
                course.credits = courseDto.credits
                course.instructorId = courseDto.instructorId?.let { id -> org.jetbrains.exposed.v1.core.dao.id.EntityID(id, com.ahmed.model.Instructors) }
                course.maxStudents = courseDto.maxStudents
                course.status = courseDto.status
                true
            }
            if (success) {
                Logger.info("Course updated successfully: ID ${courseDto.id}")
                Pair(true, null)
            } else {
                val errorMessage = "Course not found with ID: ${courseDto.id}"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error updating course: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }

    // Delete
    suspend fun deleteCourse(id: Int): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val course = Course.findById(id) ?: return@transaction false
                course.delete()
                true
            }
            if (success) {
                Logger.info("Course deleted successfully: ID $id")
                Pair(true, null)
            } else {
                val errorMessage = "Course not found with ID: $id"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error deleting course: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }

    // Additional utility methods
    suspend fun searchCourses(query: String): Pair<List<Course>, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(emptyList(), it)
        }

        try {
            val courses = transaction {
                Course.find {
                    (Courses.courseCode.lowerCase() like "%${query.lowercase()}%") or
                            (Courses.title.lowerCase() like "%${query.lowercase()}%") or
                            (Courses.description.lowerCase() like "%${query.lowercase()}%")
                }.toList()
            }
            Logger.info("Search completed. Found ${courses.size} courses matching query: $query")
            Pair(courses, null)
        } catch (e: Exception) {
            val errorMessage = "Error searching courses: ${e.message}"
            Logger.error(errorMessage)
            Pair(emptyList(), errorMessage)
        }
    }
}
