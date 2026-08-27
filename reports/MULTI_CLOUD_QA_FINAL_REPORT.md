# Dark Med — MULTI_CLOUD_QA_FINAL_REPORT

## Verdict

**QA ONLY / RELEASE BLOCKED.** The multi-cloud pipeline is prepared and preflighted against the exact artifact `Dark Med f.apk` across six independent providers, but no cloud provider run was started because the required projects, credentials, endpoints, device pools, and service accounts are absent. No paid or authenticated upload was performed.

## Providers

| Provider | Official client/API path | Preflight result | Blocker |
|---|---|---|---|
| Firebase Test Lab | gcloud firebase test android run | `BLOCKED` | `GOOGLE_CLOUD_PROJECT`/`GCLOUD_PROJECT` and active auth/ADC absent |
| AWS Device Farm | AWS CLI create-upload/schedule-run | `BLOCKED` | credentials, region, project ARN, device pool ARN absent |
| BrowserStack Real Device Cloud | Appium upload + W3C suite | `BLOCKED` | username/access key/Appium endpoint absent |
| Kobiton Real Device Cloud | upload-url/S3/app version + W3C suite | `BLOCKED` | username/API key/Appium endpoint absent |
| Sauce Labs Real Device Cloud | File Storage upload + W3C suite | `BLOCKED` | username/access key/Appium endpoint absent |
| Perfecto Real Device Cloud | repository app reference + W3C suite | `BLOCKED` | cloud name/security token/app reference absent |
| Local Emulator | ADB/Gradle connected test | `BLOCKED` | no `/dev/kvm`; x86_64 requires hardware acceleration; UI smoke exposed system ANR |

The official Firebase documentation states that Test Lab runs apps on devices hosted in a Google data center and supports instrumentation, Robo, device matrices, logs, and video [1]. AWS documents Android Instrumentation support in Device Farm [2]. BrowserStack documents the multipart upload endpoint and `bs://` app identifier for App Automate [3]. Sauce Labs documents real-device Appium and App Storage [4]. Kobiton documents authenticated upload-url/S3/app-version processing [5]. Perfecto documents Android native-app Appium capabilities and repository app references [6]. These capabilities are not treated as evidence until a real run returns results.

## Implemented pipeline

The following scripts are now present:

| Script | Purpose |
|---|---|
| `tools/multicloud_qa.py` | Exact-APK hash, provider preflight, Device Matrix CSV, cross-provider CSV, run manifest |
| `tools/run_firebase_test_lab.sh` | Firebase instrumentation matrix command with Android 14/device specs, video, performance, unique results path |
| `tools/run_aws_device_farm.sh` | AWS app/test upload, schedule-run, ARN capture, initial run record |
| `tools/run_browserstack_real_device.sh` | BrowserStack upload followed by the shared W3C Appium suite |
| `tools/run_kobiton_real_device.sh` | Kobiton upload-url/S3/app-version flow followed by the shared W3C Appium suite |
| `tools/run_sauce_labs_real_device.sh` | Sauce File Storage upload followed by the shared W3C Appium suite |
| `tools/run_perfecto_real_device.sh` | Perfecto repository-reference validation followed by the shared W3C Appium suite |
| `tools/run_universal_appium_suite.sh` | Common W3C Appium session and evidence gate |
| `qa/appium/universal_appium_suite.py` | Provider-neutral session, screenshot, source, UI element, and result evidence collector |
| `tools/collect_device_smoke.sh` | ADB install/launch/UI/logcat collector that rejects system dialogs and timeouts |
| `tools/collect_network_runtime_evidence.sh` | Network metadata collector with controlled-profile gate |
| `tools/collect_crash_anr_evidence.sh` | logcat/Dropbox/process/memory collector with no-false-PASS semantics |

All scripts fail closed on missing credentials or configuration. They do not echo secret values, invent device identities, or write simulated PASS records. The adapters were executed independently after their executable-bit issue was corrected: Firebase returned exit 20 because `GOOGLE_CLOUD_PROJECT`/`GCLOUD_PROJECT` is absent; AWS returned exit 20 because its credentials, region, project ARN, and device-pool ARN are absent; BrowserStack returned exit 20 because its username/access key are absent. The corrected records are under `reports/multicloud_qa/2026-08-26_adapters/`.

## Exact artifact

The preflight and planned provider runs use the same APK. The planned matrix contains 126 `NOT_TESTED` targets spanning 18 requested OEM labels across Android 10–16; these are coverage targets, not claimed device availability or test results. MobSFscan after UI hardening returned four INFO findings and zero ERROR findings. The latest local CI also includes the Clear All Data reducer tests and WebView navigation policy contract:

```text
Path: deliverables/Dark Med f.apk
SHA-256: e61ce5e72273f680888a4026a83846fd65b5066cdb7bc1d5ba71be4bcfbd276f
Package: com.darkmed.app
Version: 0.1.0 / versionCode 1
```

## Next authorized execution

After the user supplies or configures provider accounts outside chat, the operator can set the provider variables locally, review quotas/costs, select device specs, and run each provider independently. Results must be downloaded into `reports/multicloud_qa/<provider>/<run-id>/`, verified against the exact APK SHA, and then merged into the cross-provider matrix. Cloud device behavior must still be separated from Infinix SMART 9 X6532 behavior and from network-security claims.

## References

[1]: https://firebase.google.com/docs/test-lab "Firebase Test Lab"
[2]: https://docs.aws.amazon.com/devicefarm/latest/developerguide/test-types-android-instrumentation.html "Instrumentation for Android and AWS Device Farm"
[3]: https://www.browserstack.com/docs/app-automate/api-reference/appium/apps "BrowserStack App Automate Apps API"
[4]: https://docs.saucelabs.com/mobile-apps/automated-testing/appium/real-devices/ "Sauce Labs Appium real devices"
[5]: https://docs.kobiton.com/apps/upload-an-app/using-the-kobiton-api-postman "Kobiton API app upload"
[6]: https://help.perfecto.io/perfecto-help/content/perfecto/automation-testing/supported_appium_capabilities.htm "Perfecto Appium capabilities"

## Post-discovery agent and cost controls

A project-scoped Master Orchestrator now records internal agents and external-agent availability. Code, Security, QA, Device Compatibility, Cloud Execution, and Evidence agents are available as local workflows. Claude Code is officially documented but its executable and authenticated session are absent in this environment, so it remains `BLOCKED`. OX Alpha remains `BLOCKED` because no official API, CLI, SDK, or MCP integration was verified; no third-party endpoint or credential is assumed.

A Cost Guard and approval-file verifier now sit before all six provider adapters. The adapters require `CLOUD_TEST_EXECUTE=1`, `CLOUD_COST_APPROVED=1`, and a matching `DARKMED_COST_APPROVAL_FILE` whose completed plan contains provider, exact catalog device, duration, cost, quota, run count, and `status=APPROVED`. The contract tests rejected waiting plans, provider mismatches, zero-duration plans, and placeholders. No upload, schedule, or paid cloud run occurred.

The latest local CI gate `reports/ci/20260827T_final_tun_config/status.txt` passed clean, unit tests, debug/release lint, debug/test/release assembly, Python compilation, Bash syntax, agent evidence contracts, Cost Guard contracts, task orchestrator contracts, UI/security contracts, security audit, icon pixel comparison, and orchestrator preflight. It explicitly records `cloud_execution=NOT_RUN`. This does not change the runtime verdict: real-device, VPN/TUN/Tor/DNS/Kill Switch, OEM, network, and SMART 9 evidence remain unavailable.
