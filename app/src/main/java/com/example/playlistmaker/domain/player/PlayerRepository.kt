package com.example.playlistmaker.domain.player

interface PlayerRepository {

    fun preparePlayer(url: String, statusObserver: PlayerInteractor.StatusObserver)

    fun play()

    fun pause()

    fun seek(position: Float)

    fun release()

    fun currentPosition(): Int

}