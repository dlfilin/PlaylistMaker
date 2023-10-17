package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.domain.models.Track

sealed class PlayerScreenState {
    object Loading : PlayerScreenState()
    data class Content(
        val track: Track,
    ) : PlayerScreenState()

}