package com.darkmed.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.darkmed.app.core.CompatibilityStatus
import com.darkmed.app.core.DeviceCompatibilityCenter
import com.darkmed.app.core.ClearAllDataEvent
import com.darkmed.app.core.ClearAllDataPhase
import com.darkmed.app.core.ClearAllDataReducer
import com.darkmed.app.core.ClearAllDataState
import com.darkmed.app.core.ConnectionProfile
import com.darkmed.app.core.ProfileCatalog
import com.darkmed.app.core.ProfileStore

private val DarkBackground = Color(0xFF050507)
private val Panel = Color(0xFF101017)
private val Accent = Color(0xFFFF3D5A)
private val Violet = Color(0xFF8B5CFF)
private val Muted = Color(0xFF9A9AA6)

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        window.decorView.setFilterTouchesWhenObscured(true)
        setContent { LockedDarkMedApp() }
    }
}

@Composable
private fun LockedDarkMedApp() {
    var unlocked by remember { mutableStateOf(false) }
    var retryRequest by remember { mutableStateOf(0) }
    val initialMessage = stringResource(R.string.fingerprint_required)
    var message by remember { mutableStateOf(initialMessage) }
    val activity = LocalActivity.current
    val fragmentActivity = activity as? FragmentActivity
    val activityUnavailable = stringResource(R.string.activity_unavailable)
    val enrollBiometric = stringResource(R.string.enroll_biometric)
    val biometricUnavailable = stringResource(R.string.biometric_unavailable)
    LaunchedEffect(fragmentActivity, retryRequest) {
        if (fragmentActivity == null) {
            message = activityUnavailable
            return@LaunchedEffect
        }
        val gate = com.darkmed.app.core.BiometricGate(fragmentActivity)
        when (gate.status()) {
            com.darkmed.app.core.BiometricStatus.Available -> gate.authenticate(
                activity = fragmentActivity,
                onSuccess = { unlocked = true },
                onFailure = { message = it }
            )
            com.darkmed.app.core.BiometricStatus.Unavailable -> message = enrollBiometric
            com.darkmed.app.core.BiometricStatus.Unsupported -> message = biometricUnavailable
        }
    }
    if (unlocked) {
        DarkMedApp(onWipeCompleted = { unlocked = false })
    } else {
        LockedScreen(message = message, onRetry = { retryRequest += 1 })
    }
}

@Composable
private fun LockedScreen(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize().background(DarkBackground).padding(24.dp), verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = Accent)
        Spacer(Modifier.height(14.dp))
        Text(stringResource(R.string.brand_name), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(message, color = Muted)
        Spacer(Modifier.height(18.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
private fun DarkMedApp(onWipeCompleted: () -> Unit) {
    var selected by remember { mutableStateOf("dashboard") }
    val destinations = listOf(
        "dashboard" to stringResource(R.string.nav_dashboard),
        "browser" to stringResource(R.string.nav_browser),
        "profiles" to stringResource(R.string.nav_profiles),
        "privacy" to stringResource(R.string.nav_privacy),
        "settings" to stringResource(R.string.nav_settings)
    )
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
            Scaffold(
                containerColor = DarkBackground,
                bottomBar = {
                    NavigationBar(containerColor = Panel) {
                        destinations.forEach { (key, label) ->
                            NavigationBarItem(
                                selected = selected == key,
                                onClick = { selected = key },
                                icon = {
                                    Icon(
                                        imageVector = when (key) {
                                            "privacy" -> Icons.Default.Shield
                                            "browser" -> Icons.Default.Language
                                            "profiles" -> Icons.Default.Lock
                                            "settings" -> Icons.Default.Settings
                                            else -> Icons.Default.Wifi
                                        },
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            ) { padding ->
                when (selected) {
                    "dashboard" -> DashboardScreen(Modifier.padding(padding))
                    "browser" -> BrowserScreen(Modifier.padding(padding))
                    "settings" -> SettingsScreen(Modifier.padding(padding), onWipeCompleted)
                    "profiles" -> ProfilesScreen(Modifier.padding(padding))
                    "privacy" -> SecurityCenterScreen(Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
private fun DashboardScreen(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.brand_name), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.privacy_control_center), color = Muted)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.unprotected), color = Accent, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.no_verified_route), color = Muted)
                    Spacer(Modifier.height(14.dp))
                    Text(stringResource(R.string.connect_disconnect), color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.connection_unavailable), color = Muted)
                }
            }
        }
        item { RouteCard() }
        item { Text(stringResource(R.string.layer_status), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(listOf("WireGuard", "Proxy 1", "Proxy 2", "Tor")) { layer -> LayerRow(layer) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Violet)
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(stringResource(R.string.fail_closed_policy), color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(stringResource(R.string.direct_traffic_not_protected), color = Muted)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(18.dp)) }
    }
}

@Composable
private fun RouteCard() {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.route_current), color = Muted)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.route_blocked), color = Color.White, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.route_ready_requirement), color = Muted)
        }
    }
}

