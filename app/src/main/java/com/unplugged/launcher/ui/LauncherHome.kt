package com.unplugged.launcher.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unplugged.launcher.ui.components.GlassBox
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.unplugged.launcher.currentDate
import com.unplugged.launcher.currentTime
import com.unplugged.launcher.ui.components.GlassKey
import kotlinx.coroutines.delay

@Composable
fun LauncherHome() {
    var time by remember { mutableStateOf(currentTime()) }
    var date by remember { mutableStateOf(currentDate()) }
    var enteredNumber by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(enteredNumber) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    fun onNumberClick(digit: String) {
        enteredNumber += digit
    }

    LaunchedEffect(Unit) {
        while (true) {
            time = currentTime()
            date = currentDate()
            delay(1000L)
        }
    }

    Box(Modifier.fillMaxSize()) {
        GlassBox(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 120.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(time, fontSize = 48.sp, color = Color.White)
                Text(date, fontSize = 20.sp, color = Color.White)
            }
        }

        Icon(
            imageVector = Icons.Filled.Email,
            contentDescription = "Mail",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
            tint = Color.White
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            // Eingabefeld in GlasBox
            GlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(80.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(scrollState),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = enteredNumber,
                            fontSize = 28.sp,
                            color = Color.White,
                            maxLines = 1
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Löschen",
                        tint = Color.White,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(start = 8.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    if (enteredNumber.isNotEmpty()) {
                                        enteredNumber = enteredNumber.dropLast(1)
                                    }
                                }
                            )
                    )
                }
            }


            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("*", "0", "#")
            )
            rows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { label ->
                        GlassKey(
                            onClick = { onNumberClick(label) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(label, fontSize = 28.sp, color = Color.White)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                GlassKey(
                    onClick = { /* TODO: Call Action */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Anrufen",
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
