# Dark Med — STRESS_TEST_REPORT

## Current result

No two-hour or eight-hour real-device stress run was executed. The local software-emulated API 34 AVD could not install the APK, and no cloud-provider credentials are configured. Consequently crash rate, ANR rate, memory, CPU, battery, thermal, service death, and network recovery metrics are **N/A**, not zero and not PASS.

## Required stress protocol

Each selected real device must run at least two hours, with an eight-hour preferred run where quotas permit. The run must include foreground/background transitions, screen off/on, Wi-Fi/mobile changes, airplane mode, VPN restart, Tor restart, profile changes, app restart, force stop, process recreation, and service death/recovery. The run must retain logcat, provider video where available, screenshots, device metadata, timestamp, exact APK SHA, `dumpsys meminfo`, CPU/battery/thermal samples, ANR/crash evidence, and network state transitions.

## Promotion rule

A stress result is provider- and device-scoped. One clean UI session cannot establish stress confidence or compensate for a critical VPN, DNS, Tor, or Kill Switch failure. A provider limitation must be marked `BLOCKED_BY_PLATFORM`.
