package com.example.playlistmaker.domain.models

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Track(
    val trackId: Int, // id трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val collectionName: String, // Название альбома
    val releaseDate: Date, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    fun getTrackTimeMMSS(): String = SimpleDateFormat("mm:ss", Locale.getDefault())
        .format(trackTimeMillis.toLong()
    )

    fun getReleaseYear(): Int {
        val cal = Calendar.getInstance()
        cal.time = releaseDate
        return cal.get(Calendar.YEAR)
    }
}