@Composable
private fun LayerRow(name: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Muted)
            Column(Modifier.padding(start = 12.dp)) {
                Text(name, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(stringResource(R.string.not_configured), color = Accent)
            }
        }
    }
}

@Composable
private fun BrowserScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(DarkBackground).padding(20.dp)) {
        Text(stringResource(R.string.browser_title), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.browser_disclaimer), color = Muted)
        Spacer(Modifier.height(20.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.browser_requires_protection), color = Accent, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SettingsScreen(modifier: Modifier = Modifier, onWipeCompleted: () -> Unit) {
    val activity = LocalActivity.current
    var result by remember { mutableStateOf<String?>(null) }
    var wipeState by remember { mutableStateOf(ClearAllDataState()) }
    val wipeScope = rememberCoroutineScope()
    val wipeFailedFormat = stringResource(R.string.wipe_failed)
    val wipeNotDeletedFormat = stringResource(R.string.wipe_not_deleted)
    val confirmTitle = stringResource(R.string.confirm_clear_title)
    val strongBiometricRequired = stringResource(R.string.strong_biometric_required)
    val fragmentActivityUnavailable = stringResource(R.string.fragment_activity_unavailable)
    val wipeCompleted = stringResource(R.string.wipe_completed)
    Column(modifier.fillMaxSize().background(DarkBackground).padding(20.dp).verticalScroll(rememberScrollState())) {
        Text(stringResource(R.string.settings_title), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        DeviceCompatibilityCard()
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.sensitive_actions_biometric), color = Muted)
        Spacer(Modifier.height(24.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.clear_all_data), color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.clear_all_data_description), color = Muted)
                Spacer(Modifier.height(14.dp))
                Button(
                    enabled = wipeState.phase != ClearAllDataPhase.Authenticating && wipeState.phase != ClearAllDataPhase.Wiping,
                    onClick = {
                        wipeState = ClearAllDataReducer.reduce(wipeState, ClearAllDataEvent.StartAuthentication)
                        val fragmentActivity = activity as? FragmentActivity
                        if (fragmentActivity == null) {
                            wipeState = ClearAllDataReducer.reduce(wipeState, ClearAllDataEvent.AuthenticationFailed(fragmentActivityUnavailable))
                            result = fragmentActivityUnavailable
                        } else {
                            com.darkmed.app.core.BiometricGate(fragmentActivity).authenticate(
                                activity = fragmentActivity,
                                title = confirmTitle,
                                subtitle = strongBiometricRequired,
                                onSuccess = {
                                    wipeState = ClearAllDataReducer.reduce(wipeState, ClearAllDataEvent.AuthenticationSucceeded)
                                },
                                onFailure = {
                                    wipeState = ClearAllDataReducer.reduce(wipeState, ClearAllDataEvent.AuthenticationFailed(wipeNotDeletedFormat.format(it)))
                                    result = wipeNotDeletedFormat.format(it)
                                }
                            )
                        }
                    }
                ) {
                    Text(stringResource(R.string.authenticate_and_clear))
                }
                result?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = if (it == wipeCompleted) Color(0xFF75E6A5) else Accent)
                }
            }
        }
    }
    if (wipeState.phase == ClearAllDataPhase.Confirmation) {
        AlertDialog(
            onDismissRequest = {
                wipeState = ClearAllDataReducer.reduce(wipeState, ClearAllDataEvent.Dismissed)
            },
            title = { Text(confirmTitle) },
            text = { Text(stringResource(R.string.confirm_clear_message)) },
            dismissButton = {
                TextButton(onClick = {
                    wipeState = ClearAllDataReducer.reduce(wipeState, ClearAllDataEvent.Cancelled)
                }) { Text(stringResource(R.string.cancel)) }
            },
            confirmButton = {
                TextButton(
                    enabled = wipeState.phase == ClearAllDataPhase.Confirmation,
                    onClick = {
                        wipeState = ClearAllDataReducer.reduce(wipeState, ClearAllDataEvent.Confirmed)
                        val fragmentActivity = activity as? FragmentActivity
                        if (fragmentActivity == null) {
                            val failure = ClearAllDataEvent.WipeFailed(fragmentActivityUnavailable)
                            wipeState = ClearAllDataReducer.reduce(wipeState, failure)
                            result = fragmentActivityUnavailable
                        } else {
                            wipeScope.launch {
                                val wipeResult = withContext(Dispatchers.IO) {
                                    com.darkmed.app.core.ClearAllDataCoordinator(fragmentActivity).wipeAfterAuthorization()
                                }
                                when (wipeResult) {
                                    com.darkmed.app.core.DataWipeResult.Completed -> {
                                        wipeState = ClearAllDataReducer.reduce(wipeState, ClearAllDataEvent.WipeCompleted)
                                        result = wipeCompleted
                                        onWipeCompleted()
                                    }
                                    is com.darkmed.app.core.DataWipeResult.Failed -> {
                                        val message = wipeFailedFormat.format(wipeResult.reason)
                                        wipeState = ClearAllDataReducer.reduce(wipeState, ClearAllDataEvent.WipeFailed(message))
                                        result = message
                                    }
                                }
                            }
                        }
                    }
                ) { Text(stringResource(R.string.confirm)) }
            }
        )
    }
}

