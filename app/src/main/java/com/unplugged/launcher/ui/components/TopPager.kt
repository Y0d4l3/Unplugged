package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.unplugged.launcher.data.model.AppNotification

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopPager(
    modifier: Modifier = Modifier,
    topPagerState: PagerState,
    time: String,
    date: String,
    isBatterySaverOn: Boolean,
    onOpenBatterySettings: () -> Unit,
    openNotificationAccessSettings: () -> Unit,
    onDismissNotification: () -> Unit,
    lastNotification: AppNotification?
) {
    val grayscaleColorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })

    HorizontalPager(
        state = topPagerState,
        modifier = modifier
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (page % 5) {
                0 -> {}
                1 -> Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = time, color = Color.White, fontSize = 64.sp)
                        Text(text = date, color = Color.White.copy(alpha = 0.8f), fontSize = 24.sp)
                    }

                    if (lastNotification != null) {
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
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Tipp: Aktiviere den Extrem Battery Saver um den Launcher am effektivsten zu nutzen. Füge deine Shortcut Apps als Ausnahme hinzu, um sie effektiv nutzen zu können",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
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
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 20.sp
                            )
                        }
                    }
                }

                3 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Tipp: Aktiviere den Benachrichtigungszugriff, um eine Vorschau zu erhalten.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = openNotificationAccessSettings,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Text("Benachrichtigungszugriff erteilen", color = Color.White)
                        }
                    }
                }

                4 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (lastNotification != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(
                                        painter = rememberDrawablePainter(drawable = lastNotification.appIcon),
                                        contentDescription = lastNotification.appName,
                                        modifier = Modifier.size(20.dp),
                                        colorFilter = grayscaleColorFilter
                                    )
                                    Text(
                                        text = lastNotification.appName,
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 16.sp
                                    )
                                }
                                Text(
                                    text = lastNotification.title,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = lastNotification.text,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss notification",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 16.dp, end = 24.dp)
                                    .size(24.dp)
                                    .clickable { onDismissNotification() }
                            )

                        } else {
                            Icon(
                                imageVector = Icons.Default.MailOutline,
                                contentDescription = "No notifications",
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
