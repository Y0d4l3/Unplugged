package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unplugged.launcher.data.model.LauncherApp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppAndDialerPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    enteredNumber: String,
    appSlots: List<LauncherApp?>,
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    onAddApp: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        when (page % 2) {
            0 -> AppGrid(
                appSlots = appSlots,
                onAddApp = onAddApp,
                onLaunchApp = onLaunchApp
            )
            1 -> Dialer(
                enteredNumber = enteredNumber,
                onNumberClick = onNumberClick,
                onDelete = onDelete,
                onCall = onCall
            )
        }
    }
}

@Composable
private fun AppGrid(
    appSlots: List<LauncherApp?>,
    onAddApp: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit
) {
    val rows = appSlots.chunked(3)

    GridPad(
        rows = rows,
        onCellClick = { row, col, item ->
            val index = row * 3 + col
            if (item != null) {
                onLaunchApp(item)
            } else {
                onAddApp(index)
            }
        },
        cellContent = { item ->
            AppSlot(app = item)
        }
    )
}

@Composable
private fun AppSlot(app: LauncherApp?) {
    Box(
        modifier = Modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (app?.icon != null) {
            Image(
                bitmap = app.icon.asImageBitmap(),
                contentDescription = app.label,
                modifier = Modifier.aspectRatio(1f)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "App hinzufügen"
            )
        }
    }
}

@Composable
private fun Dialer(
    enteredNumber: String,
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = enteredNumber, fontSize = 32.sp, modifier = Modifier.padding(vertical = 16.dp))
        Numpad(
            onNumberClick = onNumberClick,
            onDelete = onDelete,
            onCall = onCall
        )
    }
}

@Composable
private fun Numpad(
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit
) {
    val buttons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "*", "0", "#"
    )
    val rows = buttons.chunked(3)

    GridPad(
        rows = rows,
        onCellClick = { _, _, item -> onNumberClick(item) },
        cellContent = { item ->
            Text(text = item, fontSize = 24.sp)
        }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("Löschen", modifier = Modifier.clickable { onDelete() })
        Text("Anrufen", modifier = Modifier.clickable { onCall() })
    }
}

@Composable
private fun <T> GridPad(
    rows: List<List<T>>,
    onCellClick: (row: Int, col: Int, item: T) -> Unit,
    cellContent: @Composable (item: T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        rows.forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEachIndexed { colIndex, item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.5f) // Passt das Seitenverhältnis der Zellen an
                            .clickable { onCellClick(rowIndex, colIndex, item) },
                        contentAlignment = Alignment.Center
                    ) {
                        cellContent(item)
                    }
                }
            }
        }
    }
}
