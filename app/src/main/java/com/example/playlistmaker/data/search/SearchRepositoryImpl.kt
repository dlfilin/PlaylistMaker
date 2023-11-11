package com.example.playlistmaker.data.search

import android.util.Log
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase,
    ) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {

        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(code = -1))
            }

            200 -> {
                val favoriteTracks = appDatabase.getTracksDao().getFavoriteTracksIds()
                Log.d("SearchRepositoryImpl", favoriteTracks.toString())

                val data = (response as TracksSearchResponse).results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName ?: "N/A",
                        artistName = it.artistName ?: "N/A",
                        collectionName = it.collectionName ?: "N/A",
                        releaseYear = it.releaseDate?.let { it1 ->
                            Track.getReleaseYear(it1) } ?: "N/A",
                        primaryGenreName = it.primaryGenreName ?: "N/A",
                        country = it.country ?: "N/A",
                        trackTimeMillis = it.trackTimeMillis?.let { it1 ->
                            Track.getTrackTimeMMSS(it1)} ?: "N/A",
                        artworkUrl100 = it.artworkUrl100 ?: "N/A",
                        previewUrl = it.previewUrl ?: "N/A",
                        isFavorite = favoriteTracks.contains(it.trackId)
                    )
                }
                emit(Resource.Success(data))
            }

            else -> {
                emit(Resource.Error(code = response.resultCode))
            }
        }
    }
}