package com.unplugged.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.MaterialTheme
import com.unplugged.launcher.LauncherApp
import com.unplugged.launcher.currentDate
import com.unplugged.launcher.currentTime
import com.unplugged.launcher.ui.components.AppPickerDialog
import com.unplugged.launcher.ui.components.GlassBox
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun LauncherHome() {
    val context = LocalContext.current
    var time by remember { mutableStateOf(currentTime()) }
    var date by remember { mutableStateOf(currentDate()) }
    var enteredNumber by remember { mutableStateOf("") }
    var appSlots by remember { mutableStateOf(List<LauncherApp?>(12) { null }) }

    var selectedSlot by remember { mutableStateOf<Int?>(null) }
    var showAppPicker by remember { mutableStateOf(false) }

    val topRealPages = 3
    val topPagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2, pageCount = { Int.MAX_VALUE })

    val bottomRealPages = 2 // z. B. 1x Dialpad, 1x App-Grid
    val bottomPagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2, pageCount = { Int.MAX_VALUE })

    LaunchedEffect(Unit) {
        while (true) {
            time = currentTime()
            date = currentDate()
            delay(1000L)
        }
    }

    Column(Modifier.fillMaxSize()) {

        HorizontalPager(
            state = topPagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            val index = (page % topRealPages).absoluteValue
            when (index) {
                0 -> {
                    GlassBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                WindowInsets.displayCutout.asPaddingValues()
                            )
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
                1 -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Widget-Seite", fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }

        HorizontalPager(
            state = bottomPagerState,
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
        ) { page ->
            val index = (page % bottomRealPages).absoluteValue
            when (index) {
                0 -> DialPad(
                    enteredNumber = enteredNumber,
                    onNumberClick = { digit -> enteredNumber += digit },
                    onDelete = {
                        if (enteredNumber.isNotEmpty()) {
                            enteredNumber = enteredNumber.dropLast(1)
                        }
                    }
                )
                1 -> AppPad(
                    slots = appSlots,
                    onAddApp = { slotIndex ->
                        selectedSlot = slotIndex
                        showAppPicker = true
                    },
                    onLaunchApp = { app ->
                        val intent = context.packageManager
                            .getLaunchIntentForPackage(app.packageName)
                        if (intent != null) context.startActivity(intent)
                    }
                )
            }
        }
    }

    if (showAppPicker && selectedSlot != null) {
        AppPickerDialog(
            onDismiss = { showAppPicker = false },
            onAppSelected = { chosenApp ->
                appSlots = appSlots.toMutableList().also {
                    it[selectedSlot!!] = chosenApp
                }
                showAppPicker = false
                selectedSlot = null
            }
        )
    }
}
