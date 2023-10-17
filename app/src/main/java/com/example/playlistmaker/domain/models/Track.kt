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
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val previewUrl: String, // Ссылка на отрывок трека
    var isFavorite: Boolean = false
) {

    companion object {
        fun getCoverArtwork(artworkUrl100: String) = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        fun getTrackTimeMMSS(trackTimeMillis: Int): String = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(
                trackTimeMillis
            )

        fun getReleaseYear(releaseDate: String): String {
            return releaseDate.substringBefore('-')
        }
    }
}