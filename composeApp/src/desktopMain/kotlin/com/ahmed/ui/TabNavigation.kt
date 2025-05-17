package com.ahmed.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.ahmed.ui.course.CourseTab
import com.ahmed.ui.student.StudentTab
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.rememberSplitPaneState

enum class TabItem(
    val title: String,
    val icon: ImageVector
) {
    Students("Students", Icons.Default.Person),
    Courses("Courses", Icons.Default.School)
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun TabNavigation(
    snackbarHostState: SnackbarHostState,
) {
    var selectedTab by remember { mutableStateOf(TabItem.Students) }
    val splitterState = rememberSplitPaneState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Tabs
        TabRow(
            selectedTabIndex = TabItem.entries.indexOf(selectedTab)
        ) {
            TabItem.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.title) },
                    icon = { Icon(tab.icon, contentDescription = null) }
                )
            }
        }

        // Tab Content with SnackbarHost
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                TabItem.Students -> StudentTab(snackbarHostState,  splitterState)
                TabItem.Courses -> CourseTab(snackbarHostState, splitterState)
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
