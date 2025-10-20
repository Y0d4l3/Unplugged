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

    fun updateNotification(notification: AppNotification?) {
        _lastNotification.value = notification
    }
}

class NotificationStateService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        val latestNotification = activeNotifications.lastOrNull()
        updateRepository(latestNotification)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        updateRepository(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        val latestNotification = activeNotifications.lastOrNull()
        updateRepository(latestNotification)
    }

    private fun updateRepository(sbn: StatusBarNotification?) {
        if (sbn == null) {
            NotificationRepository.updateNotification(null)
            return
        }

        if (!sbn.isClearable) {
            if (activeNotifications.size <= 1) {
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
        val appInfo = pm.getApplicationInfo(sbn.packageName, 0)
        val appName = pm.getApplicationLabel(appInfo).toString()
        val appIcon = pm.getApplicationIcon(sbn.packageName)


        val appNotification = AppNotification(
            appName = appName,
            appIcon = appIcon,
            title = title,
            text = text
        )

        NotificationRepository.updateNotification(appNotification)
    }
}
