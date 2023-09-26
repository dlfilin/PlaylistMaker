package com.example.playlistmaker.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.favorites.FavoritesScreenState

class FavoritesViewModel() : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesScreenState>(FavoritesScreenState.EmptyList)
    fun observeState(): LiveData<FavoritesScreenState> = stateLiveData


}