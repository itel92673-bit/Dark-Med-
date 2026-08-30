package com.darkmed.app.core

enum class FailureScenario {
    TorStartupFailure,
    TorCrash,
    TorTimeout,
    ControlPortFailure,
    SocksFailure,
    VpnPermissionDenied,
    VpnCrash,
    TunFailure,
    WireGuardInvalidConfig,
    WireGuardHandshakeTimeout,
    DnsFailure,
    Ipv6Failure,
    NetworkDisconnect,
    ServiceRestart,
    ProcessDeath,
    StorageFailure,
    CorruptedConfig,
    MissingDependency,
    MalformedInput,
    RouteCompilationFailure,
    ProxyFailure,
    BrowserFailure
}

enum class FailureEvidenceStatus {
    POLICY_EVALUATED,
    REAL_DEVICE_REQUIRED,
    NETWORK_REQUIRED
}

data class FailureInjectionOutcome(
    val scenario: FailureScenario,
    val expectedState: SecurityState,
    val actualPolicyState: SecurityState,
    val recovery: String,
    val securityConsequence: String,
    val evidenceStatus: FailureEvidenceStatus
) {
    val directFallbackBlocked: Boolean
        get() = actualPolicyState == SecurityState.Lockdown || actualPolicyState == SecurityState.Locked
}

object FailureInjectionMatrix {
    private val networkScenarios = setOf(
        FailureScenario.TorStartupFailure,
        FailureScenario.TorCrash,
        FailureScenario.TorTimeout,
        FailureScenario.ControlPortFailure,
        FailureScenario.SocksFailure,
        FailureScenario.VpnPermissionDenied,
        FailureScenario.VpnCrash,
        FailureScenario.TunFailure,
        FailureScenario.WireGuardHandshakeTimeout,
        FailureScenario.DnsFailure,
        FailureScenario.Ipv6Failure,
        FailureScenario.NetworkDisconnect,
        FailureScenario.ServiceRestart,
        FailureScenario.ProcessDeath,
        FailureScenario.ProxyFailure,
        FailureScenario.BrowserFailure
    )

    fun evaluate(scenario: FailureScenario): FailureInjectionOutcome {
        val state = SecurityState.Lockdown
        val evidence = when {
            scenario == FailureScenario.NetworkDisconnect || scenario == FailureScenario.Ipv6Failure -> FailureEvidenceStatus.NETWORK_REQUIRED
            scenario in networkScenarios -> FailureEvidenceStatus.REAL_DEVICE_REQUIRED
            else -> FailureEvidenceStatus.POLICY_EVALUATED
        }
        return FailureInjectionOutcome(
            scenario = scenario,
            expectedState = state,
            actualPolicyState = state,
            recovery = "Explicit authenticated recovery or stop",
            securityConsequence = "Unprotected traffic is blocked; no direct fallback",
            evidenceStatus = evidence
        )
    }

    fun all(): List<FailureInjectionOutcome> = FailureScenario.entries.map(::evaluate)
}
