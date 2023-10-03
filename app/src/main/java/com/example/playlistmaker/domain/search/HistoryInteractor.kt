package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {

    fun addTrackToHistory(track: Track)

    fun clearTracksHistory()

    fun getTracksFromHistory(consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorCode: Int?)
    }

}