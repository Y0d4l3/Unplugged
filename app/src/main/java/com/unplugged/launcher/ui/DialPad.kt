package com.unplugged.launcher.ui
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unplugged.launcher.ui.components.GlassBox
import com.unplugged.launcher.ui.components.GlassKey
import com.unplugged.launcher.ui.components.GridPad

@Composable
fun DialPad(
    enteredNumber: String,
    onNumberClick: (String) -> Unit,
    onDelete: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Eingabefeld
        GlassBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = enteredNumber,
                    fontSize = 28.sp,
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Löschen",
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onDelete() }
                )
            }
        }

        // GridPad wiederverwenden
        GridPad(rows = rows) { _, _, item ->
            if (item is String) onNumberClick(item)
        }

        // Call Button unten
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
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}
