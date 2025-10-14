package com.unplugged.launcher.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.LauncherApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppPickerViewModel(application: Application) : AndroidViewModel(application) {

    private val _apps = MutableStateFlow<List<LauncherApp>>(emptyList())
    val apps: StateFlow<List<LauncherApp>> = _apps

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val installedApps = withContext(Dispatchers.IO) {
                val context = getApplication<Application>()
                val pm = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                pm.queryIntentActivities(intent, PackageManager.MATCH_ALL).mapNotNull { resolveInfo ->
                    val label = resolveInfo.loadLabel(pm).toString()
                    val packageName = resolveInfo.activityInfo.packageName
                    val activityName = resolveInfo.activityInfo.name

                    if (packageName.isNullOrBlank() || activityName.isNullOrBlank()) {
                        null
                    } else {
                        LauncherApp(label, ComponentName(packageName, activityName))
                    }
                }.sortedBy { it.label.lowercase() }
            }
            _apps.value = installedApps
        }
    }
}