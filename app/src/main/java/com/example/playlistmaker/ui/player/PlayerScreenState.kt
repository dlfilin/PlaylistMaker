package com.example.playlistmaker.ui.player

import com.example.playlistmaker.domain.models.Track

sealed class PlayerScreenState {
    object Loading : PlayerScreenState()
    data class Content(
        val track: Track,
    ) : PlayerScreenState()

}