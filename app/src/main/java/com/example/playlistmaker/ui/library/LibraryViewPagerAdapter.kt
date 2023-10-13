package com.example.playlistmaker.ui.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.ui.favorites.FavoritesFragment
import com.example.playlistmaker.ui.playlist.PlaylistFragment

class LibraryViewPagerAdapter(parentFragment: Fragment) :
    FragmentStateAdapter(parentFragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }

}
