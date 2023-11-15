package com.example.playlistmaker.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemViewBinding
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(private val binding: TrackItemViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corner_2)))
            .into(binding.trackArt)

        with(binding) {
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = model.trackTimeMillis
        }
    }

}