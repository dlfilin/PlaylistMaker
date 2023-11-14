package com.example.playlistmaker.ui.playlists

import android.net.Uri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBigBinding
import com.example.playlistmaker.domain.models.Playlist

class PlaylistViewHolder(private val binding: PlaylistItemBigBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {

        with(binding) {

            if (playlist.imageUri == null) {
                playlistImage.isVisible = false
                placeholderImage.isVisible = true
            } else {
                playlistImage.isVisible = true
                placeholderImage.isVisible = false
                playlistImage.setImageURI(Uri.parse(playlist.imageUri.toString()))
            }

            playlistName.text = playlist.name
            playlistTrackCount.text =
                itemView.context.applicationContext.resources.getQuantityString(
                    R.plurals.tracks, playlist.tracksCount, playlist.tracksCount
                )

        }
    }

}