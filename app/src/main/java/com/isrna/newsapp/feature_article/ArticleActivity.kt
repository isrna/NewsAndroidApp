package com.isrna.newsapp.feature_article

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.isrna.newsapp.R
import com.isrna.newsapp.databinding.ActivityArticleBinding
import com.isrna.newsapp.feature_article.ui.ArticleUIEvent
import com.isrna.newsapp.feature_article.ui.ArticleViewModel
import com.isrna.newsapp.feature_webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Activity for displaying single [Article]
 */
@AndroidEntryPoint
class ArticleActivity : AppCompatActivity() {
    private val articleViewModel: ArticleViewModel by viewModels()

    private lateinit var binding: ActivityArticleBinding
    private lateinit var topAppBar: MaterialToolbar

    private lateinit var articleImageView: ImageView
    private lateinit var articleTitle: TextView
    private lateinit var articleContent: TextView
    private lateinit var calendarIcon: ImageView
    private lateinit var articlePublishingDate: TextView
    private lateinit var authorIcon: ImageView
    private lateinit var articleAuthor: TextView

    private lateinit var readMoreButton: Button
    private lateinit var shareFacebook: ImageButton
    private lateinit var shareTwitter: ImageButton
    private lateinit var shareWhatsApp: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get Intent data
        articleViewModel.articleId = intent.getIntExtra("articleId", 0)
        articleViewModel.searchBased = intent.getBooleanExtra("search", false)

        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Store UI elements
        topAppBar = binding.globalAppBar.topAppBar
        articleTitle = binding.articleTitle
        articleContent = binding.articleContent
        calendarIcon = binding.calendarIcon
        articlePublishingDate = binding.articlePublishingDate
        authorIcon = binding.authorIcon
        articleAuthor = binding.articleAuthor
        articleImageView = binding.articleImage
        readMoreButton = binding.readMoreButton
        shareFacebook = binding.shareFacebook
        shareTwitter = binding.shareTwitter
        shareWhatsApp = binding.shareWhatsApp

        setupTopAppBar()

        setupArticleViewModelListener()

        articleViewModel.getArticle()
    }

    /**
     * Prepare the [MaterialToolbar]
     */
    private fun setupTopAppBar() {
        topAppBar.title = ""
        setSupportActionBar(topAppBar)

        topAppBar.setLogo(R.mipmap.ic_launcher_foreground)
        topAppBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        topAppBar.setNavigationOnClickListener {
            this@ArticleActivity.onBackPressed()
        }
    }

    /**
     * Prepare the listener for the UI
     */
    private fun setupArticleViewModelListener() {
        lifecycleScope.launch {
            articleViewModel.articleViewModelUiEvent.collectLatest { it ->
                when (it) {
                    is ArticleUIEvent.UpdateData -> {
                        articleImageView.load(it.data.urlToImage) {
                            crossfade(true)
                        }
                        articleTitle.text = it.data.title
                        articleContent.text = it.data.content

                        val parsedDate = LocalDateTime.parse(it.data.publishedAt, DateTimeFormatter.ISO_DATE_TIME)
                        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                        articlePublishingDate.text = formattedDate.toString()

                        it.data.author?.let { author ->
                            articleAuthor.text = author
                            authorIcon.visibility = View.VISIBLE
                        } ?: run { authorIcon.visibility = View.GONE }


                        if(it.data.url != null) {
                            readMoreButton.setOnClickListener { _ ->
                                openLinkInBrowser(it.data.url)
                            }

                            shareFacebook.setOnClickListener { _ ->shareViaFacebook(it.data.url) }
                            shareTwitter.setOnClickListener { _ ->shareViaTwitter(it.data.url) }
                            shareWhatsApp.setOnClickListener { _ ->shareViaWhatsApp(it.data.url) }

                            readMoreButton.visibility = View.VISIBLE
                            shareFacebook.visibility = View.VISIBLE
                            shareTwitter.visibility = View.VISIBLE
                            shareWhatsApp.visibility = View.VISIBLE
                        }
                        else {
                            readMoreButton.visibility = View.GONE
                            shareFacebook.visibility = View.GONE
                            shareTwitter.visibility = View.GONE
                            shareWhatsApp.visibility = View.GONE
                        }
                        readMoreButton.setOnClickListener { _ ->
                            it.data.url?.let { it1 -> openLinkInBrowser(it1) }
                        }
                    }
                    is ArticleUIEvent.Error -> {
                        Snackbar.make(binding.root, getString(R.string.unknown_error_message), Snackbar.LENGTH_SHORT).show()
                        delay(500)
                        this@ArticleActivity.onBackPressed()
                    }
                }
            }
        }
    }

    /**
     * This function starts a new WebViewActivity and opens the provided link
     * @param url String
     */
    private fun openLinkInBrowser(url: String) {
        val webViewActivity = Intent(this, WebViewActivity::class.java)
        webViewActivity.putExtra("url", url)
        startActivity(webViewActivity)
    }

    /**
     * This functions triggers the share function of installed Facebook app
     * @param url String
     */
    private fun shareViaFacebook(url: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.facebook.katana")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, url)
        try {
            startActivity(whatsappIntent)
        } catch (ex: ActivityNotFoundException) {
            Snackbar.make(binding.root, getString(R.string.missing_facebook), Snackbar.LENGTH_LONG).show()
        }
    }

    /**
     * This functions triggers the share function of installed Twitter app
     * @param url String
     */
    private fun shareViaTwitter(url: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.twitter.android")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, url)
        try {
            startActivity(whatsappIntent)
        } catch (ex: ActivityNotFoundException) {
            Snackbar.make(binding.root, getString(R.string.missing_twitter), Snackbar.LENGTH_LONG).show()
        }
    }

    /**
     * This functions triggers the share function of installed WhatsApp
     * @param url String
     */
    private fun shareViaWhatsApp(url: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, url)
        try {
            startActivity(whatsappIntent)
        } catch (ex: ActivityNotFoundException) {
            Snackbar.make(binding.root, getString(R.string.missing_whatsapp), Snackbar.LENGTH_LONG).show()
        }
    }

}
