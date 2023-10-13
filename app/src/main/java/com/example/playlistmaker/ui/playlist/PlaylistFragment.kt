package com.example.playlistmaker.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.playlist.PlaylistScreenState
import com.example.playlistmaker.presentation.playlist.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()

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

    private fun renderScreen(state: PlaylistScreenState) {
        when (state) {
            is PlaylistScreenState.Loading -> showLoading()
            is PlaylistScreenState.EmptyList -> showEmpty()
            is PlaylistScreenState.Content -> showContent(state.playlists)
            is PlaylistScreenState.Error -> showError()
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
        fun newInstance() = PlaylistFragment()
    }
}