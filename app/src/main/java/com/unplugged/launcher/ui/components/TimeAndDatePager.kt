package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeAndDatePager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    time: String,
    date: String
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        when (page % 2) {
            0 -> TimeDisplay(time = time)
            1 -> DateDisplay(date = date)
        }
    }
}

@Composable
private fun TimeDisplay(time: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            fontSize = 86.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DateDisplay(date: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )
    }
}
