package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository
import kotlinx.coroutines.flow.Flow

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    override fun preparePlayer(url: String, statusObserver: PlayerInteractor.StatusObserver) {
        repository.preparePlayer(url, statusObserver)
    }

    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun currentPosition(): Flow<Int> {
        return repository.currentPosition()
    }

}