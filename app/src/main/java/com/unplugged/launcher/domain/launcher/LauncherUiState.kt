package com.unplugged.launcher.domain.launcher

import com.unplugged.launcher.data.model.AppNotification
import com.unplugged.launcher.data.model.LauncherApp

data class LauncherUiState(
    val time: String = "",
    val date: String = "",
    val isBatterySaverOn: Boolean = false,
    val enteredNumber: String = "",
    val appSlots: List<LauncherApp?> = List(12) { null },
    val showAppPicker: Boolean = false,
    val installedApps: List<LauncherApp> = emptyList(),
    val hasNotifications: Boolean = false,
    val lastNotification: AppNotification? = null,
    val appPickerSearchQuery: String = "",
    val filteredApps: List<LauncherApp> = emptyList()
)
