package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.darkTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.toggleDarkTheme(checked)
        }

        binding.shareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.contactSupport.setOnClickListener {
            viewModel.openSupport()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.openTerms()
        }

        viewModel.observeTheme().observe(this) {
            binding.darkTheme.isChecked = it.isDarkTheme

            App.setDarkTheme(it.isDarkTheme)
        }
    }
}