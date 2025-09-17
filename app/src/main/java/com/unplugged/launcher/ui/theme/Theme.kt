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
    val randomColors = remember {
        List(2) {
            Color.hsl(
                hue = Random.nextFloat() * 360f,
                saturation = 0.6f + Random.nextFloat() * 0.4f,
                lightness = 0.4f + Random.nextFloat() * 0.4f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(randomColors)
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
