package com.unplugged.launcher.tiles

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class LauncherSwitchTileService : TileService() {

    override fun onStartListening() {
        updateTileLabel()
    }

    override fun onClick() {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pi = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        startActivityAndCollapse(pi)
    }


    private fun updateTileLabel() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }

        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val currentLauncherPackage = resolveInfo?.activityInfo?.packageName

        val myPackage = applicationContext.packageName

        if (currentLauncherPackage == myPackage) {
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = "Unplugged aktiv"
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = "Launcher wechseln"
        }
        qsTile.updateTile()
    }
}
