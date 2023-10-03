package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.HistoryStorage
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.HistoryRepository
import com.example.playlistmaker.util.Resource

class HistoryRepositoryImpl(
    private val historyStorage: HistoryStorage,
) : HistoryRepository {

    override fun addTrackToHistory(track: Track) {
        val trackDto = TrackDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = track.previewUrl,
        )
        historyStorage.addTrackToHistory(trackDto)
    }

    override fun clearTracksHistory() {
        historyStorage.clearTracksHistory()
    }

    override fun getTracksFromHistory(): Resource<List<Track>> {

        val response = historyStorage.getTracksFromHistory()

        return Resource.Success((response as TracksSearchResponse).results.map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.previewUrl,
            )
        })
    }
}