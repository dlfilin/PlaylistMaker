package com.example.playlistmaker.app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.playlistmaker.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
    }

    enum class Placeholder {
        NOTHING_FOUND,
        INTERNET_ISSUE;
    }

    private var savedSearchRequest: String = ""

    private lateinit var backButton: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var clearSearch: ImageView
    private lateinit var rvTrackSearch: RecyclerView
    private lateinit var placeholderVG: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var placeholderRefreshButton: Button

    private lateinit var tracksListResponseCallback: Callback<TracksListResponse>

    private val searchTracksUseCase = SearchTracksUseCase()

    private val tracks = ArrayList<Track>()

    private val adapter = TrackSearchAdapter(tracks) {
        Toast.makeText(this, it.trackName, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderVG = findViewById(R.id.placeholder_view)
        placeholderImage = findViewById(R.id.placeholder_image)
        placeholderText = findViewById(R.id.placeholder_text)
        placeholderRefreshButton = findViewById(R.id.placeholder_refresh)
        backButton = findViewById(R.id.search_back)
        searchEditText = findViewById(R.id.searchEditText)
        clearSearch = findViewById(R.id.clearSearch)
        rvTrackSearch = findViewById(R.id.rv_track_search)

        backButton.setOnClickListener { finish() }

        clearSearch.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            if (tracks.size > 0) {
                tracks.clear()
                adapter.notifyDataSetChanged()
            }
        }

        val searchTextWatcher = object : TextWatcher {

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
        }

        searchEditText.addTextChangedListener(searchTextWatcher)

        rvTrackSearch.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvTrackSearch.adapter = adapter

        tracksListResponseCallback = object : Callback<TracksListResponse> {

            override fun onResponse(
                call: Call<TracksListResponse>,
                response: Response<TracksListResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val res = response.body()?.results
                        val resCount = response.body()?.resultCount
                        if (res?.isNotEmpty() == true) {
                            placeholderVG.visibility = View.GONE
                            tracks.clear()
                            tracks.addAll(res)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(
                                applicationContext,
                                "Found ${tracks.size} = $resCount",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            showPlaceholderView(Placeholder.NOTHING_FOUND)
                        }
                    }
                    else -> showPlaceholderView(Placeholder.INTERNET_ISSUE)
                }
            }

            override fun onFailure(call: Call<TracksListResponse>, t: Throwable) {
                showPlaceholderView(Placeholder.INTERNET_ISSUE)
                Toast.makeText(
                    applicationContext,
                    t.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(searchEditText.text.toString())
            }
            false
        }

        placeholderRefreshButton.setOnClickListener {
            searchTracks(searchEditText.text.toString())
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_REQUEST, savedSearchRequest)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedSearchRequest = savedInstanceState.getString(SEARCH_REQUEST, "")
        searchEditText.setText(savedSearchRequest)

        if (savedSearchRequest.isNotEmpty()) searchTracks(savedSearchRequest)
    }

    private fun showPlaceholderView(placeholder: Placeholder) {

        tracks.clear()
        adapter.notifyDataSetChanged()

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

    private fun searchTracks(query: String) {
        if (query.isNotEmpty()) {
            searchTracksUseCase.execute(
                query = query,
                callback = tracksListResponseCallback
            )
        }
    }
}