package com.unplugged.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unplugged.launcher.data.repository.AppRepository
import android.content.ComponentName
import android.graphics.Bitmap

@Composable
fun AppIcon(
    componentName: ComponentName,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val appRepository = AppRepository(LocalContext.current)

    val iconBitmap by produceState<Bitmap?>(initialValue = null, key1 = componentName) {
        value = appRepository.loadIconForApp(componentName)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (iconBitmap != null) {
            Image(
                bitmap = iconBitmap!!.asImageBitmap(),
                contentDescription = contentDescription
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}
