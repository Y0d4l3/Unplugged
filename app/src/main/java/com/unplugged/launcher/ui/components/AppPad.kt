package com.unplugged.launcher.ui.components

import androidx.compose.runtime.Composable
import com.unplugged.launcher.data.LauncherApp

@Composable
fun AppPad(
    slots: List<LauncherApp?>,
    onAddApp: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit
) {
    val rows = slots.chunked(3)

    GridPad(
        rows = rows,
        onClick = { row, col, item ->
            val index = row * 3 + col
            if (item == null) onAddApp(index)
            else onLaunchApp(item as LauncherApp)
        },
        cellContent = { item ->
            AppSlot(app = item as? LauncherApp)
        }
    )
}
