package com.example.playlistmaker.ui.player

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemSmallBinding
import com.example.playlistmaker.domain.models.Playlist

class PlayerBottomSheetViewHolder(private val binding: PlaylistItemSmallBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        Glide.with(itemView)
            .load(model.imageUri)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corner_2)))
            .into(binding.playlistCover)

        with(binding) {
            playlistName.text = model.name
            playlistTrackCount.text = model.trackIds.size.toString()
        }
    }

}