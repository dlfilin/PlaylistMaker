package com.example.playlistmaker.domain.playlists

import android.net.Uri
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun createNewPlaylist(playlist: Playlist): Long

    fun getPlaylistsFlow(): Flow<List<Playlist>>

    fun getPlaylistFlow(id: Long): Flow<Playlist>

    suspend fun getPlaylist(id: Long): Playlist

    suspend fun updatePlaylist(playlist: Playlist, newImageUri: Uri?): Int

    fun getPlaylistWithTracksFlow(id: Long): Flow<Pair<Playlist, List<Track>>>

    fun getTracksFromPlaylistSortedFlow(id: Long): Flow<List<Track>>

    suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Long): Boolean

    suspend fun isTrackInFavorites(trackId: Int): Boolean

}