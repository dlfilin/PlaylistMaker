package com.example.playlistmaker.presentation.ui.searchTracks

import com.example.playlistmaker.domain.models.Track

fun interface OnTrackClickListener {
    fun onTrackClick(item: Track)
}