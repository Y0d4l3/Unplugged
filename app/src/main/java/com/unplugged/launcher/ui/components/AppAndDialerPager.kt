package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unplugged.launcher.data.model.LauncherApp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppAndDialerPager(
    modifier: Modifier = Modifier,
    bottomPagerState: PagerState,
    uiState: com.unplugged.launcher.ui.feature.launcher.LauncherUiState,
    onNumberClicked: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    onCallClicked: () -> Unit,
    onAddAppClicked: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit,
) {
    HorizontalPager(
        state = bottomPagerState,
        modifier = modifier
    ) { page ->
        when (page % 2) {
            0 -> AppGrid(
                appSlots = uiState.appSlots,
                onAddAppClicked = onAddAppClicked,
                onLaunchApp = onLaunchApp
            )
            1 -> Dialer(
                enteredNumber = uiState.enteredNumber,
                onNumberClicked = onNumberClicked,
                onDeleteClicked = onDeleteClicked,
                onCallClicked = onCallClicked
            )
        }
    }
}

@Composable
fun AppGrid(
    appSlots: List<LauncherApp?>,
    onAddAppClicked: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(appSlots) { index, app ->
            GlassKey(
                onClick = {
                    if (app != null) {
                        onLaunchApp(app)
                    } else {
                        onAddAppClicked(index)
                    }
                }
            ) {
                if (app?.icon != null) {
                    Image(
                        bitmap = app.icon.asImageBitmap(),
                        contentDescription = app.label,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add App",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Dialer(
    enteredNumber: String,
    onNumberClicked: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    onCallClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = enteredNumber, fontSize = 32.sp, color = Color.White)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(9) { index ->
                val number = (index + 1).toString()
                GlassKey(onClick = { onNumberClicked(number) }) {
                    Text(text = number, fontSize = 28.sp, color = Color.White)
                }
            }

            item { /* Leerer Platz */ }

            item {
                GlassKey(onClick = { onNumberClicked("0") }) {
                    Text(text = "0", fontSize = 28.sp, color = Color.White)
                }
            }

            item {
                GlassKey(onClick = onDeleteClicked) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        GlassKey(
            onClick = onCallClicked,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
