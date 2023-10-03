package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.HistoryInteractor
import com.example.playlistmaker.domain.search.HistoryRepository
import com.example.playlistmaker.util.Resource
import java.util.concurrent.Executors

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun clearTracksHistory() {
        repository.clearTracksHistory()
    }

    override fun getTracksFromHistory(consumer: HistoryInteractor.TracksConsumer) {
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