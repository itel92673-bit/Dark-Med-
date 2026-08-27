package com.darkmed.app

import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlatformSmokeTest {
    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun packageInfo() = context.packageManager.getPackageInfo(
        context.packageName,
        PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES
    )

    @Test
    fun packageAndLauncherAreDeclared() {
        assertEquals("com.darkmed.app", context.packageName)
        val launcher = packageInfo().activities.orEmpty().firstOrNull { activity ->
            activity.name == MainActivity::class.java.name
        }
        assertNotNull(launcher)
        assertTrue(launcher!!.exported)
    }

    @Test
    fun securityRelevantApplicationFlagsArePresent() {
        val appInfo = context.applicationInfo
        assertFalse(appInfo.flags and android.content.pm.ApplicationInfo.FLAG_ALLOW_BACKUP != 0)
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            assertFalse(appInfo.flags and android.content.pm.ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC != 0)
        }
    }

    @Test
    fun vpnAndTorServicesAreDeclaredNonExported() {
        val services = packageInfo().services.orEmpty().associateBy { it.name }
        val vpn = services["com.darkmed.app.core.DarkMedVpnService"]
        val wireGuard = services["com.wireguard.android.backend.GoBackend\$VpnService"]
        val tor = services["com.darkmed.app.core.TorForegroundService"]
        assertNotNull(vpn)
        assertNotNull(wireGuard)
        assertNotNull(tor)
        assertEquals("android.permission.BIND_VPN_SERVICE", vpn!!.permission)
        assertEquals("android.permission.BIND_VPN_SERVICE", wireGuard!!.permission)
        assertFalse(vpn.exported)
        assertFalse(wireGuard.exported)
        assertFalse(tor!!.exported)
    }

    @Test
    fun fourBrowserActivitiesUseDedicatedProcesses() {
        val activities = packageInfo().activities.orEmpty().associateBy { it.name }
        val expected = mapOf(
            "com.darkmed.app.core.BrowserSession1Activity" to "browser_session_1",
            "com.darkmed.app.core.BrowserSession2Activity" to "browser_session_2",
            "com.darkmed.app.core.BrowserSession3Activity" to "browser_session_3",
            "com.darkmed.app.core.BrowserSession4Activity" to "browser_session_4"
        )
        expected.forEach { (name, suffix) ->
            val activity = activities[name]
            assertNotNull(activity)
            assertFalse(activity!!.exported)
            assertTrue(activity.processName.endsWith(":$suffix"))
        }
    }
}
