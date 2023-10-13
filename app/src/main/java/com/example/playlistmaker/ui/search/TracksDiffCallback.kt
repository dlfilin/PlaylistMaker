package com.example.playlistmaker.ui.search

import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.domain.models.Track

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
