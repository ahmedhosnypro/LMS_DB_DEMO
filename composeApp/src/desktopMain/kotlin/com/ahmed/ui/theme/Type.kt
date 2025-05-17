package com.ahmed.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import studentmanagement.composeapp.generated.resources.Res
import studentmanagement.composeapp.generated.resources.jet_brains_mono
import studentmanagement.composeapp.generated.resources.jet_brains_mono_italic
import studentmanagement.composeapp.generated.resources.roboto
import studentmanagement.composeapp.generated.resources.roboto_italic

@Composable
fun displayFontFamily() = FontFamily(
    Font(Res.font.roboto),
    Font(Res.font.roboto_italic),
)

@Composable
fun bodyFontFamily() = FontFamily(
    Font(Res.font.jet_brains_mono),
    Font(Res.font.jet_brains_mono_italic),
)


val baseline = Typography()

@Composable
fun AppTypography() = Typography().run {
    val displayFontFamily = displayFontFamily()
    val bodyFontFamily = bodyFontFamily()

    copy(
        displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily)
    )
}