package com.example.playlistmaker.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.ui.player.PlayerScreenState
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    private val playerStateLiveData = MutableLiveData(PlayerState.STATE_DEFAULT)
    private val trackPositionLiveData = MutableLiveData(0)

    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData
    fun getTrackPositionLiveData(): LiveData<Int> = trackPositionLiveData

    init {

        screenStateLiveData.postValue(PlayerScreenState.Content(track))

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

}