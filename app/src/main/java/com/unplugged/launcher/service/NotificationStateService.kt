package com.unplugged.launcher.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log // Log-Import hinzugefügt für besseres Debugging
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
        Log.d("NOTIF_DEBUG", "Whitelist aktualisiert: $packageNames")
        _whitelistedApps.value = packageNames
    }
}

class NotificationStateService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        // Beim Start die neueste, für uns relevante Benachrichtigung verarbeiten.
        val latestRelevantNotification = activeNotifications
            .filter { NotificationRepository.whitelistedApps.value.contains(it.packageName) && it.isClearable }
            .lastOrNull()
        updateRepository(latestRelevantNotification)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val isWhitelisted = NotificationRepository.whitelistedApps.value.contains(sbn.packageName)

        // Verarbeite die Benachrichtigung für die UI, WENN sie auf der Whitelist steht.
        if (isWhitelisted) {
            Log.d("NOTIF_DEBUG", "Benachrichtigung von ${sbn.packageName} wird verarbeitet.")
            updateRepository(sbn)
        }

        // Unterdrücke die System-UI für alles außer Anrufen und Alarmen.
        val category = sbn.notification.category
        if (category != Notification.CATEGORY_CALL && category != Notification.CATEGORY_ALARM) {
            // Dieser Aufruf löst onNotificationRemoved aus!
            cancelNotification(sbn.key)
            Log.d("NOTIF_DEBUG", "System-Benachrichtigung für ${sbn.packageName} unterdrückt.")
        }
    }

    // --- ANFANG DER KORREKTUR ---
    // Wir fügen den `reason`-Parameter hinzu, um den Grund für die Entfernung zu prüfen.
    override fun onNotificationRemoved(sbn: StatusBarNotification?, rankingMap: RankingMap?, reason: Int) {
        super.onNotificationRemoved(sbn, rankingMap, reason)
        if (sbn == null) return

        // GRUND DER KORREKTUR:
        // Ignoriere die "Entfernung", wenn wir sie gerade selbst in onNotificationPosted ausgelöst haben.
        // REASON_LISTENER_CANCEL bedeutet, dass cancelNotification() von unserem Service aufgerufen wurde.
        if (reason == REASON_LISTENER_CANCEL) {
            Log.d("NOTIF_DEBUG", "Ignoriere onNotificationRemoved, da wir es selbst gecancelt haben.")
            return
        }
        // --- ENDE DER KORREKTUR ---

        // Diese Logik wird jetzt nur noch ausgeführt, wenn der Benutzer die Benachrichtigung
        // selbst wegwischt oder in der App liest.
        Log.d("NOTIF_DEBUG", "Benachrichtigung wurde extern entfernt, aktualisiere UI.")
        val latestRelevantNotification = activeNotifications
            .filter { it.isClearable && NotificationRepository.whitelistedApps.value.contains(it.packageName) }
            .lastOrNull()
        updateRepository(latestRelevantNotification)
    }

    private fun updateRepository(sbn: StatusBarNotification?) {
        // ... (Der Rest dieser Funktion ist korrekt und braucht keine Änderung)
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
