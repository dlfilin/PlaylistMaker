package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.ui.search.models.SearchState

class TracksSearchViewModel(
    application: Application,
    private val tracksInteractor: TracksInteractor
) :
    AndroidViewModel(application) {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText: String? = null

    init {
        renderTracksHistory()
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounced(changedText: String, debounced: Boolean) {

        if (changedText.isBlank()) {
            renderTracksHistory()
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            return
        }

        latestSearchText = changedText

        if (debounced) {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

            val searchRunnable = Runnable { searchRequest(changedText) }

            handler.postDelayed(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                SEARCH_DEBOUNCE_DELAY
            )
        } else {
            searchRequest(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorCode: Int?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorCode != null -> {
                            renderState(SearchState.Error(code = errorCode))
                        }

                        tracks.isEmpty() -> {
                            renderState(SearchState.EmptySearch)
                        }

                        else -> {
                            renderState(
                                SearchState.Content(
                                    tracks = tracks,
                                )
                            )
                        }
                    }
                }
            })
        }
    }

    fun renderTracksHistory() {
        tracksInteractor.getTracksFromHistory(object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorCode: Int?) {
                val history = mutableListOf<Track>()
                if (foundTracks != null) {
                    history.addAll(foundTracks)
                }

                if (history.isEmpty()) {
                    renderState(SearchState.ClearScreen)
                } else {
                    renderState(SearchState.History(history))
                }
            }
        })
    }

    fun addTrackToHistory(track: Track) {
        tracksInteractor.addTrackToHistory(track)
        if (stateLiveData.value is SearchState.History) {
            renderTracksHistory()
        }
    }

    fun clearHistory() {
        tracksInteractor.clearTracksHistory()
        renderState(SearchState.ClearScreen)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                TracksSearchViewModel(app, Creator.provideTracksInteractor(app))
            }
        }
    }
}