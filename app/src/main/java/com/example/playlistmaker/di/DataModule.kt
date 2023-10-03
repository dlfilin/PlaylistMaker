package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.App
import com.example.playlistmaker.data.FavoritesStorage
import com.example.playlistmaker.data.search.HistoryStorage
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.network.ItunesApi
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.impl.HistoryStorageImpl
import com.example.playlistmaker.data.settings.SettingsStorage
import com.example.playlistmaker.data.settings.impl.SettingsStorageImpl
import com.example.playlistmaker.data.storage.FavoritesStorageImpl
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

    single<SharedPreferences> {
        androidContext().getSharedPreferences(App.LOCAL_STORAGE, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single<HistoryStorage> {
        HistoryStorageImpl(get(), get())
    }

    single<FavoritesStorage> {
        FavoritesStorageImpl(get())
    }

    single<SettingsStorage> {
        SettingsStorageImpl(get())
    }

}