private data class ProfileEditorState(val profileId: String?, val name: String)

@Composable
private fun ProfilesScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember(context) { ProfileStore(context) }
    var profiles by remember(context) { mutableStateOf(runCatching { store.load() }.getOrDefault(ProfileCatalog.defaultProfiles())) }
    var editor by remember { mutableStateOf<ProfileEditorState?>(null) }
    var deleteTarget by remember { mutableStateOf<ConnectionProfile?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val profileOperationFailed = stringResource(R.string.profile_operation_failed)
    LazyColumn(modifier = modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text(stringResource(R.string.profiles_title), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.profile_activation_blocked), color = Muted)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { editor = ProfileEditorState(null, "") }) {
                Text(stringResource(R.string.profile_add))
            }
            message?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Accent)
            }
        }
        items(profiles, key = { it.id }) { profile ->
            Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Accent)
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(profile.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(stringResource(R.string.not_verified), color = Accent)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { editor = ProfileEditorState(profile.id, profile.name) }) {
                            Text(stringResource(R.string.profile_edit))
                        }
                        TextButton(onClick = {
                            runCatching {
                                val updated = ProfileCatalog.duplicate(profiles, profile.id, "${profile.name} Copy")
                                check(store.save(updated)) { "Profile persistence failed" }
                                profiles = updated
                            }.onFailure { message = it.message ?: profileOperationFailed }
                        }) {
                            Text(stringResource(R.string.profile_duplicate))
                        }
                        TextButton(onClick = { deleteTarget = profile }) {
                            Text(stringResource(R.string.profile_delete))
                        }
                    }
                }
            }
        }
    }
    editor?.let { state ->
        var draftName by remember(state) { mutableStateOf(state.name) }
        AlertDialog(
            onDismissRequest = { editor = null },
            title = { Text(if (state.profileId == null) stringResource(R.string.profile_add) else stringResource(R.string.profile_edit)) },
            text = {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    label = { Text(stringResource(R.string.profile_name)) },
                    singleLine = true
                )
            },
            dismissButton = {
                TextButton(onClick = { editor = null }) { Text(stringResource(R.string.cancel)) }
            },
            confirmButton = {
                TextButton(onClick = {
                    runCatching {
                        val updated = if (state.profileId == null) {
                            ProfileCatalog.create(profiles, draftName)
                        } else {
                            ProfileCatalog.rename(profiles, state.profileId, draftName)
                        }
                        check(store.save(updated)) { "Profile persistence failed" }
                        profiles = updated
                        editor = null
                        message = null
                    }.onFailure { message = it.message ?: profileOperationFailed }
                }) { Text(stringResource(R.string.save)) }
            }
        )
    }
    deleteTarget?.let { profile ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(stringResource(R.string.profile_delete_title)) },
            text = { Text(stringResource(R.string.profile_delete_message).format(profile.name)) },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text(stringResource(R.string.cancel)) }
            },
            confirmButton = {
                TextButton(onClick = {
                    runCatching {
                        val updated = ProfileCatalog.delete(profiles, profile.id)
                        check(store.save(updated)) { "Profile persistence failed" }
                        profiles = updated
                        deleteTarget = null
                        message = null
                    }.onFailure { message = it.message ?: profileOperationFailed }
                }) { Text(stringResource(R.string.profile_delete)) }
            }
        )
    }
}

