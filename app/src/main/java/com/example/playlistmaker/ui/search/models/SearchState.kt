package com.example.playlistmaker.ui.search.models

import com.example.playlistmaker.domain.models.Track

sealed interface SearchState {

    object Loading : SearchState

    object EmptySearch : SearchState

    object ClearScreen : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class History(
        val history: List<Track>
    ) : SearchState

    data class Error(val code: Int) : SearchState

}