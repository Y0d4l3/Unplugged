package com.unplugged.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import com.unplugged.launcher.ui.components.GlassBox
import kotlin.math.absoluteValue

@Composable
fun TopPagerContent(topPagerState: PagerState, time: String, date: String, modifier: Modifier) {
    val topRealPages = 3
    HorizontalPager(
        state = topPagerState,
        modifier = modifier.fillMaxWidth()
    ) { page ->
        val index = (page % topRealPages).absoluteValue
        when (index) {
            0 -> TimeAndDatePage(time = time, date = date)
            1 -> WidgetPage()
        }
    }
}

@Composable
fun TimeAndDatePage(time: String, date: String) {
    GlassBox(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.displayCutout.asPaddingValues())
            .padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Email,
            contentDescription = "Mail",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(time, fontSize = 48.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(date, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun WidgetPage() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Widget-Seite", fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}