package com.example.playlistmaker.presentation.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.searchTracks.SearchActivity
import com.example.playlistmaker.presentation.ui.SettingsActivity
import com.example.playlistmaker.presentation.ui.LibraryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var searchButton: Button
    private lateinit var libraryButton: Button
    private lateinit var settingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchButton = findViewById(R.id.searchButton)
        searchButton.setOnClickListener { navigateTo(SearchActivity::class.java) }

        libraryButton = findViewById(R.id.libraryButton)
        libraryButton.setOnClickListener { navigateTo(LibraryActivity::class.java) }

        settingsButton = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener { navigateTo(SettingsActivity::class.java) }
    }

    private fun navigateTo(clazz: Class<out AppCompatActivity>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }
}