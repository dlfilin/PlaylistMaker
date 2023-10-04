package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {

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

}