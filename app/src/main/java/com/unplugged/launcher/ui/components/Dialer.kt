package com.unplugged.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unplugged.launcher.domain.dialer.DialerUiState

@Composable
fun Dialer(
    dialerUiState: DialerUiState,
    onNumberClicked: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    onCallClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(80.dp)
                .border(
                    2.dp, Color.White.copy(alpha = 0.6f),
                    RoundedCornerShape(12.dp)
                )
                .background(
                    Color.Black,
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dialerUiState.enteredNumber,
                fontSize = 32.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(9) { index ->
                val number = (index + 1).toString()
                GlassKey(
                    onClick = { onNumberClicked(number) }
                ) {
                    Text(text = number, fontSize = 28.sp, color = Color.White)
                }
            }

            item {
                GlassKey(
                    onClick = onDeleteClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }

            item {
                GlassKey(
                    onClick = { onNumberClicked("0") }
                ) {
                    Text(text = "0", fontSize = 28.sp, color = Color.White)
                }
            }

            item {
                GlassKey(
                    onClick = onCallClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(name = "Dialer", showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
private fun DialerPreview_WithNumber() {
    val uiStateWithNumber = DialerUiState(enteredNumber = "12345")

    Dialer(
        dialerUiState = uiStateWithNumber,
        onNumberClicked = {},
        onDeleteClicked = {},
        onCallClicked = {}
    )
}
