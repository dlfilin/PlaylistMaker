package com.example.playlistmaker.app

import com.example.playlistmaker.models.Track

fun interface OnTrackClickListener {
    fun onTrackClick(item: Track)
}