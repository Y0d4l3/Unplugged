package com.unplugged.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun UnpluggedTheme(content: @Composable () -> Unit) {
    val scheme = darkColorScheme().copy(
        background = Color.Black,
        surface = Color.Black,
        primary = Color.White.copy(alpha = 0.7f),
        onPrimary = Color.Black,
        onBackground = Color.White.copy(alpha = 0.7f),
        onSurface = Color.White.copy(alpha = 0.7f)
    )

    MaterialTheme(
        colorScheme = scheme, content = content
    )
}
