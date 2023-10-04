package com.example.playlistmaker.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.data.FavoritesStorage

class FavoritesStorageImpl(private val sharedPreferences: SharedPreferences) : FavoritesStorage {

    override fun addToFavorites(trackId: Int) {
        changeFavorites(trackId = trackId, remove = false)
    }

    override fun removeFromFavorites(trackId: Int) {
        changeFavorites(trackId = trackId, remove = true)
    }

    override fun changeFavorites(trackId: Int, remove: Boolean) {
        val mutableSet = getSavedFavorites().toMutableSet()
        val modified =
            if (remove) mutableSet.remove(trackId.toString()) else mutableSet.add(trackId.toString())
        if (modified) sharedPreferences.edit().putStringSet(KEY_FAVORITES, mutableSet).apply()
    }

    override fun getSavedFavorites(): Set<String> {
        return sharedPreferences.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    private companion object {
        const val KEY_FAVORITES = "KEY_FAVORITES"
    }
}