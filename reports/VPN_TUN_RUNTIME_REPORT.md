# Dark Med — VPN_TUN_RUNTIME_REPORT

## Current result

`VpnService`, Hev source/submodules, and the app-owned JNI bridge compile successfully. This is implementation/build evidence only. No cloud or physical-device execution occurred in the current multi-cloud cycle, and the local API 34 AVD could not install the APK because its PackageManager/system_server failed.

| Test | Status | Required evidence |
|---|---|---|
| VpnService consent | `BLOCKED` | device prompt and result |
| VPN start/stop/restart | `BLOCKED` | service lifecycle log and dumpsys |
| TUN initialization | `BLOCKED` | file descriptor/native stats and device log |
| Hev packet forwarding | `BLOCKED` | controlled packet capture and route check |
| routing/default route | `BLOCKED` | `ip route`, VpnService state, no direct path |
| process death/recovery | `BLOCKED` | kill/restart evidence and fail-closed state |
| background/screen-off | `BLOCKED` | device lifecycle trace |
| platform-limited cloud execution | `UNKNOWN` | provider-specific capability result required |

## Non-negotiable rule

`setBlocking(true)` is not counted as Kill Switch proof. Kill Switch requires a real path failure with an observed block and a no-direct-fallback result. A cloud provider that restricts VPN/TUN must be recorded as `BLOCKED_BY_PLATFORM`.

## Next execution

Run the instrumentation and manual smoke suite on a healthy real Android device or authorized cloud device. Then collect service state, packet counters, route tables, DNS behavior, and controlled failure evidence using the exact APK SHA recorded in the Device Matrix.
