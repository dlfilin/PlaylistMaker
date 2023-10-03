package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, Int?>>

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