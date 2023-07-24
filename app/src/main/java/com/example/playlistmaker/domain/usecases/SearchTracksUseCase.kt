package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.data.dto.TracksListResponse
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

        if (query.isNotEmpty()) {
            itunesService.searchTracks(text = query).enqueue(callback)
        }
    }

}