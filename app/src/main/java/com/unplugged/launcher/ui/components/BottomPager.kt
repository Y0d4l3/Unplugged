package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.domain.app_pad.AppPadUiState
import com.unplugged.launcher.domain.dialer.DialerUiState
import com.unplugged.launcher.domain.settings.SettingsUiState

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

    settingsUiState: SettingsUiState,
    onOpenBatterySettings: () -> Unit,
    openNotificationAccessSettings: () -> Unit,
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
                    settingsUiState = settingsUiState,
                    onOpenBatterySettings = onOpenBatterySettings,
                    openNotificationAccessSettings = openNotificationAccessSettings,
                    onToggleNotifications = onToggleNotifications
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(name = "Bottom Pager Preview", showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
private fun BottomPagerPreview() {
    val pagerState = rememberPagerState(initialPage = 1) { 3 }

    BottomPager(
        bottomPagerState = pagerState,

        appPadUiState = AppPadUiState(),
        onAddAppClicked = {},
        onLaunchApp = {},
        onRemoveApp = {},

        dialerUiState = DialerUiState(),
        onNumberClicked = {},
        onDeleteClicked = {},
        onCallClicked = {},

        settingsUiState = SettingsUiState(),
        onOpenBatterySettings = {},
        openNotificationAccessSettings = {},
        onToggleNotifications = {}
    )
}
