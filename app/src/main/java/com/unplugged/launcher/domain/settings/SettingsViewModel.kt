package com.unplugged.launcher.domain.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.repository.DeviceStateRepository
import com.unplugged.launcher.data.source.local.SettingsManager
import com.unplugged.launcher.domain.notifications.NotificationHandler
import com.unplugged.launcher.util.hasNotificationListenerPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app),
    LifecycleEventObserver {

    private val deviceStateRepository: DeviceStateRepository by lazy { DeviceStateRepository(app) }
    private val settingsManager: SettingsManager by lazy { SettingsManager(app) }
    private val notificationHandler: NotificationHandler by lazy { NotificationHandler(app) }
    private val _hasNotificationPermission = MutableStateFlow(hasNotificationListenerPermission(app))

    val uiState: StateFlow<SettingsUiState> = combine(
        deviceStateRepository.isBatterySaverOn,
        settingsManager.showPushNotificationsFlow.distinctUntilChanged(),
        _hasNotificationPermission.asStateFlow()
    ) { isBatterySaverOn, areNotificationsEnabled, hasPermission ->
        SettingsUiState(
            isBatterySaverOn = isBatterySaverOn,
            areNotificationsEnabled = areNotificationsEnabled,
            hasNotificationPermission = hasPermission
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingsUiState()
    )

    init {
        notificationHandler.toggleNotificationService(enable = true)
    }

    override fun onCleared() {
        super.onCleared()
        notificationHandler.toggleNotificationService(enable = false)
    }

    fun onOpenBatterySettings() {
        deviceStateRepository.openBatterySettings()
    }

    fun onToggleNotifications(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setShowPushNotifications(isEnabled)
        }
    }

    fun openNotificationAccessSettings() {
        notificationHandler.openNotificationAccessSettings()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            _hasNotificationPermission.update {
                hasNotificationListenerPermission(getApplication())
            }
        }
    }
}

data class SettingsUiState(
    val isBatterySaverOn: Boolean = false,
    val areNotificationsEnabled: Boolean = false,
    val hasNotificationPermission: Boolean = false
)
