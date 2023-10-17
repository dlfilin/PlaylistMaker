package com.example.playlistmaker.presentation.playlist

import com.example.playlistmaker.domain.models.Playlist

sealed interface PlaylistScreenState {

    object Loading : PlaylistScreenState

    object EmptyList : PlaylistScreenState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistScreenState

    data class Error(val code: Int) : PlaylistScreenState

}