package com.example.playlistmaker.domain.playlists.impl

import android.net.Uri
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {

    override suspend fun createNewPlaylist(playlist: Playlist): Long {
        return repository.createNewPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun getPlaylistFlow(id: Long): Flow<Playlist> {
        return repository.getPlaylistFlow(id)
    }

    override suspend fun getPlaylist(id: Long): Playlist {
        return repository.getPlaylist(id)
    }

    override suspend fun updatePlaylist(playlist: Playlist, newImageUri: Uri?): Int {
        return repository.updatePlaylist(playlist, newImageUri)
    }

    override fun getPlaylistWithTracks(id: Long): Flow<Pair<Playlist, List<Track>>> {
        return repository.getPlaylistWithTracks(id)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long): Boolean {
        return repository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Long): Boolean {
        return repository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun isTrackInFavorites(trackId: Int): Boolean {
        return repository.isTrackInFavorites(trackId)
    }


}