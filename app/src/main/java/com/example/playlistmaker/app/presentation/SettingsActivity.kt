package com.example.playlistmaker.app.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var shareAppButton: TextView
    private lateinit var contactSupportButton: TextView
    private lateinit var userAgreementButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        backButton = findViewById(R.id.settings_back)
        backButton.setOnClickListener { finish() }

        themeSwitcher = findViewById(R.id.darkTheme)
        themeSwitcher.isChecked = (applicationContext as App).isDarkThemeOn

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        shareAppButton = findViewById(R.id.share_app)
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

        contactSupportButton = findViewById(R.id.contact_support)
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

        userAgreementButton = findViewById(R.id.user_agreement)
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