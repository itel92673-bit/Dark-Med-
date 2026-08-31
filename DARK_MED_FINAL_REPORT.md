# DARK MED — Final QA Report

## Decision

**NO-GO**. The Debug APK was built and the biometric startup audit passed statically, but no Android runtime was available and the GitHub repository could not be updated because Git push and GitHub Git Database API both returned HTTP 403. Therefore the UI runtime and all VPN/Tor/network gates remain unverified.

## Environment

| Field | Value |
|---|---|
| Local OS | Ubuntu 24.04 sandbox |
| Local Android runtime | Not available; `adb: command not found` and no emulator |
| Target device | Infinix SMART 9 / X6532 / Android 14 |
| GitHub repository | https://github.com/itel92673-bit/Dark-Med- |
| Local commit | `7f9755ed52ffa062d671d2c7c3ea3d1399b6d033` |
| Remote main commit | `a319c6497bae74ac02893f5f15c236572f532564` before attempted upload |
| GitHub Actions run ID | NOT RUN |

## Build

The new Debug APK was built after the startup audit. SHA-256: `7fa127a3c457b9c16e78e4904b71cc0852039728d8e4f0e97fda939c77043467`. The APK contains the expected native libraries, but APK packaging is not runtime evidence.

## Final Gate

| Test | Status | Evidence |
|---|---|---|
| Biometric startup | PASS | `reports/runtime/startup_ui_biometric_audit.txt`; no `BiometricPrompt`, `FingerprintManager`, `BiometricGate`, or biometric dependency references found |
| APK build | PASS | `reports/runtime/debug_apk_build.log`; SHA in `reports/runtime/debug_apk_sha256.txt` |
| Dashboard runtime | BLOCKED | `reports/runtime/ui_runtime_result.txt`: `RUNTIME_STATUS=BLOCKED_NO_ADB_DEVICE`; no screenshot or observed screen |
| VPN/TUN | BLOCKED | No Android runtime; no TUN/dumpsys/logcat evidence |
| DNS leak | BLOCKED | No Android runtime and no leak test artifact |
| IPv4 leak | BLOCKED | No Android runtime and no external-IP test artifact |
| IPv6 leak | BLOCKED | No Android runtime and no IPv6 test artifact |
| Kill Switch | BLOCKED | No Android runtime and no failure-injection network trace |
| Tor bootstrap | BLOCKED | No Android runtime and no Tor log |
| SOCKS | BLOCKED | No Android runtime and no endpoint request evidence |
| NEWNYM | NOT RUN | No runtime; feature not executed |
| API 29 | BLOCKED | GitHub workflow not run |
| API 30 | BLOCKED | GitHub workflow not run |
| API 31 | BLOCKED | GitHub workflow not run |
| API 33 | BLOCKED | GitHub workflow not run |
| API 34 | BLOCKED | GitHub workflow not run |
| CI | BLOCKED | Local push returned HTTP 403; GitHub API returned `Resource not accessible by integration (HTTP 403)`; no run ID/artifacts |
| Security | PASS | Static source/resource/Gradle audit found no biometric references or obvious credential-like files; runtime security behavior remains unverified |

## GitHub blocker

The authenticated account was identified as `itel92673-bit`, and the repository API reported push permission. Nevertheless, `git push` returned `Permission to itel92673-bit/Dark-Med-.git denied`, including after `gh auth setup-git`. A second official route using the GitHub Git Database API also returned HTTP 403 with `Resource not accessible by integration` while creating a blob. No token or secret is included in this report. Because the source commit is not on remote `main`, GitHub Actions could not be started from it.

## Workflow coverage review

The workflow contains an API 29/30/31/33/34 emulator matrix, a KVM precondition, instrumentation execution, logcat collection, dumpsys collection, optional tcpdump, screenshot capture on failure, bugreport capture on failure, and artifact upload. The currently present `androidTest` suite is limited to platform smoke/manifest and compatibility checks; it does not provide executed evidence for real DNS/IP/IPv6 leak tests, Tor bootstrap/ SOCKS/NEWNYM, or Kill Switch interruption. The workflow's existence is therefore not a PASS for those tests.

## Evidence index

| Evidence | Location |
|---|---|
| Startup audit | `reports/runtime/startup_ui_biometric_audit.txt` |
| Runtime availability | `reports/runtime/ui_runtime_result.txt` |
| Debug build log | `reports/runtime/debug_apk_build.log` |
| Debug APK checksum | `reports/runtime/debug_apk_sha256.txt` |
| GitHub API attempt | `reports/runtime/github_api_push_result.json` |
| Earlier network integration report | `reports/embedded_tor_vpn_integration_report.md` |
| Current APK | `app/build/outputs/apk/debug/app-debug.apk` |

## Release decision

**NO-GO**. A GO requires all required runtime tests to be PASS with re-checkable evidence. The current evidence proves only the static biometric removal audit and APK build; it does not prove that the Dashboard rendered or that VPN, TUN, Tor, SOCKS, DNS protection, leak protection, or Kill Switch work on Android.
