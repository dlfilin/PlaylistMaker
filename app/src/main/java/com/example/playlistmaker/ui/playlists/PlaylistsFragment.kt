package com.example.playlistmaker.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.playlists.PlaylistsScreenState
import com.example.playlistmaker.presentation.playlists.PlaylistsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private var isClickAllowed = true

    private var _playlistsAdapter: PlaylistsAdapter? = null
    private val playlistsAdapter get() = _playlistsAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        _playlistsAdapter = PlaylistsAdapter { onPlaylistClicked(playlist = it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRv.adapter = playlistsAdapter

        binding.btNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            renderScreen(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _playlistsAdapter = null
        binding.playlistsRv.adapter = null
        _binding = null
    }

    private fun renderScreen(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.Loading -> showLoading()
            is PlaylistsScreenState.EmptyList -> showEmpty()
            is PlaylistsScreenState.Content -> showContent(state.playlists)
            is PlaylistsScreenState.Error -> showError()
        }
    }

    private fun showEmpty() {
        showScreenViews(progressVisible = false, recyclerVisible = false, placeholderVisible = true)
    }

    private fun showError() {
        showScreenViews(progressVisible = false, recyclerVisible = false, placeholderVisible = true)
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistsAdapter.updateListItems(playlists)
        showScreenViews(progressVisible = false, recyclerVisible = true, placeholderVisible = false)
    }

    private fun showLoading() {
        showScreenViews(progressVisible = true, recyclerVisible = false, placeholderVisible = false)
    }

    private fun showScreenViews(progressVisible: Boolean, recyclerVisible: Boolean, placeholderVisible: Boolean) {
        with(binding) {
            progressBar.isVisible = progressVisible
            playlistsRv.isVisible = recyclerVisible
            placeholderImage.isVisible = placeholderVisible
            placeholderText.isVisible = placeholderVisible
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

    private fun onPlaylistClicked(playlist: Playlist) {
        if (clickDebounced()) {
            Toast.makeText(
                requireContext(), playlist.name, Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()

        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L

    }
}