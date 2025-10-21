package com.unplugged.launcher.ui.feature.launcher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.SettingsManager
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.data.repository.AppRepository
import com.unplugged.launcher.data.repository.DeviceStateRepository
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.domain.usecase.apps.GetAppSlots
import com.unplugged.launcher.domain.usecase.dialer.DialerManager
import com.unplugged.launcher.domain.usecase.notifications.NotificationHandler
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

class LauncherViewModel(app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState = _uiState.asStateFlow()

    private var selectedSlotIndex: Int? = null

    private val appRepository = AppRepository(app)
    private val deviceStateRepository = DeviceStateRepository(app)
    private val notificationHandler = NotificationHandler(app)
    private val settingsManager = SettingsManager(app)
    private val getAppSlotsUseCase = GetAppSlots(AppRepository(app), SettingsManager(app))
    private val dialerManager = DialerManager(app)

    init {
        loadInitialState()
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
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            val appSlots = getAppSlotsUseCase()

            _uiState.update { it.copy(
                installedApps = appRepository.getAllInstalledApps(),
                appSlots = appSlots
            ) }
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
        _uiState.update {
            it.copy(
                showAppPicker = true,
                filteredApps = it.installedApps
            )
        }
    }

    fun onAppPickerSearchQueryChanged(query: String) {
        _uiState.update { it.copy(appPickerSearchQuery = query) }

        val filtered = if (query.isBlank()) {
            _uiState.value.installedApps
        } else {
            _uiState.value.installedApps.filter { app ->
                app.label.contains(query, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(filteredApps = filtered) }
    }

    fun onAppSelected(chosenApp: LauncherApp) {
        viewModelScope.launch {
            val appWithIcon = chosenApp.copy(
                icon = appRepository.loadIconForApp(chosenApp.componentName)
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
        appRepository.launchApp(appToLaunch.componentName)
    }

    fun onDismissAppPicker() {
        _uiState.update {
            it.copy(
                showAppPicker = false,
                appPickerSearchQuery = "" // <-- NEU
            )
        }
        selectedSlotIndex = null
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
