package com.unplugged.launcher.domain.home_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.data.repository.NotificationRepository
import com.unplugged.launcher.domain.notifications.NotificationHandler
import com.unplugged.launcher.util.TimeTicker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeScreenViewModel(app: Application) : AndroidViewModel(app) {

    private val notificationHandler: NotificationHandler by lazy { NotificationHandler(app) }

    val uiState: StateFlow<HomeScreenUiState> = combine(
        TimeTicker.time,
        TimeTicker.date,
        NotificationRepository.lastNotification
    ) { time, date, notification ->
        HomeScreenUiState(
            time = time,
            date = date,
            lastNotification = notification
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeScreenUiState()
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
