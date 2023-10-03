package com.example.playlistmaker.domain.player

import kotlinx.coroutines.flow.Flow

interface PlayerInteractor {

    fun preparePlayer(url: String, statusObserver: StatusObserver)

    fun play()

    fun pause()

    fun seek(position: Float)

    fun release()

    fun currentPosition(): Flow<Int>

    interface StatusObserver {

        fun onPrepared()

        fun onCompletion()
    }

}