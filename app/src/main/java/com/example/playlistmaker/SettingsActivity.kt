package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.settings_back)
        backButton.setOnClickListener { finish() }

        val shareAppButton = findViewById<TextView>(R.id.share_app)
        shareAppButton.setOnClickListener {

            val shareIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.android_course_link)
                )
                type = "text/plain"
            }, null)
            startActivity(shareIntent)
        }

        val contactSupportButton = findViewById<TextView>(R.id.contact_support)
        contactSupportButton.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SENDTO)
            sendIntent.data = Uri.parse("mailto:")
            sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            sendIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.default_email_subject)
            )
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.default_email_text)
            )
            startActivity(sendIntent)
        }

        val userAgreementButton = findViewById<TextView>(R.id.user_agreement)
        userAgreementButton.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.ys_offer_link))
                )
            )
        }
    }
}