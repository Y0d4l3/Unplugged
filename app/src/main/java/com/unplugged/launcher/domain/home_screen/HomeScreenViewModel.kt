package com.unplugged.launcher.domain.home_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.source.local.SettingsManager
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.data.repository.AppRepository
import com.unplugged.launcher.data.repository.DeviceStateRepository
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.domain.app_pad.AppPadManager
import com.unplugged.launcher.domain.app_picker.AppPickerManager
import com.unplugged.launcher.domain.dialer.DialerManager
import com.unplugged.launcher.domain.notifications.NotificationHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreenViewModel(app: Application) : AndroidViewModel(app) {

    private val appRepository = AppRepository(app)
    private val deviceStateRepository = DeviceStateRepository(app)
    private val notificationHandler = NotificationHandler(app)
    private val settingsManager = SettingsManager(app)
    private val dialerManager = DialerManager(app)
    private val appPickerManager = AppPickerManager()
    private val appPadManager = AppPadManager(appRepository, settingsManager, viewModelScope)

    private val getHomeScreenUiStateUseCase = GetHomeScreenUiStateUseCase(
        appPadManager, appPickerManager, dialerManager, deviceStateRepository
    )

    val uiState: StateFlow<HomeScreenUiState> = getHomeScreenUiStateUseCase(getApplication())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeScreenUiState()
        )

    private var selectedSlotIndex: Int? = null

    init {
        loadInitialData()
        observeAndPersistFavoriteApps()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val allApps = appRepository.getAllInstalledApps()
            appPickerManager.setAllApps(allApps)
        }
        notificationHandler.toggleNotificationService(enable = true)
    }

    private fun observeAndPersistFavoriteApps() {
        appPadManager.appSlots
            .onEach { appSlots ->
                val packageNames = appSlots.mapNotNull { it?.componentName?.packageName }.toSet()
                settingsManager.saveFavoriteApps(packageNames)
                NotificationRepository.setWhitelistedApps(packageNames)
            }
            .launchIn(viewModelScope)
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
        val currentKey = uiState.value.lastNotification?.key ?: return
        NotificationRepository.dismissNotification(currentKey)
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
        appPickerManager.openPicker()
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
