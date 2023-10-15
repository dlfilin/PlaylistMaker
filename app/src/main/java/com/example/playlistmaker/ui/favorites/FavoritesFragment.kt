package com.example.playlistmaker.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.favorites.FavoritesScreenState
import com.example.playlistmaker.presentation.favorites.FavoritesViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.TrackListAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoritesViewModel>()

    private var favoriteTracksAdapter: TrackListAdapter? = null

    private var isClickAllowed = true

    private val gson: Gson by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteTracksAdapter = TrackListAdapter { onTrackClicked(track = it) }

        binding.favoritesRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesRV.adapter = favoriteTracksAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            renderScreen(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        favoriteTracksAdapter = null
        binding.favoritesRV.adapter = null
        _binding = null
    }

    private fun renderScreen(state: FavoritesScreenState) {
        when (state) {
            is FavoritesScreenState.Loading -> showLoading()
            is FavoritesScreenState.EmptyList -> showEmpty()
            is FavoritesScreenState.Content -> showContent(state.tracks)
            is FavoritesScreenState.Error -> showError()
        }
    }

    private fun showEmpty() {
        with(binding) {
            progressBar.isVisible = false
            favoritesRV.isVisible = false
            placeholderView.isVisible = true
        }
    }

    private fun showError() {
        with(binding) {
            progressBar.isVisible = false
            favoritesRV.isVisible = false
            placeholderView.isVisible = true
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            progressBar.isVisible = false
            favoritesRV.isVisible = true
            placeholderView.isVisible = false
        }
        favoriteTracksAdapter?.updateListItems(tracks)
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            favoritesRV.isVisible = false
            placeholderView.isVisible = false
        }
    }

    private fun clickDebounced(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun onTrackClicked(track: Track) {
        if (clickDebounced()) {
            viewModel.addTrackToHistory(track = track)

            findNavController().navigate(
                R.id.action_libraryFragment_to_playerActivity,
                PlayerActivity.createArgs(gson.toJson(track)))

        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.fillData()
    }

    companion object {

        fun newInstance() = FavoritesFragment()

        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L

    }
}