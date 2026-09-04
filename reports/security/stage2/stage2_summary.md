# DARK MED — STAGE 2 SUMMARY

## Baseline and tested build

The audited baseline is commit `c7c583863792a47c5e09d841861557509b8785ec`. This Stage 2 working tree adds a fail-closed route state model and binds it to `DarkMedVpnService`; therefore the APK tested by local build is not identical to the baseline. The resulting Release APK is `deliverables/stage2/Dark_Med_Stage2_release.apk` with SHA-256 `972f343f8f7c5fdd2ac7d7e1c865af29b3a62687bb416ffb556c52810fe44022`.

## Changes implemented

A single auditable `ProtectedRouteState` model now distinguishes `TUN_ESTABLISHED`, `TOR_READY`, `PROXY_READY`, `READY`, `DEGRADED`, `FAILED`, and `KILLED`. The service no longer closes the detached TUN descriptor immediately when native tun2socks startup fails; it retains the TUN while reporting a blocked state, preventing the previous silent transition from a failed engine to ordinary-looking connectivity while the service remains active. A monitor polls the native worker and moves the route to `FAILED` when the worker exits. The service notification explicitly says `BLOCKED` when upstream protection is unverified. The JNI protector callback now has a dedicated lifetime mutex; callback removal is serialized with Java calls, and Hev's verified `protect=false` behavior closes the socket and returns an error before connection. This remains source/build evidence only until runtime failure injection is executed. Unit regression tests prove that TUN alone is not `PROTECTED`, that `READY` requires all chain components and upstream protection, and that fatal failure clears protected components.

This is a fail-closed improvement for the service-active failure path. It is not a universal Android system kill switch: Android may restore ordinary connectivity after VPN service death or descriptor removal unless user/system lockdown is configured. That limitation remains a release blocker.

## Local verification

| Gate | Result | Evidence |
|---|---|---|
| Unit tests including fail-closed regression | PASS | `reports/security/stage2/regression_release.log` and Gradle XML reports |
| Debug lint | PASS | `reports/security/stage2/fail_closed_build.log` |
| Debug APK build | PASS | same build log |
| Release APK build | PASS | `reports/security/stage2/regression_release.log` |
| Source diff check | PASS | shell verification |
| Dashboard runtime | BLOCKED | Emulator installation failed before app launch |

The local Android API 34 emulator used software acceleration because `/dev/kvm` is unavailable. It reached an ADB device state, but Android package installation failed with `PackageManagerInternal.freeStorage` `NullPointerException` in `StorageManagerService.allocateBytes`, despite approximately 9 GB available under `/data/user/0`. The emulator also did not complete first boot. This is an environment blocker, not evidence of an application startup crash. Raw logs are under `reports/security/stage2/stage2_logs/` and `reports/security/runtime_api34/retry/`.

## Runtime leak matrix

The machine-readable matrix is `stage2_leak_matrix.csv`. No leak-prevention gate receives PASS. There is no packet capture, independently observed external IPv4/IPv6 result, DNS resolver observation, or reproducible failure-injection runtime artifact for this Stage 2 build.

| Gate | Result | Reason |
|---|---|---|
| VPN/TUN packet forwarding | BLOCKED | APK could not be installed on local emulator; no physical device attached |
| DNS leak | NOT RUN | No runtime DNS capture or independent resolver evidence |
| IPv4 direct egress | NOT RUN | No controlled external endpoint and packet capture |
| IPv6 direct egress | NOT RUN | No dual-stack runtime evidence |
| Kill switch | BLOCKED | Android lockdown/direct-egress failure test unavailable |
| Tor bootstrap 100% | NOT RUN | No Tor runtime bootstrap log |
| SOCKS response | NOT RUN | No runtime SOCKS probe |
| NEWNYM | NOT RUN | No ControlPort client/response evidence |
| UDP/QUIC | NOT RUN | No runtime traffic generation and capture |
| protect(false) | NOT RUN | Static callback semantics are known to abort Hev socket creation, but injected runtime behavior was not executed |

## Security decision

The local code change and regression tests are PASS at the code/build level. They do not prove packet routing, absence of DNS/IP/IPv6 leaks, Tor upstream protection, or kill-switch behavior. The Stage 2 release gate is therefore **NO-GO**. The mandatory next evidence is a stable Android runtime—preferably a physical Android device or a CI emulator with completed boot and network observation—plus packet capture and independently observed external IP/DNS evidence for every mandatory leak test.

## GitHub Actions runtime evidence

GitHub Actions run `33829430812` completed successfully at commit `56d0a77f09d20b44487ba297c6ac3052c1190a13`: [run link](https://github.com/itel92673-bit/Dark-Med-/actions/runs/33829430812). The matrix executed on API 29, 30, 31, 33 and 34. Each API artifact reports six passing instrumentation tests with zero failures, zero errors and zero skipped tests: `DeviceCompatibilityInstrumentedTest`, `PlatformSmokeTest` and `StartupRuntimeTest`.

This is valid runtime evidence for emulator startup/manifest/compatibility smoke coverage, including MainActivity reaching the resumed lifecycle state without a biometric gate. It is not evidence for VPN/TUN packet forwarding, DNS leak absence, IPv4/IPv6 leak absence, Kill Switch, Tor bootstrap, SOCKS, NEWNYM, UDP/QUIC, or packet-level fail-closed behavior. The artifact inventory contains no LEAK-001 through LEAK-009 acceptance tests and no packet capture proving direct egress equals zero.
