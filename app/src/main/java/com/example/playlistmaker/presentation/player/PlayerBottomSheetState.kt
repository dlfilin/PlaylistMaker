package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.domain.models.Playlist

sealed interface PlayerBottomSheetState {

    object Hidden : PlayerBottomSheetState

    data class Content(val playlists: List<Playlist>) : PlayerBottomSheetState

    data class TrackAddedResult(val result: Boolean) : PlayerBottomSheetState


}
