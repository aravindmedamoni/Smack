package com.example.smack.controllers

import android.app.Application
import com.example.smack.utils.SharedPrefs

class App : Application() {

    companion object{
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
    }
}