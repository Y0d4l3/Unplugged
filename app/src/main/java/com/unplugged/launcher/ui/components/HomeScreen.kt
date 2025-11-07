package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unplugged.launcher.domain.app_pad.AppPadUiState
import com.unplugged.launcher.domain.app_pad.AppPadViewModel
import com.unplugged.launcher.domain.dialer.DialerUiState
import com.unplugged.launcher.domain.dialer.DialerViewModel
import com.unplugged.launcher.domain.notifications.GlobalUiState
import com.unplugged.launcher.domain.notifications.NotificationViewModel
import com.unplugged.launcher.domain.settings.SettingsUiState
import com.unplugged.launcher.domain.settings.SettingsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    notificationViewModel: NotificationViewModel = viewModel(),
    dialerViewModel: DialerViewModel = viewModel(),
    appPadViewModel: AppPadViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val globalUiState by notificationViewModel.uiState.collectAsState()
    val dialerUiState by dialerViewModel.uiState.collectAsState()
    val appPadUiState by appPadViewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    DisposableEffect(lifecycleOwner, settingsViewModel) {
        val observer = settingsViewModel
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val topPagerState = rememberPagerState(initialPage = 0) { 3 }
    val bottomPagerState = rememberPagerState(initialPage = 0) { 3 }

    HomeScreenContent(
        topPagerState = topPagerState,
        bottomPagerState = bottomPagerState,
        globalUiState = globalUiState,
        dialerUiState = dialerUiState,
        appPadUiState = appPadUiState,
        settingsUiState = settingsUiState,
        onDismissNotification = notificationViewModel::onDismissNotification,
        onAddAppClicked = appPadViewModel::onAddAppClicked,
        onLaunchApp = appPadViewModel::onLaunchApp,
        onRemoveApp = appPadViewModel::onRemoveApp,
        onNumberClicked = dialerViewModel::onNumberClicked,
        onDeleteClicked = dialerViewModel::onDeleteClicked,
        onCallClicked = dialerViewModel::onCallClicked,
        onOpenBatterySettings = settingsViewModel::onOpenBatterySettings,
        onToggleNotifications = settingsViewModel::onToggleNotifications,
        openNotificationAccessSettings = settingsViewModel::openNotificationAccessSettings,
        onDismissAppPicker = appPadViewModel::onDismissAppPicker,
        onAppSelected = appPadViewModel::onAppSelected,
        onSearchQueryChanged = appPadViewModel::onAppPickerSearchQueryChanged
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    topPagerState: PagerState,
    bottomPagerState: PagerState,
    globalUiState: GlobalUiState,
    dialerUiState: DialerUiState,
    appPadUiState: AppPadUiState,
    settingsUiState: SettingsUiState,
    onDismissNotification: () -> Unit,
    onAddAppClicked: (Int) -> Unit,
    onLaunchApp: (com.unplugged.launcher.data.model.LauncherApp) -> Unit,
    onRemoveApp: (Int) -> Unit,
    onNumberClicked: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    onCallClicked: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    openNotificationAccessSettings: () -> Unit,
    onDismissAppPicker: () -> Unit,
    onAppSelected: (com.unplugged.launcher.data.model.LauncherApp) -> Unit,
    onSearchQueryChanged: (String) -> Unit
) {
    Column {
        TopPager(
            modifier = Modifier
                .weight(1f)
                .padding(top = 10.dp),
            topPagerState = topPagerState,
            time = globalUiState.time,
            date = globalUiState.date,
            lastNotification = globalUiState.lastNotification,
            onDismissNotification = onDismissNotification,
        )

        BottomPager(
            modifier = Modifier.weight(2f),
            bottomPagerState = bottomPagerState,
            appPadUiState = appPadUiState,
            onAddAppClicked = onAddAppClicked,
            onLaunchApp = onLaunchApp,
            onRemoveApp = onRemoveApp,
            dialerUiState = dialerUiState,
            onNumberClicked = onNumberClicked,
            onDeleteClicked = onDeleteClicked,
            onCallClicked = onCallClicked,
            settingsUiState = settingsUiState,
            onOpenBatterySettings = onOpenBatterySettings,
            onToggleNotifications = onToggleNotifications,
            openNotificationAccessSettings = openNotificationAccessSettings
        )
    }

    if (appPadUiState.showAppPicker) {
        AppPickerDialog(
            appPadUiState = appPadUiState,
            onDismiss = onDismissAppPicker,
            onAppSelected = onAppSelected,
            onSearchQueryChanged = onSearchQueryChanged
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
private fun HomeScreenPreview() {
    val topPagerState = rememberPagerState { 1 }
    val bottomPagerState = rememberPagerState(initialPage = 1) { 3 }

    HomeScreenContent(
        topPagerState = topPagerState,
        bottomPagerState = bottomPagerState,
        globalUiState = GlobalUiState(time = "12:34", date = "Mon, 1 Jan"),
        dialerUiState = DialerUiState(enteredNumber = "9876"),
        appPadUiState = AppPadUiState(showAppPicker = false),
        settingsUiState = SettingsUiState(),
        onDismissNotification = {},
        onAddAppClicked = {},
        onLaunchApp = {},
        onRemoveApp = {},
        onNumberClicked = {},
        onDeleteClicked = {},
        onCallClicked = {},
        onOpenBatterySettings = {},
        onToggleNotifications = {},
        openNotificationAccessSettings = {},
        onDismissAppPicker = {},
        onAppSelected = {},
        onSearchQueryChanged = {})
}
