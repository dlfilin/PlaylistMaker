package com.example.playlistmaker.data.history

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.TracksHistoryResponse
import com.example.playlistmaker.data.storage.HistoryStorage
import com.example.playlistmaker.domain.history.HistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val historyStorage: HistoryStorage,
    private val trackDbConverter: TrackDbConverter,
    ) : HistoryRepository {

    override fun getTracksFromHistory(): Flow<Resource<List<Track>>> = flow {

        val response = historyStorage.getTracksFromHistory()

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(code = -1))
            }
            else -> {
                val favoriteTracks = appDatabase.getTracksDao().getFavoriteTracksIds()

                val data = (response as TracksHistoryResponse).results.map {
                    trackDbConverter.map(it.copy(isFavorite = favoriteTracks.contains(it.trackId)))
                }
                emit(Resource.Success(data))
            }
        }
    }

    override suspend fun addTrackToHistory(track: Track) {
        historyStorage.addTrackToHistory(trackDbConverter.map(track))
    }

    override suspend fun clearTracksHistory() {
        historyStorage.clearTracksHistory()
    }

}