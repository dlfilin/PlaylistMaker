package com.example.playlistmaker.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBigBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.util.ListDiffCallback
import java.util.ArrayList

class PlaylistsAdapter(private val onPlaylistClickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    private var items = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)

        return PlaylistViewHolder(PlaylistItemBigBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener { onPlaylistClickListener.onPlaylistClick(items[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateListItems(newList: List<Playlist>) {
        if (newList.isEmpty()) {
            val size = items.size
            items.clear()
            this.notifyItemRangeRemoved(0, size)
        } else {
            val diffCallback = ListDiffCallback(oldList = items, newList = newList)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            items.clear()
            items.addAll(newList)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(item: Playlist)
    }

}