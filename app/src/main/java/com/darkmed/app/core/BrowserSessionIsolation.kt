package com.darkmed.app.core

import android.webkit.WebView

/**
 * Session metadata used by the process-level WebView isolation policy.
 */
data class BrowserSession(
    val id: String,
    val privateMode: Boolean,
    val dataDirectorySuffix: String
)

object WebViewSessionInitializer {
    private var initializedSuffix: String? = null

    @Synchronized
    fun initialize(session: BrowserSession) {
        val existing = initializedSuffix
        if (existing != null && existing != session.dataDirectorySuffix) {
            error("WebView data directory is already initialized for another session in this process; use a dedicated Android process")
        }
        if (existing == null) {
            WebView.setDataDirectorySuffix(session.dataDirectorySuffix)
            initializedSuffix = session.dataDirectorySuffix
        }
    }
}
