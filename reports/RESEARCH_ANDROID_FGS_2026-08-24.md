# Android Foreground Service Research — 2026-08-24

Source: https://developer.android.com/develop/background-work/services/fgs/service-types

The Android Developers guidance states that beginning with Android 14 (API 34), an app must declare an appropriate foreground-service type in the manifest and request the corresponding foreground-service permission in addition to `FOREGROUND_SERVICE`. For `specialUse`, the manifest type is `specialUse`, the permission is `FOREGROUND_SERVICE_SPECIAL_USE`, and the constant passed to `startForeground()` is `FOREGROUND_SERVICE_TYPE_SPECIAL_USE`. The guidance also requires a `<property>` under the service to explain the special-use case for review.

Project evidence: `AndroidManifest.xml` currently declares `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`, `android:foregroundServiceType="specialUse"`, and `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` for Dark Med VPN, WireGuard backend service, and Tor foreground service. This is manifest/configuration evidence only; it does not prove that Android accepted or kept the services running on a physical Android 14/15 device.

Classification: CONFIGURATION_SUPPORTED; REAL_DEVICE_REQUIRED for lifecycle, background-start, notification, and OEM behavior.
