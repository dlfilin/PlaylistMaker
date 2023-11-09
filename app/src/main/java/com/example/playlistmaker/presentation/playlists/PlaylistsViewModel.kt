package com.example.playlistmaker.presentation.playlists

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsScreenState>(PlaylistsScreenState.EmptyList)
    fun observeState(): LiveData<PlaylistsScreenState> = stateLiveData

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {

        Log.d("loadPlaylists", System.currentTimeMillis().toString())

        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect {
                Log.d("Coroutine", it.toString())
                processLoadPlaylists(it)
            }
        }
    }

    private fun processLoadPlaylists(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            stateLiveData.postValue(PlaylistsScreenState.EmptyList)
        } else {
            stateLiveData.postValue(PlaylistsScreenState.Content(playlists))
        }
    }

    fun addRandomPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.createPLaylist(playlist)
        }
    }

}