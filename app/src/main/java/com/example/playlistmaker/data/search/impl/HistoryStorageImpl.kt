package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.search.HistoryStorage
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryStorageImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : HistoryStorage {

    override suspend fun getTracksFromHistory(): Response {

        return try {
            withContext(dispatcher) {
                val tracksJSON = sharedPreferences.getString(KEY_TRACKS_HISTORY, null)

                if (tracksJSON != null) {
                    TracksSearchResponse(
                        gson.fromJson(tracksJSON, Array<TrackDto>::class.java).asList()
                    )
                } else {
                    TracksSearchResponse(emptyList())
                }
            }
        } catch (e: Throwable) {
            Response().apply { resultCode = -1 }
        }
    }

    override suspend fun addTrackToHistory(track: TrackDto) {

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

    override suspend fun clearTracksHistory() {
        sharedPreferences.edit()
            .remove(KEY_TRACKS_HISTORY).apply()
    }

    private companion object {
        const val KEY_TRACKS_HISTORY = "KEY_TRACKS_HISTORY"
        const val MAX_HISTORY_SIZE = 10
    }

}