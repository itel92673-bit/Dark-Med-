# Android Security and Compatibility Research — 2026-08-24

## Sources

1. [VpnService API reference](https://developer.android.com/reference/android/net/VpnService)
2. [Show a biometric authentication dialog](https://developer.android.com/identity/sign-in/biometric-auth)
3. [Restrictions on starting a foreground service from the background](https://developer.android.com/develop/background-work/services/fgs/restrictions-bg-start)
4. [Android 15 features and changes list](https://developer.android.com/about/versions/15/summary)

## Findings and design impact

The Android `VpnService` reference explains that a VPN application creates a virtual network interface and must process outgoing packets read from the interface and inject incoming packets back into it. It requires user action the first time a VPN connection is created, permits only one VPN connection at a time, displays a system-managed notification, and restores the network when the interface file descriptor is closed, including crash or system-kill cases. The documented creation flow calls `prepare()`, establishes the interface, and then processes packets. Therefore the current Dark Med builder and native Hev library are only partial integration evidence until a real TUN packet loop and protected upstream socket path are observed on a device.

The Android biometric guide distinguishes `BIOMETRIC_STRONG` (Class 3), `BIOMETRIC_WEAK` (Class 2), and `DEVICE_CREDENTIAL`. The project intentionally requests `BIOMETRIC_STRONG` only and does not configure device-credential fallback. Android reports authenticator capability rather than a universal sensor label, so a physical-device test is still required to establish that the OEM presents the desired fingerprint-only behavior. The source-level policy can be PASS as configuration, but fingerprint modality behavior is REAL_DEVICE_REQUIRED.

Android's foreground-service guidance says Android 12+ restricts background starts and may throw `ForegroundServiceStartNotAllowedException`; user-visible actions are among the documented exemptions. The Android 14 guidance requires the appropriate service type and permission, while this project uses `specialUse` plus the dedicated permission and manifest subtype properties. The app must therefore start protection from explicit foreground user interaction and still undergo real Android 14/15 lifecycle testing.

The Android 15 change list documents support for 16 KB page sizes and recommends rebuilding native applications and testing in a 16 KB environment. It also documents safer intents, restricted TLS 1.0/1.1 for apps targeting Android 15, and edge-to-edge enforcement for apps targeting 15. Dark Med contains native libraries and targets SDK 35, so 16 KB native compatibility and Android 15 UI/intent behavior remain open compatibility tests; they are not inferred from a successful local APK build.

Classification: CONFIGURATION_SUPPORTED for manifest and policy facts; UNIT_PASS where covered by tests; REAL_DEVICE_REQUIRED for VPN packet processing, biometric modality, service lifecycle, OEM behavior, and 16 KB runtime; NETWORK_REQUIRED for route, DNS, Tor, proxy, and leak assertions.
