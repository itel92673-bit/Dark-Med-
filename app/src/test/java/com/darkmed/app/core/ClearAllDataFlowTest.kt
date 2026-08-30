package com.darkmed.app.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ClearAllDataFlowTest {
    @Test
    fun confirmationPrecedesWipeWithoutAuthenticationPhase() {
        val initial = ClearAllDataState()
        val beforeConfirmation = ClearAllDataReducer.reduce(initial, ClearAllDataEvent.Confirmed)
        val confirmation = ClearAllDataReducer.reduce(initial, ClearAllDataEvent.StartConfirmation)
        val wiping = ClearAllDataReducer.reduce(confirmation, ClearAllDataEvent.Confirmed)
        assertEquals(ClearAllDataPhase.Idle, beforeConfirmation.phase)
        assertEquals(ClearAllDataPhase.Confirmation, confirmation.phase)
        assertEquals(ClearAllDataPhase.Wiping, wiping.phase)
    }

    @Test
    fun cancelAndOutsideDismissReturnToIdle() {
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
    fun completedWipeCanBeFollowedByNewConfirmation() {
        val completed = ClearAllDataReducer.reduce(
            ClearAllDataState(ClearAllDataPhase.Wiping),
            ClearAllDataEvent.WipeCompleted
        )
        val next = ClearAllDataReducer.reduce(completed, ClearAllDataEvent.StartConfirmation)
        assertEquals(ClearAllDataPhase.Completed, completed.phase)
        assertEquals(ClearAllDataPhase.Confirmation, next.phase)
    }
}
