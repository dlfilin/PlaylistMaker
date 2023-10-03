package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackDto

interface HistoryStorage {

    suspend fun getTracksFromHistory(): Response

    suspend fun addTrackToHistory(track: TrackDto)

    suspend fun clearTracksHistory()

}