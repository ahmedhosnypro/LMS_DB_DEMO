package com.ahmed.model

import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

enum class CourseStatus(val value: String) {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    ARCHIVED("Archived");

    companion object {
        fun fromString(value: String): CourseStatus = entries.find { it.value == value }
            ?: throw IllegalArgumentException("Invalid course status: $value")
    }
}

object Courses : IntIdTable("courses") {
    val courseCode = varchar("course_code", 10)
        .uniqueIndex()
        .check("course_code_valid") {
            it.match("[A-Z]{2,4}[0-9]{3,4}") and (it.greaterEq("     "))
        }
    val title = varchar("title", 100)
        .check("title_valid") {
            it.greaterEq("    ")
        }
    val description = text("description").nullable()
    val credits = integer("credits")
        .check("credits_valid") {
            it.between(1, 6)
        }
    val instructorId = reference("instructor_id", Instructors).nullable()
    val maxStudents = integer("max_students")
        .nullable()
        .check("max_students_valid") {
            it.isNull() or it.between(1, 200)
        }
    val status = varchar("status", 20)
        .default(CourseStatus.ACTIVE.value)
        .check("status_valid") {
            it.inList(CourseStatus.entries.map { status -> status.value })
        }
}

class Course(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Course>(Courses)

    var courseCode by Courses.courseCode
    var title by Courses.title
    var description by Courses.description
    var credits by Courses.credits
    var instructorId by Courses.instructorId
    var maxStudents by Courses.maxStudents
    var status by Courses.status

    fun getStatus(): CourseStatus = CourseStatus.fromString(status)
    fun setStatus(newStatus: CourseStatus) {
        status = newStatus.value
    }

    fun validate(): List<String> {
        val errors = mutableListOf<String>()

        if (!courseCode.matches(Regex("[A-Z]{2,4}[0-9]{3,4}"))) {
            errors.add("Course code must be in format: 2-4 capital letters followed by 3-4 numbers (e.g., CS101, MATH2001)")
        }

        if (title.length < 4) {
            errors.add("Title must be at least 4 characters long")
        }

        if (credits !in 1..6) {
            errors.add("Credits must be between 1 and 6")
        }

        if (maxStudents != null && (maxStudents!! < 1 || maxStudents!! > 200)) {
            errors.add("Maximum students must be between 1 and 200")
        }

        return errors
    }
}

data class CourseDTO(
    val id: Int,
    val courseCode: String,
    val title: String,
    val description: String?,
    val credits: Int,
    val instructorId: Int?,
    val maxStudents: Int?,
    val status: String
)

fun Course.toDTO() = CourseDTO(
    id = id.value,
    courseCode = courseCode,
    title = title,
    description = description,
    credits = credits,
    instructorId = instructorId?.value,
    maxStudents = maxStudents,
    status = status
)
