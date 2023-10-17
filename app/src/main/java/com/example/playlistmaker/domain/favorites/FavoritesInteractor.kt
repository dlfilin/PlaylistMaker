package com.example.playlistmaker.domain.favorites

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun addToFavorites(track: Track)

    suspend fun removeFromFavorites(track: Track)

    fun getSavedFavorites(): Flow<List<Track>>

}