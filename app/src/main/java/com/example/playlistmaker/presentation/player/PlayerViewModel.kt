package com.example.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch

class PlayerViewModel(
    private var track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<PlayerScreenState>()
    private val playerStateLiveData = MutableLiveData(PlayerState.STATE_DEFAULT)
    private val trackPositionLiveData = MutableLiveData(0)
    private val bottomSheetStateLiveData = MutableLiveData<PlayerBottomSheetState>()
    private val showSnackBarEvent = SingleLiveEvent<Pair<Boolean, String>>()

    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData
    fun getTrackPositionLiveData(): LiveData<Int> = trackPositionLiveData
    fun getBottomSheetStateLiveData(): LiveData<PlayerBottomSheetState> = bottomSheetStateLiveData
    fun getShowSnackBarEvent(): LiveData<Pair<Boolean, String>> = showSnackBarEvent

    init {
        screenStateLiveData.postValue(PlayerScreenState.Content(track))

        loadPlaylists()
    }

    override fun onCleared() {
        playerInteractor.release()
    }

    private fun getCurrentPlayerState(): PlayerState {
        return playerStateLiveData.value ?: PlayerState.STATE_DEFAULT
    }

    fun preparePlayer() {
        playerInteractor.preparePlayer(
            track.previewUrl,
            statusObserver = object : PlayerInteractor.StatusObserver {

                override fun onPrepared() {
                    playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
                }

                override fun onCompletion() {
                    playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
                    trackPositionLiveData.postValue(0)
                }
            })
    }

    private fun startPlayer() {
        playerInteractor.play()
        playerStateLiveData.postValue(PlayerState.STATE_PLAYING)

        viewModelScope.launch {
            playerInteractor.currentPosition().collect { position ->
                trackPositionLiveData.postValue(position)
            }
        }
    }

    fun pausePlayer() {
        playerInteractor.pause()
        playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
    }

    fun releasePlayer() {
        playerInteractor.release()
        playerStateLiveData.postValue(PlayerState.STATE_DEFAULT)
    }

    fun playbackControl() {
        when (getCurrentPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }
            PlayerState.STATE_DEFAULT -> {}
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            favoritesInteractor.reverseFavoriteState(track)
            track = track.let { old -> old.copy(isFavorite = !old.isFavorite) }
            screenStateLiveData.postValue(PlayerScreenState.Content(track))
        }
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect {
                if (it.isEmpty()) {
                    bottomSheetStateLiveData.postValue(PlayerBottomSheetState.Empty)
                } else {
                    bottomSheetStateLiveData.postValue(PlayerBottomSheetState.Content(it))
                }
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val result = playlistsInteractor.addTrackToPlaylist(track, playlist)
            showSnackBarEvent.postValue(Pair(result, playlist.name))
        }
    }

}