package com.example.playlistmaker.ui.player.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.ui.player.models.PlayerScreenState
import com.example.playlistmaker.ui.player.models.PlayerState

class PlayerViewModel(
    private val track: Track,
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
                trackPositionLiveData.postValue(position)

                if (getCurrentPlayerState() == PlayerState.STATE_PLAYING) handler.postDelayed(
                    this,
                    REFRESH_TRACK_TIME_DELAY
                )
            }
        }

    }

    override fun onCleared() {
        playerInteractor.release()
        handler.removeCallbacksAndMessages(REFRESH_TRACK_TIME_DELAY)
    }

    private fun getCurrentPlayerState(): PlayerState {
        return playerStateLiveData.value ?: PlayerState.STATE_DEFAULT
    }

    fun preparePlayer() {
        playerInteractor.preparePlayer(track.previewUrl,
            statusObserver = object : PlayerInteractor.StatusObserver {

                override fun onPrepared() {
                    playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
                }

                override fun onCompletion() {
                    playerStateLiveData.postValue(PlayerState.STATE_PREPARED)
                    trackPositionLiveData.postValue(0)
                    handler.removeCallbacks(trackTimeRunnable)
                }
            })
    }

    private fun startPlayer() {
        playerInteractor.play()
        playerStateLiveData.postValue(PlayerState.STATE_PLAYING)
        handler.post(trackTimeRunnable)
    }

    fun pausePlayer() {
        playerInteractor.pause()
        playerStateLiveData.postValue(PlayerState.STATE_PAUSED)
        handler.removeCallbacks(trackTimeRunnable)
    }

    fun releasePlayer() {
        playerInteractor.release()
        playerStateLiveData.postValue(PlayerState.STATE_DEFAULT)
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
            }
        }
    }

    companion object {

        private const val REFRESH_TRACK_TIME_DELAY = 300L

    }
}