package com.example.playlistmaker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.HistoryInteractor
import com.example.playlistmaker.domain.search.SearchInteractor
import com.example.playlistmaker.ui.search.SearchScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchScreenState>(SearchScreenState.ClearScreen)
    fun observeState(): LiveData<SearchScreenState> = stateLiveData

    private var latestSearchText: String? = null

    private var searchJob: Job? = null

    init {
        renderTracksHistory()
    }

    fun searchDebounced(changedText: String, debounced: Boolean) {

        if (changedText.isBlank()) {
            renderTracksHistory()
            searchJob?.cancel()
            return
        }

        latestSearchText = changedText

        searchJob?.cancel()
        if (debounced) {
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                searchRequest(changedText)
            }
        } else {
            searchRequest(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchScreenState.Loading)

            viewModelScope.launch{
                searchInteractor.searchTracks(newSearchText).collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorCode: Int?) {
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


    fun renderTracksHistory() {
        historyInteractor.getTracksFromHistory(object : HistoryInteractor.TracksConsumer {
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
        historyInteractor.addTrackToHistory(track)
        if (stateLiveData.value is SearchScreenState.History) {
            renderTracksHistory()
        }
    }

    fun clearHistory() {
        historyInteractor.clearTracksHistory()
        renderState(SearchScreenState.ClearScreen)
    }

    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    fun clearSearchCoroutine() {
        searchJob?.cancel()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }
}