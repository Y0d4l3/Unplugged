package com.unplugged.launcher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unplugged.launcher.domain.settings.SettingsUiState

@Composable
fun SettingsPage(
    settingsUiState: SettingsUiState,

    onOpenBatterySettings: () -> Unit,
    openNotificationAccessSettings: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Push-Benachrichtigungen", color = Color.White, fontSize = 16.sp)
            Switch(
                checked = settingsUiState.areNotificationsEnabled,
                onCheckedChange = onToggleNotifications
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!settingsUiState.isBatterySaverOn) {
                Text(
                    text = "Tipp: Für die beste Erfahrung den extremen Batteriesparmodus aktivieren.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onOpenBatterySettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Text("Einstellungen öffnen", color = Color.White)
                }
            } else {
                Text(
                    text = "Batteriesparmodus ist aktiv",
                    color = Color.Green.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tipp: Benachrichtigungszugriff erteilen, um eine Vorschau im Launcher zu erhalten.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = openNotificationAccessSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Text("Zugriff erteilen", color = Color.White)
            }
        }
    }
}
