package com.example.playlistmaker.data.converters

import android.net.Uri
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return with(playlist) {
            PlaylistEntity(
                playlistId = id,
                name = name,
                description = description,
                imageUri = imageUri?.toString(),
            )
        }
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return with(playlist) {
            Playlist(
                id = playlistId,
                name = name,
                description = description,
                imageUri = imageUri?.let { Uri.parse(it) ?: null },
            )
        }
    }
}