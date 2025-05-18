package com.ahmed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.ahmed.ui.modifier.cursorForHorizontalResize
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HandleScope

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun SplitterHandle(
    handleScope: HandleScope
) {
    with(handleScope) {
        Box(
            Modifier
                .markAsHandle()
                .cursorForHorizontalResize()
                .background(SolidColor(Color.Gray), alpha = 0.50f)
                .width(4.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
fun SplitterVisiblePart(){
    Box(
        Modifier
            .width(1.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    )
}