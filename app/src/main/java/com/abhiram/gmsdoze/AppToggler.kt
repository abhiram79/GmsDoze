package com.abhiram.gmsdoze

import android.util.Log
import rikka.shizuku.Shizuku
import android.content.pm.PackageManager

object AppToggler {
    fun toggleAppWithRoot(packageName: String, enable: Boolean) {
        val cmd = if (enable)
            "pm enable --user 0 $packageName"
        else
            "pm disable-user --user 0 $packageName"
        
        try {
            Log.d("GmsDoze", "Executing root command: $cmd")
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
            process.waitFor()
            Log.d("GmsDoze", "Command executed: $cmd")
        } catch (e: Exception) {
            Log.e("GmsDoze", "Root command failed", e)
        }
    }

    fun isAppEnabled(packageName: String): Boolean {
        return try {
            val pm = GmsDozeApp.context.packageManager
            val state = pm.getApplicationEnabledSetting(packageName)
            state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
        } catch (e: Exception) {
            Log.e("GmsDoze", "Error checking app status", e)
            false
        }
    }
}