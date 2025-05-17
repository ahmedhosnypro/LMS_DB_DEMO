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

enum class AttendanceStatus(val value: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late"),
    EXCUSED("Excused");

    companion object {
        fun fromString(value: String): AttendanceStatus = entries.find { it.value == value }
            ?: throw IllegalArgumentException("Invalid attendance status: $value")
    }
}

object Attendance : IntIdTable("attendance") {
    val enrollmentId = reference("enrollment_id", Enrollments)
    val date = date("date")
        .check("date_not_empty") { it.isNotNull() }
    val status = varchar("status", 20)
        .check("status_valid") { 
            it.inList(AttendanceStatus.entries.map { status -> status.value }) 
        }

    init {
        uniqueIndex("unique_attendance", enrollmentId, date)
    }
}

class AttendanceRecord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AttendanceRecord>(Attendance)
    
    var enrollment by Enrollment referencedOn Attendance.enrollmentId
    var date by Attendance.date
    var status by Attendance.status

    fun getStatus(): AttendanceStatus = AttendanceStatus.fromString(status)
    fun setStatus(newStatus: AttendanceStatus) {
        status = newStatus.value
    }

    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        if (date > currentDate) {
            errors.add("Attendance date cannot be in the future")
        }
        
        return errors
    }
}

data class AttendanceDTO(
    val id: Int?,
    val enrollmentId: Int,
    val date: LocalDate,
    val status: AttendanceStatus
)

fun AttendanceRecord.toDTO() = AttendanceDTO(
    id = id.value,
    enrollmentId = enrollment.id.value,
    date = date,
    status = AttendanceStatus.fromString(status)
)
