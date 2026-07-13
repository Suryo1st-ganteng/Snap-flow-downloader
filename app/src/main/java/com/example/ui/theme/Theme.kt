package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AmberYellow, // D0BCFF (Lavender)
    secondary = CoralRed, // 381E72 (Dark Purple)
    tertiary = DarkGrayText, // 938F99
    background = DarkBackground, // 1C1B1F
    surface = DarkSurface, // 2B2930
    onPrimary = CoralRed, // 381E72 (Dark Purple text on Lavender)
    onSecondary = LightGrayText, // E6E1E5
    onBackground = LightGrayText, // E6E1E5
    onSurface = LightGrayText // E6E1E5
)

private val LightColorScheme = lightColorScheme(
    primary = AmberYellow,
    secondary = CoralRed,
    tertiary = DarkGrayText,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = DarkBackground,
    onSecondary = LightBackground,
    onBackground = DarkBackground,
    onSurface = DarkBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) NavigationBarColor.toArgb() else colorScheme.background.toArgb()
            window.navigationBarColor = if (darkTheme) NavigationBarColor.toArgb() else colorScheme.background.toArgb()
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
