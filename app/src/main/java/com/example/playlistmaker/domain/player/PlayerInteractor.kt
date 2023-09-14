package com.example.playlistmaker.domain.player

interface PlayerInteractor {

    fun preparePlayer(url: String, statusObserver: StatusObserver)

    fun play()

    fun pause()

    fun seek(position: Float)

    fun release()

    fun currentPosition(): Int

    interface StatusObserver {

        fun onPrepared()

        fun onCompletion()
    }

}