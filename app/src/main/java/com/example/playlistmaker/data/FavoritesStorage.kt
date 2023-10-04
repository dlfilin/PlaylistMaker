package com.example.playlistmaker.data

interface FavoritesStorage {

    fun addToFavorites(trackId: Int)

    fun removeFromFavorites(trackId: Int)

    fun changeFavorites(trackId: Int, remove: Boolean)

    fun getSavedFavorites(): Set<String>

}