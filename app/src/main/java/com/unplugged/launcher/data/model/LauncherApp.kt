package com.unplugged.launcher.data.model

import android.content.ComponentName
import android.graphics.Bitmap

data class LauncherApp(
    val label: String,
    val componentName: ComponentName,
    val icon: Bitmap? = null
)
