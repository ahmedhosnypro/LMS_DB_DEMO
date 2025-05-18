package com.ahmed.ui.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.ahmed.model.CourseDTO
import com.ahmed.ui.components.SplitterHandle
import com.ahmed.ui.components.SplitterVisiblePart
import com.ahmed.ui.course.form.CourseForm
import com.ahmed.ui.course.list.CourseList
import com.ahmed.ui.modifier.cursorForHorizontalResize
import com.ahmed.viewModel.CourseViewModel
import com.ahmed.viewModel.ViewModelProvider
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun CourseTab(
    snackbarHostState: SnackbarHostState,
    splitterState: SplitPaneState,
    viewModel: CourseViewModel = ViewModelProvider.courseViewModel
) {
    var selectedCourse by remember { mutableStateOf<CourseDTO?>(null) }

    val courses by viewModel.courses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

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
                onSearch = viewModel::searchCourses,
                onRefresh = viewModel::loadCourses,
                onDelete = { id ->
                    viewModel.deleteCourse(id)
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
                        viewModel.updateCourse(courseDto)
                    } ?: viewModel.createCourse(courseDto)

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
