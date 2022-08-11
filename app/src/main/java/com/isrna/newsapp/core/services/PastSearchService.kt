package com.isrna.newsapp.core.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.isrna.newsapp.feature_article_list.ArticleListActivity
import com.isrna.newsapp.R
import com.isrna.newsapp.feature_search.data.repository.SearchRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This service is a JobService, and its responsible to check every 15 minutes, if a new article
 * is available based on the saved keywords
 */
@AndroidEntryPoint
class PastSearchService : JobService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var searchRepository: SearchRepository

    /**
     * This function is responsible to display a notification for new articles based on saved keywords
     * @param title String
     * @param body String
     * @param contentText String
     */
    private fun showNotification( title: String, body: String, contentText: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = getString(R.string.saved_searches_notification_id)

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            notificationChannelId
        )
        val intent = Intent(this, ArticleListActivity::class.java)
        intent.putExtra("show_search_view", true)
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        notificationBuilder.setAutoCancel(true).setDefaults(android.app.Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body))
            .setContentInfo(title)
            .setContentIntent(contentIntent)
        notificationManager.notify(0, notificationBuilder.build())
    }

    /**
     * This function starts a new Coroutine on the Dispatchers.IO and runs the check for new
     * articles based on saved keywords, and collects all events
     * @param p0 JobParameters?
     */
    private fun startCheckSavedSearches(p0: JobParameters?) {
       scope.launch {
           searchRepository.checkForNewSearchData().collectLatest { event ->
               when (event) {
                   is EventPastSearchService.NewResults -> {
                        val stringBuilderContentText = StringBuilder()
                       stringBuilderContentText.append(getString(R.string.for_keywords))

                       val stringBuilderBigText = StringBuilder()
                       stringBuilderBigText.append(getString(R.string.latest_article_keyword_are))
                       stringBuilderBigText.append(System.getProperty("line.separator"))

                       event.articleTitleList.forEachIndexed { index, pair ->
                           stringBuilderBigText.append("${pair.first} - ${pair.second}")
                           stringBuilderContentText.append(pair.first)

                           if(index != event.articleTitleList.lastIndex) {
                               stringBuilderBigText.append(System.getProperty("line.separator"))
                               stringBuilderContentText.append(", ")
                           }
                       }

                       showNotification(
                           getString(R.string.new_article_notification_title),
                           stringBuilderBigText.toString(),
                           stringBuilderContentText.toString()
                       )
                       jobFinished(p0, false)
                   }
                   is EventPastSearchService.NoResults -> {
                       jobFinished(p0, false)
                   }
                   is EventPastSearchService.Error -> {
                       jobFinished(p0, true)
                   }
               }
           }
       }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        startCheckSavedSearches(p0)
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }
}