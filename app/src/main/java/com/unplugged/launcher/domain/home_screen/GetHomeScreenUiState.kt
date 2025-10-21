package com.unplugged.launcher.domain.home_screen

import com.unplugged.launcher.data.repository.DeviceStateRepository
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.domain.app_pad.AppPadManager
import com.unplugged.launcher.domain.app_picker.AppPickerManager
import com.unplugged.launcher.domain.dialer.DialerManager
import com.unplugged.launcher.util.currentDate
import com.unplugged.launcher.util.currentTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetHomeScreenUiStateUseCase(
    private val appPadManager: AppPadManager,
    private val appPickerManager: AppPickerManager,
    private val dialerManager: DialerManager,
    private val deviceStateRepository: DeviceStateRepository
) {
    private val timeTickerFlow = flow {
        while (true) {
            emit(Unit)
            delay(1000L)
        }
    }

    operator fun invoke(): Flow<HomeScreenUiState> {
        val flows = listOf(
            appPadManager.appSlots,
            appPickerManager.pickerState,
            dialerManager.enteredNumber,
            deviceStateRepository.isBatterySaverOn,
            NotificationRepository.lastNotification,
            timeTickerFlow
        )

        return combine(flows) { values ->
            val appSlots = values[0] as List<*>
            val pickerState = values[1] as AppPickerManager.AppPickerState
            val enteredNumber = values[2] as String
            val isBatterySaverOn = values[3] as Boolean
            val lastNotification = values[4] as? com.unplugged.launcher.data.model.AppNotification

            HomeScreenUiState(
                appSlots = appSlots.map { it as? com.unplugged.launcher.data.model.LauncherApp },
                showAppPicker = pickerState.isVisible,
                appPickerSearchQuery = pickerState.searchQuery,
                filteredApps = pickerState.filteredApps,
                enteredNumber = enteredNumber,
                isBatterySaverOn = isBatterySaverOn,
                lastNotification = lastNotification,
                time = currentTime(),
                date = currentDate()
            )
        }
    }

}
