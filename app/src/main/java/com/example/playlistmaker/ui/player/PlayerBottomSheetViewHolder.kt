package com.example.playlistmaker.ui.player

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemSmallBinding
import com.example.playlistmaker.domain.models.Playlist

class PlayerBottomSheetViewHolder(private val binding: PlaylistItemSmallBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        with(binding) {

            Glide.with(itemView)
                .load(playlist.imageUri)
                .fallback(R.drawable.ic_placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.rounded_corner_2)))
                .into(playlistCover)

            playlistName.text = playlist.name
            playlistTrackCount.text =
                itemView.context.applicationContext.resources.getQuantityString(
                    R.plurals.tracks, playlist.tracksCount, playlist.tracksCount
                )
        }
    }

}