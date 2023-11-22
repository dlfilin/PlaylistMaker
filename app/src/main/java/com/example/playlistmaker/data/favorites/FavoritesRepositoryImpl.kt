package com.example.playlistmaker.data.favorites

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoritesRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.getTracksDao().getFavoriteTracksFlow().map { listOfEntities ->
            listOfEntities.map {
                trackDbConverter.map(it)
            }
        }
    }

    override suspend fun reverseFavoriteState(track: Track) {
        val trackEntity = trackDbConverter.map(track)
            .copy(isFavorite = !track.isFavorite, favLastUpdate = System.currentTimeMillis())

        //если трек есть хоть в одном плейлисте, значит запись в таблице есть - обновляем isFavorite
        if (appDatabase.getTracksDao().isTrackInAnyPlaylist(track.trackId)) {
            appDatabase.getTracksDao().updateTrack(trackEntity)
        } else {
            //если нет в плейлисте, то проверяем, создавать или удалять
            if (track.isFavorite) {
                appDatabase.getTracksDao().deleteTrack(track.trackId)
            } else {
                appDatabase.getTracksDao().insertTrack(trackEntity)
            }
        }
    }

}