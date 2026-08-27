# Dark Med Actual Baseline — 2026-08-24

Generated UTC: 2026-08-24T10:38:11Z

## Repository
/home/ubuntu/DarkMed
fatal: not a git repository (or any of the parent directories): .git
Not a Git repository

## Toolchain
openjdk version "21.0.11" 2026-04-21
OpenJDK Runtime Environment (build 21.0.11+10-1-24.04.2-Ubuntu)
OpenJDK 64-Bit Server VM (build 21.0.11+10-1-24.04.2-Ubuntu, mixed mode, sharing)

------------------------------------------------------------
Gradle 9.5.0
------------------------------------------------------------

Build time:    2026-04-28 12:05:30 UTC
Revision:      3fe117d68f3907790f3809f121aa36303a9151f8

Kotlin:        2.3.20
Groovy:        4.0.29
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  21.0.11 (Ubuntu 21.0.11+10-1-24.04.2-Ubuntu)
Daemon JVM:    /usr/lib/jvm/java-21-openjdk-amd64 (no Daemon JVM specified, using current Java home)
OS:            Linux 6.1.102 amd64

ANDROID_HOME=/home/ubuntu/android-sdk
drwxr-xr-x 11 ubuntu ubuntu 4096 Aug 23 16:58 /home/ubuntu/android-sdk
drwxr-xr-x  6 ubuntu ubuntu 4096 Aug 23 16:27 /home/ubuntu/android-sdk/build-tools
drwxr-xr-x  3 ubuntu ubuntu 4096 Aug 23 15:48 /home/ubuntu/android-sdk/platform-tools

## Device and virtualization
List of devices attached

/dev/kvm: ABSENT
AVDs: darkmed_api34 darkmed_api34_arm64 

## Gradle dependency graph
+--- org.jetbrains.kotlin:kotlin-stdlib:2.2.10
+--- androidx.compose.ui:ui-tooling -> 1.7.6
+--- androidx.compose:compose-bom:2024.12.01
+--- androidx.activity:activity-compose:1.10.0 (*)
+--- androidx.compose.ui:ui -> 1.7.6 (*)
+--- androidx.compose.ui:ui-tooling-preview -> 1.7.6 (*)
+--- androidx.compose.material3:material3 -> 1.3.1
+--- androidx.compose.material:material-icons-extended -> 1.7.6
+--- androidx.lifecycle:lifecycle-runtime-compose:2.8.7 (*)
+--- androidx.biometric:biometric:1.1.0
+--- com.wireguard.android:tunnel:1.0.20260102
+--- info.guardianproject:tor-android:0.4.9.11
|    +--- info.guardianproject:jtorctl:0.4.5.7
\--- info.guardianproject:jtorctl:0.4.5.7

## Manifest
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:allowBackup="false"
        android:fullBackupContent="@xml/backup_rules_legacy"
        android:dataExtractionRules="@xml/backup_rules"
        android:icon="@drawable/dark_med_icon"
        android:label="Dark Med"
        android:supportsRtl="true"
        android:theme="@style/Theme.DarkMed">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".core.BrowserSession1Activity" android:exported="false" android:process=":browser_session_1" />
        <activity android:name=".core.BrowserSession2Activity" android:exported="false" android:process=":browser_session_2" />
        <activity android:name=".core.BrowserSession3Activity" android:exported="false" android:process=":browser_session_3" />
        <activity android:name=".core.BrowserSession4Activity" android:exported="false" android:process=":browser_session_4" />
        <service
            android:name=".core.DarkMedVpnService"
            android:exported="false"
            android:permission="android.permission.BIND_VPN_SERVICE"
            android:foregroundServiceType="specialUse">
            <property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE" android:value="User-initiated TUN to local SOCKS privacy routing" />
            <intent-filter>
                <action android:name="android.net.VpnService" />
            </intent-filter>
        </service>
        <service
            android:name="com.wireguard.android.backend.GoBackend$VpnService"
            android:exported="false"
            android:permission="android.permission.BIND_VPN_SERVICE"
            android:foregroundServiceType="specialUse">
            <property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE" android:value="User-initiated privacy routing with local VPN and Tor components" />
            <intent-filter>
                <action android:name="android.net.VpnService" />
            </intent-filter>
        </service>
        <service
            android:name=".core.TorForegroundService"
            android:exported="false"
            android:foregroundServiceType="specialUse">
            <property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE" android:value="User-initiated local Tor privacy engine" />
        </service>
    </application>
</manifest>

