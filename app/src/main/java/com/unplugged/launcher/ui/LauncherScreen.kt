package com.unplugged.launcher.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.unplugged.launcher.data.LauncherApp
import com.unplugged.launcher.currentDate
import com.unplugged.launcher.currentTime
import com.unplugged.launcher.ui.components.AppPickerDialog
import kotlinx.coroutines.delay
import androidx.core.net.toUri

@Composable
fun LauncherScreen() {
    val context = LocalContext.current

    // ⏰ UI-State
    var time by remember { mutableStateOf(currentTime()) }
    var date by remember { mutableStateOf(currentDate()) }
    var enteredNumber by remember { mutableStateOf("") }
    var appSlots by remember { mutableStateOf(List<LauncherApp?>(12) { null }) }

    var selectedSlot by remember { mutableStateOf<Int?>(null) }
    var showAppPicker by remember { mutableStateOf(false) }

    // Pager States
    val topPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )
    val bottomPagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    LaunchedEffect(Unit) {
        while (true) {
            time = currentTime()
            date = currentDate()
            delay(1000L)
        }
    }

    // 📱 Haupt-Layout
    Column(Modifier.fillMaxSize()) {
        TopPagerContent(
            modifier = Modifier.weight(1f),
            topPagerState = topPagerState,
            time = time,
            date = date
        )

        BottomPagerContent(
            modifier = Modifier.weight(2f),
            bottomPagerState = bottomPagerState,
            enteredNumber = enteredNumber,
            onNumberClick = { digit -> enteredNumber += digit },
            onDelete = {
                if (enteredNumber.isNotEmpty()) {
                    enteredNumber = enteredNumber.dropLast(1)
                }
            },
            // 🔹 NEU: Call-Button aus DialPad
            onCall = {
                if (enteredNumber.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = "tel:$enteredNumber".toUri()
                    }
                    context.startActivity(intent)
                }
            },
            appSlots = appSlots,
            onAddApp = { slotIndex ->
                selectedSlot = slotIndex
                showAppPicker = true
            },
            onLaunchApp = { app ->
                val intent =
                    context.packageManager.getLaunchIntentForPackage(app.componentName.packageName)
                if (intent != null) {
                    context.startActivity(intent)
                }
            }
        )
    }

    // 📦 App-Picker-Dialog
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
