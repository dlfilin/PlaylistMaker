package com.example.playlistmaker.presentation.new_playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val showSnackBarEvent = SingleLiveEvent<String?>()
    fun observeShowSnackBarEvent(): LiveData<String?> = showSnackBarEvent

    private val closeFragmentEvent = SingleLiveEvent<Boolean?>()
    fun observeCloseFragmentEvent(): LiveData<Boolean?> = closeFragmentEvent

    fun createNewPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val result = playlistsInteractor.createPLaylist(playlist)
            closeFragmentEvent.postValue(true)

            if (result > 0) {
                showSnackBarEvent.postValue(playlist.name)
            } else {
                showSnackBarEvent.postValue(null)
            }
        }
    }

}