@Composable
private fun SecurityCenterScreen(modifier: Modifier = Modifier) {
    val statuses = listOf(
        stringResource(R.string.status_vpn),
        stringResource(R.string.status_tor),
        stringResource(R.string.status_socks),
        stringResource(R.string.status_dns),
        stringResource(R.string.status_ipv4),
        stringResource(R.string.status_ipv6),
        stringResource(R.string.status_routing),
        stringResource(R.string.status_kill_switch),
        stringResource(R.string.status_browser_isolation),
        stringResource(R.string.status_biometric),
        stringResource(R.string.status_storage)
    )
    LazyColumn(modifier = modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text(stringResource(R.string.security_center_title), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.security_center_note), color = Muted)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.why_not_protected), color = Accent, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.protection_locked), color = Color.White)
        }
        items(statuses) { component ->
            Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(component, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.not_verified), color = Accent)
                }
            }
        }
    }
}

@Composable
private fun DeviceCompatibilityCard() {
    val context = LocalContext.current
    val snapshot = remember(context) { DeviceCompatibilityCenter(context).snapshot() }
    Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.device_compatibility), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.device_profile), color = Muted)
            Text("API ${snapshot.apiLevel} · ${snapshot.manufacturer} ${snapshot.model}", color = Color.White)
            Text("${snapshot.abi} · ${snapshot.ramBytes / (1024L * 1024L)} MB · ${snapshot.webViewVersion}", color = Muted)
            snapshot.checks.forEach { check ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(check.name, color = Color.White)
                    Text(compatibilityStatusLabel(check.status), color = compatibilityStatusColor(check.status), fontWeight = FontWeight.SemiBold)
                }
            }
            Text(stringResource(R.string.compatibility_runtime_note), color = Muted)
        }
    }
}

@Composable
private fun compatibilityStatusLabel(status: CompatibilityStatus): String = when (status) {
    CompatibilityStatus.READY -> stringResource(R.string.compatibility_ready)
    CompatibilityStatus.WARNING -> stringResource(R.string.compatibility_warning)
    CompatibilityStatus.REQUIRES_ACTION -> stringResource(R.string.compatibility_action)
    CompatibilityStatus.UNSUPPORTED -> stringResource(R.string.compatibility_unsupported)
}

private fun compatibilityStatusColor(status: CompatibilityStatus): Color = when (status) {
    CompatibilityStatus.READY -> Color(0xFF75E6A5)
    CompatibilityStatus.WARNING -> Color(0xFFFFC857)
    CompatibilityStatus.REQUIRES_ACTION -> Accent
    CompatibilityStatus.UNSUPPORTED -> Color(0xFFFF6B6B)
}

@Composable
private fun UnavailableScreen(name: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(DarkBackground).padding(20.dp)) {
        Text(name, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(stringResource(R.string.module_unavailable), color = Muted)
    }
}
