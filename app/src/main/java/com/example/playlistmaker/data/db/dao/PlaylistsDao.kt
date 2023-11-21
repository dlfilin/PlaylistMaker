package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.data.db.entity.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity): Int

    @Query("SELECT * FROM playlists_table ORDER BY tracks_count DESC")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists_table WHERE playlist_id = :id")
    fun getPlaylistFlow(id: Long): Flow<PlaylistEntity>

    @Query("SELECT * FROM playlists_table WHERE playlist_id = :id")
    suspend fun getPlaylist(id: Long): PlaylistEntity

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlaylistTrackCrossRef(crossRef: PlaylistTrackCrossRef): Long

    @Query("DELETE FROM playlist_track_crossref WHERE playlist_id = :playlistId AND track_id = :trackId")
    suspend fun deletePlaylistTrackCrossRef(playlistId: Long, trackId: Int): Int

    @Transaction
    @Query("SELECT * FROM playlists_table WHERE playlist_id = :id")
    fun getPlaylistWithTracks(id: Long): Flow<PlaylistWithTracks>

    @Query("UPDATE playlists_table SET tracks_count = tracks_count + 1 WHERE playlist_id = :playlistId")
    suspend fun incPlaylistTrackCount(playlistId: Long)

    @Query("UPDATE playlists_table SET tracks_count = tracks_count - 1 WHERE playlist_id = :playlistId")
    suspend fun decPlaylistTrackCount(playlistId: Long)

    @Query("DELETE FROM playlists_table WHERE playlist_id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long): Int

    @Query(value = "SELECT track_id FROM playlist_track_crossref WHERE playlist_id = :playlistId ORDER BY added_on_date DESC")
    suspend fun getPlaylistTracksIds(playlistId: Long): List<Int>

}