package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "playlist_track_crossref",
    primaryKeys = ["playlist_id", "track_id"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlist_id"],
            childColumns = ["playlist_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
    ]
)
data class PlaylistTrackCrossRef(

    @ColumnInfo(name = "playlist_id")
    val playlistId: Long,

    @ColumnInfo(name = "track_id")
    val trackId: Int,

    @ColumnInfo(name = "added_on_date")
    val addedOnDate: Long = 0, // Дата добавления трека

)