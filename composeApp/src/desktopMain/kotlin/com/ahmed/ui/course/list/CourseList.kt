package com.ahmed.ui.course.list

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.CourseDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.ListHeader
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CourseList(
    courses: List<CourseDTO>,
    selectedCourse: CourseDTO?,
    onCourseSelect: (CourseDTO?) -> Unit,
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
            onAdd = { onCourseSelect(null) },
            searchPlaceHolder = "Search by course name or code...",
            refreshTooltipText = "Refresh course list",
            addTooltipText = "Add new course",
            modifier = Modifier.requiredHeightIn(max = 128.dp)
        )

        CourseListContent(
            courses = courses,
            selectedCourse = selectedCourse,
            onCourseSelect = onCourseSelect,
            onDelete = onDelete,
            isLoading = isLoading,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun CourseListPreview() {
    CenteredDarkPreview {
        CourseList(
            courses = listOf(
                CourseDTO(1, "CS101", "Introduction to Computer Science", null, 3, null, null, "Active"),
                CourseDTO(2, "CS102", "Data Structures", null, 3, null, null, "Active")
            ),
            selectedCourse = CourseDTO(1, "CS101", "Introduction to Computer Science", null, 3, null, null, "Active"),
            onCourseSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun CourseListEmptyPreview() {
    CenteredDarkPreview {
        CourseList(
            courses = emptyList(),
            selectedCourse = null,
            onCourseSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}
