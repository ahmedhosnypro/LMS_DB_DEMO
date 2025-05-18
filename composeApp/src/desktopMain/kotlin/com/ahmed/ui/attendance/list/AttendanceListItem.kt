package com.ahmed.ui.attendance.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ahmed.model.AttendanceDTO
import com.ahmed.model.AttendanceStatus
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.ConfirmDeleteDialog
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDate

private val KotlinRose = Color(0xffba203d)
private val KotlinPurple = Color(0xff8613b6)
private val KotlinPurpleBlue = Color(0xff5234ab)

private val StatusColors = mapOf(
    AttendanceStatus.PRESENT to Color(0xFF4CAF50),
    AttendanceStatus.ABSENT to Color(0xFFF44336),
    AttendanceStatus.LATE to Color(0xFFFF9800),
    AttendanceStatus.EXCUSED to Color(0xFF2196F3)
)

fun Modifier.attendanceListItemBackground(
    isSelected: Boolean
) : Modifier =
    background(
        brush = Brush.linearGradient(
            colors = if (isSelected) {
                listOf(KotlinRose, KotlinPurple, KotlinPurple, KotlinPurpleBlue)
            } else {
                listOf(
                    KotlinRose.copy(alpha = 0.8f),
                    KotlinPurple.copy(alpha = 0.8f),
                    KotlinPurple.copy(alpha = 0.8f),
                    KotlinPurpleBlue.copy(alpha = 0.8f)
                )
            }
        )
    )

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AttendanceListItem(
    attendance: AttendanceDTO,
    onSelect: (AttendanceDTO) -> Unit,
    onDelete: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onDelete = onDelete,
            title = "Confirm Delete",
            text = "Are you sure you want to delete this attendance record?"
        )
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(attendance) }
            .padding(8.dp),
        elevation = if (isSelected) CardDefaults.outlinedCardElevation() else CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .attendanceListItemBackground(isSelected)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Date: ${attendance.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enrollment ID: ${attendance.enrollmentId}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.8f
                        )
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Chip(
                        onClick = { },
                        colors = ChipDefaults.chipColors(
                            backgroundColor = StatusColors[attendance.status] ?: Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text(attendance.status.value)
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete attendance record",
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AttendanceListItemPreview() {
    val demoAttendance = AttendanceDTO(
        id = 1,
        enrollmentId = 1,
        date = LocalDate(2025, 5, 18),
        status = AttendanceStatus.PRESENT
    )
    CenteredDarkPreview {
        AttendanceListItem(
            attendance = demoAttendance,
            onSelect = {},
            onDelete = {},
            isSelected = false
        )
    }
}
