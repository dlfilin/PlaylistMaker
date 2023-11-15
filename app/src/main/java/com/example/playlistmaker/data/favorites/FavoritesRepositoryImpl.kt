package com.example.playlistmaker.data.favorites

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoritesRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.getTracksDao().getFavoriteTracks().map { listOfEntities ->
            listOfEntities.map {
                trackDbConverter.map(it)
            }
        }
    }

    override suspend fun reverseFavoriteState(track: Track) {
        val trackEntity = trackDbConverter.map(track)
            .copy(isFavorite = !track.isFavorite, favLastUpdate = System.currentTimeMillis())

        if (appDatabase.getTracksDao().updateTrack(trackEntity) == 0) {
            appDatabase.getTracksDao().insertTrack(trackEntity)
        }
    }

}