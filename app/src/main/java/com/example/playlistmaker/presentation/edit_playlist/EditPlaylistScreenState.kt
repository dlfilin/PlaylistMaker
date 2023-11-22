package com.example.playlistmaker.presentation.edit_playlist

import android.net.Uri

sealed interface EditPlaylistScreenState {

    object InitState : EditPlaylistScreenState
    data class PlaylistLoaded(val uri: Uri?, val name: String, val description: String) : EditPlaylistScreenState

    data class PlaylistCreated(val successful: Boolean, val name: String) : EditPlaylistScreenState

    object PlaylistUpdated : EditPlaylistScreenState

}