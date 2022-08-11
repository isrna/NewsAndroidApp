package com.isrna.newsapp.feature_settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.isrna.newsapp.R
import com.isrna.newsapp.databinding.SettingsActivityBinding
import com.isrna.newsapp.feature_settings.ui.SettingsFragment

/**
 * This activity is used for Settings and its displaying the [SettingsFragment]
 */
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding
    private lateinit var topAppBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        topAppBar = binding.globalAppBar.topAppBar

        setTopAppBar()

        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Setup the [MaterialToolbar]
     */
    private fun setTopAppBar() {
        topAppBar.title = ""
        setSupportActionBar(topAppBar)

        topAppBar.setLogo(R.mipmap.ic_launcher_foreground)
        topAppBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        topAppBar.setNavigationOnClickListener {
            this@SettingsActivity.onBackPressed()
        }
    }
}