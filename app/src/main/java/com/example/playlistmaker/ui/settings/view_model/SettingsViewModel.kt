package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.creator.Creator

class SettingsViewModel(
    private val application: Application,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {

    private val themeLiveData = MutableLiveData<ThemeSettings>()

    init {
        themeLiveData.value = settingsInteractor.getThemeSettings()
    }

    fun observeTheme(): LiveData<ThemeSettings> = themeLiveData

    fun toggleDarkTheme(isDarkTheme: Boolean) {
        val theme = ThemeSettings(isDarkTheme)
        themeLiveData.postValue(theme)
        settingsInteractor.updateThemeSetting(theme)
    }

    fun shareApp() {
        val link = application.getString(R.string.android_course_link)
        sharingInteractor.shareApp(link)
    }

    fun openTerms() {
        val link = application.getString(R.string.ys_offer_link)
        sharingInteractor.openTerms(link)
    }

    fun openSupport() {
        val myEmail = application.getString(R.string.my_email)
        val defaultSubject = application.getString(R.string.default_email_subject)
        val defaultText = application.getString(R.string.default_email_text)

        sharingInteractor.openSupport(EmailData(myEmail, defaultSubject, defaultText))
    }

    companion object {

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                SettingsViewModel(
                    app,
                    Creator.provideSharingInteractor(app),
                    Creator.provideSettingsInteractor(app)
                )
            }
        }
    }
}