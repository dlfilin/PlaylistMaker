package com.example.playlistmaker.presentation.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistScreenState>(PlaylistScreenState.EmptyList)
    fun observeState(): LiveData<PlaylistScreenState> = stateLiveData


}