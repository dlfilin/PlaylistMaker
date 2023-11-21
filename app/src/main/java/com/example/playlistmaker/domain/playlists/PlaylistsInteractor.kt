package com.example.playlistmaker.domain.playlists

import android.net.Uri
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun createNewPlaylist(playlist: Playlist): Long

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistFlow(id: Long): Flow<Playlist>

    suspend fun getPlaylist(id: Long): Playlist

    suspend fun updatePlaylist(playlist: Playlist, newImageUri: Uri?): Int

    fun getPlaylistWithTracks(id: Long): Flow<Pair<Playlist, List<Track>>>

    suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Long): Boolean

    suspend fun isTrackInFavorites(trackId: Int): Boolean

}