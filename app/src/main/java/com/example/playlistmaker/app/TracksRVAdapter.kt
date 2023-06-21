package com.example.playlistmaker.app

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.OnTrackClickListener
import com.example.playlistmaker.domain.models.Track
import java.util.ArrayList

class TracksRVAdapter(
    private val items: ArrayList<Track>,
    private val onTrackClickListener: OnTrackClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener { onTrackClickListener.onTrackClick(items[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateListItems(newList: List<Track>) {
        if (newList.isEmpty()) {
            val size = items.size
            items.clear()
            this.notifyItemRangeRemoved(0, size)
        } else {
            val diffCallback = TracksDiffCallback(oldList = items, newList = newList)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            items.clear()
            items.addAll(newList)
            diffResult.dispatchUpdatesTo(this)
        }
    }
}