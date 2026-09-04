package com.darkmed.app.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.ParcelFileDescriptor
import java.io.File

class DarkMedVpnService : VpnService() {
    private var tunnelFd: Int? = null
    private var engine: HevTun2Socks? = null
    @Volatile
    private var routeState: ProtectedRouteState = ProtectedRouteStateMachine.stopped()
    private val monitor = Handler(Looper.getMainLooper())
    private val engineMonitor = object : Runnable {
        override fun run() {
            val activeEngine = engine
            if (activeEngine != null && !activeEngine.isRunning()) {
                routeState = ProtectedRouteStateMachine.failed(routeState, "tun2socks worker stopped")
                updateNotification("Dark Med BLOCKED: tunnel engine stopped")
                return
            }
            if (activeEngine != null) monitor.postDelayed(this, ENGINE_POLL_MS)
        }
    }

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
        routeState = ProtectedRouteStateMachine.starting()
        updateNotification("Dark Med VPN: starting protected chain")
        val config = runCatching { File(configPath).canonicalFile }.getOrElse {
            routeState = ProtectedRouteStateMachine.failed(routeState, "invalid tunnel configuration path")
            updateNotification("Dark Med BLOCKED: invalid tunnel configuration")
            return
        }
        val allowedRoots = listOf(filesDir.canonicalFile, cacheDir.canonicalFile)
        if (allowedRoots.none { root -> config.path.startsWith(root.path + File.separator) }) {
            routeState = ProtectedRouteStateMachine.failed(routeState, "tunnel configuration outside app storage")
            updateNotification("Dark Med BLOCKED: invalid tunnel configuration")
            return
        }

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
        } catch (error: Throwable) {
            routeState = ProtectedRouteStateMachine.failed(routeState, "VPN establish failed: ${error.javaClass.simpleName}")
            updateNotification("Dark Med BLOCKED: VPN unavailable")
            return
        } ?: run {
            routeState = ProtectedRouteStateMachine.failed(routeState, "VPN establish returned no descriptor")
            updateNotification("Dark Med BLOCKED: VPN unavailable")
            return
        }

        routeState = ProtectedRouteStateMachine.tunEstablished(routeState)
        val fd = descriptor.detachFd()
        tunnelFd = fd
        val candidate = try {
            HevTun2Socks()
        } catch (_: Throwable) {
            null
        }
        routeState = ProtectedRouteStateMachine.proxyStarting(routeState)
        if (candidate != null && candidate.start(config.path, fd, this)) {
            engine = candidate
            routeState = ProtectedRouteStateMachine.proxyReady(routeState, upstreamProtected = false)
            updateNotification("Dark Med BLOCKED: upstream protection unverified")
            monitor.post(engineMonitor)
        } else {
            routeState = ProtectedRouteStateMachine.failed(routeState, "tun2socks native start failed")
            updateNotification("Dark Med BLOCKED: tunnel engine unavailable")
        }
    }

    private fun stopTunnel() {
        routeState = ProtectedRouteStateMachine.stopping(routeState)
        monitor.removeCallbacks(engineMonitor)
        val activeEngine = engine
        activeEngine?.stop()
        engine = null
        if (activeEngine == null) {
            tunnelFd?.let { fd ->
                runCatching { ParcelFileDescriptor.adoptFd(fd).close() }
            }
        }
        tunnelFd = null
        routeState = ProtectedRouteStateMachine.stopped()
    }

    fun securityState(): ProtectedRouteState = routeState

    private fun updateNotification(text: String) {
        getSystemService(NotificationManager::class.java)?.notify(NOTIFICATION_ID, notification(text))
    }

    private fun createNotificationChannel() {
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(NotificationChannel(CHANNEL_ID, "Dark Med VPN", NotificationManager.IMPORTANCE_LOW))
    }

    private fun notification(text: String = "TUN routing is active only after a verified route"): Notification = Notification.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.stat_sys_warning)
        .setContentTitle("Dark Med VPN")
        .setContentText(text)
        .setOngoing(true)
        .build()

    companion object {
        const val ACTION_START = "com.darkmed.app.action.START_TUN2SOCKS"
        const val ACTION_STOP = "com.darkmed.app.action.STOP_TUN2SOCKS"
        const val EXTRA_CONFIG_PATH = "com.darkmed.app.extra.TUN2SOCKS_CONFIG_PATH"
        private const val CHANNEL_ID = "darkmed_vpn"
        private const val NOTIFICATION_ID = 902
        private const val ENGINE_POLL_MS = 500L

        fun startIntent(context: Context, configPath: String): Intent = Intent(context, DarkMedVpnService::class.java)
            .setAction(ACTION_START)
            .putExtra(EXTRA_CONFIG_PATH, configPath)
    }
}
