package com.ahmed.ui.enrollment.list

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.EnrollmentDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.ListHeader
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EnrollmentList(
    enrollments: List<EnrollmentDTO>,
    selectedEnrollment: EnrollmentDTO?,
    onEnrollmentSelect: (EnrollmentDTO?) -> Unit,
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
            onSearch = { query -> if (query.isNotBlank()) onSearch(query) },
            onRefresh = onRefresh,
            onAdd = { 
                onEnrollmentSelect(null)
            },
            searchPlaceHolder = "Search by student name...",
            refreshTooltipText = "Refresh enrollments list",
            addTooltipText = "Add new enrollment",
            modifier = Modifier.requiredHeightIn(max = 128.dp)
        )

        EnrollmentListContent(
            enrollments = enrollments,
            selectedEnrollment = selectedEnrollment,
            onEnrollmentSelect = onEnrollmentSelect,
            onDelete = onDelete,
            isLoading = isLoading,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun EnrollmentListPreview() {
    val testEnrollment = EnrollmentDTO.demoEnrollment
    CenteredDarkPreview {
        EnrollmentList(
            enrollments = listOf(testEnrollment, testEnrollment.copy(id = 2)),
            selectedEnrollment = testEnrollment,
            onEnrollmentSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun EnrollmentListEmptyPreview() {
    CenteredDarkPreview {
        EnrollmentList(
            enrollments = emptyList(),
            selectedEnrollment = null,
            onEnrollmentSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}
