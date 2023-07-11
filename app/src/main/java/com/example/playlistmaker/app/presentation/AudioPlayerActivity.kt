package com.example.playlistmaker.app.presentation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.app.CURRENT_TRACK
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var currentTrack: Track

    private lateinit var trackTimeRunnable: Runnable

    private var mediaPlayer = MediaPlayer()

    private var playerState = STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = Gson().fromJson(intent.getStringExtra(CURRENT_TRACK), Track::class.java)

        preparePlayer(currentTrack.previewUrl)

        with(binding) {

            btBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            btPlay.isEnabled = false
            btPlay.setOnClickListener { playbackControl() }

            Glide.with(applicationContext)
                .load(currentTrack.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder_big)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        this@AudioPlayerActivity.resources
                            .getDimensionPixelSize(R.dimen.album_rounded_corner)
                    )
                )
                .into(albumCover)

            trackName.text = currentTrack.trackName
            artistName.text = currentTrack.artistName
            trackLengthValue.text = currentTrack.getTrackTimeMMSS()
            playbackTime.text = getString(R.string.track_time_00)
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

        trackTimeRunnable = object : Runnable {
            override fun run() {
                binding.playbackTime.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)

                if (playerState == STATE_PLAYING)
                    handler.postDelayed(this, REFRESH_TRACK_TIME_DELAY)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(trackTimeRunnable)
        mediaPlayer.release()
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)

        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            binding.btPlay.isEnabled = true
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            binding.btPlay.setImageResource(R.drawable.bt_play)
            binding.playbackTime.text = getString(R.string.track_time_00)
            handler.removeCallbacks(trackTimeRunnable)
            playerState = STATE_PREPARED
        }

    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        binding.btPlay.setImageResource(R.drawable.bt_pause)
        handler.post(trackTimeRunnable)
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying)
            mediaPlayer.pause()
        playerState = STATE_PAUSED
        binding.btPlay.setImageResource(R.drawable.bt_play)
        handler.removeCallbacks(trackTimeRunnable)
    }

    private fun playbackControl() {

        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val REFRESH_TRACK_TIME_DELAY = 400L
    }

}