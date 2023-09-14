package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.Resource

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>

    fun addTrackToFavorites(track: Track)

    fun removeTrackFromFavorites(track: Track)

    fun addTrackToHistory(track: Track)

    fun removeTrackFromHistory(track: Track)

    fun clearTracksHistory()

    fun getTracksFromHistory(): Resource<List<Track>>

}