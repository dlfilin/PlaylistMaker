package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.models.Track

class TrackDbConverter {

    fun map(track: TrackEntity): Track {
        return with(track) {
            Track(
                trackId = trackId,
                trackName = trackName ?: "",
                artistName = artistName ?: "",
                collectionName = collectionName ?: "",
                releaseYear = releaseYear ?: "",
                primaryGenreName = primaryGenreName ?: "",
                country = country ?: "",
                trackTimeMillis = trackTimeMillis ?: 0,
                artworkUrl100 = artworkUrl100 ?: "",
                previewUrl = previewUrl ?: "",
                isFavorite = isFavorite
            )
        }
    }

    fun map(track: Track): TrackEntity {
        return with(track)  {
            TrackEntity(
                trackId = trackId,
                trackName = trackName,
                artistName = artistName,
                collectionName = collectionName,
                releaseYear = releaseYear,
                primaryGenreName = primaryGenreName,
                country = country,
                trackTimeMillis = trackTimeMillis,
                artworkUrl100 = artworkUrl100,
                previewUrl = previewUrl,
                isFavorite = isFavorite,
                favLastUpdate = null
            )
        }
    }

    fun map(tracks: List<TrackEntity>): List<Track> = tracks.map(this::map)

}