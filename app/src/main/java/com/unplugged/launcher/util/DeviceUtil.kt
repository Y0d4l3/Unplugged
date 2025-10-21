package com.unplugged.launcher.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

fun isMyAppDefaultLauncher(context: Context): Boolean {
    val localPackageManager = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    val resolveInfo = localPackageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

    return resolveInfo?.activityInfo?.packageName == context.packageName
}
