package com.unplugged.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unplugged.launcher.LauncherApp
import com.unplugged.launcher.ui.util.toImageBitmap

@Composable
fun GridPad(
    rows: List<List<Any?>>,
    onClick: (row: Int, col: Int, item: Any?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        rows.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEachIndexed { colIndex, item ->
                    GlassKey(
                        onClick = { onClick(rowIndex, colIndex, item) },
                        modifier = Modifier.weight(1f)
                    ) {
                        when (item) {
                            is String -> {
                                Text(item, fontSize = 28.sp, color = MaterialTheme.colorScheme.onBackground)
                            }
                            is LauncherApp -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        bitmap = item.icon.toImageBitmap(),
                                        contentDescription = item.label,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(item.label, fontSize = 12.sp)
                                }
                            }
                            null -> {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "App hinzufügen",
                                    modifier = Modifier.size(28.dp),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
