package com.example.playlistmaker.domain.api

interface PlayerRepository {

    fun preparePlayer(url: String, listener: PlayerStateListener)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun currentPosition(): Int

}