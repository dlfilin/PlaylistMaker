package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

class TracksListResponse(val resultCount: Int, val results: List<Track>)