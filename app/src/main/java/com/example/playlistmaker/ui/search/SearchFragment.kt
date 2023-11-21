package com.example.playlistmaker.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.SearchScreenState
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.util.debounce
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    private var tracksSearchAdapter: TrackListAdapter? = null
    private var tracksHistoryAdapter: TrackListAdapter? = null

    private lateinit var onTrackClickDebounced: (Track) -> Unit

    private lateinit var textWatcher: TextWatcher

    private val gson: Gson by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

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
                R.id.action_searchFragment_to_playerActivity,
                PlayerActivity.createArgs(gson.toJson(track))
            )
        }
        tracksSearchAdapter = TrackListAdapter(object : TrackListAdapter.TrackClickListener {
            override fun onTrackClick(item: Track) {
                onTrackClickDebounced(item)
            }

            override fun onTrackLongClick(item: Track) {
                Log.d("XXX", "onTrackLongClick $item")
            }
        })

        tracksHistoryAdapter = TrackListAdapter(object : TrackListAdapter.TrackClickListener {
            override fun onTrackClick(item: Track) {
                onTrackClickDebounced(item)
            }

            override fun onTrackLongClick(item: Track) {
                Log.d("XXX", "onTrackLongClick $item")
            }
        })

        with(binding) {
            rvTrackSearch.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvTrackSearch.adapter = tracksSearchAdapter

            rvTracksHistory.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvTracksHistory.adapter = tracksHistoryAdapter
        }
        setListeners()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracksSearchAdapter = null
        tracksHistoryAdapter = null
        binding.rvTrackSearch.adapter = null
        binding.rvTracksHistory.adapter = null
        textWatcher.let { binding.searchEditText.removeTextChangedListener(it) }
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearSearchCoroutine()
    }

    private fun setListeners() {

        with(binding) {
            clearSearch.setOnClickListener { handleClearSearchClick() }

            placeholderRefresh.setOnClickListener {
                viewModel.searchDebounced(
                    changedText = searchEditText.text.toString(), debounced = false
                )
            }

            tracksHistoryClear.setOnClickListener {
                viewModel.clearHistory()
            }

            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchEditText.text.isNotBlank()) {
                        hideInputKeyboard()
                        viewModel.searchDebounced(
                            changedText = searchEditText.text.toString(), debounced = false
                        )
                    }
                }
                true
            }

            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    clearSearch.isVisible = !s.isNullOrEmpty()
                    viewModel.searchDebounced(
                        changedText = s.toString(), debounced = true
                    )
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
            searchEditText.addTextChangedListener(textWatcher)
        }
    }

    private fun hideInputKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun render(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Content -> showContent(state.tracks)
            is SearchScreenState.EmptySearch -> showPlaceholder(isEmptyResult = true, null)
            is SearchScreenState.Error -> showPlaceholder(isEmptyResult = false, code = state.code)
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.History -> showHistory(state.history)
            is SearchScreenState.ClearScreen -> showClearScreen()
        }
    }

    private fun showClearScreen() {
        showScreenViews(
            progressVisible = false,
            searchRvVisible = false,
            historyRvVisible = false,
            placeholderVisible = false
        )
    }

    private fun showLoading() {
        showScreenViews(
            progressVisible = true,
            searchRvVisible = false,
            historyRvVisible = false,
            placeholderVisible = false
        )
    }

    private fun showPlaceholder(isEmptyResult: Boolean, code: Int?) {
        with(binding) {
            if (isEmptyResult) {
                placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                placeholderText.text = getString(R.string.nothing_found)
                placeholderRefresh.visibility = View.GONE
            } else {
                placeholderImage.setImageResource(R.drawable.ic_internet_issue)
                placeholderText.text = getString(R.string.internet_issue)
                placeholderRefresh.visibility = View.VISIBLE
            }
            showScreenViews(
                progressVisible = false,
                searchRvVisible = false,
                historyRvVisible = false,
                placeholderVisible = true
            )
        }
    }

    private fun showContent(tracks: List<Track>) {
        tracksSearchAdapter?.updateListItems(tracks)
        showScreenViews(
            progressVisible = false,
            searchRvVisible = true,
            historyRvVisible = false,
            placeholderVisible = false
        )
    }

    private fun showHistory(tracks: List<Track>) {
        tracksHistoryAdapter?.updateListItems(tracks)
        showScreenViews(
            progressVisible = false,
            searchRvVisible = false,
            historyRvVisible = true,
            placeholderVisible = false
        )
    }

    private fun showScreenViews(
        progressVisible: Boolean,
        searchRvVisible: Boolean,
        historyRvVisible: Boolean,
        placeholderVisible: Boolean
    ) {
        with(binding) {
            progressBar.isVisible = progressVisible
            placeholderView.isVisible = placeholderVisible
            rvTrackSearch.isVisible = searchRvVisible
            tracksHistoryView.isVisible = historyRvVisible
        }
    }

    private fun handleClearSearchClick() {
        binding.searchEditText.setText("")

        viewModel.renderTracksHistory()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 20L
    }
}