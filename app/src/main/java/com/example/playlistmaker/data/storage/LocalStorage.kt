package com.example.playlistmaker.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TrackDto
import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.google.gson.Gson

class LocalStorage(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    fun addToFavorites(trackId: Int) {
        changeFavorites(trackId = trackId, remove = false)
    }

    fun removeFromFavorites(trackId: Int) {
        changeFavorites(trackId = trackId, remove = true)
    }

    private fun changeFavorites(trackId: Int, remove: Boolean) {
        val mutableSet = getSavedFavorites().toMutableSet()
        val modified =
            if (remove) mutableSet.remove(trackId.toString()) else mutableSet.add(trackId.toString())
        if (modified) sharedPreferences.edit().putStringSet(KEY_FAVORITES, mutableSet).apply()
    }

    fun getSavedFavorites(): Set<String> {
        return sharedPreferences.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun addTrackToHistory(track: TrackDto) {

        val tracks = (getTracksFromHistory() as TracksSearchResponse).results.toMutableList()

        val wasRemoved = tracks.remove(track)
        if (!wasRemoved && tracks.size == MAX_HISTORY_SIZE) {
            tracks.removeLastOrNull()
        }
        tracks.add(0, element = track)

        val tracksJSON = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(KEY_TRACKS_HISTORY, tracksJSON)
            .apply()
    }

//    fun removeTrackFromHistory(track: Track) {}

    fun clearTracksHistory() {
        sharedPreferences.edit()
            .remove(KEY_TRACKS_HISTORY).apply()
    }

    fun getTracksFromHistory(): Response {

        val tracksJSON = sharedPreferences.getString(KEY_TRACKS_HISTORY, null)

        return if (tracksJSON != null) {
            TracksSearchResponse(gson.fromJson(tracksJSON, Array<TrackDto>::class.java).asList())
        } else {
            return TracksSearchResponse(emptyList())
        }
    }

    fun getThemeSettings(): ThemeSettings {
        val darkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        return ThemeSettings(darkTheme)
    }

    fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_THEME, settings.isDarkTheme)
            .apply()
    }

    private companion object {
        const val KEY_FAVORITES = "KEY_FAVORITES"
        const val KEY_TRACKS_HISTORY = "KEY_TRACKS_HISTORY"
        const val MAX_HISTORY_SIZE = 10
        const val KEY_DARK_THEME = "DARK_THEME"

    }

}