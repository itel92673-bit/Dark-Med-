package com.darkmed.app.core

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout

/**
 * Real WebView host. Each manifest subclass is assigned a dedicated Android
 * process so WebView.setDataDirectorySuffix can provide process-level storage
 * separation. This does not claim anonymity or network anonymity.
 */
open class BrowserSessionActivity : Activity() {
    protected open val sessionId: String = "session_1"
    protected open val privateMode: Boolean = true
    private lateinit var webView: WebView
    private val closeAllReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_CLOSE_ALL_BROWSER_SESSIONS) {
                finishAndRemoveTask()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WebViewSessionInitializer.initialize(
            BrowserSession(
                id = sessionId,
                privateMode = privateMode,
                dataDirectorySuffix = "darkmed_$sessionId"
            )
        )
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        window.decorView.setFilterTouchesWhenObscured(true)
        ContextCompat.registerReceiver(
            this,
            closeAllReceiver,
            IntentFilter(ACTION_CLOSE_ALL_BROWSER_SESSIONS),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        webView = WebView(this)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: android.webkit.WebResourceRequest): Boolean {
                return !WebViewSecurityPolicy.isAllowedNavigation(request.url)
            }
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false)
        configure(webView.settings)
        val requestedUrl = savedInstanceState?.getString(KEY_URL)?.let(Uri::parse)
        val initialUrl = if (requestedUrl != null && WebViewSecurityPolicy.isAllowedNavigation(requestedUrl)) {
            requestedUrl.toString()
        } else {
            DEFAULT_URL
        }
        webView.loadUrl(initialUrl)
        setContentView(FrameLayout(this).apply { addView(webView) })
    }

    private fun configure(settings: WebSettings) {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = false
        settings.saveFormData = false
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        settings.setAllowFileAccessFromFileURLs(false)
        settings.setAllowUniversalAccessFromFileURLs(false)
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        settings.safeBrowsingEnabled = true
        settings.setSupportMultipleWindows(false)
        settings.cacheMode = if (privateMode) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_URL, webView.url ?: DEFAULT_URL)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(closeAllReceiver) }
        if (privateMode) clearPrivateData()
        webView.stopLoading()
        webView.loadUrl("about:blank")
        webView.clearHistory()
        webView.removeAllViews()
        webView.destroy()
        super.onDestroy()
    }

    private fun clearPrivateData() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
        webView.clearCache(true)
        webView.clearFormData()
        webView.clearSslPreferences()
    }

    companion object {
        const val ACTION_CLOSE_ALL_BROWSER_SESSIONS = "com.darkmed.app.action.CLOSE_ALL_BROWSER_SESSIONS"
        private const val KEY_URL = "darkmed.browser.url"
        private const val DEFAULT_URL = "about:blank"
    }
}

class BrowserSession1Activity : BrowserSessionActivity() {
    override val sessionId: String = "session_1"
}

class BrowserSession2Activity : BrowserSessionActivity() {
    override val sessionId: String = "session_2"
}

class BrowserSession3Activity : BrowserSessionActivity() {
    override val sessionId: String = "session_3"
}

class BrowserSession4Activity : BrowserSessionActivity() {
    override val sessionId: String = "session_4"
}
