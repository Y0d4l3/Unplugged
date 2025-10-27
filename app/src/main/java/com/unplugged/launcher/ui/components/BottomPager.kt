package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.domain.home_screen.HomeScreenUiState

@Composable
private fun SettingsPage(
    isBatterySaverOn: Boolean,
    onOpenBatterySettings: () -> Unit,
    openNotificationAccessSettings: () -> Unit,
    areNotificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Push-Benachrichtigungen", color = Color.White, fontSize = 16.sp)
            Switch(
                checked = areNotificationsEnabled,
                onCheckedChange = onToggleNotifications
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isBatterySaverOn) {
                Text(
                    text = "Tipp: Für die beste Erfahrung den extremen Batteriesparmodus aktivieren.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onOpenBatterySettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Text("Einstellungen öffnen", color = Color.White)
                }
            } else {
                Text(
                    text = "Batteriesparmodus ist aktiv",
                    color = Color.Green.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tipp: Benachrichtigungszugriff erteilen, um eine Vorschau im Launcher zu erhalten.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = openNotificationAccessSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Text("Zugriff erteilen", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppGrid(
    appSlots: List<LauncherApp?>,
    onAddAppClicked: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit,
    onRemoveApp: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val grayscaleColorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = false
        ) {
            itemsIndexed(appSlots) { index, app ->
                val interactionSource = remember { MutableInteractionSource() }

                GlassKey(
                    modifier = Modifier.combinedClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            if (app != null) {
                                onLaunchApp(app)
                            } else {
                                onAddAppClicked(index)
                            }
                        },
                        onLongClick = {
                            if (app != null) {
                                onRemoveApp(index)
                            }
                        }
                    )
                ) {
                    if (app?.icon != null) {
                        Image(
                            bitmap = app.icon.asImageBitmap(),
                            contentDescription = app.label,
                            modifier = Modifier.size(40.dp),
                            colorFilter = grayscaleColorFilter
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
        Spacer(modifier = Modifier.height(20.dp))
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(80.dp)
                .border(
                    2.dp, Color.White.copy(alpha = 0.6f),
                    RoundedCornerShape(12.dp)
                )
                .background(
                    Color.Black,
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = enteredNumber,
                fontSize = 32.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(9) { index ->
                val number = (index + 1).toString()
                val interactionSource = remember { MutableInteractionSource() }
                GlassKey(
                    interactionSource = interactionSource,
                    modifier = Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onNumberClicked(number) }
                    )
                ) {
                    Text(text = number, fontSize = 28.sp, color = Color.White)
                }
            }

            item {
                val interactionSource = remember { MutableInteractionSource() }
                GlassKey(
                    interactionSource = interactionSource,
                    modifier = Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onDeleteClicked
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }

            item {
                val interactionSource = remember { MutableInteractionSource() }
                GlassKey(
                    interactionSource = interactionSource,
                    modifier = Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onNumberClicked("0") }
                    )
                ) {
                    Text(text = "0", fontSize = 28.sp, color = Color.White)
                }
            }

            item {
                val interactionSource = remember { MutableInteractionSource() }
                GlassKey(
                    interactionSource = interactionSource,
                    modifier = Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onCallClicked
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPager(
    modifier: Modifier = Modifier,
    bottomPagerState: PagerState,
    uiState: HomeScreenUiState,
    onNumberClicked: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    onCallClicked: () -> Unit,
    onAddAppClicked: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit,
    onRemoveApp: (Int) -> Unit,
    isBatterySaverOn: Boolean,
    onOpenBatterySettings: () -> Unit,
    openNotificationAccessSettings: () -> Unit,
    areNotificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit
) {
    HorizontalPager(
        state = bottomPagerState,
        modifier = modifier
    ) { page ->
        when (page % 3) {
            0 -> AppGrid(
                appSlots = uiState.appSlots,
                onAddAppClicked = onAddAppClicked,
                onLaunchApp = onLaunchApp,
                onRemoveApp = onRemoveApp
            )

            1 -> Dialer(
                enteredNumber = uiState.enteredNumber,
                onNumberClicked = onNumberClicked,
                onDeleteClicked = onDeleteClicked,
                onCallClicked = onCallClicked
            )

            2 -> {
                SettingsPage(
                    isBatterySaverOn = isBatterySaverOn,
                    onOpenBatterySettings = onOpenBatterySettings,
                    openNotificationAccessSettings = openNotificationAccessSettings,
                    areNotificationsEnabled = areNotificationsEnabled,
                    onToggleNotifications = onToggleNotifications
                )
            }
        }
    }
}