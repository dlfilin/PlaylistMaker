package com.example.playlistmaker.presentation.ui.searchTracks

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context).inflate(R.layout.search_item_view, parentView, false)
) {

    private val itemArtwork: ImageView = itemView.findViewById(R.id.track_art)
    private val itemTrackName: TextView = itemView.findViewById(R.id.track_name)
    private val itemArtistName: TextView = itemView.findViewById(R.id.artist_name)
    private val itemTrackTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_rounded_corner)))
            .into(itemArtwork)
        itemTrackName.text = model.trackName
        itemArtistName.text = model.artistName
        itemTrackTime.text = model.getTrackTimeMMSS()

    }

}