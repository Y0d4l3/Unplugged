package com.unplugged.launcher.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.unplugged.launcher.data.model.AppNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


object NotificationRepository {
    private val _lastNotification = MutableStateFlow<AppNotification?>(null)
    val lastNotification = _lastNotification.asStateFlow()

    private val _whitelistedApps = MutableStateFlow<Set<String>>(emptySet())
    val whitelistedApps = _whitelistedApps.asStateFlow()

    fun updateNotification(notification: AppNotification?) {
        _lastNotification.value = notification
    }

    fun setWhitelistedApps(packageNames: Set<String>) {
        _whitelistedApps.value = packageNames
    }
}

class NotificationStateService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        val latestRelevantNotification = activeNotifications.lastOrNull {
            NotificationRepository.whitelistedApps.value.contains(it.packageName) && it.isClearable
        }
        updateRepository(latestRelevantNotification)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val isWhitelisted = NotificationRepository.whitelistedApps.value.contains(sbn.packageName)

        if (isWhitelisted) {
            updateRepository(sbn)
        }

        val category = sbn.notification.category
        if (category != Notification.CATEGORY_CALL && category != Notification.CATEGORY_ALARM) {
            cancelNotification(sbn.key)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?, rankingMap: RankingMap?, reason: Int) {
        super.onNotificationRemoved(sbn, rankingMap, reason)
        if (sbn == null) return

        if (reason == REASON_LISTENER_CANCEL) {
            return
        }
        val latestRelevantNotification = activeNotifications.lastOrNull {
            it.isClearable && NotificationRepository.whitelistedApps.value.contains(it.packageName)
        }
        updateRepository(latestRelevantNotification)
    }

    private fun updateRepository(sbn: StatusBarNotification?) {
        if (sbn == null) {
            NotificationRepository.updateNotification(null)
            return
        }

        val isWhitelisted = NotificationRepository.whitelistedApps.value.contains(sbn.packageName)
        if (!isWhitelisted) {
            return
        }

        if (!sbn.isClearable) {
            if (activeNotifications.none { it.isClearable }) {
                NotificationRepository.updateNotification(null)
            }
            return
        }

        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        if (title.isBlank() && text.isBlank()) {
            return
        }

        val pm = applicationContext.packageManager
        try {
            val appInfo = pm.getApplicationInfo(sbn.packageName, 0)
            val appName = pm.getApplicationLabel(appInfo).toString()
            val appIcon = pm.getApplicationIcon(sbn.packageName)

            val appNotification = AppNotification(
                appName = appName, appIcon = appIcon, title = title, text = text
            )

            NotificationRepository.updateNotification(appNotification)
        } catch (_: Exception) {
        }
    }
}
