package com.example.playlistmaker.domain.api

interface PlayerStateListener {

    fun onPrepared()

    fun onCompleted()

}