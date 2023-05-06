package com.example.playlistmaker.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        backButton = findViewById(R.id.settings_back)
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
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.default_email_subject)
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.default_email_text)
                )
                startActivity(this)
            }
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