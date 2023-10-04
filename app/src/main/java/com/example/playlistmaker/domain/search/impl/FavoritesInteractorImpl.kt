package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.FavoritesInteractor
import com.example.playlistmaker.domain.search.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {

    override fun addToFavorites(trackId: Int) {
        repository.addToFavorites(trackId)
    }

    override fun removeFromFavorites(trackId: Int) {
        repository.removeFromFavorites(trackId)
    }

    override fun changeFavorites(trackId: Int, remove: Boolean) {
        repository.changeFavorites(trackId, remove)
    }

    override fun getSavedFavorites(): Flow<Set<String>> {
        return repository.getSavedFavorites()
    }

}