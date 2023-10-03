package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, Int?>> {

        return repository.searchTracks(expression).map { result ->

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

    override fun addTrackToFavorites(track: Track) {
        repository.addTrackToFavorites(track)
    }

    override fun removeTrackFromFavorites(track: Track) {
        repository.removeTrackFromFavorites(track)
    }

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun removeTrackFromHistory(track: Track) {
        repository.removeTrackFromHistory(track)
    }

    override fun clearTracksHistory() {
        repository.clearTracksHistory()
    }

    override fun getTracksFromHistory(consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.getTracksFromHistory()) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.code)
                }
            }
        }
    }
}