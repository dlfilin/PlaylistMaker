package com.example.playlistmaker.presentation.playlist

import com.example.playlistmaker.domain.models.Track

sealed interface TracksBottomSheetState {

    data class Content(val tracks: List<Track>) : TracksBottomSheetState

}
