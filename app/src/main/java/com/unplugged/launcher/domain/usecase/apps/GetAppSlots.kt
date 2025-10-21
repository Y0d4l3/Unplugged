package com.unplugged.launcher.domain.usecase.apps

import com.unplugged.launcher.data.SettingsManager
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.data.repository.AppRepository
import kotlinx.coroutines.flow.first

class GetAppSlots(
    private val appRepository: AppRepository,
    private val settingsManager: SettingsManager
) {
    suspend operator fun invoke(): List<LauncherApp?> {
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

        return newSlots
    }
}
