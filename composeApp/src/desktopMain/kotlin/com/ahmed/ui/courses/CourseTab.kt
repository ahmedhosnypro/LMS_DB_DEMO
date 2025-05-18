package com.ahmed.ui.courses

import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.CourseDTO
import com.ahmed.ui.components.SplitterHandle
import com.ahmed.ui.components.SplitterVisiblePart
import com.ahmed.ui.courses.form.CourseForm
import com.ahmed.ui.courses.list.CourseList
import com.ahmed.viewModel.CourseViewModel
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun CourseTab(
    snackbarHostState: SnackbarHostState,
    splitterState: SplitPaneState,
    courseViewModel: CourseViewModel,
) {
    var selectedCourse by remember { mutableStateOf<CourseDTO?>(null) }

    val courses by courseViewModel.courses.collectAsState()
    val isLoading by courseViewModel.isLoading.collectAsState()
    val error by courseViewModel.error.collectAsState()

    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
        }
    }

    HorizontalSplitPane(
        splitPaneState = splitterState
    ) {
        first(700.dp) {
            CourseList(
                courses = courses,
                selectedCourse = selectedCourse,
                onCourseSelect = { course -> selectedCourse = course },
                onSearch = courseViewModel::searchCourses,
                onRefresh = courseViewModel::loadCourses,
                onDelete = { id ->
                    courseViewModel.deleteCourse(id)
                    if (selectedCourse?.id == id) {
                        selectedCourse = null
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxSize()
            )
        }
        second(300.dp) {
            CourseForm(
                selectedCourse = selectedCourse,
                onSave = { courseDto ->
                    selectedCourse?.let {
                        courseViewModel.updateCourse(courseDto)
                    } ?: courseViewModel.createCourse(courseDto)

                    selectedCourse = null
                },
                onCancel = { selectedCourse = null },
                modifier = Modifier.fillMaxSize()
            )
        }
        splitter {
            visiblePart {
                SplitterVisiblePart()
            }
            handle {
                SplitterHandle(this)
            }
        }
    }
}
