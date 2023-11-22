package com.example.playlistmaker.presentation.playlist

import com.example.playlistmaker.domain.models.Playlist

sealed interface PlaylistScreenState {

    object Loading : PlaylistScreenState
    data class Content(val playlist: Playlist) : PlaylistScreenState
    object Error : PlaylistScreenState
}