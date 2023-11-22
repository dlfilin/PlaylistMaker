package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "playlists_table")
data class PlaylistEntity(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "playlist_id") val playlistId: Long = 0,

    val name: String,

    val description: String?,

    @ColumnInfo(name = "image_uri") val imageUri: String?,

    @ColumnInfo(name = "tracks_count") var tracksCount: Int = 0,
    )

data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "track_id",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    ) val tracks: List<TrackEntity>
)