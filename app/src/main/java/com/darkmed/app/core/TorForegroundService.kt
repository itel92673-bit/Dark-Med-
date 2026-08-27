package com.darkmed.app.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.content.pm.ServiceInfo
import org.torproject.jni.TorService

class TorForegroundService : Service() {
    private var torBound = false

    private val torConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            torBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            torBound = false
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(
            NOTIFICATION_ID,
            notification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopTor()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        val torIntent = Intent(this, TorService::class.java).apply {
            action = TorService.ACTION_START
        }
        startService(torIntent)
        torBound = bindService(Intent(this, TorService::class.java), torConnection, BIND_AUTO_CREATE)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopTor()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun stopTor() {
        if (torBound) {
            runCatching { unbindService(torConnection) }
            torBound = false
        }
        stopService(Intent(this, TorService::class.java))
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Dark Med Tor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Local Tor engine status"
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun notification(): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("Dark Med Tor")
            .setContentText("Local Tor engine STARTING; bootstrap unverified")
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_STOP = "com.darkmed.app.action.STOP_TOR"
        private const val CHANNEL_ID = "darkmed_tor"
        private const val NOTIFICATION_ID = 901
    }
}
