package com.ahmed.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.datetime.date

enum class StudentStatus(val value: String) {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    GRADUATED("Graduated"),
    ON_LEAVE("On Leave"),
    WITHDRAWN("Withdrawn");

    companion object {
        fun fromString(value: String): StudentStatus = entries.find { it.value == value }
            ?: throw IllegalArgumentException("Invalid student status: $value")
    }

}

private val currentDate: LocalDate
    get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

object Students : IntIdTable("students") {
    val firstName = varchar("first_name", 50).check("first_name_valid") { 
        (it.match("[A-Za-z-]+")) and (it.greaterEq("  ")) 
    }
    val lastName = varchar("last_name", 50).check("last_name_valid") { 
        (it.match("[A-Za-z-]+")) and (it.greaterEq("  ")) 
    }
    val email = varchar("email", 100)
        .uniqueIndex()
        .check("email_valid") { 
            it.match("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") and 
            (it.greaterEq("     ")) 
        }
    val dateOfBirth = date("date_of_birth").check("dob_valid") {
        it.less(currentDate) and it.greater(currentDate.minus(DatePeriod(years = 100)))
    }
    val enrollmentDate = date("enrollment_date").check("enrollment_date_valid") {
        it.lessEq(currentDate)
    }
    val status = varchar("status", 20).default(StudentStatus.ACTIVE.value)
        .check("status_valid") { 
            it.inList(StudentStatus.entries.map { status -> status.value }) 
        }
}

class Student(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Student>(Students)

    var firstName by Students.firstName
    var lastName by Students.lastName
    var email by Students.email
    var dateOfBirth by Students.dateOfBirth
    var enrollmentDate by Students.enrollmentDate
    var status by Students.status
    
    fun getStatus(): StudentStatus = StudentStatus.fromString(status)
    fun setStatus(newStatus: StudentStatus) {
        status = newStatus.value
    }

    fun validate(): List<String> {
        val errors = validatePersonFields(firstName, lastName, email)
        
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val age = currentDate.year - dateOfBirth.year
        if (age < 16 || age > 100) {
            errors.add("Student must be between 16 and 100 years old")
        }
        
        if (enrollmentDate > currentDate) {
            errors.add("Enrollment date cannot be in the future")
        }
        
        return errors
    }
}

data class StudentDTO(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val dateOfBirth: LocalDate,
    val enrollmentDate: LocalDate,
    val status: StudentStatus
) {
    companion object {
        val demoStudent = StudentDTO(
            id = 1,
            firstName = "Ahmed",
            lastName = "Hosny",
            email = "ahmedhosny@me.com",
            dateOfBirth = LocalDate(2000, 1, 1),
            enrollmentDate = LocalDate(2020, 1, 1),
            status = StudentStatus.ACTIVE
        )
    }
}

fun Student.toDTO() = StudentDTO(
    id = id.value,
    firstName = firstName,
    lastName = lastName,
    email = email,
    dateOfBirth = dateOfBirth,
    enrollmentDate = enrollmentDate,
    status = StudentStatus.fromString(status)
)
