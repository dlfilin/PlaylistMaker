package com.example.playlistmaker.data.storage.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksHistoryResponse
import com.example.playlistmaker.data.storage.HistoryStorage
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
                    TracksHistoryResponse(
                        gson.fromJson(tracksJSON, Array<TrackEntity>::class.java).asList()
                    )
                } else {
                    TracksHistoryResponse(emptyList())
                }
            }
        } catch (e: Throwable) {
            Response().apply { resultCode = -1 }
        }
    }

    override suspend fun addTrackToHistory(track: TrackEntity) {

        val tracks = (getTracksFromHistory() as TracksHistoryResponse).results.toMutableList()

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