package com.incanatoapps.retofut.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SoftUI_ColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = TextPrimary,
    primaryContainer = BrandGreenSoft,
    onPrimaryContainer = BrandGreenDeep,
    secondary = BrandGreen,
    onSecondary = TextPrimary,
    background = AppBackground,
    surface = CardWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderGray,
    surfaceVariant = AppBackground,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = CardWhite
)

@Composable
fun RetofutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Keeping the param but we focus on light for Soft UI
    content: @Composable () -> Unit
) {
    // We prioritize the Light Soft UI for this premium fitness-style look
    val colorScheme = SoftUI_ColorScheme 

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
