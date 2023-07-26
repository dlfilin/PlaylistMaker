package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.MediaPlayerInterface
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.PlayerStateListener

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayerInterface) : PlayerRepository {

    override fun preparePlayer(url: String, listener: PlayerStateListener) {
        mediaPlayer.preparePlayer(url, listener)
    }

    override fun startPlayer() {
        mediaPlayer.startPlayer()
    }

    override fun pausePlayer() {
        mediaPlayer.pausePlayer()
    }

    override fun releasePlayer() {
        mediaPlayer.releasePlayer()
    }

    override fun currentPosition(): Int {
        return mediaPlayer.currentPosition()
    }
}