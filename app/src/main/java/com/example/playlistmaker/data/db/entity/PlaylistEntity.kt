package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "playlist_id") val playlistId: Long = 0,

    val name: String,

    val description: String?,

    @ColumnInfo(name = "image_uri") val imageUri: String?,

    @ColumnInfo(name = "tracks_count") val tracksCount: Int = 0,

)

fun PlaylistEntity.incrementTracksCount(): PlaylistEntity {
    return this.let{ old -> old.copy(tracksCount = old.tracksCount+1) }
}
