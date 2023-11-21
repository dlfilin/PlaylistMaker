package com.example.playlistmaker.ui.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.playlist.TracksBottomSheetState
import com.example.playlistmaker.presentation.playlist.PlaylistViewModel
import com.example.playlistmaker.presentation.playlist.PlaylistScreenState
import com.example.playlistmaker.presentation.playlist.PlaylistSingleEventState
import com.example.playlistmaker.ui.edit_playlist.EditPlaylistFragment
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.TrackListAdapter
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.max
import kotlin.math.roundToInt


class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel> {
        parametersOf(
            requireArguments().getLong(CURRENT_PLAYLIST)
        )
    }

    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var tracksBottomSheetCallback: BottomSheetBehavior.BottomSheetCallback

    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var menuBottomSheetCallback: BottomSheetBehavior.BottomSheetCallback

    private var tracksAdapter: TrackListAdapter? = null

    private lateinit var onTrackClickDebounced: (Track) -> Unit

    private val gson: Gson by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)

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
                R.id.action_playlistFragment_to_playerActivity,
                PlayerActivity.createArgs(gson.toJson(track))
            )
        }

        tracksAdapter = TrackListAdapter(object : TrackListAdapter.TrackClickListener {
            override fun onTrackClick(item: Track) {
                onTrackClickDebounced(item)
            }

            override fun onTrackLongClick(item: Track) {
                MaterialAlertDialogBuilder(
                    requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog
                ).setTitle(getString(R.string.delete_track_dialog_title))
                    .setMessage(getString(R.string.delete_track_dialog_text))
                    .setNegativeButton(getString(R.string.delete_no)) { _, _ ->
                    }.setPositiveButton(getString(R.string.delete_yes)) { _, _ ->
                        viewModel.deleteTrackFromPlaylist(item)
                    }.show()
            }
        })

        binding.tracksRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksRv.adapter = tracksAdapter

        setListeners()

        setBottomSheets()

        setObservables()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracksAdapter = null
        binding.tracksRv.adapter = null
        tracksBottomSheetBehavior.removeBottomSheetCallback(tracksBottomSheetCallback)
        menuBottomSheetBehavior.removeBottomSheetCallback(menuBottomSheetCallback)
        _binding = null
    }

    private fun setListeners() {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        with(binding) {

            btBack.setOnClickListener { findNavController().navigateUp() }

            btShare.setOnClickListener { viewModel.sharePlaylist() }

            btMenu.setOnClickListener {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            btMenuShare.setOnClickListener {
                viewModel.sharePlaylist()
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            btMenuEditPlaylist.setOnClickListener {
                findNavController().navigate(
                    R.id.action_playlistFragment_to_editPlaylistFragment,
                    EditPlaylistFragment.createArgs(
                        requireArguments().getLong(CURRENT_PLAYLIST).toString()
                    )
                )
            }

            btMenuDeletePlaylist.setOnClickListener {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                MaterialAlertDialogBuilder(
                    requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog
                ).setTitle(getString(R.string.menu_delete_playlist))
                    .setMessage(getString(R.string.delete_playlist_dialog_text))
                    .setNegativeButton(getString(R.string.delete_no)) { _, _ ->
                    }.setPositiveButton(getString(R.string.delete_yes)) { _, _ ->
                        viewModel.deletePlaylist()
                    }.show()
            }

            binding.overlay.setOnClickListener {
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

    }

    private fun setBottomSheets() {
        //TRACKS
        var bottomPeekHeight: Int
        with(resources.displayMetrics) {
            //166dp - from image bottom to btShare bottom
            bottomPeekHeight = max((heightPixels - widthPixels - (166 * density)).toInt(), 150)
        }

        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            peekHeight = bottomPeekHeight
        }

        tracksBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.alpha = 0.5f
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = 0.5f * (1 + slideOffset)
            }

        }
        tracksBottomSheetBehavior.addBottomSheetCallback(tracksBottomSheetCallback)

        //MENU
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.alpha = 0.5f
                        binding.overlay.visibility = View.VISIBLE
                    }

                    else -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = 0.5f * (1 + slideOffset)
            }

        }
        menuBottomSheetBehavior.addBottomSheetCallback(menuBottomSheetCallback)
    }

    private fun setObservables() {
        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistScreenState.Content -> {
                    showContent(state.playlist)
                }

                is PlaylistScreenState.Loading -> {
                    showLoading()
                }

                is PlaylistScreenState.Error -> {
                    showError()
                }
            }
        }

        viewModel.getBottomSheetStateLiveData().observe(viewLifecycleOwner) { state ->
            with(binding) {
                val tracks = (state as TracksBottomSheetState.Content).tracks
                if (tracks.isNotEmpty()) {
                    placeholderText.isVisible = false
                    val minutes = (state.tracks.sumOf { it.trackTimeMillis } / 60000f).roundToInt()

                    playlistDuration.text = resources.getQuantityString(
                        R.plurals.minutes, minutes, minutes
                    )

                    tracksAdapter?.updateListItems(state.tracks)
                    tracksRv.isVisible = true
                    tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    placeholderText.isVisible = true
                    tracksRv.isVisible = false
                    playlistDuration.text = resources.getQuantityString(
                        R.plurals.minutes, 0, 0
                    )
                }
            }
        }

        viewModel.getPlaylistSingleEventState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistSingleEventState.TrackDeleted -> {
                    if (state.result) showSnackBar(
                        getString(
                            R.string.track_deleted_confirmation, state.name
                        )
                    )
                    else showSnackBar(getString(R.string.something_wrong))
                }

                is PlaylistSingleEventState.PlaylistShared -> {
                    if (state.result) {
                        Log.d("XXX", "playlist was shared successfully")
                    } else {
                        showSnackBar(getString(R.string.no_tracks_for_sharing))
                    }
                }

                is PlaylistSingleEventState.PlaylistDeleted -> {
                    if (state.result) {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun showSnackBar(toast: String) {
        Snackbar.make(binding.root, toast, Snackbar.LENGTH_LONG).show()
    }

    private fun showScreenViews(
        progressVisible: Boolean, playlistVisible: Boolean, placeholderVisible: Boolean
    ) {
        with(binding) {
            progressBar.isVisible = progressVisible
            playlistGroup.isVisible = playlistVisible
            placeholder.isVisible = placeholderVisible
        }
    }

    private fun showLoading() {
        showScreenViews(progressVisible = true, playlistVisible = false, placeholderVisible = false)
    }

    private fun showError() {
        showScreenViews(progressVisible = false, playlistVisible = false, placeholderVisible = true)
    }

    private fun showContent(playlist: Playlist) {
        with(binding) {
            Glide.with(this@PlaylistFragment).load(playlist.imageUri)
                .fallback(R.drawable.ic_placeholder_big).transform(
                    CenterCrop()
                ).into(playlistCover)

            Glide.with(this@PlaylistFragment).load(playlist.imageUri)
                .fallback(R.drawable.ic_placeholder).transform(
                    CenterCrop(),
                    RoundedCorners(resources.getDimensionPixelSize(R.dimen.rounded_corner_2))
                ).into(menuPlaylistCover)

            playlistName.text = playlist.name
            menuPlaylistName.text = playlist.name

            if (playlist.description != null) {
                playlistDescription.text = playlist.description
                playlistDescription.isVisible = true
            } else {
                playlistDescription.text = ""
                playlistDescription.isVisible = false
            }

            val trackCountStr = resources.getQuantityString(
                R.plurals.tracks, playlist.tracksCount, playlist.tracksCount
            )
            playlistTrackCount.text = trackCountStr
            menuPlaylistTrackCount.text = trackCountStr
        }
        showScreenViews(progressVisible = false, playlistVisible = true, placeholderVisible = false)
    }

    companion object {
        private const val CURRENT_PLAYLIST = "current_playlist"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 20L

        fun createArgs(playlistId: Long): Bundle = bundleOf(
            CURRENT_PLAYLIST to playlistId,
        )
    }
}
