package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

interface HistoryRepository {

    fun addTrackToHistory(track: Track)

    fun clearTracksHistory()

    fun getTracksFromHistory(): Resource<List<Track>>

}