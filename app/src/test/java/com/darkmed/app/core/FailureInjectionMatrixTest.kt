package com.darkmed.app.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FailureInjectionMatrixTest {
    @Test
    fun coversAllDirectiveFailureScenarios() {
        assertEquals(FailureScenario.entries.toSet(), FailureInjectionMatrix.all().map { it.scenario }.toSet())
        assertEquals(22, FailureInjectionMatrix.all().size)
    }

    @Test
    fun componentFailuresAreLockdownAndFailClosed() {
        FailureInjectionMatrix.all()
            .forEach { outcome ->
                assertEquals(SecurityState.Lockdown, outcome.expectedState)
                assertEquals(SecurityState.Lockdown, outcome.actualPolicyState)
                assertTrue(outcome.directFallbackBlocked)
                assertTrue(outcome.securityConsequence.contains("no direct fallback"))
            }
    }

    @Test
    fun networkAndDeviceEvidenceAreNeverPolicyPass() {
        FailureInjectionMatrix.all()
            .filter { it.evidenceStatus != FailureEvidenceStatus.POLICY_EVALUATED }
            .forEach { outcome ->
                assertTrue(outcome.evidenceStatus == FailureEvidenceStatus.NETWORK_REQUIRED || outcome.evidenceStatus == FailureEvidenceStatus.REAL_DEVICE_REQUIRED)
                assertTrue(outcome.directFallbackBlocked)
            }
    }
}
