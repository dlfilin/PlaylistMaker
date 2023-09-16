package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.ui.ResourceProvider

class SettingsViewModel(
    private val resourceProvider: ResourceProvider,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val themeLiveData = MutableLiveData<ThemeSettings>()
    fun observeTheme(): LiveData<ThemeSettings> = themeLiveData

    init {
        themeLiveData.postValue(settingsInteractor.getThemeSettings())
    }

    fun toggleDarkTheme(isDarkTheme: Boolean) {
        val theme = ThemeSettings(isDarkTheme)
        themeLiveData.postValue(theme)
        settingsInteractor.updateThemeSetting(theme)
    }

    fun shareApp() {
        val link = resourceProvider.getString(R.string.android_course_link)
        sharingInteractor.shareApp(link)
    }

    fun openTerms() {
        val link = resourceProvider.getString(R.string.ys_offer_link)
        sharingInteractor.openTerms(link)
    }

    fun openSupport() {
        val myEmail = resourceProvider.getString(R.string.my_email)
        val defaultSubject = resourceProvider.getString(R.string.default_email_subject)
        val defaultText = resourceProvider.getString(R.string.default_email_text)

        sharingInteractor.openSupport(EmailData(myEmail, defaultSubject, defaultText))
    }

}