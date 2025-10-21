package com.unplugged.launcher.domain.home_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.SettingsManager
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.data.repository.AppRepository
import com.unplugged.launcher.data.repository.DeviceStateRepository
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.domain.app_pad.AppPadManager
import com.unplugged.launcher.domain.app_picker.AppPickerManager
import com.unplugged.launcher.domain.dialer.DialerManager
import com.unplugged.launcher.domain.notifications.NotificationHandler
import com.unplugged.launcher.util.currentDate
import com.unplugged.launcher.util.currentTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    private var selectedSlotIndex: Int? = null

    private val appRepository = AppRepository(app)
    private val deviceStateRepository = DeviceStateRepository(app)
    private val notificationHandler = NotificationHandler(app)
    private val settingsManager = SettingsManager(app)
    private val dialerManager = DialerManager(app)
    private val appPickerManager = AppPickerManager()
    private val appPadManager = AppPadManager(appRepository, settingsManager)

    init {
        observeDeviceState()
        notificationHandler.toggleNotificationService(enable = true)

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

        dialerManager.enteredNumber
            .onEach { number ->
                _uiState.update { it.copy(enteredNumber = number) }
            }
            .launchIn(viewModelScope)

        appPickerManager.pickerState
            .onEach { pickerState ->
                _uiState.update {
                    it.copy(
                        showAppPicker = pickerState.isVisible,
                        appPickerSearchQuery = pickerState.searchQuery,
                        filteredApps = pickerState.filteredApps
                    )
                }
            }
            .launchIn(viewModelScope)

        appPadManager.appSlots
            .onEach { slots ->
                _uiState.update { it.copy(appSlots = slots) }
            }
            .launchIn(viewModelScope)

        NotificationRepository.lastNotification
            .onEach { notification -> _uiState.update { it.copy(lastNotification = notification) } }
            .launchIn(viewModelScope)

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

        viewModelScope.launch {
            appPadManager.loadInitialSlots()

            _uiState.update { it.copy(installedApps = appRepository.getAllInstalledApps()) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        deviceStateRepository.cleanUp()
        notificationHandler.toggleNotificationService(enable = false)
    }

    private fun observeDeviceState() {
        deviceStateRepository.isBatterySaverOn
            .onEach { isSaverOn ->
                _uiState.update { it.copy(isBatterySaverOn = isSaverOn) }
            }
            .launchIn(viewModelScope)
    }

    fun openBatterySettings() {
        deviceStateRepository.openBatterySettings()
    }

    fun openNotificationAccessSettings() {
        notificationHandler.openNotificationAccessSettings()
    }

    fun onDismissNotification() {
        val currentKey = _uiState.value.lastNotification?.key ?: return
        NotificationRepository.dismissNotification(currentKey)
        notificationHandler.refreshNotifications()
    }

    fun onNumberClicked(digit: String) {
        dialerManager.addDigit(digit)
    }

    fun onDeleteClicked() {
        dialerManager.deleteLastDigit()
    }

    fun onCallClicked() {
        dialerManager.dialCurrentNumber()
    }

    fun onAddAppClicked(slotIndex: Int) {
        selectedSlotIndex = slotIndex
        appPickerManager.openPicker(allApps = _uiState.value.installedApps)
    }

    fun onAppPickerSearchQueryChanged(query: String) {
        appPickerManager.onSearchQueryChanged(query)
    }

    fun onDismissAppPicker() {
        appPickerManager.closePicker()
        selectedSlotIndex = null
    }

    fun onAppSelected(chosenApp: LauncherApp) {
        viewModelScope.launch {
            selectedSlotIndex?.let { index ->
                appPadManager.addAppToSlot(chosenApp, index)
            }

            appPickerManager.closePicker()
            selectedSlotIndex = null
        }
    }

    fun onLaunchApp(appToLaunch: LauncherApp) {
        appRepository.launchApp(appToLaunch.componentName)
    }


    fun onRemoveApp(slotIndex: Int) {
        appPadManager.removeAppFromSlot(slotIndex)
    }
}
