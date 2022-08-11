package com.isrna.newsapp.utilitiy

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.preference.PreferenceManager
import com.isrna.newsapp.R
import com.isrna.newsapp.core.services.PastSearchService
import java.util.concurrent.TimeUnit

object Utilities {
    /**
     * Helper function to check whether we got internet connection or not, its only for API >= 29
     * @param connectivityManager ConnectivityManager
     */
    fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }


    private const val PAST_SEARCH_SERVICE_JOB_ID = 1
    /**
     * This function is used to start te Service Job, whose job is to run background checks
     * for articles based on the saved keywords by the user
     * @param context Context
     */
    fun startPastSearchServiceJob(context: Context) {
        val componentName = ComponentName(context, PastSearchService::class.java)
        val info = JobInfo.Builder(PAST_SEARCH_SERVICE_JOB_ID, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .setBackoffCriteria(TimeUnit.MINUTES.toMillis(15), JobInfo.BACKOFF_POLICY_LINEAR)
            .setPeriodic(TimeUnit.MINUTES.toMillis(15))
            .build()

        val scheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(info)
        val editPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit()

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            editPreferences.putBoolean(context.getString(R.string.track_status_notifications_setting_key), true)
        } else {
            editPreferences.putBoolean(context.getString(R.string.track_status_notifications_setting_key), false)
        }
        editPreferences.apply()
    }

    /**
     * This function is used to stop te Service Job, whose job is to run background checks
     * for articles based on the saved keywords by the user
     * @param context Context
     */
    fun stopPastServiceJob(context: Context) {
        val scheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(PAST_SEARCH_SERVICE_JOB_ID)
        val editPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editPreferences.putBoolean(context.getString(R.string.track_status_notifications_setting_key), false)
        editPreferences.apply()
    }

    /**
     * This functions returns the status if the [PastSearchService] is running or not
     * @return [Boolean] true it is running, false it is not running
     */
    fun getPastServiceJobStatus(context: Context): Boolean = PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(context.getString(R.string.track_status_notifications_setting_key), false)
}