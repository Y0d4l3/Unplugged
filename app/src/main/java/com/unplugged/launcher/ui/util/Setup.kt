package com.unplugged.launcher.ui.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

fun checkAndPromptBatterySaver(context: Context) {
    fun isExtremeBatterySaverOn(): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                "low_power_mode_suggestion_state",
                0
            ) == 3
        } catch (_: Exception) {
            false
        }
    }

    if (!isExtremeBatterySaverOn()) {
        Toast.makeText(
            context,
            "Bitte aktiviere den Extreme Battery Saver.",
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } else {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(homeIntent)
    }
}

