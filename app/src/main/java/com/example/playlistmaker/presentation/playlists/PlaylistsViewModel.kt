package com.example.playlistmaker.presentation.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val stateLiveData =
        MutableLiveData<PlaylistsScreenState>(PlaylistsScreenState.EmptyList)

    fun observeState(): LiveData<PlaylistsScreenState> = stateLiveData

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->

                if (playlists.isEmpty()) {
                    stateLiveData.postValue(PlaylistsScreenState.EmptyList)
                } else {
                    stateLiveData.postValue(PlaylistsScreenState.Content(playlists))
                }
            }
        }
    }

}