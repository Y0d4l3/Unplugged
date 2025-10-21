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
import com.unplugged.launcher.domain.launcher.LauncherViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBatterySaverOn = uiState.isBatterySaverOn


    val topPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 3,
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
            time = uiState.time,
            date = uiState.date,
            isBatterySaverOn = isBatterySaverOn,
            onOpenBatterySettings = viewModel::openBatterySettings,
            openNotificationAccessSettings = viewModel::openNotificationAccessSettings,
            lastNotification = uiState.lastNotification,
            onDismissNotification = viewModel::onDismissNotification
        )

        BottomPager(
            modifier = Modifier.weight(2f),
            bottomPagerState = bottomPagerState,
            uiState = uiState,
            onNumberClicked = viewModel::onNumberClicked,
            onDeleteClicked = viewModel::onDeleteClicked,
            onCallClicked = viewModel::onCallClicked,
            onAddAppClicked = viewModel::onAddAppClicked,
            onLaunchApp = viewModel::onLaunchApp,
            onRemoveApp = viewModel::onRemoveApp
        )
    }

    if (uiState.showAppPicker) {
        AppPickerDialog(
            uiState = uiState,
            onDismiss = viewModel::onDismissAppPicker,
            onAppSelected = viewModel::onAppSelected,
            onSearchQueryChanged = viewModel::onAppPickerSearchQueryChanged
        )
    }
}
