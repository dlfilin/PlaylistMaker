package com.example.playlistmaker.presentation.edit_playlist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    val playlistId: String?,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private var currentPlaylist: Playlist? = null
    var newImageUri: Uri? = null

    private val screenStateLiveData =
        MutableLiveData<EditPlaylistScreenState>(EditPlaylistScreenState.InitState)
    fun getScreenStateLiveData(): LiveData<EditPlaylistScreenState> = screenStateLiveData

    init {
        if (playlistId != null) {
            viewModelScope.launch {
                currentPlaylist = playlistsInteractor.getPlaylist(playlistId.toLong())
                currentPlaylist?.apply {
                    screenStateLiveData.postValue(
                        EditPlaylistScreenState.PlaylistLoaded(
                            uri = imageUri, name = name, description = description ?: ""
                        )
                    )
                }
            }
        }
    }

    fun createOrUpdatePressed(name: String, description: String?) {
        val updatedPlaylist = currentPlaylist
        if (updatedPlaylist == null) {
            createPlaylist(
                Playlist(
                    imageUri = newImageUri, name = name, description = description
                )
            )
        } else {
            updatedPlaylist.let {
                if (newImageUri != null || name != it.name || description != it.description) {
                    updatePlaylist(
                        it.copy(
                            name = name, description = description
                        ), newImageUri
                    )
                } else {
                    screenStateLiveData.postValue(EditPlaylistScreenState.PlaylistUpdated)
                }
            }
        }
    }

    private fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val result = playlistsInteractor.createNewPlaylist(playlist)
            screenStateLiveData.postValue(
                EditPlaylistScreenState.PlaylistCreated(
                    result, playlist.name
                )
            )
        }
    }

    private fun updatePlaylist(playlist: Playlist, newImageUri: Uri?) {
        viewModelScope.launch {
            playlistsInteractor.updatePlaylist(playlist, newImageUri) > 0
            screenStateLiveData.postValue(EditPlaylistScreenState.PlaylistUpdated)
        }
    }

    fun changeScreenState(state: EditPlaylistScreenState) {
        screenStateLiveData.postValue(state)
    }

}