package com.abhiram.gmsdoze

import android.content.pm.PackageManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import java.io.DataOutputStream

class GmsTileService : TileService() {

    private val gmsPackage = "com.google.android.gms"

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()

        val isEnabled = isPackageEnabled(gmsPackage)

        if (isEnabled) {
            disablePackage(gmsPackage)
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.label = "GMS Disabled"
            Toast.makeText(this, "Disabled Google Play Services", Toast.LENGTH_SHORT).show()
        } else {
            enablePackage(gmsPackage)
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.label = "GMS Enabled"
            Toast.makeText(this, "Enabled Google Play Services", Toast.LENGTH_SHORT).show()
        }

        qsTile.updateTile()
    }

    private fun updateTileState() {
        val isEnabled = isPackageEnabled(gmsPackage)

        qsTile.state = if (isEnabled) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
        qsTile.label = if (isEnabled) "GMS Enabled" else "GMS Disabled"
        qsTile.updateTile()
    }

    private fun isPackageEnabled(packageName: String): Boolean {
        return try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            appInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun disablePackage(pkg: String) {
        runCommand("pm disable-user --user 0 $pkg")
    }

    private fun enablePackage(pkg: String) {
        runCommand("pm enable --user 0 $pkg")
    }

    private fun runCommand(command: String) {
        try {
            val process = Runtime.getRuntime().exec("su")
            DataOutputStream(process.outputStream).apply {
                writeBytes("$command\n")
                writeBytes("exit\n")
                flush()
            }
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}