package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTrack(track: TrackEntity): Int

    @Query("DELETE FROM tracks_table WHERE track_id = :trackId")
    suspend fun deleteTrack(trackId: Int): Int

    @Query(value = "SELECT * FROM tracks_table WHERE is_favorite = 1 ORDER BY fav_last_update DESC")
    fun getFavoriteTracksFlow(): Flow<List<TrackEntity>>

    @Query(value = "SELECT COUNT(*) > 0 FROM tracks_table WHERE track_id = :trackId")
    suspend fun isTrackAdded(trackId: Int): Boolean

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

    @Transaction
    @Query(
        """
        SELECT tracks_table.* FROM tracks_table
        JOIN playlist_track_crossref ON tracks_table.track_id = playlist_track_crossref.track_id
        WHERE playlist_track_crossref.playlist_id = :playlistId
        ORDER BY playlist_track_crossref.added_on_date DESC
    """
    )
    fun getTracksFromPlaylistSortedFlow(playlistId: Long): Flow<List<TrackEntity>>

}