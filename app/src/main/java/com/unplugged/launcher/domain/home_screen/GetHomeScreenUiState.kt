package com.unplugged.launcher.domain.home_screen

import android.content.Context
import com.unplugged.launcher.data.repository.DeviceStateRepository
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.data.source.local.SettingsManager
import com.unplugged.launcher.util.TimeTicker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class GetHomeScreenUiStateUseCase(
    private val deviceStateRepository: DeviceStateRepository,
    private val settingsManager: SettingsManager
) {
    operator fun invoke(context: Context): Flow<HomeScreenUiState> {
        return combine(
            TimeTicker.time,
            TimeTicker.date,
            NotificationRepository.lastNotification,
            deviceStateRepository.isBatterySaverOn,
            settingsManager.showPushNotificationsFlow.distinctUntilChanged()
        ) { time, date, lastNotification, isBatterySaverOn, areNotificationsEnabled ->
            HomeScreenUiState(
                time = time,
                date = date,
                lastNotification = lastNotification,
                isBatterySaverOn = isBatterySaverOn,
                areNotificationsEnabled = areNotificationsEnabled
            )
        }
    }
}
