package com.ahmed.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.datetime.date

object Instructors : IntIdTable("instructors") {
    val firstName = varchar("first_name", 50)
        .check("first_name_not_empty") { it.isNotNull() }
    val lastName = varchar("last_name", 50)
        .check("last_name_not_empty") { it.isNotNull() }
    val email = varchar("email", 100)
        .uniqueIndex()
        .check("email_not_empty") { it.isNotNull() }
    val department = varchar("department", 50)
        .check("department_not_empty") { it.isNotNull() }
    val hireDate = date("hire_date")
        .check("hire_date_not_empty") { it.isNotNull() }
}

class Instructor(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Instructor>(Instructors)
    
    var firstName by Instructors.firstName
    var lastName by Instructors.lastName
    var email by Instructors.email
    var department by Instructors.department
    var hireDate by Instructors.hireDate

    fun validate(): List<String> {
        val errors = validatePersonFields(firstName, lastName, email)
        
        if (department.length < 2) {
            errors.add("Department must be at least 2 characters long")
        }
        
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        if (hireDate > currentDate) {
            errors.add("Hire date cannot be in the future")
        }
        
        return errors
    }
}

data class InstructorDTO(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val department: String,
    val hireDate: LocalDate
){
    companion object{
        val demoInstructor  = InstructorDTO(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            email = "johndoe@me.com",
            department = "Computer Science",
            hireDate = LocalDate(2020, 1, 1)
        )

        val demoInstructor1 = InstructorDTO(
            id = 2,
            firstName = "Jane",
            lastName = "Smith",
            email = "janesmith@me.com",
            department = "Mathematics",
            hireDate = LocalDate(2021, 2, 2)
        )

        val demoInstructor2 = InstructorDTO(
            id = 3,
            firstName = "Alice",
            lastName = "Johnson",
            email = "alicejohnson@me.com",
            department = "Physics",
            hireDate = LocalDate(2022, 3, 3)
        )

        val demoInstructorList = listOf(
            demoInstructor,
            demoInstructor1,
            demoInstructor2
        )
    }
}

fun Instructor.toDTO() = InstructorDTO(
    id = id.value,
    firstName = firstName,
    lastName = lastName,
    email = email,
    department = department,
    hireDate = hireDate
)
