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

    private val playlistsDao
        get() = appDatabase.getPlaylistsDao()
    private val tracksDao
        get() = appDatabase.getTracksDao()

    override suspend fun createNewPlaylist(playlist: Playlist): Long {

        var privatePath: Uri? = playlist.imageUri

        if (playlist.imageUri != null) {
            privatePath = withContext(dispatcher) {
                imagesStorage.saveImageToPrivateStorage(playlist.imageUri, ALBUM_NAME)
            }
        }

        return playlistsDao.insertNewPlaylist(
            playlistDbConverter.map(
                playlist.copy(imageUri = privatePath)
            )
        )
    }

    override suspend fun updatePlaylist(playlist: Playlist, newImageUri: Uri?): Int {

        var privatePath: Uri? = playlist.imageUri

        if (newImageUri != null) {
            privatePath = withContext(dispatcher) {
                if (playlist.imageUri != null) imagesStorage.deleteImageFromPrivateStorage(
                    playlist.imageUri,
                    ALBUM_NAME
                )
                imagesStorage.saveImageToPrivateStorage(newImageUri, ALBUM_NAME)
            }
        }
        return playlistsDao.updatePlaylist(
            playlistDbConverter.map(playlist.copy(imageUri = privatePath))
        )
    }

    override fun getPlaylistsFlow(): Flow<List<Playlist>> {
        val result = playlistsDao.getPlaylistsFlow().map { listOfEntities ->
//            listOfEntities.map {
//                playlistDbConverter.map(it)
//            }
            playlistDbConverter.map(listOfEntities)
        }
        return result
    }

    override fun getPlaylistFlow(id: Long): Flow<Playlist> {
        return playlistsDao.getPlaylistFlow(id).map {
            playlistDbConverter.map(it)
        }
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        return playlistDbConverter.map(
            playlistsDao.getPlaylist(id)
        )
    }

    override fun getPlaylistWithTracksFlow(id: Long): Flow<Pair<Playlist, List<Track>>> {
        return playlistsDao.getPlaylistWithTracksFlow(id).map {
            Pair(
                playlistDbConverter.map(it.playlist), trackDbConverter.map(it.tracks)
            )
        }
    }

    override fun getTracksFromPlaylistSortedFlow(id: Long): Flow<List<Track>> {
        return tracksDao.getTracksFromPlaylistSortedFlow(id).map {
            trackDbConverter.map(it)
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean {
        val trackEntity = trackDbConverter.map(track)
        var result = false

        appDatabase.withTransaction {

            if (tracksDao.isTrackInPlaylist(
                    trackId = trackEntity.trackId, playlistId = playlistId
                )
            ) {
                //не нужно добавлять
                result = false
            } else {
                if (!tracksDao.isTrackAdded(trackId = trackEntity.trackId)) {
                    tracksDao.insertTrack(trackEntity)
                }

                val crossRef = PlaylistTrackCrossRef(
                    playlistId = playlistId,
                    trackId = trackEntity.trackId,
                    addedOnDate = System.currentTimeMillis()
                )

                playlistsDao.insertPlaylistTrackCrossRef(crossRef)

                playlistsDao.incPlaylistTrackCount(playlistId)

                result = true
            }
        }
        return result
    }

    override suspend fun deletePlaylist(playlistId: Long) {

        appDatabase.withTransaction {

            val trackIds = playlistsDao.getPlaylistTracksIds(playlistId)

            playlistsDao.deletePlaylist(playlistId)

            trackIds.forEach { trackId ->
                if (!tracksDao
                        .isTrackInAnyPlaylist(trackId) && !tracksDao
                        .isTrackInFavorites(trackId)
                ) {
                    tracksDao.deleteTrack(trackId)
                }
            }
        }
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Long): Boolean {
        var result = false

        appDatabase.withTransaction {

            result = playlistsDao.deletePlaylistTrackCrossRef(
                playlistId = playlistId, trackId = trackId
            ) > 0

            if (!tracksDao
                    .isTrackInFavorites(trackId = trackId) && !tracksDao
                    .isTrackInAnyPlaylist(trackId)
            ) {
                tracksDao.deleteTrack(trackId)
            }

            playlistsDao.decPlaylistTrackCount(playlistId)
        }

        return result
    }

    override suspend fun isTrackInFavorites(trackId: Int): Boolean {
        return tracksDao.isTrackInFavorites(trackId)
    }

    companion object {
        private const val ALBUM_NAME = "playlists_images"
    }
}