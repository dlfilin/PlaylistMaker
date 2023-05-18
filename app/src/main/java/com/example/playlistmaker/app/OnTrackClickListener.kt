package com.example.playlistmaker.app

import com.example.playlistmaker.data.Track

interface OnTrackClickListener {
    fun onTrackClick(item: Track)
}