package com.isrna.newsapp.feature_article_list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.isrna.newsapp.R
import com.isrna.newsapp.databinding.ActivityArticleListBinding
import com.isrna.newsapp.feature_article_list.ui.adapters.SectionsPagerAdapter
import com.isrna.newsapp.feature_article_list.utility.TagGroupDoubleClickListener
import com.isrna.newsapp.feature_search.ui.SearchUIEvent
import com.isrna.newsapp.feature_search.ui.SearchViewModel
import com.isrna.newsapp.feature_settings.SettingsActivity
import com.isrna.newsapp.feature_webview.WebViewActivity
import com.isrna.newsapp.core.ErrorResponses
import com.isrna.newsapp.feature_article_list.utility.TAB_ICONS
import com.isrna.newsapp.feature_article_list.utility.TAB_TITLES
import com.isrna.newsapp.utilitiy.Utilities
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import me.gujun.android.taggroup.TagGroup

/**
 * This activity is used to host the [SearchView] on a activity basis and
 * to handle the [ArticleListFragment]s
 */
@AndroidEntryPoint
class ArticleListActivity : AppCompatActivity() {

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var binding: ActivityArticleListBinding
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var viewPager: ViewPager2
    private lateinit var searchView: SearchView
    private lateinit var tagGroup: TagGroup
    private lateinit var tabLayout: TabLayout

    private var saveSearchMenuItem: MenuItem? = null

    private var showSearchViewOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIntent(intent)

        binding = ActivityArticleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topAppBar = binding.searchViewAppBar.topAppBar
        searchView = binding.searchViewAppBar.articleSearchView
        tagGroup = binding.searchViewAppBar.tagGroup
        tabLayout = binding.searchViewAppBar.tabLayout
        viewPager = binding.viewPager

        setupSearchViewModelEventListener()
        setupTopAppBar()
        setupViewPagerWithTabLayout()
        setupSearchView()
        setupTagGroup()

