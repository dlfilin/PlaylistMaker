package com.example.playlistmaker.data.history

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.dto.TracksHistoryResponse
import com.example.playlistmaker.data.storage.HistoryStorage
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.history.HistoryRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(
    private val historyStorage: HistoryStorage,
    private val appDatabase: AppDatabase,
    ) : HistoryRepository {

    override fun getTracksFromHistory(): Flow<Resource<List<Track>>> = flow {

        val response = historyStorage.getTracksFromHistory()

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(code = -1))
            }

            else -> {
                val favoriteTracks = appDatabase.getFavoritesDao().getTracksIds()

                val data = (response as TracksHistoryResponse).results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName ?: "",
                        artistName = it.artistName ?: "",
                        collectionName = it.collectionName ?: "",
                        releaseYear = it.releaseYear ?: "",
                        primaryGenreName = it.primaryGenreName ?: "",
                        country = it.country ?: "",
                        trackTimeMillis = it.trackTimeMillis ?: "",
                        artworkUrl100 = it.artworkUrl100 ?: "",
                        previewUrl = it.previewUrl ?: "",
                        isFavorite = favoriteTracks.contains(it.trackId)
                    )
                }
                emit(Resource.Success(data))
            }
        }
    }

    override suspend fun addTrackToHistory(track: Track) {
        val trackEntity = TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseYear = track.releaseYear,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = track.previewUrl,
            addedOnDate = 0 // для истории этот параметр не нужен
        )
        historyStorage.addTrackToHistory(trackEntity)
    }

    override suspend fun clearTracksHistory() {
        historyStorage.clearTracksHistory()
    }

}