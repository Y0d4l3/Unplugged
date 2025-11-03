package com.unplugged.launcher.domain.home_screen

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.unplugged.launcher.data.repository.DeviceStateRepository
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.data.source.local.SettingsManager
import com.unplugged.launcher.domain.app_pad.AppPadManager
import com.unplugged.launcher.domain.app_pad.ScreenStateReceiver
import com.unplugged.launcher.domain.app_picker.AppPickerManager
import com.unplugged.launcher.util.currentDate
import com.unplugged.launcher.util.currentTime
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class GetHomeScreenUiStateUseCase(
    private val appPadManager: AppPadManager,
    private val appPickerManager: AppPickerManager,
    private val deviceStateRepository: DeviceStateRepository,
    private val settingsManager: SettingsManager
) {
    private val timeTickerFlow = flow {
        while (true) {
            emit(Unit)
            delay(1000L)
        }
    }

    operator fun invoke(context: Context): Flow<HomeScreenUiState> = callbackFlow {
        val screenStateReceiver = ScreenStateReceiver {
            appPadManager.randomizeAppSlots()
        }
        context.registerReceiver(
            screenStateReceiver,
            IntentFilter(Intent.ACTION_SCREEN_ON)
        )

        val flows = listOf(
            appPadManager.appSlots,
            appPickerManager.pickerState,
            deviceStateRepository.isBatterySaverOn,
            NotificationRepository.lastNotification,
            settingsManager.showPushNotificationsFlow,
            timeTickerFlow
        )

        val combinedFlow = combine(flows) { values ->
            val appSlots = values[0] as List<*>
            val pickerState = values[1] as AppPickerManager.AppPickerState
            val enteredNumber = values[2] as String
            val isBatterySaverOn = values[3] as Boolean
            val lastNotification = values[4] as? com.unplugged.launcher.data.model.AppNotification
            val areNotificationsEnabled = values[5] as Boolean

            HomeScreenUiState(
                appSlots = appSlots.map { it as? com.unplugged.launcher.data.model.LauncherApp },
                showAppPicker = pickerState.isVisible,
                appPickerSearchQuery = pickerState.searchQuery,
                filteredApps = pickerState.filteredApps,
                enteredNumber = enteredNumber,
                isBatterySaverOn = isBatterySaverOn,
                lastNotification = lastNotification,
                areNotificationsEnabled = areNotificationsEnabled,
                time = currentTime(),
                date = currentDate()
            )
        }

        val job = launch {
            combinedFlow.collect { uiState ->
                trySend(uiState)
            }
        }

        awaitClose {
            context.unregisterReceiver(screenStateReceiver)
            job.cancel()
        }
    }

}
