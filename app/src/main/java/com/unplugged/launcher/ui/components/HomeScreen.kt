package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unplugged.launcher.domain.app_pad.AppPadViewModel
import com.unplugged.launcher.domain.dialer.DialerViewModel
import com.unplugged.launcher.domain.notifications.NotificationViewModel
import com.unplugged.launcher.domain.settings.SettingsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    notificationViewModel: NotificationViewModel = viewModel(),
    dialerViewModel: DialerViewModel = viewModel(),
    appPadViewModel: AppPadViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val globalUiState by notificationViewModel.uiState.collectAsState()
    val dialerUiState by dialerViewModel.uiState.collectAsState()
    val appPadUiState by appPadViewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    val topPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    val bottomPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    Column {
        TopPager(
            modifier = Modifier
                .weight(1f)
                .padding(top = 10.dp),
            topPagerState = topPagerState,
            time = globalUiState.time,
            date = globalUiState.date,
            lastNotification = globalUiState.lastNotification,
            onDismissNotification = notificationViewModel::onDismissNotification,
        )

        BottomPager(
            modifier = Modifier.weight(2f),
            bottomPagerState = bottomPagerState,
            appPadUiState = appPadUiState,
            onAddAppClicked = appPadViewModel::onAddAppClicked,
            onLaunchApp = appPadViewModel::onLaunchApp,
            onRemoveApp = appPadViewModel::onRemoveApp,

            dialerUiState = dialerUiState,
            onNumberClicked = dialerViewModel::onNumberClicked,
            onDeleteClicked = dialerViewModel::onDeleteClicked,
            onCallClicked = dialerViewModel::onCallClicked,

            settingsUiState = settingsUiState,
            onOpenBatterySettings = settingsViewModel::onOpenBatterySettings,
            onToggleNotifications = settingsViewModel::onToggleNotifications,

            openNotificationAccessSettings = notificationViewModel::openNotificationAccessSettings
        )
    }

    if (appPadUiState.showAppPicker) {
        AppPickerDialog(
            appPadUiState = appPadUiState,
            onDismiss = appPadViewModel::onDismissAppPicker,
            onAppSelected = appPadViewModel::onAppSelected,
            onSearchQueryChanged = appPadViewModel::onAppPickerSearchQueryChanged
        )
    }
}
