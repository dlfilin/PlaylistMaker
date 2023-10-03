package com.example.playlistmaker.data.search

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackDto

interface HistoryStorage {

    fun getTracksFromHistory(): Response

    fun addTrackToHistory(track: TrackDto)

    fun clearTracksHistory()

}