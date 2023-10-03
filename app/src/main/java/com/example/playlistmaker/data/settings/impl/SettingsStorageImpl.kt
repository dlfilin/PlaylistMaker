package com.example.playlistmaker.data.settings.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.settings.SettingsStorage
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsStorageImpl(private val sharedPreferences: SharedPreferences) : SettingsStorage {

    override fun getThemeSettings(): ThemeSettings {
        val darkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        return ThemeSettings(darkTheme)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, settings.isDarkTheme).apply()
    }

    private companion object {
        const val KEY_DARK_THEME = "DARK_THEME"
    }
}