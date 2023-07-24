package com.example.playlistmaker.data

import com.example.playlistmaker.domain.api.PlayerStateListener

interface MediaPlayerInterface {

    fun preparePlayer(url: String, listener: PlayerStateListener)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun currentPosition(): Int

}