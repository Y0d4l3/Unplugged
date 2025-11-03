package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unplugged.launcher.domain.app_pad.AppPadViewModel
import com.unplugged.launcher.domain.dialer.DialerViewModel
import com.unplugged.launcher.domain.home_screen.HomeScreenViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeScreenViewModel = viewModel(),
    dialerViewModel: DialerViewModel = viewModel(),
    appPadViewModel: AppPadViewModel = viewModel()
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val dialerUiState by dialerViewModel.uiState.collectAsState()
    val appPadUiState by appPadViewModel.uiState.collectAsState()

    val topPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    val bottomPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopPager(
            modifier = Modifier
                .weight(1f)
                .padding(top = 10.dp),
            topPagerState = topPagerState,
            time = homeUiState.time,
            date = homeUiState.date,
            lastNotification = homeUiState.lastNotification,
            onDismissNotification = homeViewModel::onDismissNotification,
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

            isBatterySaverOn = homeUiState.isBatterySaverOn,
            onOpenBatterySettings = homeViewModel::openBatterySettings,
            openNotificationAccessSettings = homeViewModel::openNotificationAccessSettings,
            areNotificationsEnabled = homeUiState.areNotificationsEnabled,
            onToggleNotifications = homeViewModel::onToggleNotifications
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
