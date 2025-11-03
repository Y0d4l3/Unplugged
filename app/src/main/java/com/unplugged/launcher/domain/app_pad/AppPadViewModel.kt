package com.unplugged.launcher.domain.app_pad

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import com.unplugged.launcher.util.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.data.repository.AppRepository
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.data.source.local.SettingsManager
import com.unplugged.launcher.domain.app_picker.AppPickerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppPadViewModel(app: Application) : AndroidViewModel(app) {

    private val appRepository: AppRepository by lazy { AppRepository(app) }
    private val settingsManager: SettingsManager by lazy { SettingsManager(app) }
    private val appPickerManager: AppPickerManager by lazy { AppPickerManager() }
    private val appPadManager: AppPadManager by lazy { AppPadManager(appRepository, settingsManager, viewModelScope) }
    private val vibratorManager: VibratorManager by lazy { VibratorManager(app) }

    private var selectedSlotIndex: Int? = null

    private val screenStateReceiver = ScreenStateReceiver(
        onScreenOn = {
            appPadManager.randomizeAppSlots()
        }
    )

    val uiState: StateFlow<AppPadUiState> = combine(
        appPadManager.appSlots,
        appPickerManager.pickerState,
    ) { appSlots, pickerState ->
        AppPadUiState(
            appSlots = appSlots,
            showAppPicker = pickerState.show,
            appPickerApps = pickerState.apps,
            appPickerSearchQuery = pickerState.searchQuery
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AppPadUiState()
    )

    init {
        loadInitialData()
        observeAndPersistFavoriteApps()

        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        app.registerReceiver(screenStateReceiver, intentFilter)
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(screenStateReceiver)
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val allApps = appRepository.getAllInstalledApps()
            appPickerManager.setAllApps(allApps)
        }
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

    fun onAddAppClicked(slotIndex: Int) {
        vibratorManager.vibrate()
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
        viewModelScope.launch {
            delay(500L)
            appRepository.launchApp(appToLaunch.componentName)
        }
    }

    fun onRemoveApp(slotIndex: Int) {
        appPadManager.removeAppFromSlot(slotIndex)
    }
}

data class AppPadUiState(
    val appSlots: List<LauncherApp?> = List(12) { null },
    val showAppPicker: Boolean = false,
    val appPickerApps: List<LauncherApp> = emptyList(),
    val appPickerSearchQuery: String = ""
)
