package com.example.playlistmaker.data.network

import android.media.MediaPlayer
import com.example.playlistmaker.data.MediaPlayerInterface
import com.example.playlistmaker.domain.api.PlayerStateListener

class AndroidMediaPlayerImp : MediaPlayerInterface {

    private var mediaPlayer = MediaPlayer()

    override fun preparePlayer(url: String, listener: PlayerStateListener) {

        mediaPlayer.setDataSource(url)

        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            listener.onPrepared()
        }

        mediaPlayer.setOnCompletionListener {
            listener.onCompleted()
        }

    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        if (mediaPlayer.isPlaying)
            mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun currentPosition(): Int = mediaPlayer.currentPosition

}