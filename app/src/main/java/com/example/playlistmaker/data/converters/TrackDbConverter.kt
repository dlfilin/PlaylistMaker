package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.models.Track

class TrackDbConverter {

    fun map(track: TrackEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName ?: "",
            artistName = track.artistName ?: "",
            collectionName = track.collectionName ?: "",
            releaseYear = track.releaseYear ?: "",
            primaryGenreName = track.primaryGenreName ?: "",
            country = track.country ?: "",
            trackTimeMillis = track.trackTimeMillis ?: "",
            artworkUrl100 = track.artworkUrl100 ?: "",
            previewUrl = track.previewUrl ?: "",
        )
    }

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseYear = track.releaseYear,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = track.previewUrl,
            addedOnDate = System.currentTimeMillis()
        )
    }
}