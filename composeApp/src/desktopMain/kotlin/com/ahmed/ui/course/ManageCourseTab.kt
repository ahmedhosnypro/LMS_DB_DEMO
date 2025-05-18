package com.ahmed.ui.course

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.ahmed.model.CourseDTO
import com.ahmed.ui.enrollment.EnrollmentTab
import com.ahmed.ui.modifier.bottomBorder
import com.ahmed.viewModel.CourseViewModel
import com.ahmed.viewModel.EnrollmentViewModel
import com.ahmed.viewModel.StudentViewModel
import com.ahmed.viewModel.ViewModelProvider
import com.ahmed.viewModel.ViewModelProvider.ManageCourseTab_Course_KEY
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi

private enum class TabItem(
    val title: String,
    val icon: ImageVector
) {
    Enrollment("Enrollment", Icons.AutoMirrored.Filled.Assignment),
    Attendance("Attendance", Icons.AutoMirrored.Filled.EventNote),
}

@OptIn(ExperimentalSplitPaneApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ManageCourseTab(
    snackbarHostState: SnackbarHostState,
    courseViewModel: CourseViewModel,
) {
    val courses by courseViewModel.courses.collectAsState()
    val currentCourse by courseViewModel.currentCourse.collectAsState()
    val selectedTab = remember { mutableStateOf(TabItem.Enrollment) }

    LaunchedEffect(Unit) {
        courseViewModel.loadCourses()
    }

    LaunchedEffect(courses){
        if (courses.isNotEmpty() && currentCourse == null) {
            courseViewModel.setCurrentCourse(courses.first())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Header(
            courses = courses,
            currentCourse = currentCourse,
            onCourseSelected = courseViewModel::setCurrentCourse
        )
        // Tabs for course-specific content
        Tabs(selectedTab = selectedTab)

        // Content based on selected tab
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when (selectedTab.value) {
                TabItem.Enrollment -> {
                    EnrollmentTab(
                        snackbarHostState,
                        viewModel = ViewModelProvider.Factory.create(
                            EnrollmentViewModel::class,
                            extras = MutableCreationExtras().apply {
                                set(ManageCourseTab_Course_KEY, currentCourse)
                            }
                        ),
                        courseDto= currentCourse,
                    )
                }

                TabItem.Attendance -> {
                    // Attendance content
                    Text("Attendance for selected course will be displayed here")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Header(
    courses: List<CourseDTO>,
    currentCourse: CourseDTO?,
    onCourseSelected: (CourseDTO?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .bottomBorder(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                height = 1f
            )
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = currentCourse?.title ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface
                ),
                placeholder = {
                    Text(
                        "Select a course...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(4.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                courses.forEach { course ->
                    DropdownMenuItem(
                        text = { Text(course.title) },
                        onClick = {
                            onCourseSelected(course)
                            expanded = false
                        }
                    )
                }
            }
        }

        TooltipArea(
            tooltip = {
                Surface(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        "Refresh courses",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    }
}

@Composable
private fun Tabs(
    selectedTab: MutableState<TabItem>
) {
    TabRow(
        selectedTabIndex = TabItem.entries.indexOf(selectedTab.value),
    ) {
        TabItem.entries.forEach { tab ->
            Tab(
                selected = selectedTab.value == tab,
                onClick = { selectedTab.value = tab },
                modifier = Modifier.height(40.dp),
                text = null,
                icon = {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            tab.icon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
