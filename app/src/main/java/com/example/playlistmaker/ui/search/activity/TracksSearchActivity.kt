package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.App.Companion.CURRENT_TRACK
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.player.activity.AudioPlayerActivity
import com.example.playlistmaker.ui.search.models.SearchState
import com.example.playlistmaker.ui.search.view_model.TracksSearchViewModel
import com.google.gson.Gson


class TracksSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var viewModel: TracksSearchViewModel

    private lateinit var textWatcher: TextWatcher

    private val tracksSearchAdapter = TracksAdapter { onTrackClicked(it) }
    private val tracksHistoryAdapter = TracksAdapter { onTrackClicked(it) }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            TracksSearchViewModel.getViewModelFactory()
        )[TracksSearchViewModel::class.java]

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {

            rvTrackSearch.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            rvTrackSearch.adapter = tracksSearchAdapter

            rvTracksHistory.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            rvTracksHistory.adapter = tracksHistoryAdapter

            searchBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

            clearSearch.setOnClickListener { handleClearSearchClick() }

            placeholderRefresh.setOnClickListener {
                viewModel.searchDebounced(
                    changedText = searchEditText.text.toString(),
                    debounced = false
                )
            }

            tracksHistoryClear.setOnClickListener {
                viewModel.clearHistory()
            }

            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideInputKeyboard()
                    viewModel.searchDebounced(
                        changedText = binding.searchEditText.text.toString(),
                        debounced = false
                    )
                }
                false
            }

        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                binding.clearSearch.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounced(
                    changedText = s.toString(),
                    debounced = true
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        binding.searchEditText.addTextChangedListener(textWatcher)

        viewModel.observeState().observe(this) {
            render(it)
        }

    }

    private fun clickDebounced(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun hideInputKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        binding.searchEditText.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.searchEditText.removeTextChangedListener(textWatcher)
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.EmptySearch -> showPlaceholder(isEmptyResult = true, null)
            is SearchState.Error -> showPlaceholder(isEmptyResult = false, code = state.code)
            is SearchState.Loading -> showLoading()
            is SearchState.History -> showHistory(state.history)
            is SearchState.ClearScreen -> showClearScreen()
        }
    }

    private fun showClearScreen() {
        with(binding) {
            rvTrackSearch.visibility = View.GONE
            tracksHistoryView.visibility = View.GONE
            placeholderView.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun showLoading() {
        with(binding) {
            rvTrackSearch.visibility = View.GONE
            tracksHistoryView.visibility = View.GONE
            placeholderView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun showPlaceholder(isEmptyResult: Boolean, code: Int?) {

        with(binding) {

            progressBar.visibility = View.GONE
            rvTrackSearch.visibility = View.GONE
            tracksHistoryView.visibility = View.GONE
            placeholderView.visibility = View.VISIBLE

            if (isEmptyResult) {
                placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                placeholderText.text = getString(R.string.nothing_found)
                placeholderRefresh.visibility = View.GONE
            } else {
                placeholderImage.setImageResource(R.drawable.ic_internet_issue)
                placeholderText.text = getString(R.string.internet_issue)
                placeholderRefresh.visibility = View.VISIBLE
            }
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            progressBar.visibility = View.GONE
            placeholderView.visibility = View.GONE
            tracksHistoryView.visibility = View.GONE
            rvTrackSearch.visibility = View.VISIBLE
        }
        tracksSearchAdapter.updateListItems(tracks)
    }

    private fun showHistory(tracks: List<Track>) {
        with(binding) {
            progressBar.visibility = View.GONE
            placeholderView.visibility = View.GONE
            rvTrackSearch.visibility = View.GONE
            tracksHistoryView.visibility = View.VISIBLE
        }
        tracksHistoryAdapter.updateListItems(tracks)
    }

    private fun handleClearSearchClick() {
        binding.searchEditText.setText("")

        viewModel.renderTracksHistory()
    }

    private fun onTrackClicked(track: Track) {
        if (clickDebounced()) {
            viewModel.addTrackToHistory(track = track)

            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra(CURRENT_TRACK, Gson().toJson(track))
            startActivity(intent)
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}