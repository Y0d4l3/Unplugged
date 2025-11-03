package com.unplugged.launcher.domain.app_picker

import com.unplugged.launcher.data.model.LauncherApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppPickerManager {

    data class AppPickerState(
        val show: Boolean = false,
        val searchQuery: String = "",
        val apps: List<LauncherApp> = emptyList()
    )

    private val _pickerState = MutableStateFlow(AppPickerState())
    val pickerState = _pickerState.asStateFlow()

    private var allInstalledApps: List<LauncherApp> = emptyList()

    fun setAllApps(apps: List<LauncherApp>) {
        this.allInstalledApps = apps
    }

    fun openPicker() {
        _pickerState.update {
            it.copy(
                show = true,
                apps = allInstalledApps
            )
        }
    }

    fun closePicker() {
        _pickerState.value = AppPickerState()
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = if (query.isBlank()) {
            allInstalledApps
        } else {
            allInstalledApps.filter { app ->
                app.label.contains(query, ignoreCase = true)
            }
        }
        _pickerState.update {
            it.copy(
                searchQuery = query,
                apps = filtered
            )
        }
    }
}
