package com.darkmed.app.core

import android.content.Context
import java.io.File

class Tun2SocksConfigWriter(private val context: Context) {
    fun write(config: Tun2SocksConfig): Result<File> = runCatching {
        val directory = File(context.filesDir, "tun2socks")
        require(directory.mkdirs() || directory.isDirectory) { "Unable to create TUN2Socks directory" }
        val target = File(directory, "config.yml")
        val temporary = File(directory, "config.yml.tmp")
        temporary.writeText(Tun2SocksConfigRenderer.render(config))
        require(temporary.renameTo(target)) { "Unable to commit TUN2Socks config" }
        target
    }
}

data class Tun2SocksConfig(
    val socksAddress: String = "127.0.0.1",
    val socksPort: Int = 9050,
    val socksUsername: String? = null,
    val socksPassword: String? = null,
    val mtu: Int = 1500,
    val ipv4Address: String = "198.18.0.1",
    val ipv6Address: String = "fc00::1",
    val udpMode: String = "tcp"
)

object Tun2SocksConfigRenderer {
    fun render(config: Tun2SocksConfig): String {
        require(config.socksAddress.isNotBlank() && safeScalar(config.socksAddress)) { "SOCKS address is invalid" }
        require(config.socksPort in 1..65535) { "SOCKS port is invalid" }
        require(config.mtu in 576..9000) { "MTU is invalid" }
        require(config.ipv4Address.isNotBlank() && safeScalar(config.ipv4Address)) { "IPv4 address is invalid" }
        require(config.ipv6Address.isNotBlank() && safeScalar(config.ipv6Address)) { "IPv6 address is invalid" }
        require(config.udpMode == "tcp" || config.udpMode == "udp") { "UDP mode is invalid" }
        require((config.socksUsername == null) == (config.socksPassword == null)) { "SOCKS credentials must be provided together" }
        config.socksUsername?.let { require(safeScalar(it)) { "SOCKS username is invalid" } }
        config.socksPassword?.let { require(safeScalar(it)) { "SOCKS password is invalid" } }

        return buildString {
            appendLine("tunnel:")
            appendLine("  name: tun0")
            appendLine("  mtu: ${config.mtu}")
            appendLine("  multi-queue: false")
            appendLine("  ipv4: '${escape(config.ipv4Address)}'")
            appendLine("  ipv6: '${escape(config.ipv6Address)}'")
            appendLine("  icmp: 'off'")
            appendLine("socks5:")
            appendLine("  address: '${escape(config.socksAddress)}'")
            appendLine("  port: ${config.socksPort}")
            appendLine("  udp: '${config.udpMode}'")
            config.socksUsername?.let {
                appendLine("  username: '${escape(it)}'")
                appendLine("  password: '${escape(config.socksPassword!!)}'")
            }
            appendLine("misc:")
            appendLine("  log-level: warn")
        }
    }

    private fun safeScalar(value: String): Boolean = !value.contains('\n') && !value.contains('\r') && !value.contains('\u0000')

    private fun escape(value: String): String = value.replace("'", "''")
}
