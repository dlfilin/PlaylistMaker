package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.library.models.PlaylistsScreenState

class PlaylistsViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsScreenState>(PlaylistsScreenState.EmptyList)
    fun observeState(): LiveData<PlaylistsScreenState> = stateLiveData


}