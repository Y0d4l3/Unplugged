package com.unplugged.launcher.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings

fun isMyAppDefaultLauncher(context: Context): Boolean {
    val localPackageManager = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    val resolveInfo = localPackageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

    return resolveInfo?.activityInfo?.packageName == context.packageName
}

fun hasNotificationListenerPermission(context: Context): Boolean {
    val enabledListeners =
        Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")

    val componentName = "com.unplugged.launcher.service.NotificationStateService"

    return enabledListeners?.contains(context.packageName) == true && enabledListeners.contains(
        componentName
    )
}