package com.unplugged.launcher.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.unplugged.launcher.data.model.AppNotification
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.data.source.local.SettingsManager
import com.unplugged.launcher.util.isMyAppDefaultLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationStateService : NotificationListenerService() {

    private lateinit var settingsManager: SettingsManager
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        if (!isMyAppDefaultLauncher(applicationContext)) return

        val excludedCategories = listOf(Notification.CATEGORY_CALL, Notification.CATEGORY_ALARM)
        if (sbn.notification.category in excludedCategories) {
            return
        }

        val isWhitelisted = NotificationRepository.whitelistedApps.value.contains(sbn.packageName)
        if (!isWhitelisted) {
            cancelNotification(sbn.key)
            return
        }

        processNotification(sbn)

        scope.launch {
            val arePushNotificationsEnabled = settingsManager.showPushNotificationsFlow.first()
            if (!arePushNotificationsEnabled) {
                cancelNotification(sbn.key)
            }
        }
    }

    private fun processNotification(sbn: StatusBarNotification) {
        if (!sbn.isClearable) {
            return
        }

        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        if (title.isBlank() && text.isBlank()) return

        val pm = applicationContext.packageManager
        try {
            val appInfo = pm.getApplicationInfo(sbn.packageName, 0)
            val appName = pm.getApplicationLabel(appInfo).toString()
            val appIcon = pm.getApplicationIcon(sbn.packageName)

            val appNotification = AppNotification(
                key = sbn.key, appName = appName, appIcon = appIcon, title = title, text = text
            )

            NotificationRepository.updateNotification(appNotification)

        } catch (_: Exception) {
            NotificationRepository.updateNotification(null)
        }
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?, rankingMap: RankingMap?, reason: Int
    ) {
        super.onNotificationRemoved(sbn, rankingMap, reason)
        sbn ?: return

        if (reason == REASON_LISTENER_CANCEL) {
            return
        }

        if (NotificationRepository.lastNotification.value?.key == sbn.key) {
            NotificationRepository.updateNotification(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
