package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerBottomSheetState
import com.example.playlistmaker.presentation.player.PlayerScreenState
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.ui.new_playlist.NewPlaylistFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var currentTrack: Track

    private val gson: Gson by inject()

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(currentTrack)
    }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var bottomSheetCallback: BottomSheetCallback

    private var isClickAllowed = true

    private val playlistsAdapter = PlayerBottomSheetAdaper { onPlaylistClicked(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(CURRENT_TRACK)
        currentTrack = gson.fromJson(json, Track::class.java)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playlistsRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.playlistsRv.adapter = playlistsAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetCallback = object : BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.alpha = 0.5f
                        binding.overlay.visibility = View.VISIBLE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = 0.5f * (1 + slideOffset)
            }

        }
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)


        viewModel.preparePlayer()

        setObservables()

        setOnClickListeners()

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
    }

    private fun setObservables() {
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

        viewModel.getShowSnackBarEvent().observe(this) { pair ->
            if (pair.first) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showSnackBar(getString(R.string.track_added, pair.second))
            } else {
                showSnackBar(getString(R.string.track_not_added, pair.second))
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btPlay.setOnClickListener { viewModel.playbackControl() }

        binding.btAddToFav.setOnClickListener { viewModel.onFavoriteClicked() }

        binding.btAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btNewPlaylist.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, NewPlaylistFragment()).addToBackStack(null)
                .commit()
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun renderScreenState(track: Track) {

        with(binding) {

            Glide.with(applicationContext).load(Track.getCoverArtwork(track.artworkUrl100))
                .placeholder(R.drawable.ic_placeholder_big).transform(
                    CenterCrop(), RoundedCorners(
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
        with(binding) {
            when (state) {
                is PlayerBottomSheetState.Content -> {
                    playlistsAdapter.updateListItems(state.playlists)
                    playlistsRv.isVisible = true
                    placeholderImage.isVisible = false
                    placeholderText.isVisible = false
                }

                is PlayerBottomSheetState.Empty -> {
                    playlistsRv.isVisible = false
                    placeholderImage.isVisible = true
                    placeholderText.isVisible = true
                }
            }
        }
    }

    private fun showSnackBar(toast: String) {
        Snackbar.make(binding.root, toast, Snackbar.LENGTH_LONG).show()
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
            viewModel.addTrackToPlaylist(playlist)
        }
    }

    companion object {
        private const val CURRENT_TRACK = "current_track"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L

        fun createArgs(trackJSON: String): Bundle = bundleOf(CURRENT_TRACK to trackJSON)
    }
}