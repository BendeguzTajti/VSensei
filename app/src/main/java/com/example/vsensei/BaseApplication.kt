package com.example.vsensei

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.vsensei.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules(appModules)
        }
    }
}