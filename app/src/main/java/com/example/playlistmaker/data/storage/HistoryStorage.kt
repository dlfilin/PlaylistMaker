package com.example.playlistmaker.data.storage

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.dto.Response

interface HistoryStorage {

    suspend fun getTracksFromHistory(): Response

    suspend fun addTrackToHistory(track: TrackEntity)

    suspend fun clearTracksHistory()

}