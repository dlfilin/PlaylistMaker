package com.example.playlistmaker.di

import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.favorites.FavoritesRepositoryImpl
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.history.HistoryRepositoryImpl
import com.example.playlistmaker.data.storage.impl.ImagesStorageImpl
import com.example.playlistmaker.data.playlists.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.search.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.favorites.FavoritesRepository
import com.example.playlistmaker.domain.history.HistoryRepository
import com.example.playlistmaker.data.storage.ImagesStorage
import com.example.playlistmaker.domain.playlists.PlaylistsRepository
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }

    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }

    single<HistoryRepository> { HistoryRepositoryImpl(get(), get(), get()) }

    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get()) }

    single<PlayerRepository> { PlayerRepositoryImpl() }

    single<PlaylistsRepository> { PlaylistsRepositoryImpl(get(), get(), get(), get()) }

    single<ImagesStorage> { ImagesStorageImpl(androidContext()) }

    factory { TrackDbConverter() }

    factory { PlaylistDbConverter() }



}