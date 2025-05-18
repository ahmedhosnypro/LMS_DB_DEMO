package com.ahmed.ui.attendance.list

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.AttendanceDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.ListHeader
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDate
import com.ahmed.model.AttendanceStatus

@Composable
fun AttendanceList(
    attendanceRecords: List<AttendanceDTO>,
    selectedAttendance: AttendanceDTO?,
    onAttendanceSelect: (AttendanceDTO?) -> Unit,
    onSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onDelete: (Int) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ListHeader(
            onSearch = onSearch,
            onRefresh = onRefresh,
            onAdd = { onAttendanceSelect(null) },
            searchPlaceHolder = "Search by date or status...",
            refreshTooltipText = "Refresh attendance list",
            addTooltipText = "Add new attendance record",
            modifier = Modifier.requiredHeightIn(max = 128.dp)
        )
        AttendanceListContent(
            attendanceRecords = attendanceRecords,
            selectedAttendance = selectedAttendance,
            onAttendanceSelect = onAttendanceSelect,
            onDelete = onDelete,
            isLoading = isLoading,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun AttendanceListPreview() {
    val demoAttendance = AttendanceDTO(
        id = 1,
        enrollmentId = 1,
        date = LocalDate(2025, 5, 18),
        status = AttendanceStatus.PRESENT
    )
    CenteredDarkPreview {
        AttendanceList(
            attendanceRecords = listOf(demoAttendance, demoAttendance.copy(id = 2)),
            selectedAttendance = demoAttendance,
            onAttendanceSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun AttendanceListEmptyPreview() {
    CenteredDarkPreview {
        AttendanceList(
            attendanceRecords = emptyList(),
            selectedAttendance = null,
            onAttendanceSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}
