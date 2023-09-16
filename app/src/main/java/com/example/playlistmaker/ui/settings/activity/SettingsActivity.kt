package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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