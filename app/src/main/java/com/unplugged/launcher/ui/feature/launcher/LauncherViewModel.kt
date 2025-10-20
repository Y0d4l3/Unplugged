package com.unplugged.launcher.ui.feature.launcher

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.PowerManager
import android.provider.Settings
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.SettingsManager
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.service.NotificationRepository
import com.unplugged.launcher.service.NotificationStateService
import com.unplugged.launcher.util.currentDate
import com.unplugged.launcher.util.currentTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LauncherViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState = _uiState.asStateFlow()

    private var selectedSlotIndex: Int? = null

    private val powerManager = app.getSystemService(Context.POWER_SERVICE) as PowerManager

    private val batterySaverReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
                _uiState.update { it.copy(isBatterySaverOn = isBatterySaverCurrentlyOn()) }
            }
        }
    }

    private val settingsManager = SettingsManager(app)

    init {
        loadInitialState()

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

        viewModelScope.launch {
            NotificationRepository.lastNotification.collect { notification ->
                _uiState.update { it.copy(lastNotification = notification) }
            }
        }

        _uiState.update { it.copy(isBatterySaverOn = isBatterySaverCurrentlyOn()) }
        val filter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        app.registerReceiver(batterySaverReceiver, filter)

        viewModelScope.launch {
            uiState
                .map { it.appSlots }
                .distinctUntilChanged()
                .onEach { appSlots ->
                    val packageNames =
                        appSlots.mapNotNull { it?.componentName?.packageName }.toSet()
                    settingsManager.saveFavoriteApps(packageNames)
                    NotificationRepository.setWhitelistedApps(packageNames)
                }
                .launchIn(viewModelScope)
        }
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            val allApps = getAllInstalledApps()
            _uiState.update { it.copy(installedApps = allApps) }

            val savedPackageNames = settingsManager.favoriteAppsFlow.first()

            val savedApps = savedPackageNames.mapNotNull { packageName ->
                allApps.find { it.componentName.packageName == packageName }
            }

            val savedAppsWithIcons = savedApps.map { app ->
                app.copy(icon = loadIconForApp(app.componentName))
            }

            val newSlots = MutableList<LauncherApp?>(12) { null }
            savedAppsWithIcons.forEachIndexed { index, appInfo ->
                if (index < newSlots.size) {
                    newSlots[index] = appInfo
                }
            }

            _uiState.update { it.copy(appSlots = newSlots) }
        }
    }

    private suspend fun getAllInstalledApps(): List<LauncherApp> {
        return withContext(Dispatchers.IO) {
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
    }


    private fun isBatterySaverCurrentlyOn(): Boolean {
        return powerManager.isPowerSaveMode
    }

    fun openBatterySettings() {
        val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        app.startActivity(intent)
    }

    fun openNotificationAccessSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        app.startActivity(intent)
    }

    fun onDismissNotification() {
        val currentNotificationKey = _uiState.value.lastNotification?.key ?: return

        NotificationRepository.dismissNotification(currentNotificationKey)

        val serviceIntent = Intent(app, NotificationStateService::class.java).apply {
            action = "REFRESH"
        }
        app.startService(serviceIntent)
    }

    override fun onCleared() {
        super.onCleared()
        app.unregisterReceiver(batterySaverReceiver)
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
        val intent =
            app.packageManager.getLaunchIntentForPackage(appToLaunch.componentName.packageName)
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


    fun onRemoveApp(slotIndex: Int) {
        val newAppSlots = _uiState.value.appSlots.toMutableList()

        if (slotIndex >= 0 && slotIndex < newAppSlots.size) {
            newAppSlots[slotIndex] = null

            _uiState.update {
                it.copy(appSlots = newAppSlots)
            }
        }
    }
}
