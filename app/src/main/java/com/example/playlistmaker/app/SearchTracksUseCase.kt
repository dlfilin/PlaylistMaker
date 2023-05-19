package com.example.playlistmaker.app

import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchTracksUseCase {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)

    fun execute(query: String, callback: Callback<TracksListResponse>) {

        itunesService.searchTracks(text = query)
            .enqueue(callback)
    }

}