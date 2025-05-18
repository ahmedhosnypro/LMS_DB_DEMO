package com.ahmed.ui.attendance.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.unit.dp
import com.ahmed.model.AttendanceDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.EmptyListPlaceholder
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDate
import com.ahmed.model.AttendanceStatus

@Composable
fun AttendanceListContent(
    attendanceRecords: List<AttendanceDTO>,
    selectedAttendance: AttendanceDTO?,
    onAttendanceSelect: (AttendanceDTO) -> Unit,
    onDelete: (Int) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            attendanceRecords.isEmpty() -> EmptyListPlaceholder("No attendance records found")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(all = 16.dp)
            ) {
                items(attendanceRecords) { attendance ->
                    AttendanceListItem(
                        attendance = attendance,
                        onSelect = onAttendanceSelect,
                        onDelete = { attendance.id?.let { onDelete(it) } },
                        isSelected = attendance == selectedAttendance,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AttendanceListContentPreview() {
    val demoAttendance = AttendanceDTO(
        id = 1,
        enrollmentId = 1,
        date = LocalDate(2025, 5, 18),
        status = AttendanceStatus.PRESENT
    )
    CenteredDarkPreview {
        AttendanceListContent(
            attendanceRecords = listOf(demoAttendance, demoAttendance.copy(id = 2)),
            selectedAttendance = demoAttendance,
            onAttendanceSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun AttendanceListContentLoadingPreview() {
    CenteredDarkPreview {
        AttendanceListContent(
            attendanceRecords = emptyList(),
            selectedAttendance = null,
            onAttendanceSelect = {},
            onDelete = {},
            isLoading = true
        )
    }
}

@Preview
@Composable
fun AttendanceListContentEmptyPreview() {
    CenteredDarkPreview {
        AttendanceListContent(
            attendanceRecords = emptyList(),
            selectedAttendance = null,
            onAttendanceSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}
