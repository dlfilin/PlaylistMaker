package com.example.playlistmaker.domain.search

interface FavoritesInteractor {

    fun addToFavorites(trackId: Int)

    fun removeFromFavorites(trackId: Int)

    fun changeFavorites(trackId: Int, remove: Boolean)

    fun getSavedFavorites(): Set<String>

}