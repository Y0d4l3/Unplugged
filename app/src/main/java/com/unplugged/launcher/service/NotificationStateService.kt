package com.unplugged.launcher.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.unplugged.launcher.data.model.AppNotification
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.util.isMyAppDefaultLauncher


class NotificationStateService : NotificationListenerService() {

    fun refreshLastNotification() {
        val dismissedKeys = NotificationRepository.getDismissedNotificationKeys()
        val latestRelevantNotification = activeNotifications.lastOrNull {
            !dismissedKeys.contains(it.key) &&
                    NotificationRepository.whitelistedApps.value.contains(it.packageName) &&
                    it.isClearable
        }
        if (latestRelevantNotification == null) {
            NotificationRepository.clearDismissedKeys()
        }
        updateRepository(latestRelevantNotification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "REFRESH") {
            refreshLastNotification()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        refreshLastNotification()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        if (isMyAppDefaultLauncher(applicationContext)) {
            refreshLastNotification()

            val category = sbn.notification.category
            if (category != Notification.CATEGORY_CALL && category != Notification.CATEGORY_ALARM) {
                cancelNotification(sbn.key)
            }
        }
    }


    override fun onNotificationRemoved(sbn: StatusBarNotification?, rankingMap: RankingMap?, reason: Int) {
        super.onNotificationRemoved(sbn, rankingMap, reason)

        if (sbn == null) return

        if (reason == REASON_LISTENER_CANCEL) {
            return
        }

        refreshLastNotification()
    }

    private fun updateRepository(sbn: StatusBarNotification?) {if (sbn == null) {
        NotificationRepository.updateNotification(null)
        return
    }

        val isWhitelisted = NotificationRepository.whitelistedApps.value.contains(sbn.packageName)
        if (!isWhitelisted) {
            return
        }

        if (!sbn.isClearable) {
            if (activeNotifications.none { it.isClearable && NotificationRepository.whitelistedApps.value.contains(it.packageName) }) {
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
                key = sbn.key,
                appName = appName,
                appIcon = appIcon,
                title = title,
                text = text
            )

            NotificationRepository.updateNotification(appNotification)

        } catch (_: Exception) {
            NotificationRepository.updateNotification(null)
        }
    }

}
