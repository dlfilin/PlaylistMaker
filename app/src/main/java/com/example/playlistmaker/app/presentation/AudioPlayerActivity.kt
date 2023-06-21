package com.example.playlistmaker.app.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btBack.setOnClickListener { finish() }

        val currentTrack: Track =
            Gson().fromJson(intent.getStringExtra("CURRENT_TRACK"), Track::class.java)

        with(binding) {

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
            val length = currentTrack.getTrackTimeMMSS()
            trackLengthValue.text = length
            playbackTime.text = length
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

}