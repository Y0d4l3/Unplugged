package com.unplugged.launcher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager

class BatterySaverReceiver(
    private val onBatterySaverChanged: (Boolean) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
            val pm = context.getSystemService(PowerManager::class.java)
            val isEnabled = pm.isPowerSaveMode
            onBatterySaverChanged(isEnabled)
        }
    }
}