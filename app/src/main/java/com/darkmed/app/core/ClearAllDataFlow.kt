package com.darkmed.app.core

enum class ClearAllDataPhase {
    Idle,
    Confirmation,
    Wiping,
    Completed,
    Failed
}

data class ClearAllDataState(
    val phase: ClearAllDataPhase = ClearAllDataPhase.Idle,
    val message: String? = null
)

sealed interface ClearAllDataEvent {
    data object StartConfirmation : ClearAllDataEvent
    data object Cancelled : ClearAllDataEvent
    data object Dismissed : ClearAllDataEvent
    data object Confirmed : ClearAllDataEvent
    data object WipeCompleted : ClearAllDataEvent
    data class WipeFailed(val message: String) : ClearAllDataEvent
}

object ClearAllDataReducer {
    fun reduce(state: ClearAllDataState, event: ClearAllDataEvent): ClearAllDataState {
        return when (state.phase) {
            ClearAllDataPhase.Idle -> when (event) {
                ClearAllDataEvent.StartConfirmation -> ClearAllDataState(ClearAllDataPhase.Confirmation)
                else -> state
            }
            ClearAllDataPhase.Confirmation -> when (event) {
                ClearAllDataEvent.Cancelled, ClearAllDataEvent.Dismissed -> ClearAllDataState(ClearAllDataPhase.Idle)
                ClearAllDataEvent.Confirmed -> ClearAllDataState(ClearAllDataPhase.Wiping)
                else -> state
            }
            ClearAllDataPhase.Wiping -> when (event) {
                ClearAllDataEvent.WipeCompleted -> ClearAllDataState(ClearAllDataPhase.Completed)
                is ClearAllDataEvent.WipeFailed -> ClearAllDataState(ClearAllDataPhase.Failed, event.message)
                else -> state
            }
            ClearAllDataPhase.Completed, ClearAllDataPhase.Failed -> when (event) {
                ClearAllDataEvent.StartConfirmation -> ClearAllDataState(ClearAllDataPhase.Confirmation)
                else -> state
            }
        }
    }
}
