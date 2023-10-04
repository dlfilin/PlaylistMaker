package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {

        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(code = -1))
            }

            200 -> {
                val data = (response as TracksSearchResponse).results.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        previewUrl = it.previewUrl,
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