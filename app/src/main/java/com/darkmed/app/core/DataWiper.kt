package com.darkmed.app.core

import android.content.Context
import java.io.File
import java.security.KeyStore

sealed interface DataWipeResult {
    data object Completed : DataWipeResult
    data class Failed(val reason: String) : DataWipeResult
}

class DataWiper(private val context: Context) {
    fun wipeAll(): DataWipeResult {
        return try {
            val deviceContext = context.createDeviceProtectedStorageContext()
            val targets = listOfNotNull(
                context.filesDir,
                context.cacheDir,
                context.codeCacheDir,
                context.getExternalFilesDir(null),
                context.externalCacheDir,
                context.noBackupFilesDir,
                File(context.dataDir, "databases"),
                File(context.dataDir, "shared_prefs"),
                File(context.dataDir, "app_webview"),
                File(context.dataDir, "webview_data"),
                File(context.dataDir, "app_textures"),
                deviceContext.filesDir,
                deviceContext.cacheDir,
                deviceContext.codeCacheDir,
                deviceContext.noBackupFilesDir,
                File(deviceContext.dataDir, "databases"),
                File(deviceContext.dataDir, "shared_prefs")
            ).distinctBy { it.canonicalPath }

            val metadataCleared = context.getSharedPreferences("darkmed_secure_metadata", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit()

            val failedPaths = targets
                .filter { !wipeDirectoryContents(it) }
                .map { it.absolutePath }
                .toMutableList()
            if (!metadataCleared) failedPaths += "darkmed_secure_metadata"

            deleteKey("darkmed-local-key")
            val keyStillPresent = keyExists("darkmed-local-key")
            when {
                failedPaths.isNotEmpty() -> DataWipeResult.Failed("Wipe verification failed for: ${failedPaths.distinct().joinToString()}")
                keyStillPresent -> DataWipeResult.Failed("Android Keystore alias still exists after wipe")
                else -> DataWipeResult.Completed
            }
        } catch (error: Exception) {
            DataWipeResult.Failed(error.message ?: "Local data wipe failed")
        }
    }

    private fun wipeDirectoryContents(directory: File): Boolean {
        if (!directory.exists()) return true
        val children = directory.listFiles() ?: return false
        var successful = true
        children.forEach { child ->
            val deleted = child.deleteRecursively()
            if (!deleted || child.exists()) successful = false
        }
        val remaining = directory.listFiles() ?: return false
        return successful && remaining.none { it.exists() }
    }

    private fun keyExists(alias: String): Boolean {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return keyStore.containsAlias(alias)
    }

    private fun deleteKey(alias: String) {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (keyStore.containsAlias(alias)) keyStore.deleteEntry(alias)
    }
}
