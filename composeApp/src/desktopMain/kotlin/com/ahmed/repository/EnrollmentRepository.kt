package com.ahmed.repository

import com.ahmed.model.Course
import com.ahmed.model.Courses
import com.ahmed.model.DatabaseManager
import com.ahmed.model.Enrollment
import com.ahmed.model.EnrollmentDTO
import com.ahmed.model.Enrollments
import com.ahmed.model.Student
import com.ahmed.model.Students
import com.ahmed.model.toDTO
import com.ahmed.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.collections.map

class EnrollmentRepository {
    private fun checkDatabaseConnection(): String? {
        return if (!DatabaseManager.isDatabaseReady()) {
            "Database is not connected or initialized"
        } else null
    }

    // Create
    suspend fun createEnrollment(enrollmentDto: EnrollmentDTO): Pair<EnrollmentDTO?, String?> =
        withContext(Dispatchers.IO) {
            checkDatabaseConnection()?.let {
                return@withContext Pair(null, it)
            }

            val studentId = enrollmentDto.student.id
            val courseId = enrollmentDto.course.id

            if (studentId == null || courseId == null) {
                return@withContext Pair(null, "Student ID or Course ID is null")
            }

            try {
                val (enrollment, student, course) = transaction {
                    val enrollment = Enrollment.new {
                        this.studentId = EntityID(enrollmentDto.student.id, Students)
                        this.courseId = EntityID(enrollmentDto.course.id, Courses)
                        this.enrollmentDate = enrollmentDto.enrollmentDate
                        this.grade = enrollmentDto.grade
                        this.status = enrollmentDto.status.value
                    }
                    val student = Student.findById(enrollment.studentId.value)?.toDTO()
                    val course = Course.findById(enrollment.courseId.value)?.toDTO()
                    Triple(enrollment, student, course)
                }
                Logger.info("Enrollment created successfully: ID ${enrollment.id}")
                Pair(enrollment.toDTO(student!!, course!!), null)
            } catch (e: Exception) {
                val errorMessage = "Error creating enrollment: ${e.message}"
                Logger.error(errorMessage)
                Pair(null, errorMessage)
            }
        }

    suspend fun getEnrollmentsByCourseId(courseId: Int): Pair<List<EnrollmentDTO>, String?> =
        withContext(Dispatchers.IO) {
            checkDatabaseConnection()?.let {
                return@withContext Pair(emptyList(), it)
            }

            try {
                val enrollmentsWithData = transaction {
                    Enrollment.find { Enrollments.courseId eq EntityID(courseId, Courses) }
                        .map { enrollment ->
                            val student = Student.findById(enrollment.studentId.value)?.toDTO()
                            val course = Course.findById(enrollment.courseId.value)?.toDTO()
                            Triple(enrollment, student, course)
                        }
                }
                val dtos = enrollmentsWithData.map { (enrollment, student, course) ->
                    enrollment.toDTO(student!!, course!!)
                }
                Logger.info("Retrieved ${dtos.size} enrollments for course ID: $courseId")
                Pair(dtos, null)
            } catch (e: Exception) {
                val errorMessage = "Error retrieving enrollments for course: ${e.message}"
                Logger.error(errorMessage)
                Pair(emptyList(), errorMessage)
            }
        }

    suspend fun getUnenrolledStudentsForCourse(courseId: Int): Pair<List<Student>, String?> =
        withContext(Dispatchers.IO) {
            checkDatabaseConnection()?.let {
                return@withContext Pair(emptyList(), it)
            }

            try {
                val students = transaction {
                    // Get all students that don't have an enrollment for this course
                    Student.all().filter { student ->
                        Enrollment.find {
                            (Enrollments.courseId eq EntityID(courseId, Courses)) and
                                    (Enrollments.studentId eq student.id)
                        }.empty()
                    }.toList()
                }
                Logger.info("Retrieved ${students.size} unenrolled students for course ID: $courseId")
                Pair(students, null)
            } catch (e: Exception) {
                val errorMessage = "Error retrieving unenrolled students: ${e.message}"
                Logger.error(errorMessage)
                Pair(emptyList(), errorMessage)
            }
        }

    // Update
    suspend fun updateEnrollment(
        enrollmentDto: EnrollmentDTO
    ): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        if (enrollmentDto.id == null) {
            return@withContext Pair(false, "Enrollment ID is null")
        }

        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val enrollment = Enrollment.findById(enrollmentDto.id) ?: return@transaction false
                enrollment.grade = enrollmentDto.grade
                enrollment.status = enrollmentDto.status.value
                true
            }
            if (success) {
                Logger.info("Enrollment updated successfully: ID ${enrollmentDto.id}")
                Pair(true, null)
            } else {
                val errorMessage = "Enrollment not found with ID: ${enrollmentDto.id}"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error updating enrollment: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }

    // Delete
    suspend fun deleteEnrollment(id: Int): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val enrollment = Enrollment.findById(id) ?: return@transaction false
                enrollment.delete()
                true
            }
            if (success) {
                Logger.info("Enrollment deleted successfully: ID $id")
                Pair(true, null)
            } else {
                val errorMessage = "Enrollment not found with ID: $id"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error deleting enrollment: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }
}
