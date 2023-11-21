package com.example.playlistmaker.domain.models

import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: Int, // id трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val collectionName: String, // Название альбома
    val releaseYear: String, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val trackTimeMillis: Int, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val previewUrl: String, // Ссылка на отрывок трека
    var isFavorite: Boolean = false
)

fun Track.getTrackTimeMMSS(): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
}

fun Track.getCoverArtwork512() = this.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

fun Track.getCoverArtwork60() = this.artworkUrl100.replaceAfterLast('/', "60x60bb.jpg")
