package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    tableName = "playlist_track_crossref",
    primaryKeys = ["playlist_id", "track_id"],
)
data class PlaylistTrackCrossRef(

    @ColumnInfo(name = "playlist_id")
    val playlistId: Long,

    @ColumnInfo(name = "track_id")
    val trackId: Int,

    @ColumnInfo(name = "added_on_date")
    val addedOnDate: Long, // Дата добавления трека

)