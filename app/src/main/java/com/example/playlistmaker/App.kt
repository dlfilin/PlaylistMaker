package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val settingsInteractor = Creator.provideSettingsInteractor(applicationContext)

        setDarkTheme(settingsInteractor.getThemeSettings().isDarkTheme)
    }

    companion object {
        const val LOCAL_STORAGE = "LOCAL_STORAGE"
        const val CURRENT_TRACK = "current_track"

        fun setDarkTheme(darkThemeEnabled: Boolean) {

            AppCompatDelegate.setDefaultNightMode(
                if (darkThemeEnabled) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }
}
