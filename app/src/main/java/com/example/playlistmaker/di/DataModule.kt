package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.App
import com.example.playlistmaker.data.LocalStorage
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.network.ItunesApi
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.storage.SharedPrefsLocalStorage
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder().baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ItunesApi::class.java)
    }

    single {
        androidContext().getSharedPreferences(App.LOCAL_STORAGE, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    factory { MediaPlayer() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single<LocalStorage> {
        SharedPrefsLocalStorage(get(), get())
    }

}