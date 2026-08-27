# Dark Med — CROSS_PROVIDER_RESULTS

**Run date:** 2026-08-26  
**Artifact SHA-256:** `e61ce5e72273f680888a4026a83846fd65b5066cdb7bc1d5ba71be4bcfbd276f`  
**Overall:** **BLOCKED_NOT_RUN / QA ONLY**

## Current provider comparison

| Test layer | Firebase | AWS | BrowserStack | Kobiton | Sauce Labs | Perfecto | Overall |
|---|---|---|---|---|---|---|---|
| Provider authentication/configuration | BLOCKED | BLOCKED | BLOCKED | BLOCKED | BLOCKED | BLOCKED | BLOCKED_NOT_RUN |
| APK upload | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED |
| Real-device identity | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED |
| INSTALL | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED |
| LAUNCH | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED |
| PERMISSIONS | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED |
| PROFILES / SECURITY CENTER / COMPATIBILITY CENTER | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED |
| VPN / TUN / DNS / IPv4 / IPv6 | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_NOT_RUN |
| Tor / .onion / Kill Switch | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_BY_PLATFORM_OR_CONFIG | BLOCKED_NOT_RUN |
| Restart / stress / crash / ANR | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED | NOT_TESTED |

`BLOCKED` means provider prerequisites are absent. `NOT_TESTED` means no execution occurred. `BLOCKED_BY_PLATFORM_OR_CONFIG` is reserved for network/runtime layers that require a provider permission and a controlled network; it is not a PASS or FAIL for Dark Med.

## Evidence and corrections

All six provider preflights returned exit code 20. Firebase lacks project/authentication; AWS lacks credentials, region, project ARN, and device-pool ARN; BrowserStack lacks username, access key, and Appium endpoint; Kobiton lacks username, API key, and Appium endpoint; Sauce Labs lacks username, access key, and Appium endpoint; Perfecto lacks cloud name and security token. No upload or paid run occurred.

The full local build gate passed after the final launcher Manifest change. MobSFscan returned four informational findings and zero error findings after `singleInstance` and empty `taskAffinity` were applied. A previous five-test instrumentation run on AVD API 34 passed before that final Manifest change and is not reused as current post-change runtime coverage.

The first UI smoke harness incorrectly emitted PASS because it checked command exit codes but not the visible system dialog. The screenshot and UI hierarchy showed **“Process system isn't responding”** and the launch log showed `Status: timeout`; that record has been corrected to `FAIL`. The improved collector now detects system dialogs, app ANR, and launch timeouts before allowing PASS.

The matrix contains 126 planned rows across Android 10–16 and 18 OEM labels. These rows are coverage targets only and are all `NOT_TESTED`; no row is presented as a tested device.

## Promotion rule

A row may become `PASS` only from a provider result containing exact APK SHA, real device ID/model/manufacturer, Android version/SDK, RAM/ABI/screen metadata, test output, timestamp, logs, screenshots, video where available, and crash/ANR evidence. A UI or install PASS does not prove VPN/TUN/Tor/DNS/Kill Switch behavior, and a provider result does not prove Infinix SMART 9 behavior unless the recorded device is that model.

## References

[1]: https://firebase.google.com/docs/test-lab "Firebase Test Lab"  
[2]: https://docs.aws.amazon.com/devicefarm/latest/developerguide/test-types-android-instrumentation.html "AWS Device Farm Android instrumentation"  
[3]: https://docs.saucelabs.com/mobile-apps/automated-testing/appium/real-devices/ "Sauce Labs Appium real devices"  
[4]: https://docs.kobiton.com/apps/upload-an-app/using-the-kobiton-api-postman "Kobiton API app upload"  
[5]: https://help.perfecto.io/perfecto-help/content/perfecto/automation-testing/supported_appium_capabilities.htm "Perfecto Appium capabilities"  
