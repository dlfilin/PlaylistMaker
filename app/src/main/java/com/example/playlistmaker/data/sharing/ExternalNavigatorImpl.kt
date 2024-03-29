package com.example.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareText(text: String) {
        val shareIntent = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    text
                )
                type = "text/plain"
            }, null
        )
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        context.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(this)
        }
    }

    override fun openEmail(email: EmailData) {

        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
            putExtra(
                Intent.EXTRA_SUBJECT,
                email.subject
            )
            putExtra(
                Intent.EXTRA_TEXT,
                email.text
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

            context.startActivity(this)
        }
    }
}