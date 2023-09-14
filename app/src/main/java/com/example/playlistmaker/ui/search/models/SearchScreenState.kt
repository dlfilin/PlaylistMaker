package com.example.playlistmaker.ui.search.models

import com.example.playlistmaker.domain.models.Track

sealed interface SearchScreenState {

    object Loading : SearchScreenState

    object EmptySearch : SearchScreenState

    object ClearScreen : SearchScreenState

    data class Content(
        val tracks: List<Track>
    ) : SearchScreenState

    data class History(
        val history: List<Track>
    ) : SearchScreenState

    data class Error(val code: Int) : SearchScreenState

}