package com.unplugged.launcher.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unplugged.launcher.data.LauncherApp
import com.unplugged.launcher.ui.components.AppPad
import com.unplugged.launcher.ui.components.DialPad
import kotlin.math.absoluteValue

@Composable
fun BottomPagerContent(
    bottomPagerState: PagerState,
    enteredNumber: String,
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    appSlots: List<LauncherApp?>,
    onAddApp: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomRealPages = 2

    HorizontalPager(
        state = bottomPagerState,
        modifier = modifier.fillMaxWidth()
    ) { page ->
        val index = (page % bottomRealPages).absoluteValue

        when (index) {
            0 -> DialPad(
                enteredNumber = enteredNumber,
                onNumberClick = onNumberClick,
                onDelete = onDelete,
                onCall = onCall
            )
            1 -> AppPad(
                slots = appSlots,
                onAddApp = onAddApp,
                onLaunchApp = onLaunchApp
            )
        }
    }
}
