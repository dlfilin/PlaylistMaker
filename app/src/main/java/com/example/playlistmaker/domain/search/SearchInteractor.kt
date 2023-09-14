package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.models.Track

interface SearchInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun addTrackToFavorites(track: Track)

    fun removeTrackFromFavorites(track: Track)

    fun addTrackToHistory(track: Track)

    fun removeTrackFromHistory(track: Track)

    fun clearTracksHistory()

    fun getTracksFromHistory(consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorCode: Int?)
    }

}