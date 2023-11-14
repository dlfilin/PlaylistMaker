package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.domain.models.Playlist

sealed interface PlayerBottomSheetState {

    object Empty : PlayerBottomSheetState

    data class Content(val playlists: List<Playlist>) : PlayerBottomSheetState

}
