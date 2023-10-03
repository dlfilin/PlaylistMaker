package com.example.playlistmaker.di

import com.example.playlistmaker.data.favorites.impl.FavoritesRepositoryImpl
import com.example.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.impl.HistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.PlayerRepository
import com.example.playlistmaker.domain.search.FavoritesRepository
import com.example.playlistmaker.domain.search.HistoryRepository
import com.example.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    single<ExternalNavigator> { ExternalNavigatorImpl(androidContext()) }

    single<SearchRepository> { SearchRepositoryImpl(get()) }

    single<HistoryRepository> { HistoryRepositoryImpl(get()) }

    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }

    single<PlayerRepository> { PlayerRepositoryImpl() }

}