package com.example.playlistmaker.ui.search.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SearchItemViewBinding
import com.example.playlistmaker.domain.models.Track
import java.util.ArrayList

class TracksAdapter(private val onTrackClickListener: TrackClickListener) :
    RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    private var items = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)

        return TrackViewHolder(SearchItemViewBinding.inflate(layoutInspector, parent, false))
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

    class TrackViewHolder(private val binding: SearchItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Track) {
            Glide.with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_rounded_corner)))
                .into(binding.trackArt)

            with(binding) {
                trackName.text = model.trackName
                artistName.text = model.artistName
                trackTime.text = model.getTrackTimeMMSS()
            }
        }

    }

    class TracksDiffCallback(private val oldList: List<Track>, private val newList: List<Track>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].trackId == newList[newItemPosition].trackId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    fun interface TrackClickListener {
        fun onTrackClick(item: Track)
    }

}