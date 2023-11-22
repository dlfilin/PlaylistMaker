package com.example.playlistmaker.presentation.playlist

sealed interface PlaylistSingleEventState {

    data class TrackDeleted(val result: Boolean, val name: String) : PlaylistSingleEventState

    data class PlaylistShared(val result: Boolean) : PlaylistSingleEventState

    data class  PlaylistDeleted(val result: Boolean) : PlaylistSingleEventState

}
