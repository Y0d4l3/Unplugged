package com.unplugged.launcher.domain.home_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.repository.DeviceStateRepository
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.data.source.local.SettingsManager
import com.unplugged.launcher.domain.notifications.NotificationHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreenViewModel(app: Application) : AndroidViewModel(app) {

    private val deviceStateRepository: DeviceStateRepository by lazy { DeviceStateRepository(app) }
    private val settingsManager: SettingsManager by lazy { SettingsManager(app) }
    private val notificationHandler: NotificationHandler by lazy { NotificationHandler(app) }

    private val getHomeScreenUiStateUseCase: GetHomeScreenUiStateUseCase by lazy {
        GetHomeScreenUiStateUseCase(deviceStateRepository, settingsManager)
    }

    val uiState: StateFlow<HomeScreenUiState> = getHomeScreenUiStateUseCase(getApplication())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeScreenUiState()
        )

    init {
        notificationHandler.toggleNotificationService(enable = true)
    }

    override fun onCleared() {
        super.onCleared()
        deviceStateRepository.cleanUp()
        notificationHandler.toggleNotificationService(enable = false)
    }

    fun openBatterySettings() {
        deviceStateRepository.openBatterySettings()
    }

    fun openNotificationAccessSettings() {
        notificationHandler.openNotificationAccessSettings()
    }

    fun onDismissNotification() {
        val notificationToDismiss = uiState.value.lastNotification
        if (notificationToDismiss != null) {
            NotificationRepository.dismissNotification(notificationToDismiss.key)
            NotificationRepository.updateNotification(null)
        }
    }

    fun onToggleNotifications(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setShowPushNotifications(isEnabled)
        }
    }
}
