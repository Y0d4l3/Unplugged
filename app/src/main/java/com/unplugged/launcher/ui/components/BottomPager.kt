package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.domain.app_pad.AppPadUiState
import com.unplugged.launcher.domain.dialer.DialerUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPager(
    modifier: Modifier = Modifier,
    bottomPagerState: PagerState,

    appPadUiState: AppPadUiState,
    onAddAppClicked: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit,
    onRemoveApp: (Int) -> Unit,

    dialerUiState: DialerUiState,
    onNumberClicked: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    onCallClicked: () -> Unit,

    isBatterySaverOn: Boolean,
    onOpenBatterySettings: () -> Unit,
    openNotificationAccessSettings: () -> Unit,
    areNotificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit
) {
    HorizontalPager(
        state = bottomPagerState,
        modifier = modifier
    ) { page ->
        when (page % 3) {
            0 -> AppGrid(
                appSlots = appPadUiState.appSlots,
                onAddAppClicked = onAddAppClicked,
                onLaunchApp = onLaunchApp,
                onRemoveApp = onRemoveApp
            )

            1 -> Dialer(
                dialerUiState = dialerUiState,
                onNumberClicked = onNumberClicked,
                onDeleteClicked = onDeleteClicked,
                onCallClicked = onCallClicked
            )

            2 -> {
                SettingsPage(
                    isBatterySaverOn = isBatterySaverOn,
                    onOpenBatterySettings = onOpenBatterySettings,
                    openNotificationAccessSettings = openNotificationAccessSettings,
                    areNotificationsEnabled = areNotificationsEnabled,
                    onToggleNotifications = onToggleNotifications
                )
            }
        }
    }
}