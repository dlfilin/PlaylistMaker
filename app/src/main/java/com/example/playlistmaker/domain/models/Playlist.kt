package com.example.playlistmaker.domain.models

import android.net.Uri

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String?,
    val imageUri: Uri? = null,
    val tracksCount: Int = 0,
)