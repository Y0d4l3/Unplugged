package com.unplugged.launcher.data.model

import android.graphics.drawable.Drawable

data class AppNotification(
    val appName: String,
    val appIcon: Drawable?,
    val title: String,
    val text: String
)