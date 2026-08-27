# Dark Med — Final Forensic Audit

**Scope:** current repository, current APK artifacts, current SDK/AVD state, and current source only.

## Executive finding

Dark Med is a Kotlin/Compose Android application with real Tor Android, WireGuard tunnel, VpnService, Hev native source/JNI, AndroidKeyStore storage, isolated WebView processes, biometric gating, local assistant contracts, failure-injection policies, and a rerunnable static/APK auditor. The project builds locally after environment recovery. Runtime verification is not complete: the API 34 AVD is not healthy enough to install the APK, and no Infinix SMART 9 X6532 or network test endpoint is connected.

## Inventory

| Area | Current evidence | Classification |
|---|---|---|
| Gradle/toolchain | Gradle wrapper 9.5.1, AGP/Kotlin configuration, JDK 21, compileSdk 37, minSdk 29, targetSdk 35 | `CODE_VERIFIED` |
| UI | `MainActivity.kt`, Arabic/English resources, dark UI, truthful locked/unavailable states, compatibility card | `CODE_VERIFIED` |
| Manifest | VPN services, Tor service, notification permission, cleartext disabled, backup disabled, non-exported browser activities | `STATIC_VERIFIED` |
| Tor | tor-android/jtorctl dependencies, TorService wrapper, torrc writer, ControlPort/NEWNYM paths, SafeLogging | `INTEGRATION_VERIFIED`; bootstrap `NETWORK_REQUIRED` |
| VPN/TUN | Android VpnService, Hev source/submodules, app JNI bridge and lifecycle methods | `CODE_VERIFIED`; packet flow `DEVICE_REQUIRED` |
| WireGuard | official tunnel dependency, config validator/controller | `CODE_VERIFIED`; handshake `NETWORK_REQUIRED` |
| DNS | route/planner and mapped DNS policy | planner/config only; leak proof `NETWORK_REQUIRED` |
| Proxy | chain compiler/validation | no actual forwarder found; `NOT_IMPLEMENTED` |
| Browser | WebView host, four process declarations, suffix/isolation policy, cleanup | code present; protected routing `.onion` `DEVICE_REQUIRED` |
| Biometric | `BIOMETRIC_STRONG`, no credential fallback, localized prompt | code present; OEM fingerprint behavior `DEVICE_REQUIRED` |
| Storage/wipe | AES/GCM AndroidKeyStore, coordinator, post-check | static/code verified; residual check `DEVICE_REQUIRED` |
| Assistant | local command model, registry, consent/redaction | local mode `LOCALLY_VERIFIED`; OpenAI remote `NOT_CONFIGURED` |
| Native libraries | APK archive contains Tor/WireGuard/Hev/JNI libraries; four ABI build configuration | `APK_STATIC_VERIFIED` |
| PT | no obfs4/snowflake assets or runtime integration | `NOT_IMPLEMENTED / BLOCKED` |

## Current environment finding

The current sandbox has JDK 21 and an Android SDK restored under `/home/ubuntu/android-sdk`, but `ANDROID_HOME` is not globally exported in a fresh shell and `/dev/kvm` is absent. AVD API 34 can appear in ADB while still failing `PackageManagerInternal.freeStorage(...)` and `Broken pipe` during install. This is an emulator system-server/storage failure, not evidence that the Dark Med APK is invalid.

## Required remaining evidence

A healthy hardware-accelerated Emulator or the target phone must install the APK, launch it, run instrumentation, grant/reject permissions, exercise the biometric flow, start/stop services, and collect logcat/dumpsys. A controlled network must then verify Tor bootstrap, SOCKS, ControlPort, TUN forwarding, DNS and IPv6 behavior, WireGuard handshake, kill-switch failure behavior, browser isolation, and safe onion loading.

## Forensic conclusion

No runtime feature is promoted to PASS by this audit. The artifact is suitable for QA installation attempts, not a production security release.
