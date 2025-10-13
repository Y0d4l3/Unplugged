package com.unplugged.launcher

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.unplugged.launcher.ui.LauncherScreen
import com.unplugged.launcher.ui.theme.UnpluggedTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LauncherApp(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

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
}

fun currentTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

fun currentDate(): String =
    SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.getDefault()).format(Date())
