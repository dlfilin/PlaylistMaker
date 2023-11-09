package com.example.playlistmaker.ui.playlists

import android.os.Bundle
import android.util.Log
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
import com.example.playlistmaker.ui.root.RootActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private var isClickAllowed = true

    private val playlistsAdapter = PlaylistsAdapter { onPlaylistClicked(it) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRV.adapter = playlistsAdapter

        binding.newPlaylistButton.setOnClickListener {
            (activity as RootActivity).animateBottomNavigationView()
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            renderScreen(it)
        }

        binding.addList.setOnClickListener {
            Log.d("pressed", it.toString())

            viewModel.addRandomPlaylist(
                Playlist(
                    name = UUID.randomUUID().toString(),
                    description = UUID.randomUUID().toString(),
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
        with(binding) {
            progressBar.isVisible = false
            playlistsRV.isVisible = false
            placeholderView.isVisible = true
        }
    }

    private fun showError() {
        with(binding) {
            progressBar.isVisible = false
            playlistsRV.isVisible = false
            placeholderView.isVisible = true
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        with(binding) {
            progressBar.isVisible = false
            playlistsRV.isVisible = true
            placeholderView.isVisible = false
        }
        playlistsAdapter.updateListItems(playlists)
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            playlistsRV.isVisible = false
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