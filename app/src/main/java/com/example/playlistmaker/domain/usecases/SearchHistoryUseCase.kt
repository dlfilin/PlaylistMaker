package com.example.playlistmaker.domain.usecases

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson


private const val MAX_HISTORY_SIZE = 10
const val KEY_TRACKS_HISTORY = "key_tracks_history"

class SearchHistoryUseCase(private val sharedPrefs: SharedPreferences) {

    fun addTrack(track: Track): List<Track> {

        val tracks = getTracksFromHistory().toMutableList()

        val index = tracks.indexOf(track)
        if (index != -1) {
            tracks.removeAt(index)
        } else {
            if (tracks.size == MAX_HISTORY_SIZE)
                tracks.removeLastOrNull()
        }
        tracks.add(0, element = track)

        val tracksJSON = Gson().toJson(tracks)
        sharedPrefs.edit().putString(KEY_TRACKS_HISTORY, tracksJSON).apply()

        return tracks
    }

    fun clearHistory() {
        sharedPrefs.edit().remove(KEY_TRACKS_HISTORY).apply()
    }

    fun getTracksFromHistory(): Array<Track> {

        val tracksJSON = sharedPrefs.getString(KEY_TRACKS_HISTORY, null)

        return if (tracksJSON != null ) Gson().fromJson(tracksJSON,Array<Track>::class.java)
        else return emptyArray()

    }
}