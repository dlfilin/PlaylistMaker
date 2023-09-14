package com.example.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.ui.player.models.PlayerScreenState
import com.example.playlistmaker.ui.player.models.PlayerState

class PlayerViewModel(
    track: Track,
    private val playerInteractor: PlayerInteractor,
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val trackTimeRunnable: Runnable

    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    private val playerStateLiveData = MutableLiveData(PlayerState.STATE_DEFAULT)
    private val trackPositionLiveData = MutableLiveData(0)

    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData
    fun getTrackPositionLiveData(): LiveData<Int> = trackPositionLiveData

    init {
        screenStateLiveData.postValue(PlayerScreenState.Content(track))

        trackTimeRunnable = object : Runnable {
            override fun run() {
                val position = playerInteractor.currentPosition()
                trackPositionLiveData.value = position

                if (getCurrentPlayerState() == PlayerState.STATE_PLAYING) handler.postDelayed(
                    this,
                    REFRESH_TRACK_TIME_DELAY
                )
            }
        }

        playerInteractor.preparePlayer(track.previewUrl,
            statusObserver = object : PlayerInteractor.StatusObserver {

                override fun onPrepared() {
                    playerStateLiveData.value = PlayerState.STATE_PREPARED
                }

                override fun onCompletion() {
                    playerStateLiveData.value = PlayerState.STATE_PREPARED
                    trackPositionLiveData.value = 0
                    handler.removeCallbacks(trackTimeRunnable)
                }
            })
    }

    override fun onCleared() {
        playerInteractor.release()
        handler.removeCallbacksAndMessages(REFRESH_TRACK_TIME_DELAY)
    }

    private fun getCurrentPlayerState(): PlayerState {
        return playerStateLiveData.value ?: PlayerState.STATE_DEFAULT
    }

    private fun startPlayer() {
        playerInteractor.play()
        playerStateLiveData.value = PlayerState.STATE_PLAYING
        handler.post(trackTimeRunnable)
    }

    fun pausePlayer() {
        playerInteractor.pause()
        playerStateLiveData.value = PlayerState.STATE_PAUSED
        handler.removeCallbacks(trackTimeRunnable)
    }

    fun releasePlayer() {
        playerInteractor.release()
        playerStateLiveData.value = PlayerState.STATE_DEFAULT
        handler.removeCallbacks(trackTimeRunnable)
    }

    fun playbackControl() {

        when (getCurrentPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_DEFAULT -> {
                return
            }
        }
    }

    companion object {

        private const val REFRESH_TRACK_TIME_DELAY = 400L

        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(
                    track,
                    Creator.providePlayerInteractor(),
                )
            }
        }
    }
}