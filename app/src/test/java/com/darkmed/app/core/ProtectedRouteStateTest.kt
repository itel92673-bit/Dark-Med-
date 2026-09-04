package com.darkmed.app.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProtectedRouteStateTest {
    @Test
    fun tunOnlyIsNotProtectedAndRemainsFailClosed() {
        val state = ProtectedRouteStateMachine.tunEstablished(
            ProtectedRouteStateMachine.starting()
        )

        assertFalse(state.isProtected)
        assertTrue(state.isFailClosed)
    }

    @Test
    fun readyRequiresAllChainComponentsAndUpstreamProtection() {
        var state = ProtectedRouteStateMachine.starting()
        state = ProtectedRouteStateMachine.tunEstablished(state)
        state = ProtectedRouteStateMachine.torReady(state)
        state = ProtectedRouteStateMachine.proxyStarting(state)
        state = ProtectedRouteStateMachine.proxyReady(state, upstreamProtected = false)

        assertFalse(state.isProtected)
        assertTrue(state.isFailClosed)

        state = ProtectedRouteStateMachine.proxyReady(state, upstreamProtected = true)
        assertTrue(state.isProtected)
        assertFalse(state.isFailClosed)
    }

    @Test
    fun fatalFailureClearsProtectedComponents() {
        var state = ProtectedRouteStateMachine.starting()
        state = ProtectedRouteStateMachine.tunEstablished(state)
        state = ProtectedRouteStateMachine.torReady(state)
        state = ProtectedRouteStateMachine.proxyReady(
            ProtectedRouteStateMachine.proxyStarting(state),
            upstreamProtected = true
        )
        assertTrue(state.isProtected)

        val failed = ProtectedRouteStateMachine.failed(state, "native worker stopped")
        assertFalse(failed.isProtected)
        assertTrue(failed.isFailClosed)
        assertFalse(failed.torReady)
        assertFalse(failed.proxyReady)
        assertFalse(failed.upstreamProtected)
    }
}
