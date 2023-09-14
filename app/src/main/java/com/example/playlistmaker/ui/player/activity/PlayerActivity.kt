package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.CURRENT_TRACK
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.player.models.PlayerState
import com.example.playlistmaker.ui.player.models.PlayerScreenState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentTrack = Gson().fromJson(intent.getStringExtra(CURRENT_TRACK), Track::class.java)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(currentTrack)
        )[PlayerViewModel::class.java]

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    renderTrackInfo(screenState.track)
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
                "mm:ss",
                Locale.getDefault()
            ).format(it)
        }

        binding.btBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.btPlay.setOnClickListener { viewModel.playbackControl() }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun renderTrackInfo(currentTrack: Track) {

        with(binding) {

            Glide.with(applicationContext)
                .load(currentTrack.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder_big)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        this@PlayerActivity.resources
                            .getDimensionPixelSize(R.dimen.album_rounded_corner)
                    )
                )
                .into(albumCover)

            trackName.text = currentTrack.trackName
            artistName.text = currentTrack.artistName
            trackLengthValue.text = currentTrack.getTrackTimeMMSS()
            if (currentTrack.collectionName.isNotEmpty()) {
                albumInfoGroup.visibility = View.VISIBLE
                albumValue.text = currentTrack.collectionName
            } else {
                albumInfoGroup.visibility = View.GONE
            }
            yearValue.text = currentTrack.getReleaseYear().toString()
            genreValue.text = currentTrack.primaryGenreName
            countryValue.text = currentTrack.country
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
}