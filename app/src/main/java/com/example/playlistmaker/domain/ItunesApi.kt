package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.TracksListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {

    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<TracksListResponse>

}