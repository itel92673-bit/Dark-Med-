package com.darkmed.app

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupRuntimeTest {
    @Test
    fun mainActivityReachesResumedWithoutBiometricGate() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertEquals(androidx.lifecycle.Lifecycle.State.RESUMED, scenario.state)
                assertEquals(MainActivity::class.java.name, activity.javaClass.name)
            }
        }
    }
}