## Resource tree
app/src/main/res/drawable-nodpi/dark_med_icon.png
app/src/main/res/values-ar/strings.xml
app/src/main/res/values-en/strings.xml
app/src/main/res/values/strings.xml
app/src/main/res/values/styles.xml
app/src/main/res/xml/backup_rules.xml
app/src/main/res/xml/backup_rules_legacy.xml

## Native and JNI tree
app/src/main/jni/Android.mk
app/src/main/jni/Application.mk
app/src/main/jni/darkmed_tun2socks_jni.cpp
app/src/main/jni/hev-socks5-tunnel/.clang-format
app/src/main/jni/hev-socks5-tunnel/.dockerignore
app/src/main/jni/hev-socks5-tunnel/.git/HEAD
app/src/main/jni/hev-socks5-tunnel/.git/config
app/src/main/jni/hev-socks5-tunnel/.git/description
app/src/main/jni/hev-socks5-tunnel/.git/index
app/src/main/jni/hev-socks5-tunnel/.git/packed-refs
app/src/main/jni/hev-socks5-tunnel/.git/shallow
app/src/main/jni/hev-socks5-tunnel/.gitignore
app/src/main/jni/hev-socks5-tunnel/.gitmodules
app/src/main/jni/hev-socks5-tunnel/Android.mk
app/src/main/jni/hev-socks5-tunnel/Application.mk
app/src/main/jni/hev-socks5-tunnel/Dockerfile
app/src/main/jni/hev-socks5-tunnel/LICENSE
app/src/main/jni/hev-socks5-tunnel/Makefile
app/src/main/jni/hev-socks5-tunnel/README.md
app/src/main/jni/hev-socks5-tunnel/build-apple.sh
app/src/main/jni/hev-socks5-tunnel/build.mk
app/src/main/jni/hev-socks5-tunnel/conf/main.yml
app/src/main/jni/hev-socks5-tunnel/docker/entrypoint.sh
app/src/main/jni/hev-socks5-tunnel/module.modulemap
app/src/main/jni/hev-socks5-tunnel/src/hev-config-const.h
app/src/main/jni/hev-socks5-tunnel/src/hev-config.c
app/src/main/jni/hev-socks5-tunnel/src/hev-config.h
app/src/main/jni/hev-socks5-tunnel/src/hev-jni.c
app/src/main/jni/hev-socks5-tunnel/src/hev-jni.h
app/src/main/jni/hev-socks5-tunnel/src/hev-main.c
app/src/main/jni/hev-socks5-tunnel/src/hev-main.h
app/src/main/jni/hev-socks5-tunnel/src/hev-mapped-dns.c
app/src/main/jni/hev-socks5-tunnel/src/hev-mapped-dns.h
app/src/main/jni/hev-socks5-tunnel/src/hev-socks5-session-tcp.c
app/src/main/jni/hev-socks5-tunnel/src/hev-socks5-session-tcp.h
app/src/main/jni/hev-socks5-tunnel/src/hev-socks5-session-udp.c
app/src/main/jni/hev-socks5-tunnel/src/hev-socks5-session-udp.h
app/src/main/jni/hev-socks5-tunnel/src/hev-socks5-session.c
app/src/main/jni/hev-socks5-tunnel/src/hev-socks5-session.h
app/src/main/jni/hev-socks5-tunnel/src/hev-socks5-tunnel.c
app/src/main/jni/hev-socks5-tunnel/src/hev-socks5-tunnel.h
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-freebsd.c
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-freebsd.h
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-linux.c
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-linux.h
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-macos.c
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-macos.h
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-netbsd.c
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-netbsd.h
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-windows.c
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel-windows.h
app/src/main/jni/hev-socks5-tunnel/src/hev-tunnel.h

