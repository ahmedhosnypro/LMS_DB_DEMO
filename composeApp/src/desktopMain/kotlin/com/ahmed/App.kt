package com.ahmed

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.ahmed.ui.theme.AppTheme
import com.ahmed.ui.AppScaffold
import com.ahmed.ui.student.StudentTab
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme(true) {
        Surface {
            AppScaffold { snackbarHostState ->
                StudentTab(snackbarHostState)
            }
        }
    }
}