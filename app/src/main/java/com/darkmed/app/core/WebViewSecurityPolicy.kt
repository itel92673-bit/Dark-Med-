package com.darkmed.app.core

import android.net.Uri
import java.net.URI

object WebViewSecurityPolicy {
    fun isAllowedNavigation(url: String): Boolean {
        return runCatching {
            URI(url).scheme?.lowercase() in setOf("http", "https")
        }.getOrDefault(false)
    }

    fun isAllowedNavigation(uri: Uri): Boolean {
        return isAllowedNavigation(uri.toString())
    }
}
