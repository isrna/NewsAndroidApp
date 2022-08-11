package com.isrna.newsapp.feature_webview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.isrna.newsapp.R
import com.isrna.newsapp.databinding.ActivityWebViewBinding

/**
 * This is a simple activity which uses [WebView] to display websites inside the app
 */
class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding
    private lateinit var toolbar: MaterialToolbar
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.globalAppBar.topAppBar
        webView = binding.webView

        setupTopAppBar()

        checkIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        intent?.let { checkIntent(it) }
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
        if(webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }

    /**
     * Setup the [MaterialToolbar]
     */
    private fun setupTopAppBar() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        toolbar.setLogo(R.mipmap.ic_launcher_foreground)
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        toolbar.setNavigationOnClickListener {
            this@WebViewActivity.onBackPressed()
        }
    }

    /**
     * Check whether the intent with which the activity was called contains Extras
     * @param intent: Intent?
     */
    private fun checkIntent(intent: Intent) {
        val goToUrl = intent.getStringExtra("url")

        if(goToUrl != null) {
            webView.webViewClient = WebViewClient()
            webView.loadUrl(goToUrl)
        }
        else
            Snackbar.make(binding.root, getString(R.string.invalid_url), Snackbar.LENGTH_SHORT).show()
    }
}