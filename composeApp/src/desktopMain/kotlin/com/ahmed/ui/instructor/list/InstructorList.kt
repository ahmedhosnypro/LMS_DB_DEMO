package com.ahmed.ui.instructor.list

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.InstructorDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.ListHeader
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun InstructorList(
    instructors: List<InstructorDTO>,
    selectedInstructor: InstructorDTO?,
    onInstructorSelect: (InstructorDTO?) -> Unit,
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
            onAdd = { onInstructorSelect(null) },
            searchPlaceHolder = "Search by instructor name or email...",
            refreshTooltipText = "Refresh instructor list",
            addTooltipText = "Add new instructor",
            modifier = Modifier.requiredHeightIn(max = 128.dp)
        )
        InstructorListContent(
            instructors = instructors,
            selectedInstructor = selectedInstructor,
            onInstructorSelect = onInstructorSelect,
            onDelete = onDelete,
            isLoading = isLoading,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun InstructorListPreview() {
    CenteredDarkPreview {
        InstructorList(
            instructors = InstructorDTO.demoInstructorList,
            selectedInstructor = InstructorDTO.demoInstructor,
            onInstructorSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun InstructorListEmptyPreview() {
    CenteredDarkPreview {
        InstructorList(
            instructors = emptyList(),
            selectedInstructor = null,
            onInstructorSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}
