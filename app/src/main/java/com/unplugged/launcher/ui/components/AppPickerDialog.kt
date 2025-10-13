package com.unplugged.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unplugged.launcher.data.LauncherApp
import com.unplugged.launcher.ui.util.toImageBitmap
import com.unplugged.launcher.ui.viewmodel.AppPickerViewModel

@Composable
fun AppPickerDialog(
    onDismiss: () -> Unit,
    onAppSelected: (LauncherApp) -> Unit,
    viewModel: AppPickerViewModel = viewModel()
) {
    val apps by viewModel.apps.collectAsState()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("App auswählen") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                if (apps.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(apps) { app ->
                            AppItem(app = app, onAppSelected = onAppSelected, context = context)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AppItem(
    app: LauncherApp,
    onAppSelected: (LauncherApp) -> Unit,
    context: android.content.Context
) {
    val appIconBitmap: ImageBitmap? by produceState(
        initialValue = null,
        key1 = app.componentName
    ) {
        val drawable = app.loadIcon(context)
        value = drawable?.toImageBitmap()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAppSelected(app) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (appIconBitmap != null) {
            Image(
                painter = BitmapPainter(appIconBitmap!!),
                contentDescription = app.label,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(app.label)
    }
}