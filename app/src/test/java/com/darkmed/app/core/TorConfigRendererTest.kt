package com.darkmed.app.core

import org.junit.Assert.assertTrue
import org.junit.Test

class TorConfigRendererTest {
    @Test
    fun rendersIsolatedSocksPortsAndControlPort() {
        val rendered = TorConfigRenderer.render(
            TorLocalConfig(socksPorts = listOf(9050, 9052), controlPort = 9051),
            "/data/user/0/com.darkmed.app/files/tor-data"
        )
        assertTrue(rendered.contains("SocksPort 9050 IsolateSOCKSAuth"))
        assertTrue(rendered.contains("SocksPort 9052 IsolateSOCKSAuth"))
        assertTrue(rendered.contains("ControlPort 9051"))
        assertTrue(rendered.contains("CookieAuthentication 1"))
        assertTrue(rendered.contains("SafeLogging 1"))
    }

    @Test
    fun rendersBridgesAndTransport() {
        val rendered = TorConfigRenderer.render(
            TorLocalConfig(
                socksPorts = listOf(9050),
                controlPort = 9051,
                bridges = listOf("obfs4 192.0.2.1:443 CERT=fixture iat-mode=0"),
                transportPlugin = TorTransportPlugin("obfs4", "/data/user/0/com.darkmed.app/files/obfs4proxy")
            ),
            "/data/user/0/com.darkmed.app/files/tor-data"
        )
        assertTrue(rendered.contains("UseBridges 1"))
        assertTrue(rendered.contains("Bridge obfs4 192.0.2.1:443"))
        assertTrue(rendered.contains("ClientTransportPlugin obfs4 exec"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsDuplicatePorts() {
        TorConfigRenderer.render(
            TorLocalConfig(socksPorts = listOf(9050, 9050)),
            "/data/user/0/com.darkmed.app/files/tor-data"
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNewlineInBridge() {
        TorConfigRenderer.render(
            TorLocalConfig(bridges = listOf("obfs4 192.0.2.1:443\nmalicious")),
            "/data/user/0/com.darkmed.app/files/tor-data"
        )
    }
}
