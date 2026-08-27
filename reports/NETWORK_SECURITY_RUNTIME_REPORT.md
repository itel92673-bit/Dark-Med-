# Dark Med — NETWORK_SECURITY_RUNTIME_REPORT

## Current result

No controlled cloud or physical-device network run was executed. The local AVD was not stable enough to install the APK. Therefore all network-security outcomes remain `NETWORK_REQUIRED`, `DEVICE_REQUIRED`, or `BLOCKED`.

| Area | Required test | Current status |
|---|---|---|
| DNS resolution | resolve through the protected resolver | `NETWORK_REQUIRED` |
| DNS routing | verify resolver path and no direct resolver | `NETWORK_REQUIRED` |
| DNS leak | IPv4/IPv6 leak capture | `NETWORK_REQUIRED` |
| VPN DNS | DNS while TUN is active | `DEVICE_REQUIRED + NETWORK_REQUIRED` |
| IPv4 | protected IPv4 request | `NETWORK_REQUIRED` |
| IPv6 | protected IPv6 request and no leak | `NETWORK_REQUIRED` |
| dual stack | simultaneous IPv4/IPv6 transition | `NETWORK_REQUIRED` |
| Wi-Fi/mobile transition | route continuity and fail-closed behavior | `DEVICE_REQUIRED + NETWORK_REQUIRED` |
| airplane/network loss | expected block and recovery | `DEVICE_REQUIRED + NETWORK_REQUIRED` |
| Kill Switch | path/process failure with no direct fallback | `DEVICE_REQUIRED + NETWORK_REQUIRED` |
| proxy chain | each hop and ordered chain | `NOT_IMPLEMENTED/NETWORK_REQUIRED` |

## Evidence requirements

Each test must retain device metadata, provider, timestamp, exact APK SHA, network profile, request target, packet or resolver capture, app log, and final state. A UI status or a successful build cannot substitute for packet-level evidence. Provider restrictions must be labeled `BLOCKED_BY_PLATFORM` rather than interpreted as a pass.