        searchViewModel.loadPastSearches()
    }

    override fun onNewIntent(intent: Intent?) {
        intent?.let { checkIntent(it) }
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
        if(!searchView.isIconified)
        {
            searchView.setQuery("", false)
            searchView.isIconified = true
            topAppBar.navigationIcon = null
            return
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu?.let { saveSearchMenuItem = it.findItem(R.id.saveSearch) }
        if(showSearchViewOpen) {
            saveSearchMenuItem?.let {  it.isVisible = true }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveSearch -> {
                if(searchView.query.isNotEmpty())
                    searchViewModel.saveSearchByKeyword(searchView.query.toString())
            }
            R.id.settings -> {
                val settingsActivity = Intent(this, SettingsActivity::class.java)
                startActivity(settingsActivity)
            }
            R.id.author -> {
                val webViewActivity = Intent(this, WebViewActivity::class.java)
                webViewActivity.putExtra("url", getString(R.string.author_website))
                startActivity(webViewActivity)
            }
            R.id.daily_quote -> {
                Snackbar.make(binding.root, GetRandomQuoteString(), Snackbar.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Setup the [MaterialToolbar]
     */
    private fun setupTopAppBar() {
        setSupportActionBar(topAppBar)
        topAppBar.setLogo(R.mipmap.ic_launcher_foreground)
        topAppBar.setNavigationOnClickListener { view ->
            if(view != null)
            {
                if(!searchView.isIconified)
                {
                    searchView.setQuery("", false)
                    searchView.isIconified = true
                    topAppBar.navigationIcon = null
                }
            }
        }
        topAppBar.setLogo(R.mipmap.ic_launcher_foreground)
    }

    /**
     * Setup the [ViewPager2] to work with [TabLayout]
     */
    private fun setupViewPagerWithTabLayout() {
        viewPager.adapter = SectionsPagerAdapter(supportFragmentManager, lifecycle, TAB_TITLES.count())

        TabLayoutMediator(tabLayout, viewPager) {
                tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
            tab.setIcon(TAB_ICONS[position])
        }.attach()
    }

    /**
     * Initialize all the settings and listeners for [SearchView]
     */
    private fun setupSearchView() {
        searchView.setOnSearchClickListener {
            topAppBar.logo = null
            topAppBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            tabLayout.visibility = View.GONE
            tagGroup.visibility = View.VISIBLE
            saveSearchMenuItem?.let {  it.isVisible = true }
            viewPager.isUserInputEnabled = false
        }

        searchView.setOnCloseListener {
            topAppBar.setLogo(R.mipmap.ic_launcher_foreground)
            topAppBar.navigationIcon = null
            tabLayout.visibility = View.VISIBLE
            tagGroup.visibility = View.GONE
            saveSearchMenuItem?.let {  it.isVisible = false }
            viewPager.isUserInputEnabled = true

            searchViewModel.searchClosed()

            return@setOnCloseListener false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchViewModel.searchByKeyword(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        if(showSearchViewOpen)
            searchView.isIconified = false
    }

    /**
     * Initialize [TagGroup]
     */
    private fun setupTagGroup() {
        tagGroup.setOnTagClickListener(object : TagGroupDoubleClickListener() {
            override fun onDoubleClick(tag: String) {
                showDeleteSearchKeywordDialog(tag)
            }

            override fun onSingleClick(tag: String) {
                searchView.setQuery(tag, false)
            }
        })
    }

    /**
     * Setup the event listener for [SearchViewModel]
     */
    private fun setupSearchViewModelEventListener() {
        lifecycleScope.launchWhenStarted {
            searchViewModel.searchViewModelUiEvent.collectLatest { event ->
                when(event) {
                    is SearchUIEvent.UpdateSearchHistory -> {
                        /* It currently runs in the background, but it can be made to trigger it,
                           depending on availability of search saved keywords. Just uncomment this,
                           and comment the job start at [NewsApplication].kt

                        if (event.data.isNotEmpty())
                            savedSearchNotificationStartStop(true)
                        else
                            savedSearchNotificationStartStop(false)*/

                        tagGroup.setTags(event.data)
                    }
                    is SearchUIEvent.RefreshSavedSearches -> searchViewModel.loadPastSearches()
                    is SearchUIEvent.Error -> if(event.errorType == ErrorResponses.DeleteError)
                        Snackbar.make(binding.root, getString(R.string.error_cannot_delete_keyword), Snackbar.LENGTH_SHORT).show()
                    else -> {}
                }
            }
        }
    }

    /**
     * Check whether the intent with which the activity was called contains Extras
     * @param intent: Intent?
     */
    private fun checkIntent(intent: Intent?) {
        intent?.let {
            showSearchViewOpen = it.getBooleanExtra("show_search_view", false)
        }
    }

    /**
     * Creates an AlertDialog for the user to decide whether he wants to delete a keyword or not
     * @param keyword (String) the keyword to be deleted
     */
    private fun showDeleteSearchKeywordDialog(keyword: String) {
        val builder = AlertDialog.Builder(this@ArticleListActivity)
        builder.setMessage(getString(R.string.delete_keyword_question, keyword))
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ -> searchViewModel.deleteSearchKeyword(keyword) }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    /**
     * This is currently not in use.
     * This functions contains the logic to decide whether to start the saved searches notifications
     * service or not.
     * @param start [Boolean] true start it if possible, false stop it if possible
     */
    private fun savedSearchNotificationStartStop(start: Boolean) {
        // First check the global setting for notifications, then
        // check if the notification service is running,
        // if we got saved searches start it if its not running,
        // or stop it if we dont have saved searches
        // this also updates the settings
        val getSettingForNotification = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(getString(R.string.enable_notifications_setting_key), true)
        if(getSettingForNotification) {
            if (start && !Utilities.getPastServiceJobStatus(this))
                Utilities.startPastSearchServiceJob(this)
            else if (!start && Utilities.getPastServiceJobStatus(this))
                Utilities.stopPastServiceJob(this)
        }
        else if(Utilities.getPastServiceJobStatus(this))
            Utilities.stopPastServiceJob(this)
    }

    /**
     * Native C++ Android Function
     */
    private external fun GetRandomQuoteString(): String
    /**
     * Load C++ library
     */
    companion object {
        init {
            System.loadLibrary("newsapp")
        }
    }
}
