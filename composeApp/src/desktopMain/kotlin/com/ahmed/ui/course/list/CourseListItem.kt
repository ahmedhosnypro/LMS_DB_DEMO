package com.ahmed.ui.course.list

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
import com.ahmed.model.CourseDTO
import com.ahmed.ui.CenteredDarkPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

private val EduBlue = Color(0xFF1565C0)
private val EduTeal = Color(0xFF00838F)
private val EduGreen = Color(0xFF2E7D32)
private val EduCyan = Color(0xFF00ACC1)

private val StatusColors = mapOf(
    "Active" to Color(0xFF4CAF50),
    "Inactive" to Color(0xFFF44336),
    "Upcoming" to Color(0xFF2196F3),
    "Archived" to Color(0xFF9E9E9E)
)

fun Modifier.courseListItemBackground(
    isSelected: Boolean
) : Modifier =
    background(
        brush = Brush.linearGradient(
            colors = if (isSelected) {
                listOf(EduBlue, EduTeal, EduGreen, EduCyan)
            } else {
                listOf(
                    EduBlue.copy(alpha = 0.7f),
                    EduTeal.copy(alpha = 0.7f),
                    EduGreen.copy(alpha = 0.7f),
                    EduCyan.copy(alpha = 0.7f)
                )
            }
        )
    )

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CourseListItem(
    course: CourseDTO,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                onDelete()
                showDeleteDialog = false
            },
            course = course
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(8.dp),
        elevation = if (isSelected) CardDefaults.outlinedCardElevation() else CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .courseListItemBackground(isSelected)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${course.courseCode} - ${course.title}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Credits: ${course.credits}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Chip(
                        onClick = { },
                        colors = ChipDefaults.chipColors(
                            backgroundColor = StatusColors[course.status] ?: Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text(course.status)
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
                            contentDescription = "Delete course"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    course: CourseDTO
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Delete") },
        text = { Text("Are you sure you want to delete ${course.courseCode} - ${course.title}?") },
        confirmButton = {
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun CourseListItemPreview() {
    CenteredDarkPreview {
        CourseListItem(
            course = CourseDTO.demoCourse,
            isSelected = false,
            onSelect = {},
            onDelete = {}
        )
    }
}

@Preview
@Composable
fun CourseListItemSelectedPreview() {
    CenteredDarkPreview {
        CourseListItem(
            course = CourseDTO.demoCourse,
            isSelected = true,
            onSelect = {},
            onDelete = {}
        )
    }
}
