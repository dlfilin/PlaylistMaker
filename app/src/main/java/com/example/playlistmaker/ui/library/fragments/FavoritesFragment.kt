package com.example.playlistmaker.ui.library.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.library.models.FavoritesScreenState
import com.example.playlistmaker.ui.library.view_model.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
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
    }

    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            favoritesRV.isVisible = false
            placeholderView.isVisible = false
        }
    }


    companion object {
        fun newInstance() = FavoritesFragment()
    }
}