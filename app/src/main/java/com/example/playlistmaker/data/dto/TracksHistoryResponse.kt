package com.example.playlistmaker.data.dto

import com.example.playlistmaker.data.db.entity.TrackEntity

class TracksHistoryResponse(val results: List<TrackEntity>) : Response()