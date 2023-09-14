package com.example.playlistmaker.data.search.dto

import java.util.Date

data class TrackDto(
    val trackId: Int, // id трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val collectionName: String, // Название альбома
    val releaseDate: Date, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val previewUrl: String // Ссылка на отрывок трека
)