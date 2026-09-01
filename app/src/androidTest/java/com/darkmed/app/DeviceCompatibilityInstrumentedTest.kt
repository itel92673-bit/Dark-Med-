package com.darkmed.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.darkmed.app.core.CompatibilityStatus
import com.darkmed.app.core.DeviceCompatibilityCenter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeviceCompatibilityInstrumentedTest {
    @Test
    fun snapshotContainsRequiredChecks() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val snapshot = DeviceCompatibilityCenter(context).snapshot()
        assertNotNull(snapshot.manufacturer)
        assertNotNull(snapshot.model)
        assertTrue(snapshot.apiLevel >= 29)
        assertTrue(snapshot.abi.isNotBlank())
        assertTrue(snapshot.checks.size >= 7)
        assertFalse(snapshot.checks.any { it.status == CompatibilityStatus.READY && it.name == "Tor capability" && it.detail == "Tor bootstrap verified" })
    }
}
