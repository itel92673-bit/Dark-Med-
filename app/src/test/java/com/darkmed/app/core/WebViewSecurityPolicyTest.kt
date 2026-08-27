package com.darkmed.app.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WebViewSecurityPolicyTest {
    @Test
    fun allowsOnlyHttpAndHttps() {
        assertTrue(WebViewSecurityPolicy.isAllowedNavigation("http://example.com"))
        assertTrue(WebViewSecurityPolicy.isAllowedNavigation("https://example.onion"))
        assertFalse(WebViewSecurityPolicy.isAllowedNavigation("file:///data/data/app/file"))
        assertFalse(WebViewSecurityPolicy.isAllowedNavigation("content://provider/item"))
        assertFalse(WebViewSecurityPolicy.isAllowedNavigation("javascript:alert(1)"))
        assertFalse(WebViewSecurityPolicy.isAllowedNavigation("about:blank"))
        assertFalse(WebViewSecurityPolicy.isAllowedNavigation("not a url"))
    }
}
