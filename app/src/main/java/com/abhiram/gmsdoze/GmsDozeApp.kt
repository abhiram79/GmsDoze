package com.abhiram.gmsdoze

import android.app.Application

class GmsDozeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        lateinit var context: android.content.Context
    }
}