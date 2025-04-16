package com.abhiram.gmsdoze.util

import java.io.DataOutputStream

fun runRootCommand(command: String): Boolean {
    return try {
        val process = Runtime.getRuntime().exec("su")
        val outputStream = DataOutputStream(process.outputStream)
        outputStream.writeBytes("$command\n")
        outputStream.writeBytes("exit\n")
        outputStream.flush()
        process.waitFor() == 0
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}