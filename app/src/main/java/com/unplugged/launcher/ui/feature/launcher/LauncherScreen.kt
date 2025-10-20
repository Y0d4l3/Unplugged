package com.unplugged.launcher.ui.feature.launcher

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unplugged.launcher.ui.components.AppAndDialerPager
import com.unplugged.launcher.ui.components.AppPickerDialog
import com.unplugged.launcher.ui.components.TimeAndDatePager

@OptIn(ExperimentalFoundationApi::class) // Notwendig für rememberPagerState
@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // PagerState für den oberen Pager (Uhrzeit/Datum)
    val topPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    // PagerState für den unteren Pager (Apps/Dialer)
    val bottomPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    Column(Modifier.fillMaxSize()) {
        TimeAndDatePager(
            modifier = Modifier.weight(1f),
            topPagerState = topPagerState,
            time = uiState.time,
            date = uiState.date
        )

        AppAndDialerPager(
            modifier = Modifier.weight(2f),
            bottomPagerState = bottomPagerState,
            uiState = uiState,
            onNumberClicked = viewModel::onNumberClicked,
            onDeleteClicked = viewModel::onDeleteClicked,
            onCallClicked = viewModel::onCallClicked,
            onAddAppClicked = viewModel::onAddAppClicked,
            onLaunchApp = viewModel::onLaunchApp
        )
    }

    // Zeigt den App-Auswahldialog bei Bedarf über allem an
    if (uiState.showAppPicker) {
        AppPickerDialog(
            appList = uiState.installedApps,
            onDismiss = viewModel::onDismissAppPicker,
            onAppSelected = viewModel::onAppSelected,
        )
    }
}
