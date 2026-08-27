# Dark Med — Final Release Readiness

## Decision

> **RELEASE BLOCKED — QA ARTIFACT ONLY**

The current application is buildable and locally auditable, but it is not ready to be represented as a final security/network release. The decisive missing evidence is healthy device execution and controlled network verification. The current APK is signed with the Android Debug certificate and is therefore not a production-signed release.

## Gate status

| Gate | Status | Evidence or blocker |
|---|---|---|
| Source/build toolchain | `PASS — LOCAL` | Gradle and JDK 21 configuration; sequential build logs |
| Unit tests | `PASS — LOCAL` | 16 tests, 0 failures, 0 errors |
| Lint | `PASS — LOCAL` | debug/release lint logs |
| Android test compilation | `PASS — LOCAL` | `assembleDebugAndroidTest` |
| Instrumentation execution | `BLOCKED` | API 34 AVD PackageManager/system_server NPE; 0 tests executed |
| Emulator matrix | `BLOCKED` | no KVM; API 29/31/33/34 matrix not completed |
| Firebase Test Lab | `NOT RUN` | no authorized project credentials |
| Tor bootstrap | `NETWORK_REQUIRED` | no real bootstrap evidence |
| VPN/TUN/Hev packet flow | `DEVICE_REQUIRED` | no device TUN execution |
| WireGuard handshake | `NETWORK_REQUIRED` | no valid endpoint/profile evidence |
| DNS/IPv4/IPv6 leak behavior | `NETWORK_REQUIRED` | no controlled packet/leak capture |
| Kill Switch | `DEVICE_REQUIRED + NETWORK_REQUIRED` | policy tested; traffic blocking not proven |
| Browser/.onion | `DEVICE_REQUIRED + NETWORK_REQUIRED` | WebView exists; protected onion route not proven |
| Session isolation | `DEVICE_REQUIRED` | code/process architecture exists; cross-session proof absent |
| Biometric/OEM | `DEVICE_REQUIRED` | strong biometric code exists; Infinix behavior untested |
| Clear All Data residual verification | `DEVICE_REQUIRED` | coordinator/post-check code exists; device residuals untested |
| obfs4 | `NOT IMPLEMENTED / BLOCKED` | no real PT asset/integration |
| Snowflake | `NOT IMPLEMENTED / BLOCKED` | no real WebRTC PT integration |
| Security static audit | `PASS — LOCAL WITH WARNINGS` | failures=0, warnings=3; warnings are retained |
| Production signing | `USER_KEY_REQUIRED` | current certificate is `CN=Android Debug` |
| Final release | `NO` | dependent gates remain blocked |

## Delivered QA artifact

| Artifact | Size | SHA-256 | Certificate |
|---|---:|---|---|
| `deliverables/Dark Med f.apk` | 93,647,178 bytes | `0c365d2e4f68bc4e88c7ceb7c3708eeb3c376f9dfb76debcd24c9f21c67a87d3` | Android Debug; v2 verified |

The packaged icon hash matches the supplied official icon hash `9d877c5adb06c57549d3680c8848ec7bc5c312b9c12009b619af60bd7c6e629d`.

## Required next evidence

A healthy hardware-accelerated Emulator or the Infinix SMART 9 must install the artifact and execute instrumentation. A controlled network and valid profiles are then required to verify Tor, VPN/TUN, WireGuard, DNS, IPv6, fail-closed behavior, browser isolation, and onion access. A user-owned production keystore is required before a production APK can be signed. No private key or password should be placed in chat or source control.
