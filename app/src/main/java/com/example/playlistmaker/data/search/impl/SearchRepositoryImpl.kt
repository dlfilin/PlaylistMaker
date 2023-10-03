package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.LocalStorage
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localStorage: LocalStorage,
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(code = -1))
            }

            200 -> {
                val data = (response as TracksSearchResponse).results.map {
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
                }
                emit(Resource.Success(data))
            }

            else -> {
                emit(Resource.Error(code = response.resultCode))
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