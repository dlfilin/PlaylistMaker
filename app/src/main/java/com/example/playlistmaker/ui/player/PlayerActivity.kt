package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerBottomSheetState
import com.example.playlistmaker.presentation.player.PlayerScreenState
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.ui.playlists.PlaylistsAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var currentTrack: Track

    private val gson: Gson by inject()

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(currentTrack)
    }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private var isClickAllowed = true

    private val playlistsAdapter = PlaylistsAdapter { onPlaylistClicked(it) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(CURRENT_TRACK)
        currentTrack = gson.fromJson(json, Track::class.java)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playlistsRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.playlistsRV.adapter = playlistsAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        Log.d("BottomSheet", "Hidden")

                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d("BottomSheet", "Collapsed")
                        binding.overlay.alpha = 0.5f
                        binding.overlay.visibility = View.VISIBLE

                    }

                    else -> {
                        Log.d("BottomSheet", "Else")

                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset
                Log.d("onSlide", slideOffset.toString())

            }
        })

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    renderScreenState(screenState.track)
                }

                is PlayerScreenState.Loading -> {
                    // пока ничего по ТЗ
                }
            }
        }

        viewModel.getPlayerStateLiveData().observe(this) { playStatus ->
            renderPlayerState(playStatus)
        }

        viewModel.getTrackPositionLiveData().observe(this) {
            binding.playbackTime.text = SimpleDateFormat(
                "mm:ss", Locale.getDefault()
            ).format(it)
        }

        viewModel.getBottomSheetStateLiveData().observe(this) {
            renderBottomSheetState(it)
        }

        binding.btBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btPlay.setOnClickListener { viewModel.playbackControl() }

        binding.btAddToFav.setOnClickListener { viewModel.onFavoriteClicked() }

        binding.btAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.addList.setOnClickListener {
            Log.d("pressed", it.toString())

            viewModel.addRandomPlaylist(
                Playlist(
                    name = UUID.randomUUID().toString(),
                    description = UUID.randomUUID().toString(),
                )
            )
        }

        viewModel.preparePlayer()

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun renderScreenState(track: Track) {

        with(binding) {

            Glide.with(applicationContext).load(Track.getCoverArtwork(track.artworkUrl100))
                .placeholder(R.drawable.ic_placeholder_big).centerCrop().transform(
                    RoundedCorners(
                        this@PlayerActivity.resources.getDimensionPixelSize(R.dimen.rounded_corner_8)
                    )
                ).into(albumCover)

            trackName.text = track.trackName
            artistName.text = track.artistName
            trackLengthValue.text = track.trackTimeMillis
            if (track.collectionName.isNotEmpty()) {
                albumInfoGroup.visibility = View.VISIBLE
                albumValue.text = track.collectionName
            } else {
                albumInfoGroup.visibility = View.GONE
            }
            yearValue.text = track.releaseYear
            genreValue.text = track.primaryGenreName
            countryValue.text = track.country

            if (track.isFavorite) {
                btAddToFav.setImageResource(R.drawable.bt_add_to_fav_on)
            } else {
                btAddToFav.setImageResource(R.drawable.bt_add_to_fav_off)
            }
        }
    }

    private fun renderPlayerState(playerState: PlayerState) {

        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                binding.btPlay.setImageResource(R.drawable.bt_pause)
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                binding.btPlay.setImageResource(R.drawable.bt_play)
                binding.btPlay.alpha = 1.0f
                binding.btPlay.isEnabled = true
            }

            PlayerState.STATE_DEFAULT -> {
                binding.btPlay.alpha = 0.2f
                binding.btPlay.isEnabled = false
            }
        }
    }

    private fun renderBottomSheetState(state: PlayerBottomSheetState) {
        when (state) {
            is PlayerBottomSheetState.Shown -> {
                playlistsAdapter.updateListItems(state.playlists)
            }
            is PlayerBottomSheetState.Hidden -> {

            }
            is PlayerBottomSheetState.TrackAddedResult -> {
                Log.d("renderBottomSheetState", state.result.toString())
            }
        }
    }

    private fun clickDebounced(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun onPlaylistClicked(playlist: Playlist) {
        if (clickDebounced()) {
            Toast.makeText(
                this, playlist.name, Toast.LENGTH_LONG
            ).show()
            viewModel.addTrackToPlaylist(playlist)
        }
    }

    companion object {
        private const val CURRENT_TRACK = "current_track"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L

        fun createArgs(trackJSON: String): Bundle = bundleOf(CURRENT_TRACK to trackJSON)
    }
}