package com.isrna.newsapp.feature_article_list.utility

import android.os.Handler
import android.os.SystemClock
import me.gujun.android.taggroup.TagGroup

/**
 * Extension of [TagGroup.OnTagClickListener] to detect double clicks on tags
 */
abstract class TagGroupDoubleClickListener : TagGroup.OnTagClickListener {
    private var isSingleEvent = false
    private val doubleClickQualificationSpanInMillis: Long = DEFAULT_QUALIFICATION_SPAN
    private var timestampLastClick: Long
    private val handler: Handler
    private val runnable: Runnable
    private var tagHolder: String = ""
    override fun onTagClick(tag: String) {
        if (SystemClock.elapsedRealtime() - timestampLastClick < doubleClickQualificationSpanInMillis) {
            isSingleEvent = false
            handler.removeCallbacks(runnable)
            onDoubleClick(tag)
            return
        }
        tagHolder = tag
        isSingleEvent = true
        handler.postDelayed(runnable, DEFAULT_QUALIFICATION_SPAN)
        timestampLastClick = SystemClock.elapsedRealtime()
    }

    abstract fun onDoubleClick(tag: String)
    abstract fun onSingleClick(tag: String)

    companion object {
        private const val DEFAULT_QUALIFICATION_SPAN: Long = 200
    }

    init {
        timestampLastClick = 0
        handler = Handler()
        runnable = Runnable {
            if (isSingleEvent) {
                onSingleClick(tagHolder)
            }
        }
    }
}