package com.example.playlistmaker.domain.search

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun addToFavorites(trackId: Int)

    fun removeFromFavorites(trackId: Int)

    fun changeFavorites(trackId: Int, remove: Boolean)

    fun getSavedFavorites(): Flow<Set<String>>

}