package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks_table")
data class TrackEntity (

    @PrimaryKey @ColumnInfo(name = "track_id")
    val trackId: Int, // id трека

    @ColumnInfo(name = "track_name")
    val trackName: String?, // Название композиции

    @ColumnInfo(name = "artist_name")
    val artistName: String?, // Имя исполнителя

    @ColumnInfo(name = "collection_name")
    val collectionName: String?, // Название альбома

    @ColumnInfo(name = "release_year")
    val releaseYear: String?, // Год релиза трека

    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String?, // Жанр трека

    @ColumnInfo(name = "country")
    val country: String?, // Страна исполнителя

    @ColumnInfo(name = "track_time_millis")
    val trackTimeMillis: String?, // Продолжительность трека

    @ColumnInfo(name = "artwork_url")
    val artworkUrl100: String?, // Ссылка на изображение обложки

    @ColumnInfo(name = "preview_url")
    val previewUrl: String?, // Ссылка на отрывок трека

    @ColumnInfo(name = "added_on_date")
    val addedOnDate: Long, // Дата добавления трека

)