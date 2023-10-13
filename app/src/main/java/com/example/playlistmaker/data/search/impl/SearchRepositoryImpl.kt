package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.search.NetworkClient
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
                val favoriteTracks = appDatabase.favoritesDao().getTracksIds()

                val data = (response as TracksSearchResponse).results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        collectionName = it.collectionName,
                        releaseYear = Track.getReleaseYear(it.releaseDate),
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        trackTimeMillis = Track.getTrackTimeMMSS(it.trackTimeMillis),
                        artworkUrl100 = it.artworkUrl100,
                        previewUrl = it.previewUrl,
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