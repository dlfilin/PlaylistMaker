package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp(link: String) {
        externalNavigator.shareText(link)
    }

    override fun openTerms(link: String) {
        externalNavigator.openLink(link)
    }

    override fun openSupport(emailData: EmailData) {
        externalNavigator.openEmail(emailData)
    }

    override fun shareMessage(message: String) {
        externalNavigator.shareText(message)
    }

}