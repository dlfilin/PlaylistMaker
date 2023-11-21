package com.example.playlistmaker.domain.player

import kotlinx.coroutines.flow.Flow

interface PlayerRepository {

    fun preparePlayer(url: String, statusObserver: PlayerInteractor.StatusObserver)

    fun play()

    fun pause()

    fun release()

    fun currentPosition(): Flow<Int>

}