package com.example.playlistmaker.data.favorites.impl

import com.example.playlistmaker.data.FavoritesStorage
import com.example.playlistmaker.domain.search.FavoritesRepository

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

    override fun getSavedFavorites(): Set<String> {
        return storage.getSavedFavorites()
    }

}