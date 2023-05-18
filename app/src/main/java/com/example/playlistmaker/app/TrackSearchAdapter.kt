package com.example.playlistmaker.app

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.data.Track

class TrackSearchAdapter(
    private val items: List<Track>,
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
}