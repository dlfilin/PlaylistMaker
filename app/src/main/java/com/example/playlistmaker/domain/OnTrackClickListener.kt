package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.Track

fun interface OnTrackClickListener {
    fun onTrackClick(item: Track)
}