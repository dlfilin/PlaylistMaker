package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.App
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.network.ItunesApi
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.storage.HistoryStorage
import com.example.playlistmaker.data.storage.ImagesStorage
import com.example.playlistmaker.data.storage.SettingsStorage
import com.example.playlistmaker.data.storage.impl.HistoryStorageImpl
import com.example.playlistmaker.data.storage.impl.ImagesStorageImpl
import com.example.playlistmaker.data.storage.impl.SettingsStorageImpl
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

    factory {
        Gson()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single<HistoryStorage> {
        HistoryStorageImpl(get(), get())
    }

    single<SettingsStorage> {
        SettingsStorageImpl(get())
    }

    single<ImagesStorage> {
        ImagesStorageImpl(androidContext())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

}