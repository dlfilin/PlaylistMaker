package com.example.playlistmaker.data.playlists

import android.net.Uri
import androidx.room.withTransaction
import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.data.db.entity.incrementTracksCount
import com.example.playlistmaker.data.storage.ImagesStorage
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val imagesStorage: ImagesStorage,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PlaylistsRepository {

    override suspend fun createNewPlaylist(playlist: Playlist): Long {

        var privatePath: Uri? = playlist.imageUri

        if (playlist.imageUri != null) {
            privatePath = withContext(dispatcher) {
                imagesStorage.saveImageToPrivateStorage(playlist.imageUri, ALBUM_NAME)
            }
        }

        return appDatabase.getPlaylistsDao().insertNewPlaylist(
            playlistDbConverter.map(
                playlist.copy(imageUri = privatePath)
            )
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        val result = appDatabase.getPlaylistsDao().getPlaylists().map { listOfEntities ->
            listOfEntities.map {
                playlistDbConverter.map(it)
            }
        }
        return result
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        val trackEntity = trackDbConverter.map(track)
        val playlistEntity = playlistDbConverter.map(playlist)
        var result = false

        appDatabase.withTransaction {

            if (appDatabase.getTracksDao().isTrackInPlaylist(
                    trackId = trackEntity.trackId, playlistId = playlistEntity.playlistId
                )
            ) {
                //не нужно добавлять
                result = false
            } else {
                appDatabase.getPlaylistsDao().insertCrossRef(
                    PlaylistTrackCrossRef(
                        playlistId = playlistEntity.playlistId,
                        trackId = trackEntity.trackId,
                        addedOnDate = System.currentTimeMillis()
                    )
                )
                appDatabase.getTracksDao().insertTrack(trackEntity)

                val pl = playlistEntity.incrementTracksCount()

                result = appDatabase.getPlaylistsDao().updatePlaylist(pl) > 0
            }
        }
        return result
    }


    companion object {
        private const val ALBUM_NAME = "playlists_images"
    }
}