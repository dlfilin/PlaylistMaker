package com.example.playlistmaker.domain.favorites.impl

import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {

        override fun getSavedFavorites(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override suspend fun reverseFavoriteState(track: Track) {
        repository.reverseFavoriteState(track)
    }


}