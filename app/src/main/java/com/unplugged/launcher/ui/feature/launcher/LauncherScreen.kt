package com.unplugged.launcher.ui.feature.launcher

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unplugged.launcher.ui.components.AppAndDialerPager
import com.unplugged.launcher.ui.components.TimeAndDatePager
import com.unplugged.launcher.ui.components.AppPickerDialog

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val topPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    val bottomPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    Column(Modifier.fillMaxSize()) {
        TimeAndDatePager(
            modifier = Modifier.weight(1f),
            pagerState = topPagerState,
            time = uiState.time,
            date = uiState.date
        )

        AppAndDialerPager(
            modifier = Modifier.weight(2f),
            pagerState = bottomPagerState,
            enteredNumber = uiState.enteredNumber,
            appSlots = uiState.appSlots,
            onNumberClick = viewModel::onNumberClicked,
            onDelete = viewModel::onDeleteClicked,
            onCall = viewModel::onCallClicked,
            onAddApp = viewModel::onAddAppClicked,
            onLaunchApp = viewModel::onLaunchApp
        )
    }

    if (uiState.showAppPicker) {
        AppPickerDialog(
            appList = uiState.installedApps,
            onDismiss = viewModel::onDismissAppPicker,
            onAppSelected = viewModel::onAppSelected,
        )
    }
}
    