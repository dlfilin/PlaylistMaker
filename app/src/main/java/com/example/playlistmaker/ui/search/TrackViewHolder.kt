package com.example.playlistmaker.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemViewBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.getCoverArtwork60
import com.example.playlistmaker.domain.models.getTrackTimeMMSS

class TrackViewHolder(
    private val binding: TrackItemViewBinding,
    private val clickListener: TrackListAdapter.TrackClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        Glide.with(itemView)
            .load(track.getCoverArtwork60())
            .placeholder(R.drawable.ic_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corner_2))
            ).into(binding.trackArt)

        with(binding) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.getTrackTimeMMSS()
        }
        itemView.setOnClickListener { clickListener.onTrackClick(track) }
        itemView.setOnLongClickListener {
            clickListener.onTrackLongClick(track)
            true
        }
    }

}