package com.galindo.rickandmorty.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF171212),
    surface = Color(0xFF382E2E),
    surfaceVariant = Color(0xFF382E2E),
    primary = Color(0xFF00E676),
    secondary = Color(0xFF5C4033),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFB0BEC5),
    error = Color(0xFFE57373)
)

private val LightColorScheme = lightColorScheme(
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFFFFFFF),
    primary = Color(0xFF00C853),
    secondary = Color(0xFF795548),
    onBackground = Color(0xFF1A1C1E),
    onSurface = Color(0xFF1A1C1E),
    onSurfaceVariant = Color(0xFF5F6368),
    error = Color(0xFFD32F2F)
)

@Composable
fun RickandmortyTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
