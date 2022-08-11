package com.isrna.newsapp.feature_settings.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.isrna.newsapp.R
import com.isrna.newsapp.utilitiy.Utilities

/**
 * This fragment is holding the xml.root_preferences and its based on [PreferenceFragmentCompat]
 * to show app settings
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.let {  it.registerOnSharedPreferenceChangeListener(this) }
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.let { it.unregisterOnSharedPreferenceChangeListener(this) }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, tag: String?) {
        if(tag == getString(R.string.enable_notifications_setting_key))
            sharedPreferences?.let {
                if(it.getBoolean(getString(R.string.enable_notifications_setting_key), false))
                    context?.let { context -> Utilities.startPastSearchServiceJob(context) }
                else
                    context?.let { context -> Utilities.stopPastServiceJob(context) }
            }
    }


}