## Kotlin source tree
app/src/main/java/com/darkmed/app/MainActivity.kt
app/src/main/java/com/darkmed/app/core/AssistantModel.kt
app/src/main/java/com/darkmed/app/core/AssistantOrchestrator.kt
app/src/main/java/com/darkmed/app/core/AssistantTools.kt
app/src/main/java/com/darkmed/app/core/BiometricGate.kt
app/src/main/java/com/darkmed/app/core/BrowserSessionActivity.kt
app/src/main/java/com/darkmed/app/core/BrowserSessionIsolation.kt
app/src/main/java/com/darkmed/app/core/ClearAllDataCoordinator.kt
app/src/main/java/com/darkmed/app/core/DarkMedVpnService.kt
app/src/main/java/com/darkmed/app/core/DataWiper.kt
app/src/main/java/com/darkmed/app/core/DnsRoutePlanner.kt
app/src/main/java/com/darkmed/app/core/Domain.kt
app/src/main/java/com/darkmed/app/core/EngineeringContracts.kt
app/src/main/java/com/darkmed/app/core/FailureInjectionMatrix.kt
app/src/main/java/com/darkmed/app/core/HevTun2Socks.kt
app/src/main/java/com/darkmed/app/core/HevTun2SocksConfig.kt
app/src/main/java/com/darkmed/app/core/LocalTorRouteCompiler.kt
app/src/main/java/com/darkmed/app/core/ProxyChainCompiler.kt
app/src/main/java/com/darkmed/app/core/RouteActivationGuard.kt
app/src/main/java/com/darkmed/app/core/RouteCompiler.kt
app/src/main/java/com/darkmed/app/core/SecureStore.kt
app/src/main/java/com/darkmed/app/core/SecurityStateMachine.kt
app/src/main/java/com/darkmed/app/core/TorBootstrapPolicy.kt
app/src/main/java/com/darkmed/app/core/TorConfigWriter.kt
app/src/main/java/com/darkmed/app/core/TorController.kt
app/src/main/java/com/darkmed/app/core/TorForegroundService.kt
app/src/main/java/com/darkmed/app/core/VpnPermissionManager.kt
app/src/main/java/com/darkmed/app/core/WireGuardConfigValidator.kt
app/src/main/java/com/darkmed/app/core/WireGuardController.kt

## Unit and instrumentation tests
app/src/androidTest/java/com/darkmed/app/PlatformSmokeTest.kt
app/src/test/java/com/darkmed/app/core/AssistantOrchestratorTest.kt
app/src/test/java/com/darkmed/app/core/BrowserSessionIsolationTest.kt
app/src/test/java/com/darkmed/app/core/DnsRoutePlannerTest.kt
app/src/test/java/com/darkmed/app/core/EngineeringContractsTest.kt
app/src/test/java/com/darkmed/app/core/FailureInjectionMatrixTest.kt
app/src/test/java/com/darkmed/app/core/HevTun2SocksConfigTest.kt
app/src/test/java/com/darkmed/app/core/LocalTorRouteCompilerTest.kt
app/src/test/java/com/darkmed/app/core/ProxyChainCompilerTest.kt
app/src/test/java/com/darkmed/app/core/RouteActivationGuardTest.kt
app/src/test/java/com/darkmed/app/core/RouteCompilerTest.kt
app/src/test/java/com/darkmed/app/core/SecurityStateMachineTest.kt
app/src/test/java/com/darkmed/app/core/TorBootstrapPolicyTest.kt
app/src/test/java/com/darkmed/app/core/TorConfigRendererTest.kt
app/src/test/java/com/darkmed/app/core/WireGuardConfigValidatorTest.kt

