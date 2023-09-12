package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.TracksRepository
import com.example.playlistmaker.domain.models.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.code)
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

    override fun getTracksFromHistory(consumer: TracksInteractor.TracksConsumer) {
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