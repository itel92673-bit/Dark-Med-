package com.darkmed.app.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import java.io.File

class DarkMedVpnService : VpnService() {
    private var tunnelFd: Int? = null
    private var engine: HevTun2Socks? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(NOTIFICATION_ID, notification(), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTunnel(intent.getStringExtra(EXTRA_CONFIG_PATH))
            ACTION_STOP -> {
                stopTunnel()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onRevoke() {
        stopTunnel()
        stopSelf()
        super.onRevoke()
    }

    override fun onDestroy() {
        stopTunnel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = super.onBind(intent)

    private fun startTunnel(configPath: String?) {
        if (tunnelFd != null || configPath == null) return
        val config = File(configPath).canonicalFile
        val allowedRoots = listOf(filesDir.canonicalFile, cacheDir.canonicalFile)
        if (allowedRoots.none { root -> config.path.startsWith(root.path + File.separator) }) return

        val descriptor = try {
            Builder()
                .setSession("Dark Med TUN to SOCKS")
                .setBlocking(false)
                .setMtu(1500)
                .addAddress("198.18.0.1", 15)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("198.18.0.2")
                .addAddress("fc00::1", 7)
                .addRoute("::", 0)
                .establish()
        } catch (_: Throwable) {
            null
        } ?: return

        val fd = descriptor.detachFd()
        val candidate = try {
            HevTun2Socks()
        } catch (_: Throwable) {
            null
        }
        if (candidate != null && candidate.start(config.path, fd, this)) {
            tunnelFd = fd
            engine = candidate
        } else {
            try {
                ParcelFileDescriptor.adoptFd(fd).close()
            } catch (_: Throwable) {
            }
        }
    }

    private fun stopTunnel() {
        engine?.stop()
        engine = null
        tunnelFd = null
    }

    private fun createNotificationChannel() {
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(NotificationChannel(CHANNEL_ID, "Dark Med VPN", NotificationManager.IMPORTANCE_LOW))
    }

    private fun notification(): Notification = Notification.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.stat_sys_warning)
        .setContentTitle("Dark Med VPN")
        .setContentText("TUN routing is active only after a verified route")
        .setOngoing(true)
        .build()

    companion object {
        const val ACTION_START = "com.darkmed.app.action.START_TUN2SOCKS"
        const val ACTION_STOP = "com.darkmed.app.action.STOP_TUN2SOCKS"
        const val EXTRA_CONFIG_PATH = "com.darkmed.app.extra.TUN2SOCKS_CONFIG_PATH"
        private const val CHANNEL_ID = "darkmed_vpn"
        private const val NOTIFICATION_ID = 902

        fun startIntent(context: Context, configPath: String): Intent = Intent(context, DarkMedVpnService::class.java)
            .setAction(ACTION_START)
            .putExtra(EXTRA_CONFIG_PATH, configPath)
    }
}
