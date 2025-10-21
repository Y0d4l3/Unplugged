package com.unplugged.launcher

import android.content.ComponentName
import android.content.pm.PackageManager
import com.unplugged.launcher.service.NotificationStateService

import com.unplugged.launcher.domain.launcher.LauncherScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.unplugged.launcher.ui.theme.UnpluggedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawableResource(android.R.color.transparent)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            UnpluggedTheme {
                LauncherScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toggleNotificationService(enable = true)
    }

    private fun toggleNotificationService(enable: Boolean) {
        val componentName = ComponentName(this, NotificationStateService::class.java)
        val newState = if (enable) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        packageManager.setComponentEnabledSetting(componentName, newState, PackageManager.DONT_KILL_APP)
    }
}
