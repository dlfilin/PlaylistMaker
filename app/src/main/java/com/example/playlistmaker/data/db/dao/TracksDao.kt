package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(track: TrackEntity)

    @Query(value = "SELECT * FROM favorite_tracks_table ORDER BY added_on_date DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query(value = "SELECT track_id FROM favorite_tracks_table")
    suspend fun getTracksIds(): List<Int>

}