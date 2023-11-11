package com.example.playlistmaker.ui.playlists

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBigBinding
import com.example.playlistmaker.domain.models.Playlist

class PlaylistViewHolder(private val binding: PlaylistItemBigBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        Glide.with(itemView)
            .load(model.imageUri)
            .fallback(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corner_2)))
            .into(binding.playlistImage)

        with(binding) {
            playlistName.text = model.name
            playlistTrackCount.text = model.tracksCount.toString()
        }
    }

}