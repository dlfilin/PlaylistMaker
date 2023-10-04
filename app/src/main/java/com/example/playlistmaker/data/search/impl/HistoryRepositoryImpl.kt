package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.HistoryStorage
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.HistoryRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(
    private val historyStorage: HistoryStorage,
) : HistoryRepository {

    override fun getTracksFromHistory(): Flow<Resource<List<Track>>> = flow {

        val response = historyStorage.getTracksFromHistory()

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(code = -1))
            }

            else -> {
                val data = (response as TracksSearchResponse).results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        previewUrl = it.previewUrl,
                    )
                }
                emit(Resource.Success(data))
            }
        }
    }

    override suspend fun addTrackToHistory(track: Track) {
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

    override suspend fun clearTracksHistory() {
        historyStorage.clearTracksHistory()
    }

}