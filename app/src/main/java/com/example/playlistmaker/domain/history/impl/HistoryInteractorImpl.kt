package com.example.playlistmaker.domain.history.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.history.HistoryRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {

    override fun getTracksFromHistory(): Flow<Pair<List<Track>?, Int?>> {

        return repository.getTracksFromHistory().map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.code)
                }
            }
        }
    }

    override suspend fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override suspend fun clearTracksHistory() {
        repository.clearTracksHistory()
    }

}