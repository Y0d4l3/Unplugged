package com.unplugged.launcher.data.model

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable

data class LauncherApp(
    val label: String,
    val componentName: ComponentName,
) {
    fun loadIcon(context: Context): Drawable? {
        return try {
            val originalDrawable = context.packageManager.getActivityIcon(componentName)
            val mutableDrawable = originalDrawable.mutate()
            convertToGrayscale(mutableDrawable)
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }
}

private fun convertToGrayscale(drawable: Drawable): Drawable {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)
    val filter: ColorFilter = ColorMatrixColorFilter(matrix)
    drawable.colorFilter = filter
    return drawable
}