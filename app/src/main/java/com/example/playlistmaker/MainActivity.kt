package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private fun navigateTo(clazz: Class<out AppCompatActivity>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener { navigateTo(SearchActivity::class.java) }

        val libraryButton = findViewById<Button>(R.id.libraryButton)
        libraryButton.setOnClickListener { navigateTo(LibraryActivity::class.java) }

        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener { navigateTo(SettingsActivity::class.java) }
    }

}