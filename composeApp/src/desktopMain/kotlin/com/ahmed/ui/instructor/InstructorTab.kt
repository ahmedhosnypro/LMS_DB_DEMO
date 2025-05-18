package com.ahmed.ui.instructor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.InstructorDTO
import com.ahmed.ui.components.SplitterHandle
import com.ahmed.ui.components.SplitterVisiblePart
import com.ahmed.ui.instructor.form.InstructorForm
import com.ahmed.ui.instructor.list.InstructorList
import com.ahmed.viewModel.InstructorViewModel
import com.ahmed.viewModel.ViewModelProvider
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun InstructorTab(
    snackbarHostState: SnackbarHostState,
    splitterState: SplitPaneState,
    viewModel: InstructorViewModel = ViewModelProvider.instructorViewModel
) {
    var selectedInstructor by remember { mutableStateOf<InstructorDTO?>(null) }

    val instructors by viewModel.instructors.collectAsState()
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
            InstructorList(
                instructors = instructors,
                selectedInstructor = selectedInstructor,
                onInstructorSelect = { instructor -> selectedInstructor = instructor },
                onSearch = viewModel::searchInstructors,
                onRefresh = viewModel::loadInstructors,
                onDelete = { id ->
                    viewModel.deleteInstructor(id)
                    if (selectedInstructor?.id == id) {
                        selectedInstructor = null
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxSize()
            )
        }
        second(300.dp) {
            InstructorForm(
                selectedInstructor = selectedInstructor,
                onSave = { instructorDto ->
                    selectedInstructor?.let {
                        viewModel.updateInstructor(instructorDto)
                    } ?: viewModel.createInstructor(instructorDto)
                    
                    selectedInstructor = null
                },
                onCancel = { selectedInstructor = null },
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
