package com.example.playlistmaker.ui.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemSmallBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.util.ListDiffCallback
import java.util.ArrayList

class PlayerBottomSheetAdaper(private val onPlaylistClickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlayerBottomSheetViewHolder>() {

    private var items = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerBottomSheetViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)

        return PlayerBottomSheetViewHolder(PlaylistItemSmallBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: PlayerBottomSheetViewHolder, position: Int) {
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