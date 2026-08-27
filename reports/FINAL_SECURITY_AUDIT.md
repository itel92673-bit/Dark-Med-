# Dark Med — Final Security Audit

## Method

The rerunnable auditor `tools/darkmed_security_audit.sh` was executed against `deliverables/Dark Med f.apk`. The source scan excludes vendored Hev/lwIP/yaml code for app-owned findings, while the APK archive scan checks manifest-related controls and native libraries. A second run used `DARKMED_REQUIRE_PRODUCTION_SIGNING=1` to make a debug certificate a failure rather than a QA warning.

## Results

| Check | Result | Evidence |
|---|---|---|
| Cleartext traffic | PASS | `android:usesCleartextTraffic="false"` |
| Backup control | PASS | `android:allowBackup="false"` and backup rules present |
| VPN binding | PASS | `BIND_VPN_SERVICE` declaration |
| Foreground service | PASS | `FOREGROUND_SERVICE_SPECIAL_USE` and `specialUse` declarations |
| Notifications | PASS static | `POST_NOTIFICATIONS` declared; runtime grant remains device-dependent |
| Official icon | PASS static | manifest/resource reference and source hash match |
| Private-key/API literals | PASS | no obvious hardcoded material in app-owned source |
| Sensitive logging | PASS | no matched app-owned log pattern |
| Placeholder/fake source markers | PASS app-owned | no matched prohibited marker after browser button fix |
| Native APK modules | PASS static | Tor/WireGuard/Hev/JNI libraries present for checked ABI |
| obfs4 asset | WARN | no real obfs4 asset/process integration |
| Snowflake asset | WARN | no real Snowflake/WebRTC transport integration |
| Signing | WARN in QA | certificate is `CN=Android Debug` |
| Production-required signing | FAIL as intended | auditor exits 1 when debug certificate is not allowed |

## Security interpretation

The QA run returned `failures=0`, `warnings=3`. The warnings are real: debug signing, absent obfs4, and absent Snowflake. The production-required run returned exit code 1 because the APK is not signed with a user-owned production certificate. This is a deliberate gate and not a defect to suppress.

Static inspection does not prove that VPN traffic is always-on, that IPv6 cannot leak, that DNS is protected, that Tor bootstraps, that WireGuard handshakes, or that the Browser reaches an onion service. Those claims require device/network evidence.

## Corrective boundary

No fake transport binary, bridge line, renamed native library, or test bypass was added. Production signing requires a user-owned keystore kept outside source control. obfs4/Snowflake require legitimate Android-compatible implementations, license review, ABI verification, process lifecycle, Tor integration, and a real bridge/bootstrap test.
