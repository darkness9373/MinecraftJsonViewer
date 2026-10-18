package org.darkness.jsonuiview.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import org.darkness.jsonuiview.util.PreferencesManager

@Composable
fun Theme(
    themeMode: PreferencesManager.ThemeMode = PreferencesManager.ThemeMode.System,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        PreferencesManager.ThemeMode.Light -> false
        PreferencesManager.ThemeMode.Dark -> true
        PreferencesManager.ThemeMode.System -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ThemeSurface(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        content = content
    )
}

fun Activity.setupEdgeToEdge(themeMode: PreferencesManager.ThemeMode = PreferencesManager.ThemeMode.System) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = Color.Transparent.toArgb()
    window.navigationBarColor = Color.Transparent.toArgb()
    
    val isDark = when (themeMode) {
        PreferencesManager.ThemeMode.Light -> false
        PreferencesManager.ThemeMode.Dark -> true
        PreferencesManager.ThemeMode.System -> (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
    
    window.setStatusBarContrastEnforced(!isDark)
    window.setNavigationBarContrastEnforced(!isDark)
}