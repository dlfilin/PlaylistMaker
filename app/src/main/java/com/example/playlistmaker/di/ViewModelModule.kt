package com.example.playlistmaker.di

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.favorites.FavoritesViewModel
import com.example.playlistmaker.presentation.playlists.PlaylistsViewModel
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get(), get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get())
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}