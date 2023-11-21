package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackItemViewBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.ListDiffCallback

class TrackListAdapter(private val onTrackClickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    private var items = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)

        return TrackViewHolder(
            TrackItemViewBinding.inflate(layoutInspector, parent, false), onTrackClickListener
        )
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
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
            val diffCallback = ListDiffCallback(oldList = items, newList = newList)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            items.clear()
            items.addAll(newList)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    interface TrackClickListener {
        fun onTrackClick(item: Track)
        fun onTrackLongClick(item: Track)
    }

}