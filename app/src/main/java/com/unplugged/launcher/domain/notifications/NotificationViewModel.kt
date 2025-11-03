package com.unplugged.launcher.domain.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.model.AppNotification
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.util.TimeTicker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class GlobalUiState(
    val time: String = "",
    val date: String = "",
    val lastNotification: AppNotification? = null
)

class NotificationViewModel(app: Application) : AndroidViewModel(app) {

    private val notificationHandler: NotificationHandler by lazy { NotificationHandler(app) }

    val uiState: StateFlow<GlobalUiState> = combine(
        TimeTicker.time,
        TimeTicker.date,
        NotificationRepository.lastNotification
    ) { time, date, notification ->
        GlobalUiState(
            time = time,
            date = date,
            lastNotification = notification
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = GlobalUiState()
    )

    init {
        notificationHandler.toggleNotificationService(enable = true)
    }

    override fun onCleared() {
        super.onCleared()
        notificationHandler.toggleNotificationService(enable = false)
    }

    fun openNotificationAccessSettings() {
        notificationHandler.openNotificationAccessSettings()
    }

    fun onDismissNotification() {
        val notificationToDismiss = uiState.value.lastNotification
        if (notificationToDismiss != null) {
            NotificationRepository.dismissNotification(notificationToDismiss.key)
            NotificationRepository.updateNotification(null)
        }
    }
}
