package com.example.playlistmaker.domain.history

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface HistoryInteractor {

    suspend fun addTrackToHistory(track: Track)

    suspend fun clearTracksHistory()

    fun getTracksFromHistory(): Flow<Pair<List<Track>?, Int?>>

}