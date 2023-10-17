package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.PlayerScreenState
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.google.gson.Gson
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(CURRENT_TRACK)
        currentTrack = gson.fromJson(json, Track::class.java)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.btBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btPlay.setOnClickListener { viewModel.playbackControl() }

        binding.btAddToFav.setOnClickListener { viewModel.onFavoriteClicked() }

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
                        this@PlayerActivity.resources.getDimensionPixelSize(R.dimen.album_rounded_corner)
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

    companion object {
        private const val CURRENT_TRACK = "current_track"

        fun createArgs(trackJSON: String): Bundle =
            bundleOf(CURRENT_TRACK to trackJSON)
    }
}