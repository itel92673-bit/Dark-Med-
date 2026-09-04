package com.darkmed.app.core

enum class ProtectedRoutePhase {
    STOPPED,
    STARTING,
    TUN_ESTABLISHED,
    TOR_STARTING,
    TOR_READY,
    TUN2SOCKS_STARTING,
    PROXY_READY,
    PROTECTED,
    READY,
    DEGRADED,
    STOPPING,
    FAILED,
    KILLED
}

data class ProtectedRouteState(
    val phase: ProtectedRoutePhase = ProtectedRoutePhase.STOPPED,
    val tunEstablished: Boolean = false,
    val torReady: Boolean = false,
    val proxyReady: Boolean = false,
    val upstreamProtected: Boolean = false,
    val fatalReason: String? = null
) {
    val isProtected: Boolean
        get() = phase == ProtectedRoutePhase.READY &&
            tunEstablished && torReady && proxyReady && upstreamProtected && fatalReason == null

    val isFailClosed: Boolean
        get() = phase in setOf(
            ProtectedRoutePhase.DEGRADED,
            ProtectedRoutePhase.FAILED,
            ProtectedRoutePhase.KILLED,
            ProtectedRoutePhase.STOPPING
        ) || (tunEstablished && !isProtected)
}

object ProtectedRouteStateMachine {
    fun starting(): ProtectedRouteState = ProtectedRouteState(phase = ProtectedRoutePhase.STARTING)

    fun torStarting(state: ProtectedRouteState): ProtectedRouteState = state.copy(
        phase = ProtectedRoutePhase.TOR_STARTING,
        fatalReason = null
    )

    fun torReady(state: ProtectedRouteState): ProtectedRouteState = state.copy(
        phase = ProtectedRoutePhase.TOR_READY,
        torReady = true,
        fatalReason = null
    )

    fun tunEstablished(state: ProtectedRouteState): ProtectedRouteState = state.copy(
        phase = ProtectedRoutePhase.TUN_ESTABLISHED,
        tunEstablished = true,
        fatalReason = null
    )

    fun proxyStarting(state: ProtectedRouteState): ProtectedRouteState = state.copy(
        phase = ProtectedRoutePhase.TUN2SOCKS_STARTING,
        fatalReason = null
    )

    fun proxyReady(state: ProtectedRouteState, upstreamProtected: Boolean): ProtectedRouteState = state.copy(
        phase = if (upstreamProtected) ProtectedRoutePhase.READY else ProtectedRoutePhase.DEGRADED,
        proxyReady = true,
        upstreamProtected = upstreamProtected,
        fatalReason = if (upstreamProtected) null else "upstream socket protection was not verified"
    )

    fun failed(state: ProtectedRouteState, reason: String): ProtectedRouteState = state.copy(
        phase = ProtectedRoutePhase.FAILED,
        fatalReason = reason,
        torReady = false,
        proxyReady = false,
        upstreamProtected = false
    )

    fun killed(state: ProtectedRouteState, reason: String): ProtectedRouteState = state.copy(
        phase = ProtectedRoutePhase.KILLED,
        fatalReason = reason,
        torReady = false,
        proxyReady = false,
        upstreamProtected = false
    )

    fun stopping(state: ProtectedRouteState): ProtectedRouteState = state.copy(
        phase = ProtectedRoutePhase.STOPPING,
        fatalReason = null
    )

    fun stopped(): ProtectedRouteState = ProtectedRouteState()
}
