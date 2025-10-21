package com.unplugged.launcher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unplugged.launcher.data.model.LauncherApp
import com.unplugged.launcher.domain.home_screen.HomeScreenUiState

@Composable
fun AppPickerDialog(
    uiState: HomeScreenUiState,
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAppClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(
            componentName = app.componentName,
            contentDescription = app.label,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))
        Text(text = app.label)
    }
}