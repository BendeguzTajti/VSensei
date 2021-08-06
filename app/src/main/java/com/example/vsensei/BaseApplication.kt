package com.example.vsensei

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.vsensei.di.appModules
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    private val sharedPreferences: SharedPreferences by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules(appModules)
        }
        val savedNightMode = sharedPreferences.getInt("CURRENT_NIGHT_MODE", -1)
        val currentNightMode = resources?.configuration?.uiMode?.and(
            Configuration.UI_MODE_NIGHT_MASK)
        if (savedNightMode != -1 && savedNightMode != currentNightMode) {
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
    }
}