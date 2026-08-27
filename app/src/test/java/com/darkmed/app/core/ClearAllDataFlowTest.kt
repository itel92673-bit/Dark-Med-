package com.darkmed.app.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ClearAllDataFlowTest {
    @Test
    fun authenticationMustPrecedeConfirmationAndWipe() {
        val initial = ClearAllDataState()
        val beforeAuth = ClearAllDataReducer.reduce(initial, ClearAllDataEvent.Confirmed)
        val afterAuth = ClearAllDataReducer.reduce(initial, ClearAllDataEvent.StartAuthentication)
        val confirmation = ClearAllDataReducer.reduce(afterAuth, ClearAllDataEvent.AuthenticationSucceeded)
        val wiping = ClearAllDataReducer.reduce(confirmation, ClearAllDataEvent.Confirmed)
        assertEquals(ClearAllDataPhase.Idle, beforeAuth.phase)
        assertEquals(ClearAllDataPhase.Authenticating, afterAuth.phase)
        assertEquals(ClearAllDataPhase.Confirmation, confirmation.phase)
        assertEquals(ClearAllDataPhase.Wiping, wiping.phase)
    }

    @Test
    fun cancelAndOutsideDismissInvalidateAuthorization() {
        val confirmation = ClearAllDataState(ClearAllDataPhase.Confirmation)
        assertEquals(ClearAllDataPhase.Idle, ClearAllDataReducer.reduce(confirmation, ClearAllDataEvent.Cancelled).phase)
        assertEquals(ClearAllDataPhase.Idle, ClearAllDataReducer.reduce(confirmation, ClearAllDataEvent.Dismissed).phase)
    }

    @Test
    fun repeatedConfirmationCannotStartSecondWipe() {
        val wiping = ClearAllDataState(ClearAllDataPhase.Wiping)
        val repeated = ClearAllDataReducer.reduce(wiping, ClearAllDataEvent.Confirmed)
        assertEquals(ClearAllDataPhase.Wiping, repeated.phase)
        assertNotEquals(ClearAllDataPhase.Confirmation, repeated.phase)
    }

    @Test
    fun wipeFailureDoesNotBecomeSuccessfulCompletion() {
        val failed = ClearAllDataReducer.reduce(
            ClearAllDataState(ClearAllDataPhase.Wiping),
            ClearAllDataEvent.WipeFailed("verification failed")
        )
        assertEquals(ClearAllDataPhase.Failed, failed.phase)
        assertEquals("verification failed", failed.message)
    }

    @Test
    fun completedWipeIsDistinctAndCanBeFollowedByNewAuthentication() {
        val completed = ClearAllDataReducer.reduce(
            ClearAllDataState(ClearAllDataPhase.Wiping),
            ClearAllDataEvent.WipeCompleted
        )
        val next = ClearAllDataReducer.reduce(completed, ClearAllDataEvent.StartAuthentication)
        assertEquals(ClearAllDataPhase.Completed, completed.phase)
        assertEquals(ClearAllDataPhase.Authenticating, next.phase)
    }
}
