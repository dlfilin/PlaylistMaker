package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.dto.TrackDto
import com.example.playlistmaker.data.search.dto.TracksSearchRequest
import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import com.example.playlistmaker.data.search.storage.LocalStorage
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.models.Resource

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: LocalStorage,
) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(code = -1)
            }

            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
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

            else -> {
                Resource.Error(code = response.resultCode)
            }
        }
    }

    override fun addTrackToFavorites(track: Track) {
        localStorage.addToFavorites(track.trackId)
    }

    override fun removeTrackFromFavorites(track: Track) {
        localStorage.removeFromFavorites(track.trackId)
    }

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
        localStorage.addTrackToHistory(trackDto)
    }

    override fun removeTrackFromHistory(track: Track) {
//        localStorage.removeTrackFromHistory(track)
    }

    override fun clearTracksHistory() {
        localStorage.clearTracksHistory()
    }

    override fun getTracksFromHistory(): Resource<List<Track>> {

        val response = localStorage.getTracksFromHistory()

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