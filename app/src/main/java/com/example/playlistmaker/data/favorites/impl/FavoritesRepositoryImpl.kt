package com.example.playlistmaker.data.favorites.impl

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
    ) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        appDatabase.favoritesDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun removeFromFavorites(track: Track) {
        appDatabase.favoritesDao().deleteTrack(trackDbConverter.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoritesDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track).copy(isFavorite = true) }
    }

}