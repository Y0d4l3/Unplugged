package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopPager(
    modifier: Modifier = Modifier,
    topPagerState: PagerState,
    time: String,
    date: String,
    isBatterySaverOn: Boolean,
    onOpenBatterySettings: () -> Unit,
    hasNotifications: Boolean
) {
    HorizontalPager(
        state = topPagerState,
        modifier = modifier
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (page % 3) {
                0 -> ""
                1 -> Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = time, color = Color.White, fontSize = 64.sp)
                        Text(text = date, color = Color.White.copy(alpha = 0.8f), fontSize = 24.sp)
                    }

                    if (hasNotifications) {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "Unread notifications",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 24.dp)
                                .size(24.dp)
                        )
                    }
                }
                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!isBatterySaverOn) {
                            Text(
                                text = "Batteriesparmodus ist aus",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 25.sp
                            )
                            Text(
                                text = "Bitte aktiviere den Extrem Battery Saver um den Launcher am effektivsten zu nutzen. Füge deine Shortcut Apps als Ausnahme hinzu, um sie effektiv nutzen zu können",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = onOpenBatterySettings,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f)
                                )
                            ) {
                                Text("Einstellungen öffnen", color = Color.White)
                            }
                        } else {
                            Text(
                                text = "Batteriesparmodus ist aktiv",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 25.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

