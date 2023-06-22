package com.example.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val KEY_SHARED_PREFS = "playmaker_shared_prefs"
const val KEY_DARK_THEME = "dark_theme"
const val CURRENT_TRACK = "current_track"

class App : Application() {

    var isDarkThemeOn = false

    override fun onCreate() {
        super.onCreate()

        isDarkThemeOn = getSharedPreferences(KEY_SHARED_PREFS, MODE_PRIVATE)
            .getBoolean(KEY_DARK_THEME, false)

        switchTheme(isDarkThemeOn)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        isDarkThemeOn = darkThemeEnabled

        getSharedPreferences(KEY_SHARED_PREFS, MODE_PRIVATE).edit()
            .putBoolean(KEY_DARK_THEME, isDarkThemeOn)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
