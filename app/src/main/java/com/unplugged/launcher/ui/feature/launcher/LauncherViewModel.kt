package com.unplugged.launcher.ui.feature.launcher

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap

import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.util.currentDate
import com.unplugged.launcher.util.currentTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState = _uiState.asStateFlow()

    private var selectedSlotIndex: Int? = null

    init {
        viewModelScope.launch {
            while (true) {
                _uiState.update { currentState ->
                    currentState.copy(
                        time = currentTime(),
                        date = currentDate()
                    )
                }
                delay(1000L)
            }
        }

        loadAllInstalledApps()
    }

    private fun loadAllInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(installedApps = emptyList()) }

            val apps = withContext(Dispatchers.IO) {
                val pm = app.packageManager
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
            _uiState.update { it.copy(installedApps = apps) }
        }
    }

    fun onNumberClicked(digit: String) {
        _uiState.update { it.copy(enteredNumber = it.enteredNumber + digit) }
    }

    fun onDeleteClicked() {
        _uiState.update { it.copy(enteredNumber = it.enteredNumber.dropLast(1)) }
    }

    fun onCallClicked() {
        val number = _uiState.value.enteredNumber
        if (number.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$number".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            app.startActivity(intent)
        }
    }

    fun onAddAppClicked(slotIndex: Int) {
        selectedSlotIndex = slotIndex
        _uiState.update { it.copy(showAppPicker = true) }
    }

    fun onAppSelected(chosenApp: LauncherApp) {
        viewModelScope.launch {
            val appWithIcon = chosenApp.copy(
                icon = loadIconForApp(chosenApp.componentName)
            )

            selectedSlotIndex?.let { index ->
                val newAppSlots = _uiState.value.appSlots.toMutableList().also {
                    it[index] = appWithIcon
                }
                _uiState.update {
                    it.copy(
                        appSlots = newAppSlots,
                        showAppPicker = false
                    )
                }
            }
            selectedSlotIndex = null
        }
    }

    fun onLaunchApp(appToLaunch: LauncherApp) {
        val intent = app.packageManager.getLaunchIntentForPackage(appToLaunch.componentName.packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            app.startActivity(it)
        }
    }

    fun onDismissAppPicker() {
        _uiState.update { it.copy(showAppPicker = false) }
        selectedSlotIndex = null
    }

    private suspend fun loadIconForApp(appComponent: ComponentName): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val pm = app.packageManager
                pm.getActivityIcon(appComponent).toBitmap()
            } catch (_: Exception) {
                null
            }
        }
    }
}
