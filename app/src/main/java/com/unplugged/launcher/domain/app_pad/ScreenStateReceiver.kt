package com.unplugged.launcher.domain.app_pad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenStateReceiver(
    private val onScreenOn: () -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_ON) {
            onScreenOn()
        }
    }
}