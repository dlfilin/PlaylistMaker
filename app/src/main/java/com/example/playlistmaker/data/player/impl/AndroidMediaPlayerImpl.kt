package com.example.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.TrackPlayer

class AndroidMediaPlayerImpl(private var mediaPlayer: MediaPlayer) : TrackPlayer {

    override fun preparePlayer(url: String, statusObserver: TrackPlayer.StatusObserver) {

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

    override fun currentPosition(): Int = mediaPlayer.currentPosition

}