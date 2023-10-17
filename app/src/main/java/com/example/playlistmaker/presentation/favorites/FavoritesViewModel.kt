package com.example.playlistmaker.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesScreenState>(FavoritesScreenState.EmptyList)
    fun observeState(): LiveData<FavoritesScreenState> = stateLiveData

    fun fillData() {
        renderState(FavoritesScreenState.Loading)
        viewModelScope.launch {
            favoritesInteractor
                .getSavedFavorites()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesScreenState.EmptyList)
        } else {
            renderState(FavoritesScreenState.Content(tracks))
        }
    }

    private fun renderState(state: FavoritesScreenState) {
        stateLiveData.postValue(state)
    }

    fun addTrackToHistory(track: Track) {
        viewModelScope.launch {
            historyInteractor.addTrackToHistory(track)
        }
    }
}