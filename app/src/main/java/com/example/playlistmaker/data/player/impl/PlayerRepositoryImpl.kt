package com.example.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlayerRepositoryImpl : PlayerRepository {

    private lateinit var mediaPlayer: MediaPlayer

    override fun preparePlayer(url: String, statusObserver: PlayerInteractor.StatusObserver) {

        mediaPlayer = MediaPlayer()

        mediaPlayer.setDataSource(url)

        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            statusObserver.onPrepared()
        }

        mediaPlayer.setOnCompletionListener {
            statusObserver.onCompletion()
        }

    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        if (mediaPlayer.isPlaying)
            mediaPlayer.pause()
    }

    override fun seek(position: Float) {
        TODO("Not yet implemented")
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun currentPosition(): Flow<Int> = flow {
        while (mediaPlayer.isPlaying) {
            emit(mediaPlayer.currentPosition)
            delay(REFRESH_TRACK_TIME_DELAY)
        }
    }

    companion object {

        private const val REFRESH_TRACK_TIME_DELAY = 300L

    }
}