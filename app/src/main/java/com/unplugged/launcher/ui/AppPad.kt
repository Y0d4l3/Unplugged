package com.unplugged.launcher.ui

import androidx.compose.runtime.Composable
import com.unplugged.launcher.LauncherApp
import com.unplugged.launcher.ui.components.GridPad

@Composable
fun AppPad(
    slots: List<LauncherApp?>,
    onAddApp: (slotIndex: Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit
) {
    val rows = slots.chunked(3)

    GridPad(rows = rows) { row, col, item ->
        val index = row * 3 + col
        when (item) {
            is LauncherApp -> onLaunchApp(item)
            null -> onAddApp(index)
        }
    }
}
