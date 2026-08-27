package com.darkmed.app.core

import org.junit.Assert.assertTrue
import org.junit.Test

class Tun2SocksConfigRendererTest {
    @Test
    fun renders_real_hev_fields() {
        val rendered = Tun2SocksConfigRenderer.render(Tun2SocksConfig())
        assertTrue(rendered.contains("ipv4: '198.18.0.1'"))
        assertTrue(rendered.contains("ipv6: 'fc00::1'"))
        assertTrue(rendered.contains("address: '127.0.0.1'"))
        assertTrue(rendered.contains("port: 9050"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejects_incomplete_credentials() {
        Tun2SocksConfigRenderer.render(Tun2SocksConfig(socksUsername = "user"))
    }

    @Test
    fun rejects_invalid_port_and_injection_scalar() {
        try {
            Tun2SocksConfigRenderer.render(Tun2SocksConfig(socksPort = 0))
            throw AssertionError("invalid port was accepted")
        } catch (_: IllegalArgumentException) {
        }
        try {
            Tun2SocksConfigRenderer.render(Tun2SocksConfig(socksAddress = "127.0.0.1\npassword: leaked"))
            throw AssertionError("injection scalar was accepted")
        } catch (_: IllegalArgumentException) {
        }
    }
}
