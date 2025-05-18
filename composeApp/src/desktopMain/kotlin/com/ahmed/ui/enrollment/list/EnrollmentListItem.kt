package com.ahmed.ui.enrollment.list

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
import com.ahmed.model.EnrollmentDTO
import com.ahmed.model.EnrollmentStatus
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.ConfirmDeleteDialog
import org.jetbrains.compose.ui.tooling.preview.Preview

private val GradientGreen = Color(0xFF1B5E20)
private val GradientBlue = Color(0xFF0D47A1)
private val GradientPurple = Color(0xFF4A148C)
private val GradientIndigo = Color(0xFF1A237E)

private val StatusColors = mapOf(
    EnrollmentStatus.ENROLLED to Color(0xFF4CAF50),
    EnrollmentStatus.DROPPED to Color(0xFFF44336),
    EnrollmentStatus.COMPLETED to Color(0xFF2196F3),
    EnrollmentStatus.WITHDRAWN to Color(0xFF9E9E9E)
)

fun Modifier.enrollmentListItemBackground(
    isSelected: Boolean
) : Modifier =
    background(
        brush = Brush.linearGradient(
            colors = if (isSelected) {
                listOf(GradientGreen, GradientBlue, GradientPurple, GradientIndigo)
            } else {
                listOf(
                    GradientGreen.copy(alpha = 0.7f),
                    GradientBlue.copy(alpha = 0.7f),
                    GradientPurple.copy(alpha = 0.7f),
                    GradientIndigo.copy(alpha = 0.7f)
                )
            }
        )
    )

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnrollmentListItem(
    enrollment: EnrollmentDTO,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onDelete = onDelete,
            title = "Confirm Delete",
            text = "Are you sure you want to delete this student's enrollment?"
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(8.dp),
        elevation = if (isSelected) CardDefaults.outlinedCardElevation() else CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .enrollmentListItemBackground(isSelected)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = enrollment.student.let { "${it.firstName} ${it.lastName}" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ID: ${enrollment.student.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enrolled: ${enrollment.enrollmentDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) 
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    enrollment.grade?.let { grade ->
                        Text(
                            text = "Grade: $grade",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) 
                                   else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Chip(
                        onClick = { },
                        colors = ChipDefaults.chipColors(
                            backgroundColor = StatusColors[enrollment.status] ?: Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text(enrollment.status.value)
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
                            contentDescription = "Delete enrollment"
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun EnrollmentListItemPreview() {
    CenteredDarkPreview {
        EnrollmentListItem(
            enrollment = EnrollmentDTO.demoEnrollment,
            isSelected = false,
            onSelect = {},
            onDelete = {}
        )
    }
}

@Preview
@Composable
fun EnrollmentListItemSelectedPreview() {
    CenteredDarkPreview {
        EnrollmentListItem(
            enrollment = EnrollmentDTO.demoEnrollment,
            isSelected = true,
            onSelect = {},
            onDelete = {}
        )
    }
}
