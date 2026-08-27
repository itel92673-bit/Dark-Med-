package com.darkmed.app.core

import android.net.VpnService

class HevTun2Socks {
    init {
        System.loadLibrary("darkmed-tun2socks-jni")
    }

    fun start(configPath: String, tunFd: Int, protector: VpnService): Boolean = startNative(configPath, tunFd, protector)

    fun stop(): Boolean = stopNative()

    fun isRunning(): Boolean = isRunningNative()

    fun stats(): Tun2SocksStats {
        val values = statsNative()
        return if (values.size < 5) {
            Tun2SocksStats()
        } else {
            Tun2SocksStats(values[0], values[1], values[2], values[3], values[4].toInt())
        }
    }

    private external fun startNative(configPath: String, tunFd: Int, protector: VpnService): Boolean
    private external fun stopNative(): Boolean
    private external fun isRunningNative(): Boolean
    private external fun statsNative(): LongArray
}

data class Tun2SocksStats(
    val txPackets: Long = 0L,
    val txBytes: Long = 0L,
    val rxPackets: Long = 0L,
    val rxBytes: Long = 0L,
    val lastResult: Int = -1
)
