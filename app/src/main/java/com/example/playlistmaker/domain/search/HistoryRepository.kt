package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun getTracksFromHistory(): Flow<Resource<List<Track>>>

    suspend fun addTrackToHistory(track: Track)

    suspend fun clearTracksHistory()

}