package com.ahmed.ui.course.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.CourseDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.EmptyListPlaceholder
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CourseListContent(
    courses: List<CourseDTO>,
    selectedCourse: CourseDTO?,
    onCourseSelect: (CourseDTO) -> Unit,
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
            courses.isEmpty() -> EmptyListPlaceholder("No Courses found")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(all = 16.dp)
            ) {
                items(courses) { course ->
                    CourseListItem(
                        course = course,
                        isSelected = selectedCourse?.id == course.id,
                        onSelect = { onCourseSelect(course) },
                        onDelete = { onDelete(course.id) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CourseListContentPreview() {
    CenteredDarkPreview {
        CourseListContent(
            courses = listOf(
                CourseDTO(1, "CS101", "Introduction to Computer Science", null, 3, null, null, "Active"),
                CourseDTO(2, "CS102", "Data Structures", null, 3, null, null, "Active"),
                CourseDTO(3, "CS103", "Algorithms", null, 3, null, null, "Active")
            ),
            selectedCourse = CourseDTO(2, "CS102", "Data Structures", null, 3, null, null, "Active"),
            onCourseSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun CourseListContentLoadingPreview() {
    CenteredDarkPreview {
        CourseListContent(
            courses = emptyList(),
            selectedCourse = null,
            onCourseSelect = {},
            onDelete = {},
            isLoading = true
        )
    }
}

@Preview
@Composable
fun CourseListContentEmptyPreview() {
    CenteredDarkPreview {
        CourseListContent(
            courses = emptyList(),
            selectedCourse = null,
            onCourseSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}


