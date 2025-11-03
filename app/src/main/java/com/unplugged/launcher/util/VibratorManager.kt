package com.unplugged.launcher.util

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat

class VibratorManager(context: Context) {

    private val vibrator: Vibrator? = ContextCompat.getSystemService(context, Vibrator::class.java)

    fun vibrate() {
        if (vibrator?.hasVibrator() == true) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
