package com.example.playlistmaker.data.playlists

import android.net.Uri
import androidx.room.withTransaction
import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistTrackCrossRef
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

    override suspend fun updatePlaylist(playlist: Playlist, newImageUri: Uri?): Int {

        var privatePath: Uri? = playlist.imageUri

        if (newImageUri != null) {
            privatePath = withContext(dispatcher) {
                if (playlist.imageUri != null)
                    imagesStorage.deleteImageFromPrivateStorage(playlist.imageUri, ALBUM_NAME)
                imagesStorage.saveImageToPrivateStorage(newImageUri, ALBUM_NAME)
            }
        }
        return appDatabase.getPlaylistsDao().updatePlaylist(
            playlistDbConverter.map(playlist.copy(imageUri = privatePath))
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

    override fun getPlaylistFlow(id: Long): Flow<Playlist> {
        return appDatabase.getPlaylistsDao().getPlaylistFlow(id).map {
            playlistDbConverter.map(it)
        }
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        return playlistDbConverter.map(
            appDatabase.getPlaylistsDao().getPlaylist(id)
        )
    }

    override fun getPlaylistWithTracks(id: Long): Flow<Pair<Playlist, List<Track>>> {
        return appDatabase.getPlaylistsDao().getPlaylistWithTracks(id).map {
            Pair(
                playlistDbConverter.map(it.playlist), trackDbConverter.map(it.tracks)
            )
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean {
        val trackEntity = trackDbConverter.map(track)
        var result = false

        appDatabase.withTransaction {

            if (appDatabase.getTracksDao().isTrackInPlaylist(
                    trackId = trackEntity.trackId, playlistId = playlistId
                )
            ) {
                //не нужно добавлять
                result = false
            } else {
                if (!appDatabase.getTracksDao().wasTrackAdded(trackId = trackEntity.trackId)) {
                    appDatabase.getTracksDao().insertTrack(trackEntity)
                }

                val crossRef = PlaylistTrackCrossRef(
                    playlistId = playlistId,
                    trackId = trackEntity.trackId,
                    addedOnDate = System.currentTimeMillis()
                )

                appDatabase.getPlaylistsDao().insertPlaylistTrackCrossRef(crossRef)

                appDatabase.getPlaylistsDao().incPlaylistTrackCount(playlistId)

                result = true
            }
        }
        return result
    }

    override suspend fun deletePlaylist(playlistId: Long) {

        appDatabase.withTransaction {

            val trackIds = appDatabase.getPlaylistsDao().getPlaylistTracksIds(playlistId)

            appDatabase.getPlaylistsDao().deletePlaylist(playlistId)

            trackIds.forEach { trackId ->
                if (!appDatabase.getTracksDao()
                        .isTrackInAnyPlaylist(trackId) && !appDatabase.getTracksDao()
                        .isTrackInFavorites(trackId)
                ) {
                    appDatabase.getTracksDao().deleteTrack(trackId)
                }
            }
        }
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Long): Boolean {
        var result = false

        appDatabase.withTransaction {

            result = appDatabase.getPlaylistsDao().deletePlaylistTrackCrossRef(
                playlistId = playlistId, trackId = trackId
            ) > 0

            if (!appDatabase.getTracksDao()
                    .isTrackInFavorites(trackId = trackId) && !appDatabase.getTracksDao()
                    .isTrackInAnyPlaylist(trackId)
            ) {
                appDatabase.getTracksDao().deleteTrack(trackId)
            }

            appDatabase.getPlaylistsDao().decPlaylistTrackCount(playlistId)
        }

        return result
    }

    override suspend fun isTrackInFavorites(trackId: Int): Boolean {
        return appDatabase.getTracksDao().isTrackInFavorites(trackId)
    }

    companion object {
        private const val ALBUM_NAME = "playlists_images"
    }
}