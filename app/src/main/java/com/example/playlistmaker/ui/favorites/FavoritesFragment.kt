package com.example.playlistmaker.ui.favorites

import android.os.Bundle
import android.util.Log
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
import com.example.playlistmaker.util.debounce
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoritesViewModel>()

    private var favoriteTracksAdapter: TrackListAdapter? = null

    private lateinit var onTrackClickDebounced: (Track) -> Unit

    private val gson: Gson by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounced = debounce(
            delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false
        ) { track ->
            viewModel.addTrackToHistory(track = track)

            findNavController().navigate(
                R.id.action_libraryFragment_to_playerActivity,
                PlayerActivity.createArgs(gson.toJson(track))
            )
        }

        favoriteTracksAdapter = TrackListAdapter(object : TrackListAdapter.TrackClickListener {
            override fun onTrackClick(item: Track) {
                onTrackClickDebounced(item)
            }

            override fun onTrackLongClick(item: Track) {
                Log.d("XXX", "onTrackLongClick $item")
            }
        })

        binding.favoritesRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesRv.adapter = favoriteTracksAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            renderScreen(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        favoriteTracksAdapter = null
        binding.favoritesRv.adapter = null
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
        showScreenViews(progressVisible = false, recyclerVisible = false, placeholderVisible = true)
    }

    private fun showError() {
        showScreenViews(progressVisible = false, recyclerVisible = false, placeholderVisible = true)
    }

    private fun showContent(tracks: List<Track>) {
        favoriteTracksAdapter?.updateListItems(tracks)
        showScreenViews(progressVisible = false, recyclerVisible = true, placeholderVisible = false)
    }

    private fun showLoading() {
        showScreenViews(progressVisible = true, recyclerVisible = false, placeholderVisible = false)
    }

    private fun showScreenViews(
        progressVisible: Boolean, recyclerVisible: Boolean, placeholderVisible: Boolean
    ) {
        with(binding) {
            progressBar.isVisible = progressVisible
            favoritesRv.isVisible = recyclerVisible
            placeholderImage.isVisible = placeholderVisible
            placeholderText.isVisible = placeholderVisible
        }
    }

    companion object {

        fun newInstance() = FavoritesFragment()

        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 20L

    }
}