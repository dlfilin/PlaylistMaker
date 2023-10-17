package com.example.playlistmaker.domain.favorites.impl

import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {

    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track) {
        repository.removeFromFavorites(track)
    }

    override fun getSavedFavorites(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }


}