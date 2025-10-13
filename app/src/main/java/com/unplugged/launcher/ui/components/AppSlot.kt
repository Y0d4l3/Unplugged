package com.unplugged.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
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

    Column(
        modifier = Modifier.size(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            appIconBitmap != null -> {
                Image(
                    painter = remember(appIconBitmap) { BitmapPainter(appIconBitmap!!) },
                    contentDescription = app?.label ?: "App Icon",
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = app?.label ?: "",
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            app != null -> {
                Text(
                    text = app.label.firstOrNull()?.toString()?.uppercase() ?: "?",
                    fontSize = 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add App",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
