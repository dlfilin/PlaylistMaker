package com.example.playlistmaker.app.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.app.CURRENT_TRACK
import com.example.playlistmaker.app.Placeholder
import com.example.playlistmaker.app.TracksRVAdapter
import com.example.playlistmaker.domain.usecases.SearchTracksUseCase
import com.example.playlistmaker.domain.models.TracksListResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.usecases.KEY_TRACKS_HISTORY
import com.example.playlistmaker.domain.usecases.SearchHistoryUseCase
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var savedSearchRequest: String = ""

    private lateinit var backButton: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var clearSearch: ImageView
    private lateinit var trackSearchRV: RecyclerView
    private lateinit var placeholderVG: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderRefreshButton: Button
    private lateinit var tracksHistoryRV: RecyclerView
    private lateinit var tracksHistoryVG: LinearLayout
    private lateinit var clearTracksHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var tracksListResponseCallback: Callback<TracksListResponse>

    private val searchTracksUseCase = SearchTracksUseCase()

    private val sharedPrefs by lazy(LazyThreadSafetyMode.NONE) {
        getSharedPreferences(KEY_TRACKS_HISTORY, MODE_PRIVATE)
    }
    private val searchHistoryUseCase by lazy(LazyThreadSafetyMode.NONE) {
        SearchHistoryUseCase(sharedPrefs)
    }

    private val tracks = ArrayList<Track>()
    private val tracksHistory = ArrayList<Track>()

    private val tracksSearchAdapter = TracksRVAdapter(tracks) { onTrackClicked(it) }
    private val tracksHistoryAdapter = TracksRVAdapter(tracksHistory) { onTrackClicked(it) }

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable =
        Runnable { searchTracksUseCase.execute(savedSearchRequest, tracksListResponseCallback) }

    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        showTracksHistoryVG(searchHistoryUseCase.getTracksFromHistory().toList())

        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        clearSearch.setOnClickListener { handleClearSearchClick() }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideInputKeyboard()
            }
            false
        }

        searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                savedSearchRequest = s.toString()

                if (s.isNullOrEmpty()) {
                    clearSearch.isVisible = false
                    tracksHistoryVG.isVisible = true
                    trackSearchRV.isVisible = false
                    progressBar.isVisible = false
                } else {
                    searchTracksDebounced()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        tracksListResponseCallback = object : Callback<TracksListResponse> {

            override fun onResponse(
                call: Call<TracksListResponse>,
                response: Response<TracksListResponse>
            ) {
                progressBar.isVisible = false
                when (response.code()) {
                    200 -> handleSuccessfulSearch(response)
                    else -> showPlaceholderView(Placeholder.INTERNET_ISSUE)
                }
            }

            override fun onFailure(call: Call<TracksListResponse>, t: Throwable) {
                progressBar.isVisible = false
                showPlaceholderView(Placeholder.INTERNET_ISSUE)
            }
        }

        placeholderRefreshButton.setOnClickListener {
            searchTracksDebounced()
        }

        clearTracksHistoryButton.setOnClickListener {
            searchHistoryUseCase.clearHistory()
            showTracksHistoryVG(emptyList())
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_REQUEST, savedSearchRequest)

        if (tracks.size > 0)
            outState.putString(FOUND_TRACKS, Gson().toJson(tracks))

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedSearchRequest = savedInstanceState.getString(SEARCH_REQUEST, "")
        searchEditText.setText(savedSearchRequest)

        if (savedSearchRequest.isNotEmpty()) {
            val newList = Gson().fromJson(
                savedInstanceState.getString(FOUND_TRACKS, ""),
                Array<Track>::class.java
            )
            tracksSearchAdapter.updateListItems(newList = newList.toList())
        }
    }

    private fun showPlaceholderView(placeholder: Placeholder) {

        tracksSearchAdapter.updateListItems(emptyList())

        placeholderVG.visibility = View.VISIBLE

        when (placeholder) {

            Placeholder.NOTHING_FOUND -> {
                placeholderImage.setImageResource(R.drawable.ic_nothing_found)
                placeholderText.text = getString(R.string.nothing_found)
                placeholderRefreshButton.visibility = View.GONE
            }

            Placeholder.INTERNET_ISSUE -> {
                placeholderImage.setImageResource(R.drawable.ic_internet_issue)
                placeholderText.text = getString(R.string.internet_issue)
                placeholderRefreshButton.visibility = View.VISIBLE
            }

        }
    }

    private fun showTracksHistoryVG(params: List<Track>) {
        tracksHistoryAdapter.updateListItems(params)
        tracksHistoryVG.isVisible = tracksHistory.isNotEmpty()
    }

    private fun handleClearSearchClick() {
        searchEditText.setText("")
        hideInputKeyboard()
        if (tracks.size > 0) {
            tracksSearchAdapter.updateListItems(emptyList())
        } else {
            placeholderVG.visibility = View.GONE
        }
        tracksHistoryVG.isVisible = tracksHistory.isNotEmpty()
    }

    private fun initViews() {
        placeholderVG = findViewById(R.id.placeholder_view)
        placeholderImage = findViewById(R.id.placeholder_image)
        placeholderText = findViewById(R.id.placeholder_text)
        placeholderRefreshButton = findViewById(R.id.placeholder_refresh)
        backButton = findViewById(R.id.search_back)
        searchEditText = findViewById(R.id.searchEditText)
        clearSearch = findViewById(R.id.clearSearch)

        trackSearchRV = findViewById(R.id.rv_track_search)
        trackSearchRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackSearchRV.adapter = tracksSearchAdapter

        tracksHistoryRV = findViewById(R.id.rv_tracks_history)
        tracksHistoryRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksHistoryRV.adapter = tracksHistoryAdapter

        tracksHistoryVG = findViewById(R.id.tracks_history_view)
        clearTracksHistoryButton = findViewById(R.id.tracks_history_clear)

        progressBar = findViewById(R.id.progressBar)

    }

    private fun handleSuccessfulSearch(response: Response<TracksListResponse>) {
        val newList = response.body()?.results
        if (newList?.isNotEmpty() == true) {
            placeholderVG.visibility = View.GONE
            trackSearchRV.isVisible = true
            tracksSearchAdapter.updateListItems(newList)
        } else {
            showPlaceholderView(Placeholder.NOTHING_FOUND)
        }
    }

    private fun onTrackClicked(track: Track) {
        if (clickDebounced()) {
            val resultTracksHistory = searchHistoryUseCase.addTrack(track = track)
            //перейти на экран аудиоплеера
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra(CURRENT_TRACK, Gson().toJson(track))
            startActivity(intent)
            tracksHistoryAdapter.updateListItems(resultTracksHistory)
        }
    }

    private fun searchTracksDebounced() {
        clearSearch.isVisible = true
        tracksHistoryVG.isVisible = false
        trackSearchRV.isVisible = false
        placeholderVG.isVisible = false
        progressBar.isVisible = true

        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        searchEditText.requestFocus()
    }

    companion object {
        private const val SEARCH_REQUEST = "SEARCH_REQUEST"
        private const val FOUND_TRACKS = "FOUND_TRACKS"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}