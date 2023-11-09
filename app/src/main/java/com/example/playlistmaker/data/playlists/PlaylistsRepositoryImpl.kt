package com.example.playlistmaker.data.playlists

import android.net.Uri
import androidx.room.withTransaction
import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.PlaylistTrackDbConverter
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
    private val trackDbConverter: PlaylistTrackDbConverter,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PlaylistsRepository {

    override suspend fun createNewPlaylist(playlist: Playlist): Long {

        var privatePath: Uri? = playlist.imageUri

        if (playlist.imageUri != null) {
            privatePath = withContext(dispatcher) {
                imagesStorage.saveImageToPrivateStorage(playlist.imageUri, ALBUM_NAME)
            }
        }

        return appDatabase.getPlaylistDao().insertNewPlaylist(
            playlistDbConverter.map(
                playlist.copy(imageUri = privatePath)
            )
        )
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.getPlaylistDao().getPlaylists().map { listOfEntities ->
            listOfEntities.map {
                playlistDbConverter.map(it)
            }
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {

        val trackEntity = trackDbConverter.map(track)
        val playlistEntity = playlistDbConverter.map(playlist)
        var result = true

        appDatabase.withTransaction {

            if (appDatabase.getPlaylistTracksDao().isTrackInPlaylist(
                    trackId = trackEntity.trackId, playlistId = playlistEntity.playlistId
                )
            ) {
                //не нужно добавлять
                result = false
            } else {
                appDatabase.getPlaylistTracksDao().insertCrossRef(
                    PlaylistTrackCrossRef(
                        playlistId = playlistEntity.playlistId,
                        trackId = trackEntity.trackId,
                        addedOnDate = System.currentTimeMillis()
                    )
                )

                appDatabase.getPlaylistTracksDao().insertTrack(trackEntity)

                appDatabase.getPlaylistDao().updatePlaylist(playlistEntity.incrementTracksCount())

            }
        }

        return result
    }


    companion object {
        private const val ALBUM_NAME = "playlists_images"
    }
}