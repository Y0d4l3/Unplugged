package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.domain.home_screen.HomeScreenUiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPager(
    modifier: Modifier = Modifier,
    bottomPagerState: PagerState,
    uiState: HomeScreenUiState,
    onNumberClicked: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    onCallClicked: () -> Unit,
    onAddAppClicked: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit,
    onRemoveApp: (Int) -> Unit,
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
                appSlots = uiState.appSlots,
                onAddAppClicked = onAddAppClicked,
                onLaunchApp = onLaunchApp,
                onRemoveApp = onRemoveApp
            )

            1 -> Dialer(
                enteredNumber = uiState.enteredNumber,
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