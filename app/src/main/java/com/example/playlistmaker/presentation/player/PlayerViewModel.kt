package com.example.playlistmaker.presentation.player

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.favorites.FavoritesInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    private val playerStateLiveData = MutableLiveData(PlayerState.STATE_DEFAULT)
    private val trackPositionLiveData = MutableLiveData(0)
    private val bottomSheetStateLiveData =
        MutableLiveData<PlayerBottomSheetState>(PlayerBottomSheetState.Hidden)

    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData
    fun getTrackPositionLiveData(): LiveData<Int> = trackPositionLiveData
    fun getBottomSheetStateLiveData(): LiveData<PlayerBottomSheetState> = bottomSheetStateLiveData

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
            val isFavorite =
                (screenStateLiveData.value as PlayerScreenState.Content).track.isFavorite
            if (isFavorite) {
                favoritesInteractor.removeFromFavorites(track)
            } else {
                favoritesInteractor.addToFavorites(track)
            }

            screenStateLiveData.postValue(PlayerScreenState.Content(track.copy(isFavorite = !isFavorite)))
        }
    }

    fun loadPlaylists() {

        Log.d("loadPlaylists", System.currentTimeMillis().toString())

        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect {
                Log.d("Coroutine", it.toString())
                bottomSheetStateLiveData.postValue(PlayerBottomSheetState.Shown(it))
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {

        Log.d("addTrackToPlaylist", System.currentTimeMillis().toString())

        viewModelScope.launch {
            val result = playlistsInteractor.addTrackToPlaylist(track, playlist)
            Log.d("Coroutine", result.toString())
            bottomSheetStateLiveData.postValue(PlayerBottomSheetState.TrackAddedResult(result))
        }

    }

    fun addRandomPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.createPLaylist(playlist)
        }
    }

}