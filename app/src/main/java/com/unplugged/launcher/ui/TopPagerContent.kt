package com.unplugged.launcher.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.provider.Settings
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.unplugged.launcher.ui.components.GlassBox
import kotlin.math.absoluteValue

@Composable
fun TopPagerContent(topPagerState: PagerState, time: String, date: String, modifier: Modifier) {
    val topRealPages = 2
    HorizontalPager(
        state = topPagerState,
        modifier = modifier.fillMaxWidth()
    ) { page ->
        val index = (page % topRealPages).absoluteValue
        when (index) {
            0 -> InfoWidget()
            1 -> TimeAndDateWidget(time = time, date = date)
        }
    }
}

@Composable
fun TimeAndDateWidget(time: String, date: String) {
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
fun InfoWidget() {
    val context = LocalContext.current
    var isNormalBatterySaverActive by remember { mutableStateOf(false) }

    fun updateBatterySaverState() {
        try {
            val pm = context.getSystemService(PowerManager::class.java)

            val isPowerSaveModeOn = pm.isPowerSaveMode
            isNormalBatterySaverActive = false

            if (isPowerSaveModeOn) {
                isNormalBatterySaverActive = true
            }

        } catch (_: Exception) {
        }
    }

    DisposableEffect(Unit) {
        val receiver = com.unplugged.launcher.receiver.BatterySaverReceiver {
            updateBatterySaverState()
        }
        val filter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        context.registerReceiver(receiver, filter)
        updateBatterySaverState()
        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {
            }
        }
    }

    GlassBox(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.displayCutout.asPaddingValues())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isNormalBatterySaverActive) {
                Text(
                    text = "🔋 Batteriesparmodus ist aktiv",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Der normale Batteriesparmodus ist aktiv. Um den Extreme Battery Saver zu aktivieren, " +
                            "wechsle in die Akku-Einstellungen.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Batteriesparmodus-Einstellungen öffnen")
                }
            } else {
                Text(
                    text = "⚠️ Kein Batteriesparmodus aktiv",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Aktiviere den Batteriesparmodus.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Batteriesparmodus-Einstellungen öffnen")
                }
            }
        }
    }
}