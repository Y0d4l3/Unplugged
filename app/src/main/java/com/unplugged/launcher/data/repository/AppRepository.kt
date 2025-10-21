package com.unplugged.launcher.data.repository

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.unplugged.launcher.data.model.LauncherApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {
    private val pm: PackageManager = context.packageManager

    suspend fun getAllInstalledApps(): List<LauncherApp> {
        return withContext(Dispatchers.IO) {
            val mainIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            pm.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL)
                .mapNotNull { resolveInfo ->
                    val activityInfo = resolveInfo.activityInfo
                    if (activityInfo.packageName.isNullOrBlank() || activityInfo.name.isNullOrBlank()) {
                        return@mapNotNull null
                    }
                    LauncherApp(
                        label = resolveInfo.loadLabel(pm).toString(),
                        componentName = ComponentName(activityInfo.packageName, activityInfo.name)
                    )
                }
                .sortedBy { it.label.lowercase() }
        }
    }

    suspend fun loadIconForApp(componentName: ComponentName): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                pm.getActivityIcon(componentName).toBitmap()
            } catch (_: Exception) {
                null
            }
        }
    }

    fun launchApp(componentName: ComponentName) {
        val intent = pm.getLaunchIntentForPackage(componentName.packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
}
