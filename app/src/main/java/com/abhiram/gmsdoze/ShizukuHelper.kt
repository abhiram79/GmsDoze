package com.abhiram.gmsdoze

import android.util.Log
import rikka.shizuku.Shizuku

object ShizukuHelper {
    fun toggleApp(packageName: String, enable: Boolean) {
        val command = if (enable)
            "pm enable --user 0 $packageName"
        else
            "pm disable-user --user 0 $packageName"

        try {
            if (Shizuku.pingBinder()) {
                Log.d("Shizuku", "Shizuku is available. Executing command: $command")
                val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
                process.waitFor()
                Log.d("Shizuku", "Command executed successfully: $command")
            } else {
                Log.e("Shizuku", "Shizuku is not available")
            }
        } catch (e: Exception) {
            Log.e("Shizuku", "Shizuku command failed", e)
        }
    }
}