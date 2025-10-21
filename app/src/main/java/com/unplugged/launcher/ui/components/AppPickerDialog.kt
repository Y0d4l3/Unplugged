package com.unplugged.launcher.ui.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unplugged.launcher.data.model.LauncherApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap
import com.unplugged.launcher.domain.launcher.LauncherUiState

@Composable
fun AppPickerDialog(
    uiState: LauncherUiState,
    onAppSelected: (LauncherApp) -> Unit,
    onDismiss: () -> Unit,
    onSearchQueryChanged: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("App auswählen") },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                OutlinedTextField(
                    value = uiState.appPickerSearchQuery,
                    onValueChange = onSearchQueryChanged,
                    label = { Text("Suchen...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(
                        uiState.filteredApps,
                        key = { it.componentName.flattenToString() }) { app ->
                        AppPickerItem(
                            app = app,
                            onAppClick = { onAppSelected(app) }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}


@Composable
private fun AppPickerItem(
    app: LauncherApp,
    onAppClick: () -> Unit
) {
    val context = LocalContext.current

    val icon by produceState<Bitmap?>(initialValue = null, key1 = app.componentName) {
        withContext(Dispatchers.IO) {
            val result = runCatching {
                val pm = context.packageManager
                val drawable = pm.getActivityIcon(app.componentName)
                drawableToBitmap(drawable)
            }
            value = result.getOrNull()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAppClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            if (icon != null) {
                Log.d("test", "$icon")
                Image(
                    bitmap = icon!!.asImageBitmap(),
                    contentDescription = app.label
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = app.label)
    }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
            return drawable.bitmap
        }
    }

    val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        createBitmap(1, 1)
    } else {
        createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
    }

    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}