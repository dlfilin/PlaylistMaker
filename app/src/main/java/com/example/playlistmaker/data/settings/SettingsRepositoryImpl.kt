package com.example.playlistmaker.data.settings

import com.example.playlistmaker.data.storage.SettingsStorage
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsRepositoryImpl(private val settingsStorage: SettingsStorage) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return settingsStorage.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsStorage.updateThemeSetting(settings)
    }
}