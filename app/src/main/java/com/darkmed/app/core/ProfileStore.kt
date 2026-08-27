package com.darkmed.app.core

import android.content.Context
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.UUID

data class ConnectionProfile(
    val id: String,
    val name: String,
    val routeVerified: Boolean = false
)

object ProfileCatalog {
    fun defaultProfiles(): List<ConnectionProfile> = listOf(
        ConnectionProfile("maximum_privacy", "Maximum Privacy"),
        ConnectionProfile("tor", "Tor"),
        ConnectionProfile("wireguard", "WireGuard"),
        ConnectionProfile("custom", "Custom")
    )

    fun create(existing: List<ConnectionProfile>, name: String, id: String = UUID.randomUUID().toString()): List<ConnectionProfile> {
        val normalized = name.trim()
        require(normalized.isNotEmpty()) { "Profile name must not be empty" }
        require(existing.none { it.name.equals(normalized, ignoreCase = true) }) { "Profile name already exists" }
        return existing + ConnectionProfile(id = id, name = normalized)
    }

    fun rename(existing: List<ConnectionProfile>, id: String, name: String): List<ConnectionProfile> {
        val normalized = name.trim()
        require(normalized.isNotEmpty()) { "Profile name must not be empty" }
        require(existing.any { it.id == id }) { "Profile does not exist" }
        require(existing.none { it.id != id && it.name.equals(normalized, ignoreCase = true) }) { "Profile name already exists" }
        return existing.map { if (it.id == id) it.copy(name = normalized) else it }
    }

    fun duplicate(existing: List<ConnectionProfile>, id: String, name: String): List<ConnectionProfile> {
        require(existing.any { it.id == id }) { "Profile does not exist" }
        return create(existing, name)
    }

    fun delete(existing: List<ConnectionProfile>, id: String): List<ConnectionProfile> {
        require(existing.any { it.id == id }) { "Profile does not exist" }
        return existing.filterNot { it.id == id }
    }
}

class ProfileStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun load(): List<ConnectionProfile> {
        val encoded = preferences.getString(KEY_PROFILES, null) ?: return ProfileCatalog.defaultProfiles()
        return encoded.lineSequence()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split('\t', limit = 2)
                require(parts.size == 2)
                ConnectionProfile(
                    id = parts[0],
                    name = decode(parts[1])
                )
            }
            .toList()
    }

    fun save(profiles: List<ConnectionProfile>): Boolean {
        val serialized = profiles.joinToString("\n") { "${it.id}\t${encode(it.name)}" }
        return preferences.edit().putString(KEY_PROFILES, serialized).commit()
    }

    companion object {
        private const val PREFERENCES = "darkmed_profiles"
        private const val KEY_PROFILES = "profiles"

        private fun encode(value: String): String = Base64.getEncoder().encodeToString(value.toByteArray(StandardCharsets.UTF_8))
        private fun decode(value: String): String = String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8)
    }
}
