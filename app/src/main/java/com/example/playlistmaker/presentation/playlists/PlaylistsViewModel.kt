package com.example.playlistmaker.presentation.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.playlists.PlaylistsScreenState

class PlaylistsViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsScreenState>(PlaylistsScreenState.EmptyList)
    fun observeState(): LiveData<PlaylistsScreenState> = stateLiveData


}