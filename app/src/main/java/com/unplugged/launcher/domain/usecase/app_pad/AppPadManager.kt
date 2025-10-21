package com.unplugged.launcher.domain.usecase.app_pad

import com.unplugged.launcher.data.SettingsManager
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class AppPadManager(
    private val appRepository: AppRepository,
    private val settingsManager: SettingsManager
) {

    private val _appSlots = MutableStateFlow<List<LauncherApp?>>(List(12) { null })
    val appSlots = _appSlots.asStateFlow()

    suspend fun loadInitialSlots() {
        val allApps = appRepository.getAllInstalledApps()
        val savedPackageNames = settingsManager.favoriteAppsFlow.first()
        val savedApps = savedPackageNames.mapNotNull { packageName ->
            allApps.find { it.componentName.packageName == packageName }
        }
        val savedAppsWithIcons = savedApps.map { app ->
            app.copy(icon = appRepository.loadIconForApp(app.componentName))
        }
        val newSlots = MutableList<LauncherApp?>(12) { null }
        savedAppsWithIcons.forEachIndexed { index, appInfo ->
            if (index < newSlots.size) {
                newSlots[index] = appInfo
            }
        }
        _appSlots.value = newSlots
    }

    suspend fun addAppToSlot(app: LauncherApp, slotIndex: Int) {
        if (slotIndex !in 0..11) return

        val appWithIcon = app.copy(icon = appRepository.loadIconForApp(app.componentName))

        _appSlots.update { currentSlots ->
            currentSlots.toMutableList().also {
                it[slotIndex] = appWithIcon
            }
        }
    }

    fun removeAppFromSlot(slotIndex: Int) {
        if (slotIndex !in 0..11) return

        _appSlots.update { currentSlots ->
            currentSlots.toMutableList().also {
                it[slotIndex] = null
            }
        }
    }
}
