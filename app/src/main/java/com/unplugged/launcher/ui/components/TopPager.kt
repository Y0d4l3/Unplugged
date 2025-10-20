package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onOpenBatterySettings: () -> Unit
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
                1 -> {
                    Column(
                        modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = time,
                            color = Color.White,
                            fontSize = 64.sp
                        )

                        Text(
                            text = date,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 24.sp
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
                        Text(
                            text = if (isBatterySaverOn) "Batteriesparmodus ist aktiv" else "Batteriesparmodus ist aus",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 18.sp
                        )
                        Button(
                            onClick = onOpenBatterySettings,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f)
                            )
                        ) {
                            Text("Einstellungen öffnen", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

