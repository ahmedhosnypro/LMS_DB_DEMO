package com.ahmed.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.datetime.date
import java.math.BigDecimal

enum class EnrollmentStatus(val value: String) {
    ENROLLED("Enrolled"),
    DROPPED("Dropped"),
    COMPLETED("Completed"),
    WITHDRAWN("Withdrawn");

    companion object {
        fun fromString(value: String): EnrollmentStatus = entries.find { it.value == value }
            ?: throw IllegalArgumentException("Invalid enrollment status: $value")
    }
}

object Enrollments : IntIdTable("enrollments") {
    val studentId = reference("student_id", Students)
    val courseId = reference("course_id", Courses)
    val enrollmentDate = date("enrollment_date")
        .check("enrollment_date_not_empty") { it.isNotNull() }
    val grade = decimal("grade", 4, 2)
        .nullable()
        .check("grade_valid") { 
            it.isNull() or (it.between(BigDecimal("0.00"), BigDecimal("4.00"))) 
        }
    val status = varchar("status", 20)
        .default(EnrollmentStatus.ENROLLED.value)
        .check("status_valid") { 
            it.inList(EnrollmentStatus.entries.map { status -> status.value }) 
        }
}

class Enrollment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Enrollment>(Enrollments)
    
    var studentId by Enrollments.studentId
    var courseId by Enrollments.courseId  
    var enrollmentDate by Enrollments.enrollmentDate
    var grade by Enrollments.grade
    var status by Enrollments.status

    fun getStatus(): EnrollmentStatus = EnrollmentStatus.fromString(status)
    fun setStatus(newStatus: EnrollmentStatus) {
        status = newStatus.value
    }

    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        if (enrollmentDate > currentDate) {
            errors.add("Enrollment date cannot be in the future")
        }
        
        if (grade != null) {
            val gradeValue = grade!!
            if (gradeValue < BigDecimal("0.00") || gradeValue > BigDecimal("4.00")) {
                errors.add("Grade must be between 0.00 and 4.00")
            }
        }
        
        return errors
    }
}

data class EnrollmentDTO(
    val id: Int?,
    val studentId: Int,
    val courseId: Int,
    val enrollmentDate: LocalDate,
    val grade: BigDecimal?,
    val status: EnrollmentStatus
)

fun Enrollment.toDTO() = EnrollmentDTO(
    id = id.value,
    studentId = studentId.value,
    courseId = courseId.value,
    enrollmentDate = enrollmentDate,
    grade = grade,
    status = EnrollmentStatus.fromString(status)
)
