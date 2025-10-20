package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeAndDatePager(
    modifier: Modifier = Modifier,
    topPagerState: PagerState,
    time: String,
    date: String
) {
    HorizontalPager(
        state = topPagerState,
        modifier = modifier
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (page % 2) {
                0 -> GlassBox {
                    Text(
                        text = time,
                        color = Color.White,
                        fontSize = 64.sp
                    )
                }
                1 -> GlassBox {
                    Text(
                        text = date,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

