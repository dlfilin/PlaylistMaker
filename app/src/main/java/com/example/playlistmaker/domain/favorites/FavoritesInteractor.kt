package com.example.playlistmaker.domain.favorites

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    fun getSavedFavorites(): Flow<List<Track>>

    suspend fun reverseFavoriteState(track: Track)

}