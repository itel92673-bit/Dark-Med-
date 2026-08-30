package com.darkmed.app.core

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Build
import androidx.core.content.ContextCompat

class DarkMedNetworkOrchestrator(context: Context) {
    private val appContext = context.applicationContext
    private var receiver: BroadcastReceiver? = null

    fun vpnConsentIntent(activity: Activity): Intent? = VpnService.prepare(activity)

    fun startTorThenVpn(): Result<Unit> {
        if (VpnService.prepare(appContext) != null) {
            return Result.failure(IllegalStateException("VPN consent is required before starting the protected route"))
        }
        if (receiver != null) {
            return Result.failure(IllegalStateException("Protected route startup is already in progress"))
        }
        val routeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    TorForegroundService.ACTION_READY -> {
                        val socksPort = intent.getIntExtra(TorForegroundService.EXTRA_SOCKS_PORT, -1)
                        if (socksPort !in 1..65535) {
                            stopWithError("Tor reported an invalid SOCKS port")
                            return
                        }
                        val config = Tun2SocksConfigWriter(appContext).write(
                            Tun2SocksConfig(socksPort = socksPort)
                        ).getOrElse {
                            stopWithError(it.message ?: "TUN configuration failed")
                            return
                        }
                        startVpn(config)
                    }
                    TorForegroundService.ACTION_ERROR -> {
                        stopWithError(intent.getStringExtra(TorForegroundService.EXTRA_ERROR) ?: "Tor startup failed")
                    }
                    TorForegroundService.ACTION_STOPPED -> {
                        unregister()
                    }
                }
            }
        }
        receiver = routeReceiver
        ContextCompat.registerReceiver(
            appContext,
            routeReceiver,
            IntentFilter().apply {
                addAction(TorForegroundService.ACTION_READY)
                addAction(TorForegroundService.ACTION_ERROR)
                addAction(TorForegroundService.ACTION_STOPPED)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        return runCatching {
            val intent = Intent(appContext, TorForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(appContext, intent)
            } else {
                appContext.startService(intent)
            }
        }
    }

    fun stop() {
        appContext.stopService(Intent(appContext, DarkMedVpnService::class.java))
        appContext.startService(
            Intent(appContext, TorForegroundService::class.java).setAction(TorForegroundService.ACTION_STOP)
        )
        unregister()
    }

    private fun startVpn(config: java.io.File) {
        val intent = DarkMedVpnService.startIntent(appContext, config.absolutePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(appContext, intent)
        } else {
            appContext.startService(intent)
        }
    }

    private fun stopWithError(message: String) {
        appContext.stopService(Intent(appContext, DarkMedVpnService::class.java))
        appContext.stopService(Intent(appContext, TorForegroundService::class.java))
        appContext.sendBroadcast(
            Intent(TorForegroundService.ACTION_ERROR)
                .setPackage(appContext.packageName)
                .putExtra(TorForegroundService.EXTRA_ERROR, message)
        )
        unregister()
    }

    private fun unregister() {
        receiver?.let { runCatching { appContext.unregisterReceiver(it) } }
        receiver = null
    }
}
