package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.library.models.FavoritesScreenState

class FavoritesViewModel() : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesScreenState>(FavoritesScreenState.EmptyList)
    fun observeState(): LiveData<FavoritesScreenState> = stateLiveData


}