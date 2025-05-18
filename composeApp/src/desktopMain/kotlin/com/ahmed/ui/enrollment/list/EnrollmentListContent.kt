package com.ahmed.ui.enrollment.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.ahmed.model.EnrollmentDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.EmptyListPlaceholder
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EnrollmentListContent(
    enrollments: List<EnrollmentDTO>,
    selectedEnrollment: EnrollmentDTO?,
    onEnrollmentSelect: (EnrollmentDTO) -> Unit,
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
            enrollments.isEmpty() -> EmptyListPlaceholder("No enrollments found")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(all = 16.dp)
            ) {
                items(enrollments) { enrollment ->
                    EnrollmentListItem(
                        enrollment = enrollment,
                        isSelected = enrollment == selectedEnrollment,
                        onSelect = { onEnrollmentSelect(enrollment) },
                        onDelete = { onDelete(enrollment.id!!) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun EnrollmentListContentPreview() {
    CenteredDarkPreview {
        EnrollmentListContent(
            enrollments = EnrollmentDTO.demoEnrollmentList,
            selectedEnrollment = null,
            onEnrollmentSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun EnrollmentListContentLoadingPreview() {
    CenteredDarkPreview {
        EnrollmentListContent(
            enrollments = emptyList(),
            selectedEnrollment = null,
            onEnrollmentSelect = {},
            onDelete = {},
            isLoading = true
        )
    }
}

@Preview
@Composable
fun EnrollmentListContentEmptyPreview() {
    CenteredDarkPreview {
        EnrollmentListContent(
            enrollments = emptyList(),
            selectedEnrollment = null,
            onEnrollmentSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}
