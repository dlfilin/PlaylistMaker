package com.example.playlistmaker.ui.favorites

import com.example.playlistmaker.domain.models.Track

sealed interface FavoritesScreenState {

    object Loading : FavoritesScreenState

    object EmptyList : FavoritesScreenState

    data class Content(
        val tracks: List<Track>
    ) : FavoritesScreenState

    data class Error(val code: Int) : FavoritesScreenState

}