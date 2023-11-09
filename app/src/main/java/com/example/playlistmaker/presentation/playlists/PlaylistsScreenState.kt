package com.example.playlistmaker.presentation.playlists

import com.example.playlistmaker.domain.models.Playlist

sealed interface PlaylistsScreenState {

    object Loading : PlaylistsScreenState

    object EmptyList : PlaylistsScreenState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsScreenState

    data class Error(val code: Int) : PlaylistsScreenState

}