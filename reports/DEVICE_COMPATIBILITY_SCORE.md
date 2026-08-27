# Dark Med — DEVICE_COMPATIBILITY_SCORE

**Artifact SHA-256:** `e61ce5e72273f680888a4026a83846fd65b5066cdb7bc1d5ba71be4bcfbd276f`  
**Decision:** **N/A / RELEASE BLOCKED**

## Evidence-based score

This document intentionally does not assign a compatibility percentage. A device counts only after a real execution returns verified device metadata and artifacts for the exact APK SHA. The current matrix has 126 planned target rows, but there are zero executed cloud devices and zero post-final-change real-device runs. Therefore runtime confidence is **N/A**, not 0%, 90%, or 99%.

| Metric | Current value | Interpretation |
|---|---:|---|
| Independent providers configured and executed | 0 / 6 | Firebase, AWS, BrowserStack, Kobiton, Sauce Labs, and Perfecto are preflight BLOCKED |
| Planned matrix rows | 126 | Android 10–16 × 18 OEM labels; targets only, not tests |
| Planned Infinix SMART 9 rows | 7 | highest priority target; no confirmed device result |
| Devices tested in cloud | 0 | no provider credentials/device allocation |
| Android versions tested post-final-change | 0 | no current real-device execution |
| OEMs tested post-final-change | 0 | no current real-device execution |
| Remote ABIs/RAM/screens observed | 0 | no provider metadata |
| Tests executed remotely | 0 | no upload or paid run |
| Tests passed remotely | 0 | no evidence to count |
| Tests failed remotely | 0 | no remote run to fail |
| Tests blocked/not run | 6 providers + 126 planned rows | configuration and device execution blockers |
| Post-final-change instrumentation | DEVICE_REQUIRED | previous 5-test AVD result predates final launcher Manifest change |
| Critical network tests executed | 0 | VPN/TUN/DNS/IPv4/IPv6/Tor/.onion/Kill Switch not run |
| Network evidence | N/A | controlled profile/endpoints absent |
| Crash/ANR evidence | BLOCKED | no healthy post-change ADB device |
| Crash rate | N/A | no valid runtime window |
| ANR rate | N/A | no valid post-change runtime window |

## Confidence dimensions

| Dimension | Status |
|---|---|
| Build/lint/unit confidence | PASS for the current local gate |
| Static security confidence | PASS local audit; MobSFscan has four INFO and zero ERROR after UI hardening; 3 local audit warnings remain |
| UI confidence | N/A for current runtime; one AVD smoke run failed with system responsiveness dialog and launch timeout |
| Functional confidence | Limited to local source/unit/policy evidence |
| Runtime confidence | N/A |
| Device compatibility confidence | N/A |
| Network confidence | N/A |
| VPN/TUN confidence | N/A |
| Tor confidence | N/A |
| Stress confidence | N/A |
| Overall confidence | **N/A; no percentage assigned** |

## Interpretation rule

A general compatibility PASS cannot compensate for missing packet-flow, DNS leak, Tor bootstrap, `.onion`, Kill Switch, process recovery, or network recovery evidence. A provider result is scoped to its recorded device/model and does not prove Infinix SMART 9 behavior unless that exact model is returned in provider metadata.

## References

[1]: https://firebase.google.com/docs/test-lab "Firebase Test Lab"  
[2]: https://docs.aws.amazon.com/devicefarm/latest/developerguide/test-types-android-instrumentation.html "AWS Device Farm Android instrumentation"  
[3]: https://docs.saucelabs.com/mobile-apps/automated-testing/appium/real-devices/ "Sauce Labs Appium real devices"  
[4]: https://docs.kobiton.com/apps/upload-an-app/using-the-kobiton-api-postman "Kobiton API app upload"  
[5]: https://help.perfecto.io/perfecto-help/content/perfecto/automation-testing/supported_appium_capabilities.htm "Perfecto Appium capabilities"  
