package com.example.playlistmaker.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.settings.activity.SettingsActivity
import com.example.playlistmaker.ui.library.activity.LibraryActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            searchButton.setOnClickListener { navigateTo(SearchActivity::class.java) }

            libraryButton.setOnClickListener { navigateTo(LibraryActivity::class.java) }

            settingsButton.setOnClickListener { navigateTo(SettingsActivity::class.java) }
        }
    }

    private fun navigateTo(clazz: Class<out AppCompatActivity>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }
}