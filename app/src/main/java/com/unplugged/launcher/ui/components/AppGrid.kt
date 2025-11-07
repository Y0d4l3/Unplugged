package com.unplugged.launcher.ui.components

import android.annotation.SuppressLint
import android.content.ComponentName
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unplugged.launcher.data.model.LauncherApp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppGrid(
    appSlots: List<LauncherApp?>,
    onAddAppClicked: (Int) -> Unit,
    onLaunchApp: (LauncherApp) -> Unit,
    onRemoveApp: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = false
        ) {
            itemsIndexed(appSlots) { index, app ->
                GlassKey(onClick = {
                    if (app != null) {
                        onLaunchApp(app)
                    } else {
                        onAddAppClicked(index)
                    }
                }, onLongClick = {
                    if (app != null) {
                        onRemoveApp(index)
                    }
                }) {
                    if (app != null) {
                        AppIcon(
                            componentName = app.componentName,
                            contentDescription = app.label,
                            modifier = Modifier.size(40.dp),
                            applyGrayscale = true
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add App",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@SuppressLint("LocalContextResourcesRead")
@Preview(showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
private fun AppGridPreview() {
    val fakeApps = listOf(
        LauncherApp(
            label = "Phone",
            componentName = ComponentName("com.android.phone", "com.android.phone.SomeActivity"),
            icon = null,
        ),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,

        )

    AppGrid(appSlots = fakeApps, onAddAppClicked = {}, onLaunchApp = {}, onRemoveApp = {})
}