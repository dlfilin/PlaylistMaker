package com.example.playlistmaker.app.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
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

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
        const val FOUND_TRACKS = "FOUND_TRACKS"
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        showTracksHistoryVG(searchHistoryUseCase.getTracksFromHistory().toList())

        backButton.setOnClickListener { finish() }

        clearSearch.setOnClickListener { handleClearSearchClick() }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (savedSearchRequest.isNotEmpty()) {
                    searchTracksUseCase.execute(
                        savedSearchRequest,
                        callback = tracksListResponseCallback
                    )
                    tracksHistoryVG.isVisible = false
                }
            }
            false
        }

        searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearch.isVisible = !s.isNullOrEmpty()
                savedSearchRequest = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        })

        tracksListResponseCallback = object : Callback<TracksListResponse> {

            override fun onResponse(
                call: Call<TracksListResponse>,
                response: Response<TracksListResponse>
            ) {
                when (response.code()) {
                    200 -> handleSuccessfulSearch(response)
                    else -> showPlaceholderView(Placeholder.INTERNET_ISSUE)
                }
            }

            override fun onFailure(call: Call<TracksListResponse>, t: Throwable) {
                showPlaceholderView(Placeholder.INTERNET_ISSUE)
            }
        }

        placeholderRefreshButton.setOnClickListener {
            searchTracksUseCase.execute(savedSearchRequest, callback = tracksListResponseCallback)
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
            tracks.clear()
            tracks.addAll(
                Gson().fromJson(
                    savedInstanceState.getString(FOUND_TRACKS, ""),
                    Array<Track>::class.java
                )
            )
            tracksSearchAdapter.notifyDataSetChanged()
        }
    }

    private fun showPlaceholderView(placeholder: Placeholder) {

        tracks.clear()
        tracksSearchAdapter.notifyDataSetChanged()

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
        tracksHistory.clear()
        if (params.isNotEmpty())
            tracksHistory.addAll(params)
        tracksHistoryAdapter.notifyDataSetChanged()
        tracksHistoryVG.isVisible = tracksHistory.isNotEmpty()
    }

    private fun handleClearSearchClick() {
        searchEditText.setText("")
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        if (tracks.size > 0) {
            tracks.clear()
            tracksSearchAdapter.notifyDataSetChanged()
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

    }

    private fun handleSuccessfulSearch(response: Response<TracksListResponse>) {
        val res = response.body()?.results
        if (res?.isNotEmpty() == true) {
            placeholderVG.visibility = View.GONE
            tracks.clear()
            tracks.addAll(res)
            tracksSearchAdapter.notifyDataSetChanged()
        } else {
            showPlaceholderView(Placeholder.NOTHING_FOUND)
        }
    }

    private fun onTrackClicked(track: Track) {
        val resultTracksHistory = searchHistoryUseCase.addTrack(track = track)
        //перейти на экран аудиоплеера
        startActivity(Intent(this, LibraryActivity::class.java))
        Toast.makeText(this, "Playing ${track.trackName}", Toast.LENGTH_SHORT).show()
        tracksHistory.clear()
        tracksHistory.addAll(resultTracksHistory)
        tracksHistoryAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        Log.e("filin", "onStart")

        searchEditText.requestFocus()
    }

}