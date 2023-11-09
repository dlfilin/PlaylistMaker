package com.example.playlistmaker.data.storage

import com.example.playlistmaker.domain.settings.model.ThemeSettings

interface SettingsStorage {

    fun getThemeSettings(): ThemeSettings

    fun updateThemeSetting(settings: ThemeSettings)

}