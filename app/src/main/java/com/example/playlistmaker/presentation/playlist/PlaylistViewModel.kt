package com.example.playlistmaker.presentation.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.history.HistoryInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.getTrackTimeMMSS
import com.example.playlistmaker.domain.playlists.PlaylistsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.util.ResourceProvider
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private var playlistId: Long,
    private val playlistsInteractor: PlaylistsInteractor,
    private val historyInteractor: HistoryInteractor,
    private val sharingInteractor: SharingInteractor,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val screenStateLiveData = MutableLiveData<PlaylistScreenState>()
    private val bottomSheetStateLiveData = MutableLiveData<TracksBottomSheetState>()
    private val playlistSingleEventState = SingleLiveEvent<PlaylistSingleEventState>()

    fun getScreenStateLiveData(): LiveData<PlaylistScreenState> = screenStateLiveData
    fun getBottomSheetStateLiveData(): LiveData<TracksBottomSheetState> = bottomSheetStateLiveData
    fun getPlaylistSingleEventState(): LiveData<PlaylistSingleEventState> = playlistSingleEventState

    init {
        loadPlaylistWithTracks()
    }

    private fun loadPlaylistWithTracks() {
        screenStateLiveData.postValue(PlaylistScreenState.Loading)

        viewModelScope.launch {
            playlistsInteractor.getPlaylistWithTracks(playlistId).catch {
                screenStateLiveData.postValue(PlaylistScreenState.Error)
            }.collect {
                screenStateLiveData.postValue(PlaylistScreenState.Content(it.first))
                bottomSheetStateLiveData.postValue((TracksBottomSheetState.Content(it.second)))
            }
        }
    }

    fun addTrackToHistory(track: Track) {
        viewModelScope.launch {
            historyInteractor.addTrackToHistory(track)
        }
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            val result =
                playlistsInteractor.deleteTrackFromPlaylist(track.trackId, playlistId = playlistId)

            playlistSingleEventState.postValue(PlaylistSingleEventState.TrackDeleted(result, track.trackName))
        }
    }

    fun sharePlaylist() {
        val playlist = (screenStateLiveData.value as PlaylistScreenState.Content).playlist
        val tracks = (bottomSheetStateLiveData.value as TracksBottomSheetState.Content).tracks

        if (tracks.isEmpty()) {
            playlistSingleEventState.postValue(PlaylistSingleEventState.PlaylistShared(false))
        } else {
            sharingInteractor.shareMessage(prepareSharingInfo(playlist, tracks))
        }
    }

    private fun prepareSharingInfo(playlist: Playlist, tracks: List<Track>): String {
        var message = "${playlist.name}\n"
        if (playlist.description != null) message += "${playlist.description}\n"
        message += "${
            resourceProvider.getQuantityString(
                R.plurals.tracks,
                playlist.tracksCount,
            )
        }\n"

        tracks.forEachIndexed { index, track ->
            message += "${index + 1}. ${track.artistName} - ${track.trackName} (${track.getTrackTimeMMSS()})\n"
        }

        return message
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlistId)

            playlistSingleEventState.postValue(PlaylistSingleEventState.PlaylistDeleted(true))
        }
    }
}