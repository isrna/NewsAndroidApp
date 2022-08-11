package com.isrna.newsapp.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import com.isrna.newsapp.R
import com.isrna.newsapp.utilitiy.Utilities
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom android.app.Application implementation
 */
@HiltAndroidApp
class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //Support for android 12 dynamic colors
        DynamicColors.applyToActivitiesIfAvailable(this)

        setupNotifications()
    }

    private fun setupNotifications() {
        //Create notification channel
        val savedSearchesNotificationChannel = NotificationChannel(
            getString(R.string.saved_searches_notification_id),
            getString(R.string.saved_searches_name_notifications),
            NotificationManager.IMPORTANCE_HIGH
        )
        savedSearchesNotificationChannel.description = getString(R.string.saved_searches_description_notifications)
        savedSearchesNotificationChannel.enableLights(true)
        savedSearchesNotificationChannel.lightColor = Color.BLUE
        savedSearchesNotificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)

        //Register the notification channel
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannels(listOf(savedSearchesNotificationChannel))

        //Start the service job to word in background
        if(PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.enable_notifications_setting_key), true))
            Utilities.startPastSearchServiceJob(this)
    }
}