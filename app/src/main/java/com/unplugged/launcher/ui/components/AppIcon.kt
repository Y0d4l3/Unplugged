package com.unplugged.launcher.ui.components

import android.annotation.SuppressLint
import android.content.ComponentName
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.unplugged.launcher.R
import com.unplugged.launcher.data.repository.AppRepository

private val grayscaleColorFilter =
    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

@Composable
fun AppIcon(
    componentName: ComponentName,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    applyGrayscale: Boolean = false
) {
    val appRepository = AppRepository(LocalContext.current)

    val iconBitmap by produceState<Bitmap?>(initialValue = null, key1 = componentName) {
        value = appRepository.loadIconForApp(componentName)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (iconBitmap != null) {
            Image(
                bitmap = iconBitmap!!.asImageBitmap(),
                contentDescription = contentDescription,
                colorFilter = if (applyGrayscale) grayscaleColorFilter else null
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
internal fun AppIcon(
    iconBitmap: Bitmap?, contentDescription: String?, modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (iconBitmap != null) {
            Image(
                bitmap = iconBitmap.asImageBitmap(), contentDescription = contentDescription
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}

@SuppressLint("LocalContextResourcesRead")
@Preview(name = "App Icon - Loaded", showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
private fun AppIconPreview_Loaded() {
    val context = LocalContext.current
    val drawable =
        ResourcesCompat.getDrawable(context.resources, R.drawable.ic_launcher_foreground, null)!!

    AppIcon(
        iconBitmap = drawable.toBitmap(),
        contentDescription = "Sample App",
        modifier = Modifier.size(64.dp)
    )
}

@Preview(name = "App Icon - Loading", showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
private fun AppIconPreview_Loading() {
    AppIcon(
        iconBitmap = null, contentDescription = "Loading...", modifier = Modifier.size(64.dp)
    )
}
