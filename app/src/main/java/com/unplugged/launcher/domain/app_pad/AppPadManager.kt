package com.unplugged.launcher.domain.app_pad

import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.data.repository.AppRepository
import com.unplugged.launcher.data.source.local.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppPadManager(
    private val appRepository: AppRepository,
    private val settingsManager: SettingsManager,
    scope: CoroutineScope
) {

    private val _appSlots = MutableStateFlow<List<LauncherApp?>>(List(12) { null })
    val appSlots = _appSlots.asStateFlow()

    init {
        scope.launch {
            loadInitialSlots()
        }
    }

    private suspend fun loadInitialSlots() {
        val allApps = appRepository.getAllInstalledApps()
        val savedPackageNames = settingsManager.favoriteAppsFlow.first()
        val savedApps = savedPackageNames.mapNotNull { packageName ->
            allApps.find { it.componentName.packageName == packageName }
        }
        val newSlots = MutableList<LauncherApp?>(12) { null }
        savedApps.forEachIndexed { index, appInfo ->
            if (index < newSlots.size) {
                newSlots[index] = appInfo
            }
        }
        _appSlots.value = newSlots
    }

    fun addAppToSlot(app: LauncherApp, slotIndex: Int) {
        if (slotIndex !in 0..11) return

        _appSlots.update { currentSlots ->
            currentSlots.toMutableList().also {
                it[slotIndex] = app
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


    fun randomizeAppSlots() {
        _appSlots.update { currentSlots ->
            val filledSlots = currentSlots.filterNotNull()
            val newSlots = MutableList<LauncherApp?>(12) { null }
            val availableIndices = (0..11).toMutableList()
            availableIndices.shuffle()
            filledSlots.forEach { app ->
                if (availableIndices.isNotEmpty()) {
                    val randomIndex = availableIndices.removeAt(0)
                    newSlots[randomIndex] = app
                }
            }
            newSlots
        }
    }

}
