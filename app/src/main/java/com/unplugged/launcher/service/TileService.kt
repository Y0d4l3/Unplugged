package com.unplugged.launcher.service

import android.app.PendingIntent
import android.content.Intent
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.unplugged.launcher.util.isMyAppDefaultLauncher

class TileService : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile?.let { tile ->
            tile.state = if (isMyAppDefaultLauncher(this)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            tile.updateTile()
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        qsTile?.let { tile ->
            tile.state = if (isMyAppDefaultLauncher(this)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pi = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        startActivityAndCollapse(pi)
    }
}
