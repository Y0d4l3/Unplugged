package com.unplugged.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unplugged.launcher.data.LauncherApp
import com.unplugged.launcher.ui.util.toImageBitmap

@Composable
fun AppSlot(app: LauncherApp?) {
    val context = LocalContext.current
    val appIconBitmap: ImageBitmap? by produceState(
        initialValue = null,
        key1 = app?.componentName
    ) {
        value = app?.loadIcon(context)?.toImageBitmap()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            appIconBitmap != null -> {
                Image(
                    painter = remember(appIconBitmap) { BitmapPainter(appIconBitmap!!) },
                    contentDescription = app?.label ?: "App Icon",
                    modifier = Modifier.size(50.dp),
                )
            }
            app != null -> {
                Text(
                    text = app.label.firstOrNull()?.toString()?.uppercase() ?: "?",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add App",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
