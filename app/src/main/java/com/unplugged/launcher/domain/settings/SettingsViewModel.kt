package com.unplugged.launcher.domain.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.repository.DeviceStateRepository
import com.unplugged.launcher.data.source.local.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val deviceStateRepository: DeviceStateRepository by lazy { DeviceStateRepository(app) }
    private val settingsManager: SettingsManager by lazy { SettingsManager(app) }

    val uiState: StateFlow<SettingsUiState> = combine(
        deviceStateRepository.isBatterySaverOn,
        settingsManager.showPushNotificationsFlow.distinctUntilChanged()
    ) { isBatterySaverOn, areNotificationsEnabled ->
        SettingsUiState(
            isBatterySaverOn = isBatterySaverOn,
            areNotificationsEnabled = areNotificationsEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsUiState()
    )

    fun onOpenBatterySettings() {
        deviceStateRepository.openBatterySettings()
    }

    fun onToggleNotifications(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setShowPushNotifications(isEnabled)
        }
    }
}

data class SettingsUiState(
    val isBatterySaverOn: Boolean = false,
    val areNotificationsEnabled: Boolean = true
)
