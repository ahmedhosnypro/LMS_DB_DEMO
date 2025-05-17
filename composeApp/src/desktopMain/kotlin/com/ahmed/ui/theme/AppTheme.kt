package com.ahmed.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

object AppTheme {
    private val DarkColors = AppColors(
        primary = Color(0xffff8400),
        // main header colors
        mainHeaderBackgroundStart = Color(0xff161515),
        mainHeaderBackgroundEnd = Color(0xFF0D0D0D),
        mainHeaderBackgroundImageTint = Color(0xFF575656),
        onMainHeader = Color.White,
        // secondary header colors
        secondaryHeaderBackground = Color(0xff161515),
        secondaryHeaderBackgroundReverse = Color(0xffffffff),
        onSecondaryHeader = Color.White,
        onSecondaryHeaderDisabled = Color(0xff143c51).copy(alpha = 0.5f),
        secondaryHeaderBorder = Color(0xff9d590e),

        fadlText = Color(0xFFB35C00),

        listDivider = Color(0xFF333232),
        cardIconTint = Color(0xFFFFF5EA),

        // count bottom sheet
        sheetDragHandle = Color(0xff90ff8400),
        sheetDragHandleBackground = Color(0xFF141414),
        sheetBackgroundColor = Color(0xFF171717),
        sheetScrimColor = Color(0x178F8F8F),
        targetText = Color(0xFFB35C00),
        prevNextIndicator = Color(0xFFB35C00),
        disabledPrevNextIndicator = Color(0xFF333232),


        successColor = Color(0xff307b2a),
        progressColor = Color(0xffeda215),
        progressTrackColor = Color(0xFF999999),
        progressBackgroundColor = Color.Black
    )

}


/**
 * default values are for light theme
 */
@Stable
@Immutable
data class AppColors(
    val primary: Color = Color(0xff4aa643),
    // main header colors
    val mainHeaderBackgroundStart: Color = Color(0xFF398034),
    val mainHeaderBackgroundEnd: Color = primary,
    val mainHeaderBackgroundImageTint: Color = Color(0x9E143C51),
    val onMainHeader: Color = Color.White,
    val onMainHeaderDisabled: Color = onMainHeader.copy(alpha = 0.5f),
    // secondary header colors
    val secondaryHeaderBackground: Color = Color.White,
    val secondaryHeaderBackgroundReverse: Color = Color(0xfff5f5f5),
    val onSecondaryHeader: Color = Color(0xff143c51),
    val onSecondaryHeaderDisabled: Color = onSecondaryHeader.copy(alpha = 0.5f),
    val secondaryHeaderBorder: Color = Color(0xFF386A34),

    val fadlText: Color = primary,

    val listDivider: Color = Color(0xffe2e2e2),

    val cardCallToActionIcon: Color = Color(0xFF808A97),
    val cardIconTint: Color = Color.White,

    // count bottom sheet
    val sheetDragHandle: Color = onSecondaryHeader,
    val sheetDragHandleBackground: Color = Color(0xFFEEEEEE),
    val sheetBackgroundColor: Color = Color(0xFFEBEBEB),
    val sheetScrimColor: Color = Color.Black.copy(alpha = 0.2f),
    val targetText: Color = primary,
    val prevNextIndicator: Color = primary,  // arrow color, next/prev indicator
    val disabledPrevNextIndicator: Color = Color(0xFFADADAD),

    val successColor: Color = mainHeaderBackgroundEnd,
    val progressColor: Color = Color(0xffeea966),
    val progressTrackColor: Color = Color(0xFF999999),
    val progressBackgroundColor: Color = Color.Transparent
)
