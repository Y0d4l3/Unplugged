package com.unplugged.launcher.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationRepository {
    private val _hasNotifications = MutableStateFlow(false)
    val hasNotifications = _hasNotifications.asStateFlow()

    fun updateStatus(hasActiveNotifications: Boolean) {
        _hasNotifications.value = hasActiveNotifications
    }
}

class NotificationStateService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        val hasActiveNotifications = activeNotifications.isNotEmpty()
        NotificationRepository.updateStatus(hasActiveNotifications)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        NotificationRepository.updateStatus(true)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        val hasActiveNotifications = activeNotifications.isNotEmpty()
        NotificationRepository.updateStatus(hasActiveNotifications)
    }
}
