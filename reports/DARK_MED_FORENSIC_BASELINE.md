# Dark Med Forensic Baseline — Current Recovery Cycle

**Date:** 2026-08-25 UTC  
**Target:** Infinix SMART 9 X6532 / Android 14 / XOS 14.0.1  
**Repository:** no Git repository is present at `/home/ubuntu/DarkMed`; provenance is recorded explicitly where source was recovered.

## Environment

Android SDK was rebuilt under `/home/ubuntu/android-sdk` with command-line tools, platform-tools, emulator, API 34 and API 35 platforms/images, Build Tools 35.0.0, and NDK 27.2.12479018. OpenJDK 21 with `javac` was restored. Gradle Wrapper was recreated at version 9.5.1 because the official Gradle 9.5.0 binary distribution URL was unavailable; AGP/Kotlin/dependencies were not upgraded. The current project has `local.properties` pointing to the SDK.

Two AVDs exist: `darkmed_api34` and `darkmed_api35`; a clean API 34 default image AVD named `darkmed_api34_default` was also created. `/dev/kvm` is absent, so attempted boot uses TCG software acceleration. AVD API 34 appeared in ADB and exposed API 34/x86_64 properties, but package installation repeatedly failed with PackageManager/system_server errors, including `PackageManagerInternal.freeStorage(...)` NullPointerException and `Broken pipe (32)`. `connectedDebugAndroidTest` therefore started 0 tests and failed before test execution.

## Current source and recovery

The current source includes localized Compose UI, Security Center and Profiles truth screens, Device Compatibility Center, security/evidence contracts, Failure Injection matrix, Tor configuration/service, BiometricGate, Browser session host, ClearAllDataCoordinator, DataWiper, `SecurityState`, `HevTun2Socks`, `DarkMedVpnService`, and PlatformSmokeTest. Missing classes identified by the current compiler were recovered from the existing QA APK artifact as equivalent source for `BrowserSession`, `WebViewSessionInitializer`, `SecurityState`, `HevTun2Socks`, `Tun2SocksStats`, and `DarkMedVpnService`. HevSocks5Tunnel was restored from upstream commit `0428c4ebb0df933ebac8e507832f252ef7da47f1` with its `yaml`, `lwip`, and `hev-task-system` submodules. The app-owned JNI wrapper remains a real bridge to `hev_socks5_tunnel_main_from_file`, `hev_socks5_tunnel_quit`, and `hev_socks5_tunnel_stats`; no native stub was used.

The official icon was copied unchanged from `/home/ubuntu/upload/1000045205.png`. Current icon SHA-256 is `9d877c5adb06c57549d3680c8848ec7bc5c312b9c12009b619af60bd7c6e629d`.

## Build evidence

The following final sequence passed after clean:

```text
./gradlew --no-daemon --max-workers=1 clean
./gradlew --no-daemon --max-workers=1 :app:testDebugUnitTest
./gradlew --no-daemon --max-workers=1 :app:lintDebug
./gradlew --no-daemon --max-workers=1 :app:lintRelease
./gradlew --no-daemon --max-workers=1 :app:assembleDebug
./gradlew --no-daemon --max-workers=1 :app:assembleDebugAndroidTest
./gradlew --no-daemon --max-workers=1 :app:assembleRelease
```

All returned `BUILD SUCCESSFUL`. Current unit inventory is 16 test cases, 0 failures, and 0 errors. The instrumentation APK compiles. The real command `./gradlew --no-daemon --max-workers=1 :app:connectedDebugAndroidTest` was attempted on API 34 and failed before execution due to AVD installation failure; it is not a test assertion PASS.

## APK evidence

| Artifact | Size | SHA-256 | Classification |
|---|---:|---|---|
| `deliverables/DarkMed_QA_Debug.apk` | 109,775,613 bytes | `158e642cc081d98cb526b51102b9700a7e3785ac9bc53cbc2bd71ea43d21be1a` | QA/debug |
| `deliverables/DarkMed_QA_Release_DebugSigned.apk` | 93,647,178 bytes | `0c365d2e4f68bc4e88c7ceb7c3708eeb3c376f9dfb76debcd24c9f21c67a87d3` | QA release variant |
| `deliverables/Dark Med f.apk` | 93,647,178 bytes | `0c365d2e4f68bc4e88c7ceb7c3708eeb3c376f9dfb76debcd24c9f21c67a87d3` | requested name; QA/debug-signed |
| `deliverables/DarkMed_QA_AndroidTest.apk` | 956,963 bytes | `73d7ca3ed079896628f2b391f91288161ebe40c92e7a60ac8c5297b12dc83070` | instrumentation APK |

`apksigner verify --verbose` passes for debug and release variants through v2. The release variant is signed with `C=US, O=Android, CN=Android Debug`; certificate SHA-256 is `a4b04f78c7aefba840f4775336fda984720f1811830f907f05d8516143843d1f`. This is not production signing.

The release APK contains Tor, WireGuard, Hev, JNI, and `libc++_shared` libraries for `armeabi-v7a`, `arm64-v8a`, `x86`, and `x86_64`. It contains no obfs4 or snowflake transport asset. AAPT confirms `allowBackup=false`, `usesCleartextTraffic=false`, special-use foreground services, and VPN binding declarations.

## Security and runtime boundary

The static/APK auditor returns `failures=0` and three real QA warnings: debug signing, absent obfs4, and absent snowflake assets. With `DARKMED_REQUIRE_PRODUCTION_SIGNING=1`, it returns `failures=1` as intended because the certificate is Android Debug. This proves only static properties and archive contents. It does not prove Tor bootstrap, VPN/TUN packet flow, WireGuard handshake, proxy chaining, DNS/IPv6 leak absence, kill-switch behavior, Browser `.onion` access, storage isolation, biometric modality, XOS lifecycle, or residual deletion.

The honest status for those properties is `NOT VERIFIED`, `DEVICE_REQUIRED`, `NETWORK_REQUIRED`, or `ENVIRONMENT_BLOCKED`. No runtime/network PASS is recorded.

## Evidence files

Gate logs are under `reports/evidence/final_current/`. The AVD/install diagnostics are under `/tmp/darkmed_*` during the current session; the key current evidence is copied into `reports/evidence/reset_cycle/current_evidence.log` and the final gate logs. The comprehensive report is `DARK_MED_MASTER_ENGINEERING_REPORT.md`.
