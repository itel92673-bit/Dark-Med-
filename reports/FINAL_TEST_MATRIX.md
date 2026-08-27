# Dark Med — Final Test Matrix

## Executed local gates

| Test/gate | Result | Evidence |
|---|---|---|
| `:app:testDebugUnitTest` | PASS | `reports/evidence/final_current/unit.log` and regression logs |
| Contract tests | PASS | `EngineeringContractsTest` included in unit run |
| Failure policy tests | PASS | `FailureInjectionMatrixTest` included in unit run |
| Tor renderer tests | PASS | `TorConfigRendererTest` included in unit run |
| `:app:lintDebug` | PASS | current regression log |
| `:app:assembleDebug` | PASS | current regression log |
| `:app:assembleDebugAndroidTest` | PASS | current regression log |
| Static security audit | PASS with warnings | `dark_med_f_audit.log` |
| Production-signing audit | FAIL as intended | `dark_med_f_production_required_audit.log` |
| APK signature verification | PASS technically | v2 signature verified; certificate is Android Debug |
| Icon hash comparison | PASS | source icon hash equals supplied icon hash |

The current unit inventory is **16 tests, zero failures, zero errors** after the post-recovery cycle. The latest UI/compatibility regression completed with `BUILD SUCCESSFUL`.

## Android instrumentation

`PlatformSmokeTest` and `DeviceCompatibilityInstrumentedTest` compile into the test APK. Execution was attempted on API 34 AVD. Installation/runner startup failed at the emulator package manager layer with `PackageManagerInternal.freeStorage(...) NullPointerException` and `Broken pipe`; the runner therefore executed **zero tests**. This is recorded as `EMULATOR_BLOCKED`, not PASS.

## Required device tests

| Domain | Required execution | Current status |
|---|---|---|
| install/launch | install APK, launch, collect logcat | `DEVICE_REQUIRED` |
| permission flow | notifications and VPN consent | `DEVICE_REQUIRED` |
| biometric | success/cancel/failure/background/restart/enrollment change | `DEVICE_REQUIRED` |
| Tor | startup/bootstrap/SOCKS/ControlPort/NEWNYM/shutdown/retry | `NETWORK_REQUIRED` |
| VPN/TUN/Hev | establish TUN, packet flow, loop prevention, cleanup | `DEVICE_REQUIRED` |
| WireGuard | valid profile, activation, handshake, route and failure | `NETWORK_REQUIRED` |
| DNS/IPv4/IPv6 | resolver path and leak checks during failures | `NETWORK_REQUIRED` |
| Kill switch | Tor/VPN/TUN/network/process death and no direct path | `DEVICE_REQUIRED` plus `NETWORK_REQUIRED` |
| Browser | protected route, onion load, WebView policy and sessions | `DEVICE_REQUIRED` plus `NETWORK_REQUIRED` |
| wipe | data categories, keystore alias, process/session cleanup, restart | `DEVICE_REQUIRED` |
| performance | meminfo, CPU, battery, thermal, OOM/ANR | `DEVICE_REQUIRED` |
| XOS | background restrictions, battery optimization, service restart | `DEVICE_REQUIRED` |

## Not run

Firebase Test Lab was not run because no project credentials or authorized Firebase configuration is present. API 29/31/33 emulator matrix was not completed because the available environment lacks KVM and the API 34 image was not stable enough for package installation.
