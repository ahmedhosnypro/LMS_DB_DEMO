package com.ahmed.repository

import com.ahmed.model.*
import com.ahmed.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class AttendanceRepository {
    private fun checkDatabaseConnection(): String? {
        return if (!DatabaseManager.isDatabaseReady()) {
            "Database is not connected or initialized"
        } else null
    }

    // Create
    suspend fun createAttendance(attendanceDto: AttendanceDTO): Pair<AttendanceRecord?, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(null, it)
        }

        try {
            val attendance = transaction {
                // First find the enrollment
                val enrollment = Enrollment.findById(attendanceDto.enrollmentId) 
                    ?: throw IllegalArgumentException("Enrollment not found with ID: ${attendanceDto.enrollmentId}")
                
                val record = AttendanceRecord.new {
                    this.enrollment = enrollment
                    this.date = attendanceDto.date
                    this.status = attendanceDto.status.value
                }
                
                // Validate the record before committing
                val validationErrors = record.validate()
                if (validationErrors.isNotEmpty()) {
                    throw IllegalArgumentException(validationErrors.joinToString(", "))
                }
                
                record
            }
            Logger.info("Attendance record created successfully: Enrollment ${attendance.enrollment.id.value} for ${attendance.date}")
            Pair(attendance, null)
        } catch (e: Exception) {
            val errorMessage = "Error creating attendance record: ${e.message}"
            Logger.error(errorMessage)
            Pair(null, errorMessage)
        }
    }

    // Read
    suspend fun getAttendanceById(id: Int): Pair<AttendanceRecord?, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(null, it)
        }

        try {
            val attendance = transaction {
                AttendanceRecord.findById(id)
            }
            if (attendance != null) {
                Logger.info("Attendance record retrieved successfully: ID $id")
                Pair(attendance, null)
            } else {
                val errorMessage = "Attendance record not found with ID: $id"
                Logger.warning(errorMessage)
                Pair(null, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error retrieving attendance record: ${e.message}"
            Logger.error(errorMessage)
            Pair(null, errorMessage)
        }
    }

    suspend fun getAllAttendance(): Pair<List<AttendanceDTO>, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(emptyList(), it)
        }

        try {
            val dtos = transaction {
                AttendanceRecord.all().map { it.toDTO() }.toList()
            }
            Logger.info("Retrieved ${dtos.size} attendance records")
            Pair(dtos, null)
        } catch (e: Exception) {
            val errorMessage = "Error retrieving all attendance records: ${e.message}"
            Logger.error(errorMessage)
            Pair(emptyList(), errorMessage)
        }
    }

    suspend fun getAttendanceByEnrollmentId(enrollmentId: Int): Pair<List<AttendanceDTO>, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(emptyList(), it)
        }

        try {
            val dtos = transaction {
                AttendanceRecord.find { 
                    Attendance.enrollmentId eq EntityID(enrollmentId, Enrollments) 
                }.map { it.toDTO() }.toList()
            }
            Logger.info("Retrieved ${dtos.size} attendance records for enrollment $enrollmentId")
            Pair(dtos, null)
        } catch (e: Exception) {
            val errorMessage = "Error retrieving attendance records for enrollment $enrollmentId: ${e.message}"
            Logger.error(errorMessage)
            Pair(emptyList(), errorMessage)
        }
    }

    // Update
    suspend fun updateAttendance(attendanceDto: AttendanceDTO): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        if (attendanceDto.id == null) {
            return@withContext Pair(false, "Attendance ID is null")
        }

        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val record = AttendanceRecord.findById(attendanceDto.id) ?: return@transaction false
                
                // Find the enrollment first
                val enrollment = Enrollment.findById(attendanceDto.enrollmentId)
                    ?: throw IllegalArgumentException("Enrollment not found with ID: ${attendanceDto.enrollmentId}")
                    
                record.enrollment = enrollment
                record.date = attendanceDto.date
                record.status = attendanceDto.status.value

                // Validate before updating
                val validationErrors = record.validate()
                if (validationErrors.isNotEmpty()) {
                    throw IllegalArgumentException(validationErrors.joinToString(", "))
                }

                true
            }
            if (success) {
                Logger.info("Attendance record updated successfully: ID ${attendanceDto.id}")
                Pair(true, null)
            } else {
                val errorMessage = "Attendance record not found with ID: ${attendanceDto.id}"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error updating attendance record: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }

    // Delete
    suspend fun deleteAttendance(id: Int): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(false, it)
        }

        try {
            val success = transaction {
                val attendance = AttendanceRecord.findById(id) ?: return@transaction false
                attendance.delete()
                true
            }
            if (success) {
                Logger.info("Attendance record deleted successfully: ID $id")
                Pair(true, null)
            } else {
                val errorMessage = "Attendance record not found with ID: $id"
                Logger.warning(errorMessage)
                Pair(false, errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Error deleting attendance record: ${e.message}"
            Logger.error(errorMessage)
            Pair(false, errorMessage)
        }
    }

    // Additional utility methods
    suspend fun getAttendanceByStatus(status: AttendanceStatus): Pair<List<AttendanceRecord>, String?> = withContext(Dispatchers.IO) {
        checkDatabaseConnection()?.let {
            return@withContext Pair(emptyList(), it)
        }

        try {
            val records = transaction {
                AttendanceRecord.find { Attendance.status eq status.value }.toList()
            }
            Logger.info("Retrieved ${records.size} attendance records with status: $status")
            Pair(records, null)
        } catch (e: Exception) {
            val errorMessage = "Error finding attendance records by status: ${e.message}"
            Logger.error(errorMessage)
            Pair(emptyList(), errorMessage)
        }
    }
}
