package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {

    @GET("/search?entity=song")
    suspend fun searchTracks(@Query("term") text: String): TracksSearchResponse

}