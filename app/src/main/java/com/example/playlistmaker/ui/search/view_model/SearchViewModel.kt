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
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.ui.search.models.SearchScreenState

class SearchViewModel(
    application: Application,
    private val searchInteractor: SearchInteractor
) :
    AndroidViewModel(application) {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchScreenState>(SearchScreenState.ClearScreen)
    fun observeState(): LiveData<SearchScreenState> = stateLiveData

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

        if (changedText == latestSearchText) return

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
            renderState(SearchScreenState.Loading)

            searchInteractor.searchTracks(newSearchText, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorCode: Int?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorCode != null -> {
                            renderState(SearchScreenState.Error(code = errorCode))
                        }

                        tracks.isEmpty() -> {
                            renderState(SearchScreenState.EmptySearch)
                        }

                        else -> {
                            renderState(
                                SearchScreenState.Content(
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
        searchInteractor.getTracksFromHistory(object : SearchInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorCode: Int?) {
                if (foundTracks.isNullOrEmpty()) {
                    renderState(SearchScreenState.ClearScreen)
                } else {
                    renderState(SearchScreenState.History(foundTracks))
                }
            }
        })
    }

    fun addTrackToHistory(track: Track) {
        searchInteractor.addTrackToHistory(track)
        if (stateLiveData.value is SearchScreenState.History) {
            renderTracksHistory()
        }
    }

    fun clearHistory() {
        searchInteractor.clearTracksHistory()
        renderState(SearchScreenState.ClearScreen)
    }

    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                SearchViewModel(app, Creator.provideSearchInteractor(app))
            }
        }
    }
}