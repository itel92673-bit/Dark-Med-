package com.darkmed.app.core

sealed interface SecurityState {
    data object Locked : SecurityState
    data object Authenticating : SecurityState
    data object Ready : SecurityState
    data object Starting : SecurityState
    data object Connected : SecurityState
    data object Degraded : SecurityState
    data object Failed : SecurityState
    data object Stopping : SecurityState
    data object Lockdown : SecurityState
}
