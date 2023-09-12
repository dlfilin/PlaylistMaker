package com.example.playlistmaker.data.settings.impl

import com.example.playlistmaker.data.search.storage.LocalStorage
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsRepositoryImpl(private val localStorage: LocalStorage) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return localStorage.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        localStorage.updateThemeSetting(settings)
    }
}