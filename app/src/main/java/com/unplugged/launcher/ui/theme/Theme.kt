package com.unplugged.launcher.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import kotlin.random.Random


@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    val palette = listOf(
        Color(0xFFff9a9e),
        Color(0xFFfad0c4),
        Color(0xFFa1c4fd),
        Color(0xFFc2e9fb),
        Color(0xFFfbc2eb),
        Color(0xFFa6c0fe),
        Color(0xFFfddb92),
        Color(0xFFd1fdff)
    )

    val color1 = remember { palette.random(Random(System.currentTimeMillis())) }
    val color2 = remember { palette.random(Random(System.currentTimeMillis() + 1337)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(color1, color2)
                )
            )
    ) {
        content()
    }
}


@Composable
fun UnpluggedTheme(content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val scheme =
        dynamicLightColorScheme(ctx).copy(
            background = Color.Transparent,
            surface = Color.Transparent
        )

    MaterialTheme(
        colorScheme = scheme,
        //typography = Typography,
        //shapes = Shapes,
        content = content
    )
}
