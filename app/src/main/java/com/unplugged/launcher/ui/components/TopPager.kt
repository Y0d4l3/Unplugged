package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.unplugged.launcher.data.model.AppNotification
import androidx.core.graphics.drawable.toDrawable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopPager(
    modifier: Modifier = Modifier,
    topPagerState: PagerState,
    time: String,
    date: String,
    onDismissNotification: () -> Unit,
    lastNotification: AppNotification?,
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
            when (page % 3) {
                0 -> Box(modifier = Modifier.fillMaxSize()) {
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

                1 -> {
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
                2 -> {}
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(name = "Top Pager - Clock", showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
private fun TopPagerPreview_Clock() {
    val pagerState = rememberPagerState { 2 }

    val fakeNotification = AppNotification(
        key = "preview_notification_key",
        appName = "Messages",
        appIcon = android.graphics.Color.BLUE.toDrawable(),
        title = "Jane Doe",
        text = "Hey, are you free for lunch?"
    )

    TopPager(
        topPagerState = pagerState,
        time = "10:09",
        date = "Fri, 3 Nov",
        onDismissNotification = {},
        lastNotification = fakeNotification
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(name = "Top Pager - Notification", showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
private fun TopPagerPreview_Notification() {
    val pagerState = rememberPagerState(initialPage = 1) { 2 }

    val fakeNotification = AppNotification(
        key = "preview_notification_key",
        appName = "Messages",
        appIcon = android.graphics.Color.BLUE.toDrawable(),
        title = "Jane Doe",
        text = "Hey, are you free for lunch?"
    )

    TopPager(
        topPagerState = pagerState,
        time = "10:10",
        date = "Fri, 3 Nov",
        onDismissNotification = {},
        lastNotification = fakeNotification
    )
}