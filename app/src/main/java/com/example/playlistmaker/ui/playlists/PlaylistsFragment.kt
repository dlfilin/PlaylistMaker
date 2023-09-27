package com.example.playlistmaker.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.playlists.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            renderScreen(it)
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
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            playlistsRV.isVisible = false
            placeholderView.isVisible = false
        }
    }


    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}