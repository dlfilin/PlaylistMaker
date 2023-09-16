package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.settings.model.ThemeSettings

interface LocalStorage {

    fun addToFavorites(trackId: Int)

    fun removeFromFavorites(trackId: Int)

    fun changeFavorites(trackId: Int, remove: Boolean)

    fun getSavedFavorites(): Set<String>

    fun addTrackToHistory(track: TrackDto)

    fun clearTracksHistory()

    fun getTracksFromHistory(): Response

    fun getThemeSettings(): ThemeSettings

    fun updateThemeSetting(settings: ThemeSettings)

}