package com.darkmed.app.core

import android.content.Context
import android.content.Intent

class ClearAllDataCoordinator(context: Context) {
    private val appContext = context.applicationContext

    fun wipeAfterAuthorization(): DataWipeResult {
        stopProtectedServices()
        return DataWiper(appContext).wipeAll()
    }

    private fun stopProtectedServices() {
        appContext.sendBroadcast(
            Intent(BrowserSessionActivity.ACTION_CLOSE_ALL_BROWSER_SESSIONS).setPackage(appContext.packageName)
        )
        appContext.stopService(Intent(appContext, TorForegroundService::class.java))
        appContext.stopService(Intent(appContext, DarkMedVpnService::class.java))
        appContext.stopService(
            Intent().setClassName(
                appContext,
                "com.wireguard.android.backend.GoBackend\$VpnService"
            )
        )
    }
}
