package com.example.playlistmaker.domain.favorites

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun reverseFavoriteState(track: Track)

}