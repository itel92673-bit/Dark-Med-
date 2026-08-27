# Dark Med — Final Device Compatibility Report

## Target

The reference target is Infinix SMART 9 X6532 on Android 14/XOS. The implementation remains API-driven for Android 10+ (`minSdk 29`) and does not hard-code security logic to the device model.

## Implemented local compatibility layer

`DeviceCompatibilityCenter` reads API level, manufacturer, model, supported ABI, total RAM, WebView package version, VPN consent state, strong biometric capability, notification permission state on API 33+, battery optimization state, background restriction state, and application storage availability. The Settings screen displays these values and marks each check as Ready, Warning, Requires action, or Unsupported. The card explicitly says that a local capability readout does not prove network protection or XOS runtime compatibility.

## Device-specific requirements

| Check | What must be done on X6532 | Current status |
|---|---|---|
| Android/API | confirm Android 14 and API 34 | `DEVICE_REQUIRED` |
| XOS | record XOS version and OEM restrictions | `DEVICE_REQUIRED` |
| VPN consent | grant/revoke and verify lifecycle | `DEVICE_REQUIRED` |
| Notifications | grant POST_NOTIFICATIONS and verify persistent service notice | `DEVICE_REQUIRED` |
| Battery | exempt or document policy without weakening fail-closed behavior | `DEVICE_REQUIRED` |
| Background | screen-off, app standby, process/service restart | `DEVICE_REQUIRED` |
| Biometric | strong biometric enrollment and OEM behavior | `DEVICE_REQUIRED` |
| WebView | version, crash/restart and storage behavior | `DEVICE_REQUIRED` |
| RAM/CPU | 4 GB physical RAM measurements; extended RAM is not equivalent | `DEVICE_REQUIRED` |
| Network | Wi-Fi/mobile transition, IPv4/IPv6, Private DNS | `NETWORK_REQUIRED` |
| Tor/VPN | bootstrap, TUN and packet routing on device | `DEVICE_REQUIRED` plus `NETWORK_REQUIRED` |

## Emulator limitation

A software-emulated API 34 AVD was attempted. The AVD could appear in ADB but was not stable enough for package installation; `PackageManagerInternal.freeStorage` failed inside system_server and ADB returned `Broken pipe`. The sandbox has no `/dev/kvm`, so this environment cannot provide a trustworthy emulator runtime gate. A host with hardware virtualization or the physical X6532 is required.

## User-facing setup sequence

On the target phone, enable Developer options and USB debugging, connect with an authorized USB cable, confirm `adb devices`, install the QA APK, grant only requested permissions, review battery/background settings for Tor/VPN services, enroll the intended strong biometric, and run the supplied instrumentation and manual network checklist. Any failed protected-route check must leave the app in LOCKDOWN or unavailable state; it must not be interpreted as a successful compatibility result.
