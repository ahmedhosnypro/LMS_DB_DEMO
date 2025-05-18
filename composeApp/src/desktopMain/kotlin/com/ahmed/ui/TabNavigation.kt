package com.ahmed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ahmed.ui.attendance.AttendanceTab
import com.ahmed.ui.course.ManageCourseTab
import com.ahmed.ui.courses.CourseTab
import com.ahmed.ui.modifier.rightBorder
import com.ahmed.ui.student.StudentTab
import com.ahmed.viewModel.CourseViewModel
import com.ahmed.viewModel.StudentViewModel
import com.ahmed.viewModel.ViewModelProvider
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.rememberSplitPaneState

private enum class TabItem(
    val title: String,
    val icon: ImageVector
) {
    Students("Students", Icons.Default.Person),
    Courses("Courses", Icons.Default.School),
    Attendance("Attendance", Icons.AutoMirrored.Filled.EventNote),
    ManageCourse("Manage Course", Icons.Default.ManageAccounts)
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun TabNavigation(
    snackbarHostState: SnackbarHostState,
    courseViewModel: CourseViewModel = ViewModelProvider.courseViewModel,
    studentViewModel: StudentViewModel = ViewModelProvider.studentViewModel,
) {
    var selectedTab by remember { mutableStateOf(TabItem.Students) }
    val splitterState = rememberSplitPaneState()

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxHeight()
                .windowInsetsPadding(NavigationRailDefaults.windowInsets)
                .widthIn(min = 80.0.dp)
                .selectableGroup()
                .rightBorder(
                    color = MaterialTheme.colorScheme.outline,
                    width = 1f,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TabItem.entries.forEach { tab ->
                val color =
                    if (selectedTab == tab) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onBackground

                Column(
                    modifier = Modifier
                        .clickable { selectedTab = tab }
                        .requiredHeight(80.dp)
                        .requiredWidth(128.dp)
                        .background(if (selectedTab == tab) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                ) {
                    Icon(
                        tab.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = color
                    )
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = color
                    )
                }
            }
        }

        // Tab Content with SnackbarHost
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                TabItem.Students -> StudentTab(snackbarHostState, splitterState, studentViewModel)
                TabItem.Courses -> CourseTab(snackbarHostState, splitterState, courseViewModel)
                TabItem.Attendance -> AttendanceTab(snackbarHostState, splitterState)
                TabItem.ManageCourse -> ManageCourseTab(snackbarHostState, courseViewModel)
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
