package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.PlayerRepository

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

    override fun seek(position: Float) {
        repository.seek(position)
    }

    override fun release() {
        repository.release()
    }

    override fun currentPosition(): Int {
        return repository.currentPosition()
    }

}