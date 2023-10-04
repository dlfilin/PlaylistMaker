package com.example.playlistmaker.data.favorites.impl

import com.example.playlistmaker.data.FavoritesStorage
import com.example.playlistmaker.domain.search.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(private val storage: FavoritesStorage) : FavoritesRepository {

    override fun addToFavorites(trackId: Int) {
        storage.addToFavorites(trackId)
    }

    override fun removeFromFavorites(trackId: Int) {
        storage.removeFromFavorites(trackId)
    }

    override fun changeFavorites(trackId: Int, remove: Boolean) {
        storage.changeFavorites(trackId, remove)
    }

    override fun getSavedFavorites(): Flow<Set<String>> = flow {
        emit(storage.getSavedFavorites())
    }

}