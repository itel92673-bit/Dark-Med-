package com.darkmed.app.core

import android.app.ActivityManager
import android.content.Context
import android.net.VpnService
import android.os.Build
import android.os.PowerManager
import android.webkit.WebView

enum class CompatibilityStatus {
    READY,
    WARNING,
    REQUIRES_ACTION,
    UNSUPPORTED
}

data class CompatibilityCheck(
    val name: String,
    val status: CompatibilityStatus,
    val detail: String
)

data class DeviceCompatibilitySnapshot(
    val apiLevel: Int,
    val manufacturer: String,
    val model: String,
    val abi: String,
    val ramBytes: Long,
    val webViewVersion: String,
    val vpnPermissionRequired: Boolean,
    val notificationStatus: CompatibilityStatus,
    val batteryOptimizationStatus: CompatibilityStatus,
    val backgroundExecutionStatus: CompatibilityStatus,
    val storageStatus: CompatibilityStatus,
    val checks: List<CompatibilityCheck>
)

class DeviceCompatibilityCenter(private val context: Context) {
    fun snapshot(): DeviceCompatibilitySnapshot {
        val activityManager = context.getSystemService(ActivityManager::class.java)
        val powerManager = context.getSystemService(PowerManager::class.java)
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        val webViewVersion = runCatching { WebView.getCurrentWebViewPackage()?.versionName ?: "unavailable" }.getOrDefault("unavailable")
        val vpnPermissionRequired = runCatching { VpnService.prepare(context) != null }.getOrDefault(true)
        val notification = notificationStatus()
        val battery = batteryStatus(powerManager)
        val background = backgroundStatus(activityManager)
        val storage = storageStatus()
        val checks = listOf(
            CompatibilityCheck("VPN permission", if (vpnPermissionRequired) CompatibilityStatus.REQUIRES_ACTION else CompatibilityStatus.READY, if (vpnPermissionRequired) "User consent required" else "Consent available"),
            CompatibilityCheck("Notifications", notification, if (notification == CompatibilityStatus.REQUIRES_ACTION) "Notification permission required" else "Notification capability checked"),
            CompatibilityCheck("Battery optimization", battery, if (battery == CompatibilityStatus.WARNING) "Review OEM battery restrictions" else "Battery state checked"),
            CompatibilityCheck("Background execution", background, if (background == CompatibilityStatus.WARNING) "Background execution is restricted" else "Background state checked"),
            CompatibilityCheck("WebView", if (webViewVersion == "unavailable") CompatibilityStatus.UNSUPPORTED else CompatibilityStatus.READY, webViewVersion),
            CompatibilityCheck("Storage", storage, storageDetail(storage)),
            CompatibilityCheck("Tor capability", CompatibilityStatus.READY, "Bundled Tor component requires runtime bootstrap verification")
        )
        return DeviceCompatibilitySnapshot(
            apiLevel = Build.VERSION.SDK_INT,
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown",
            ramBytes = memoryInfo.totalMem,
            webViewVersion = webViewVersion,
            vpnPermissionRequired = vpnPermissionRequired,
            notificationStatus = notification,
            batteryOptimizationStatus = battery,
            backgroundExecutionStatus = background,
            storageStatus = storage,
            checks = checks
        )
    }

    private fun notificationStatus(): CompatibilityStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return CompatibilityStatus.READY
        return if (context.checkSelfPermission("android.permission.POST_NOTIFICATIONS") == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            CompatibilityStatus.READY
        } else {
            CompatibilityStatus.REQUIRES_ACTION
        }
    }

    private fun batteryStatus(powerManager: PowerManager?): CompatibilityStatus {
        if (powerManager == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return CompatibilityStatus.WARNING
        return if (powerManager.isIgnoringBatteryOptimizations(context.packageName)) CompatibilityStatus.READY else CompatibilityStatus.WARNING
    }

    private fun backgroundStatus(activityManager: ActivityManager?): CompatibilityStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P || activityManager == null) return CompatibilityStatus.READY
        return if (activityManager.isBackgroundRestricted) CompatibilityStatus.WARNING else CompatibilityStatus.READY
    }

    private fun storageStatus(): CompatibilityStatus {
        return if (context.filesDir.usableSpace >= MIN_USABLE_STORAGE_BYTES) CompatibilityStatus.READY else CompatibilityStatus.WARNING
    }

    private fun storageDetail(status: CompatibilityStatus): String = when (status) {
        CompatibilityStatus.READY -> "Application storage threshold available"
        else -> "Insufficient or unavailable application storage"
    }

    companion object {
        private const val MIN_USABLE_STORAGE_BYTES = 128L * 1024L * 1024L
    }
}