## Source markers
app/src/main/java/com/darkmed/app/core/DataWiper.kt:12:class DataWiper(private val context: Context) {
app/src/main/java/com/darkmed/app/core/AssistantOrchestrator.kt:31:interface LocalAiEngine : AssistantModel {
app/src/main/java/com/darkmed/app/core/AssistantOrchestrator.kt:32:    override val status: AssistantModelStatus
app/src/main/java/com/darkmed/app/core/AssistantOrchestrator.kt:33:        get() = AssistantModelStatus.LOCAL_COMMANDS
app/src/main/java/com/darkmed/app/core/WireGuardController.kt:4:import com.wireguard.android.backend.GoBackend
app/src/main/java/com/darkmed/app/core/WireGuardController.kt:15:    private val backend = GoBackend(context.applicationContext)
app/src/main/java/com/darkmed/app/core/DarkMedVpnService.kt:66:            .setBlocking(false)
app/src/main/java/com/darkmed/app/core/DarkMedVpnService.kt:69:            .addRoute("0.0.0.0", 0)
app/src/main/java/com/darkmed/app/core/DarkMedVpnService.kt:72:            .addRoute("::", 0)
app/src/main/java/com/darkmed/app/core/TorConfigWriter.kt:4:import org.torproject.jni.TorService
app/src/main/java/com/darkmed/app/core/TorConfigWriter.kt:13:            val torrc = TorService.getTorrc(context).apply {
app/src/main/java/com/darkmed/app/core/TorConfigWriter.kt:63:        require(type == "obfs4" || type == "snowflake") { "Unsupported transport type" }
app/src/main/java/com/darkmed/app/core/TorController.kt:8:import org.torproject.jni.TorService
app/src/main/java/com/darkmed/app/core/TorController.kt:28:    private var service: TorService? = null
app/src/main/java/com/darkmed/app/core/TorController.kt:34:            service = (binder as? TorService.LocalBinder)?.service
app/src/main/java/com/darkmed/app/core/TorController.kt:46:            TorService.setBroadcastPackageName(appContext.packageName)
app/src/main/java/com/darkmed/app/core/TorController.kt:51:                Intent(appContext, TorService::class.java),
app/src/main/java/com/darkmed/app/core/TorController.kt:55:            if (!bound) TorOperation.Failed("TorService bind failed") else TorOperation.StartRequested
app/src/main/java/com/darkmed/app/core/TorController.kt:66:            serviceStatus = current?.let { currentStatus(it, bootstrap) } ?: TorService.STATUS_OFF,
app/src/main/java/com/darkmed/app/core/TorController.kt:98:    private fun currentStatus(tor: TorService, bootstrap: String?): String {
app/src/main/java/com/darkmed/app/core/TorController.kt:99:        if (tor.getSocksPort() <= 0) return TorService.STATUS_STARTING
app/src/main/java/com/darkmed/app/core/TorController.kt:101:            TorService.STATUS_ON
app/src/main/java/com/darkmed/app/core/TorController.kt:103:            TorService.STATUS_STARTING
app/src/main/java/com/darkmed/app/core/TorForegroundService.kt:13:import org.torproject.jni.TorService
app/src/main/java/com/darkmed/app/core/TorForegroundService.kt:45:        val torIntent = Intent(this, TorService::class.java).apply {
app/src/main/java/com/darkmed/app/core/TorForegroundService.kt:46:            action = TorService.ACTION_START
app/src/main/java/com/darkmed/app/core/TorForegroundService.kt:49:        torBound = bindService(Intent(this, TorService::class.java), torConnection, BIND_AUTO_CREATE)
app/src/main/java/com/darkmed/app/core/TorForegroundService.kt:65:        stopService(Intent(this, TorService::class.java))
app/src/main/java/com/darkmed/app/core/BrowserSessionActivity.kt:17:open class BrowserSessionActivity : Activity() {
app/src/main/java/com/darkmed/app/core/BrowserSessionActivity.kt:77:class BrowserSession1Activity : BrowserSessionActivity() {
app/src/main/java/com/darkmed/app/core/BrowserSessionActivity.kt:81:class BrowserSession2Activity : BrowserSessionActivity() {
app/src/main/java/com/darkmed/app/core/BrowserSessionActivity.kt:85:class BrowserSession3Activity : BrowserSessionActivity() {
app/src/main/java/com/darkmed/app/core/BrowserSessionActivity.kt:89:class BrowserSession4Activity : BrowserSessionActivity() {
app/src/main/java/com/darkmed/app/core/LocalTorRouteCompiler.kt:3:import org.torproject.jni.TorService
app/src/main/java/com/darkmed/app/core/LocalTorRouteCompiler.kt:17:        if (snapshot.serviceStatus != TorService.STATUS_ON) {
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:3:enum class AssistantModelStatus {
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:9:interface AssistantModel {
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:10:    val status: AssistantModelStatus
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:14:class NotConfiguredAssistantModel : AssistantModel {
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:15:    override val status = AssistantModelStatus.NOT_CONFIGURED
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:20:interface LocalModelBackend : AssistantModel {
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:21:    override val status: AssistantModelStatus
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:22:        get() = AssistantModelStatus.NOT_CONFIGURED
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:25:interface ApprovedRemoteBackend : AssistantModel {
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:26:    override val status: AssistantModelStatus
app/src/main/java/com/darkmed/app/core/AssistantModel.kt:27:        get() = AssistantModelStatus.APPROVED_REMOTE_NOT_CONFIGURED
app/src/main/java/com/darkmed/app/core/FailureInjectionMatrix.kt:36:data class FailureInjectionOutcome(
app/src/main/java/com/darkmed/app/core/FailureInjectionMatrix.kt:48:object FailureInjectionMatrix {
app/src/main/java/com/darkmed/app/core/FailureInjectionMatrix.kt:68:    fun evaluate(scenario: FailureScenario): FailureInjectionOutcome {
app/src/main/java/com/darkmed/app/core/FailureInjectionMatrix.kt:76:        return FailureInjectionOutcome(
app/src/main/java/com/darkmed/app/core/FailureInjectionMatrix.kt:86:    fun all(): List<FailureInjectionOutcome> = FailureScenario.entries.map(::evaluate)
app/src/main/java/com/darkmed/app/core/ClearAllDataCoordinator.kt:6:class ClearAllDataCoordinator(context: Context) {
app/src/main/java/com/darkmed/app/core/ClearAllDataCoordinator.kt:11:        return DataWiper(appContext).wipeAll()
app/src/main/java/com/darkmed/app/core/ClearAllDataCoordinator.kt:20:                "com.wireguard.android.backend.GoBackend\$VpnService"
app/src/main/java/com/darkmed/app/MainActivity.kt:276:                                result = when (val wipe = com.darkmed.app.core.ClearAllDataCoordinator(fragmentActivity).wipeAfterAuthorization()) {
app/src/main/jni/hev-socks5-tunnel/.git/hooks/sendemail-validate.sample:22:# Replace the TODO placeholders with appropriate checks according to your
app/src/main/jni/hev-socks5-tunnel/.git/hooks/sendemail-validate.sample:27:	# TODO: Replace with appropriate checks (e.g. spell checking).
app/src/main/jni/hev-socks5-tunnel/.git/hooks/sendemail-validate.sample:35:	# TODO: Replace with appropriate checks for this patch
app/src/main/jni/hev-socks5-tunnel/.git/hooks/sendemail-validate.sample:41:	# TODO: Replace with appropriate checks for the whole series
app/src/main/jni/hev-socks5-tunnel/.git/modules/src/core/hooks/sendemail-validate.sample:22:# Replace the TODO placeholders with appropriate checks according to your
app/src/main/jni/hev-socks5-tunnel/.git/modules/src/core/hooks/sendemail-validate.sample:27:	# TODO: Replace with appropriate checks (e.g. spell checking).
app/src/main/jni/hev-socks5-tunnel/.git/modules/src/core/hooks/sendemail-validate.sample:35:	# TODO: Replace with appropriate checks for this patch
app/src/main/jni/hev-socks5-tunnel/.git/modules/src/core/hooks/sendemail-validate.sample:41:	# TODO: Replace with appropriate checks for the whole series
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/hev-task-system/hooks/sendemail-validate.sample:22:# Replace the TODO placeholders with appropriate checks according to your
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/hev-task-system/hooks/sendemail-validate.sample:27:	# TODO: Replace with appropriate checks (e.g. spell checking).
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/hev-task-system/hooks/sendemail-validate.sample:35:	# TODO: Replace with appropriate checks for this patch
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/hev-task-system/hooks/sendemail-validate.sample:41:	# TODO: Replace with appropriate checks for the whole series
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/lwip/hooks/sendemail-validate.sample:22:# Replace the TODO placeholders with appropriate checks according to your
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/lwip/hooks/sendemail-validate.sample:27:	# TODO: Replace with appropriate checks (e.g. spell checking).
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/lwip/hooks/sendemail-validate.sample:35:	# TODO: Replace with appropriate checks for this patch
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/lwip/hooks/sendemail-validate.sample:41:	# TODO: Replace with appropriate checks for the whole series
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/yaml/hooks/sendemail-validate.sample:22:# Replace the TODO placeholders with appropriate checks according to your
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/yaml/hooks/sendemail-validate.sample:27:	# TODO: Replace with appropriate checks (e.g. spell checking).
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/yaml/hooks/sendemail-validate.sample:35:	# TODO: Replace with appropriate checks for this patch
app/src/main/jni/hev-socks5-tunnel/.git/modules/third-part/yaml/hooks/sendemail-validate.sample:41:	# TODO: Replace with appropriate checks for the whole series
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/core/ipv6/dhcp6.c:12: * TODO:
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/core/ipv6/ip6.c:950:        /* TODO: process routing by the type */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/core/ipv6/nd6.c:793:              /* TODO implement Lifetime > 0 */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/core/ipv6/nd6.c:796:              /* TODO implement DNS removal in dns.c */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/include/lwip/sys.h:491: * "lev". This macro will default to calling the sys_arch_protect() function
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/include/lwip/sys.h:495:#define SYS_ARCH_PROTECT(lev) lev = sys_arch_protect()
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/include/lwip/sys.h:502: * sys_arch_unprotect() function which should be implemented in
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/include/lwip/sys.h:506:#define SYS_ARCH_UNPROTECT(lev) sys_arch_unprotect(lev)
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/include/lwip/sys.h:507:sys_prot_t sys_arch_protect(void);
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/include/lwip/sys.h:508:void sys_arch_unprotect(sys_prot_t pval);
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/include/netif/ppp/pppol2tp.h:118:#define PPPOL2TP_HOSTNAME        "lwIP" /* FIXME: make it configurable */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/include/netif/ppp/pppol2tp.h:122:#define PPPOL2TP_VENDORNAME      "lwIP" /* FIXME: make it configurable */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/include/netif/ppp/pppol2tp.h:129:#define PPPOL2TP_RECEIVEWINDOWSIZE           8 /* FIXME: make it configurable */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/lowpan6.c:732:    /* TODO: handle the case where we already have FRAGN received */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/ccp.c:649:    /* FIXME: we don't need to test if BSD compress is available
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/ccp.c:673:    /* FIXME: we don't need to test if deflate is available
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/ccp.c:722:    /* FIXME: we don't need to test if predictor is available,
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/demand.c:116:/* FIXME: find a way to die() here */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/eap.c:332:	/* FIXME: if we want to do SRP, we need to find a way to pass the PolarSSL des_context instead of using static memory */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/eap.c:472:				/* FIXME: if we want to do SRP, we need to find a way to pass the PolarSSL des_context instead of using static memory */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/eap.c:497:					/* FIXME: if we want to do SRP, we need to find a way to pass the PolarSSL des_context instead of using static memory */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/eap.c:787:			/* FIXME: if we want to do SRP, we need to find a way to pass the PolarSSL des_context instead of using static memory */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/eap.c:796:				/* FIXME: if we want to do SRP, we need to find a way to pass the PolarSSL des_context instead of using static memory */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/eap.c:806:				/* FIXME: if we want to do SRP, we need to find a way to pass the PolarSSL des_context instead of using static memory */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/mppe.c:225:	/* FIXME: use PUT* macros */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/mppe.c:244:	/* FIXME: add PFC support */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/ppp.c:157:/* FIXME: add stats per PPP session */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/ppp.c:713:  /* FIXME: user application should be responsible to call netif_set_up(),
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/ppp.c:1260:  /* FIXME: should we add an IPv6 static neighbor using his_eui64 ? */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/ppp.c:1425:  /* FIXME: add idle time support and make it optional */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/ppp/upap.c:642:/* FIXME: require ppp_pcb struct as printpkt() argument */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/zepif.c:155:  /* TODO Check CRC? */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/netif/zepif.c:183:  LWIP_ASSERT("TODO: support chained pbufs", p->next == NULL);
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/unix/include/netif/fifo.h:11:  u8_t data[FIFOSIZE+10]; /* data segment, +10 is a hack probably not needed.. FIXME! */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/unix/lib/sys_arch.c:775:/** sys_prot_t sys_arch_protect(void)
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/unix/lib/sys_arch.c:783:other words, sys_arch_protect() could be called while already protected. In
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/unix/lib/sys_arch.c:786:sys_arch_protect() is only required if your port is supporting an operating
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/unix/lib/sys_arch.c:790:sys_arch_protect(void)
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/unix/lib/sys_arch.c:809:/** void sys_arch_unprotect(sys_prot_t pval)
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/unix/lib/sys_arch.c:812:value specified by pval. See the documentation for sys_arch_protect() for
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/unix/lib/sys_arch.c:817:sys_arch_unprotect(sys_prot_t pval)
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/unix/netif/fifo.c:82:	/* FIXME: mutex around struct data.. */
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/win32/lib/sys_arch.c:166:sys_arch_protect(void)
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/win32/lib/sys_arch.c:185:sys_arch_unprotect(sys_prot_t pval)
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/win32/lib/sys_arch.c:206:  sys_arch_protect();
app/src/main/jni/hev-socks5-tunnel/third-part/lwip/src/ports/win32/lib/sys_arch.c:208:  sys_arch_unprotect(0);
app/src/main/jni/hev-socks5-tunnel/third-part/yaml/src/scanner.c:3442:            /* Check for "x:" + one of ',?[]{}' in the flow context. TODO: Fix the test "spec-08-13".
app/src/main/AndroidManifest.xml:39:            android:name="com.wireguard.android.backend.GoBackend$VpnService"
app/src/androidTest/java/com/darkmed/app/PlatformSmokeTest.kt:28:            ComponentName(context, "com.wireguard.android.backend.GoBackend\$VpnService"),
