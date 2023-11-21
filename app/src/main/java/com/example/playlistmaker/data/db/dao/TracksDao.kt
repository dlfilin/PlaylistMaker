package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTrack(track: TrackEntity): Int

    @Query(value = "SELECT * FROM tracks_table WHERE is_favorite = 1 ORDER BY fav_last_update DESC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query(value = "SELECT track_id FROM tracks_table WHERE is_favorite = 1")
    suspend fun getFavoriteTracksIds(): List<Int>

    @Query(value = "SELECT COUNT(*) > 0 FROM tracks_table WHERE track_id = :trackId")
    suspend fun wasTrackAdded(trackId: Int): Boolean

    @Query(value = "SELECT COUNT(*) > 0 FROM tracks_table WHERE track_id = :trackId AND is_favorite = 1")
    suspend fun isTrackInFavorites(trackId: Int): Boolean

    @Query(value = """
            SELECT COUNT(*) > 0
            FROM playlist_track_crossref
            WHERE playlist_id = :playlistId
            AND track_id = :trackId
        """
    )
    suspend fun isTrackInPlaylist(trackId: Int, playlistId: Long): Boolean

    @Query(value = "SELECT COUNT(*) > 0 FROM playlist_track_crossref WHERE track_id = :trackId")
    suspend fun isTrackInAnyPlaylist(trackId: Int): Boolean

    @Query("DELETE FROM tracks_table WHERE track_id = :trackId")
    suspend fun deleteTrack(trackId: Int): Int



}