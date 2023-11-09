package com.example.playlistmaker.util

import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

class ListDiffCallback<T> (private val oldList: List<T>, private val newList: List<T>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when (oldList[oldItemPosition]) {
            is Track -> (oldList[oldItemPosition] as Track).trackId == (newList[newItemPosition] as Track).trackId
            // if Playlist
            else -> (oldList[oldItemPosition] as Playlist).id == (newList[newItemPosition] as Playlist).id
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
