package com.unplugged.launcher.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassKey(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .background(Color.Black, RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Preview(name = "Glass Key", showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
private fun GlassKeyPreview() {
    GlassKey(
        modifier = Modifier.size(80.dp),
        onClick = {},
        onLongClick = {}
    ) {
        Text(
            text = "1",
            color = Color.White,
            fontSize = 32.sp,
        )
    }
}
