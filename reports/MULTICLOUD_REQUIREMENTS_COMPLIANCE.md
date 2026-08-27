# Dark Med — Multi-Cloud QA Requirements Compliance

**Date:** 2026-08-26  
**Artifact:** `deliverables/Dark Med f.apk`  
**APK SHA-256:** `e61ce5e72273f680888a4026a83846fd65b5066cdb7bc1d5ba71be4bcfbd276f`  
**Package:** `com.darkmed.app`  
**Decision:** **QA ONLY / RELEASE BLOCKED**

## Executive statement

The QA architecture now contains six independent provider paths: Firebase Test Lab, AWS Device Farm, BrowserStack Real Device Cloud, Kobiton Real Device Cloud, Sauce Labs Real Device Cloud, and Perfecto Real Device Cloud. A shared W3C Appium suite and central matrix/orchestrator have been added. The matrix covers Android 10–16, 18 OEM labels, and gives maximum priority to Infinix SMART 9. These additions are workflow capability, not execution evidence.

The current run used the exact APK SHA above. The full local Android build gate passed after the Manifest security fix. Five instrumentation tests had previously passed on an AVD API 34, but the subsequent launcher security change requires a fresh device execution that could not be repeated because the host lost the required KVM/emulator capability. A later UI smoke run exposed a real Android system responsiveness dialog and was corrected from PASS to FAIL. No cloud upload or paid run was started; all six provider preflights are BLOCKED by missing account configuration.

## Provider compliance

| Provider | Adapter | Required evidence | Current state | Honest interpretation |
|---|---|---|---|---|
| Firebase Test Lab | `tools/run_firebase_test_lab.sh` | device matrix, instrumentation output, logs, video/performance, exact APK/test APK SHA | `BLOCKED` | project/auth missing; no device run |
| AWS Device Farm | `tools/run_aws_device_farm.sh` | app/test upload, run ARN, device metadata, logs/artifacts, exact SHA | `BLOCKED` | credentials/region/project/device pool missing; no upload |
| BrowserStack | `tools/run_browserstack_real_device.sh` + universal Appium | upload `app_url`, W3C session, device metadata, screenshots/video/logs, exact SHA | `BLOCKED` | credentials/endpoint missing; no upload |
| Kobiton | `tools/run_kobiton_real_device.sh` + universal Appium | upload URL/S3/app version/parsing status, W3C session, device evidence, exact SHA | `BLOCKED` | username/API key/Appium endpoint missing; no upload |
| Sauce Labs | `tools/run_sauce_labs_real_device.sh` + universal Appium | File Storage ID, W3C session, device metadata, video/logs, exact SHA | `BLOCKED` | username/access key/Appium endpoint missing; no upload |
| Perfecto | `tools/run_perfecto_real_device.sh` + universal Appium | repository locator, W3C session, device metadata, video/screenshots/logs, exact SHA | `BLOCKED` | cloud name/security token/app reference missing; no session |

The provider scripts fail closed. They do not write simulated device rows, do not infer a model from a requested capability, and do not treat an upload as a test PASS. Sauce Labs supports storing Android APK/AAB files and referencing a storage identifier in Appium capabilities [1]. Kobiton documents its authenticated upload-url → S3 PUT → app-version flow [2]. Perfecto documents Android native-app capabilities and repository app references [3].

## Matrix compliance

| Requirement | Implementation | Current evidence |
|---|---|---|
| Android 10–16 | `config/multicloud_device_matrix_plan.json` SDK mapping 29, 30, 31, 33, 34, 35, 36 | 126 planned rows, all `NOT_TESTED` |
| OEM breadth | 18 labels across three weighted tiers | planned targets only; actual provider catalogs not queried |
| Infinix priority | Infinix tier 1 and SMART 9 target weight 5.0 | 7 planned SMART 9 rows; no real SMART 9 result |
| Matrix dimensions | provider, manufacturer, model, Android, SDK, RAM, ABI, screen, resolution, DPI | evidence fields defined; runtime values absent |
| General compatibility | install, launch, permissions, navigation, profiles, security/compatibility center, restart/state | not promoted after post-fix device block |
| Network runtime | Wi-Fi/4G/5G, IPv4/IPv6, dual-stack, DNS, switching, airplane/recovery | collector present; controlled network profile absent |
| VPN/TUN/Tor | consent, TUN, routing, DNS leak, Tor/bootstrap/circuit/.onion/kill switch | separate gates; no runtime PASS |
| Crash/ANR | logcat, activity processes, meminfo, Dropbox crash/ANR | collector present; no healthy post-fix device |

The number **126** is a planned target count, not a count of tested devices. The only real runtime device evidence in this session is a prior five-test instrumentation result on an AVD API 34 before the final launcher Manifest change; it cannot be reused as post-change PASS evidence.

## Local validation evidence

| Check | Result |
|---|---|
| Clean/unit/lint/debug/androidTest/release build after final Manifest change | `PASS` for all seven Gradle gates |
| Local security audit QA mode | `PASS`, 0 failures, 3 warnings |
| Production-signing-required audit | expected `FAIL`: APK has Android Debug certificate |
| MobSFscan source after UI hardening | `PASS` process exit, four `INFO` findings, zero `ERROR` findings |
| Universal Appium Suite syntax | `PASS` Python compilation |
| Provider shell syntax | `PASS` for all adapters |
| Provider preflight | six explicit `BLOCKED` results, exit 20 |
| Cloud uploads/runs | 0 |
| Infinix SMART 9 executions | 0 |

## Mandatory blockers before release

A production release remains blocked by the debug certificate, absent real-device execution after the final Manifest change, absent cloud credentials/device catalogs, absent controlled network endpoints, and unverified VPN/TUN/Tor/DNS/IPv4/IPv6/.onion/Kill Switch behavior. No confidence percentage is assigned. A provider UI/Appium PASS would remain scoped to that provider/device configuration and would not prove full-device network protection or Infinix SMART 9 compatibility.

## References

[1]: https://docs.saucelabs.com/mobile-apps/app-storage/ "Sauce Labs Mobile App Storage"  
[2]: https://docs.kobiton.com/apps/upload-an-app/using-the-kobiton-api-postman "Kobiton API app upload"  
[3]: https://help.perfecto.io/perfecto-help/content/perfecto/automation-testing/supported_appium_capabilities.htm "Perfecto supported Appium capabilities"  
[4]: https://docs.saucelabs.com/mobile-apps/automated-testing/appium/real-devices/ "Sauce Labs Appium real devices"  
[5]: https://docs.kobiton.com/apps/manage-apps "Kobiton manage apps"  
