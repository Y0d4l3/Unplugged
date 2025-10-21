package com.unplugged.launcher.data.repository
import com.unplugged.launcher.data.model.AppNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationRepository {
    private val _lastNotification = MutableStateFlow<AppNotification?>(null)
    val lastNotification = _lastNotification.asStateFlow()

    private val _whitelistedApps = MutableStateFlow<Set<String>>(emptySet())
    val whitelistedApps = _whitelistedApps.asStateFlow()

    private val _dismissedNotificationKeys = MutableStateFlow<Set<String>>(emptySet())

    fun updateNotification(notification: AppNotification?) {
        _lastNotification.value = notification
    }

    fun setWhitelistedApps(packageNames: Set<String>) {
        _whitelistedApps.value = packageNames
    }

    fun dismissNotification(key: String?) {
        if (key == null) return
        _dismissedNotificationKeys.value += key
    }

    fun getDismissedNotificationKeys(): Set<String> {
        return _dismissedNotificationKeys.value
    }

    fun clearDismissedKeys() {
        _dismissedNotificationKeys.value = emptySet()
    }
}
