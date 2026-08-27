package com.darkmed.app.core

import android.content.Context
import org.torproject.jni.TorService
import java.io.File

private const val TOR_SOCKS_PORT_MIN = 1024
private const val TOR_SOCKS_PORT_MAX = 65535

class TorConfigWriter(private val context: Context) {
    fun write(config: TorLocalConfig): Result<File> {
        return runCatching {
            val torrc = TorService.getTorrc(context).apply {
                parentFile?.mkdirs()
            }
            torrc.writeText(TorConfigRenderer.render(config, File(context.filesDir, "tor-data").absolutePath))
            torrc
        }
    }
}

object TorConfigRenderer {
    fun render(config: TorLocalConfig, dataDirectory: String): String {
        require(config.socksPorts.isNotEmpty()) { "At least one SOCKS port is required" }
        require(config.socksPorts.all { it in TOR_SOCKS_PORT_MIN..TOR_SOCKS_PORT_MAX }) { "SOCKS port is invalid" }
        require(config.socksPorts.distinct().size == config.socksPorts.size) { "SOCKS ports must be unique" }
        require(config.controlPort in TOR_SOCKS_PORT_MIN..TOR_SOCKS_PORT_MAX) { "Control port is invalid" }
        require(config.controlPort !in config.socksPorts) { "Control port must differ from SOCKS ports" }
        require(dataDirectory.isNotBlank() && !dataDirectory.contains('\n') && !dataDirectory.contains('\r')) { "Data directory is invalid" }
        require(config.bridges.all { it.isNotBlank() && !it.contains('\n') && !it.contains('\r') }) { "Bridge line is invalid" }

        return buildList {
            add("ClientOnly 1")
            add("AvoidDiskWrites 1")
            add("SafeLogging 1")
            config.socksPorts.forEach { add("SocksPort $it IsolateSOCKSAuth") }
            add("ControlPort ${config.controlPort}")
            add("CookieAuthentication 1")
            add("DataDirectory $dataDirectory")
            if (config.bridges.isNotEmpty()) {
                add("UseBridges 1")
                config.bridges.forEach { add("Bridge $it") }
            }
            config.transportPlugin?.let {
                require(it.path.isNotBlank() && !it.path.contains('\n') && !it.path.contains('\r')) { "Transport path is invalid" }
                add("ClientTransportPlugin ${it.type} exec ${it.path}")
            }
        }.joinToString("\n") + "\n"
    }
}

data class TorLocalConfig(
    val socksPorts: List<Int> = listOf(9050, 9052),
    val controlPort: Int = 9051,
    val bridges: List<String> = emptyList(),
    val transportPlugin: TorTransportPlugin? = null
)

data class TorTransportPlugin(
    val type: String,
    val path: String
) {
    init {
        require(type == "obfs4" || type == "snowflake") { "Unsupported transport type" }
    }
}
