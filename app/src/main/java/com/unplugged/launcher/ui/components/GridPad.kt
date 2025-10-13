package com.unplugged.launcher.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GridPad(
    rows: List<List<Any?>>,
    onClick: (row: Int, col: Int, item: Any?) -> Unit,
    cellContent: @Composable (item: Any?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        cellContent(item)
                    }
                }
            }
        }
    }
}
