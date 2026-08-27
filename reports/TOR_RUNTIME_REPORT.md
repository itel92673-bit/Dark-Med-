# Dark Med — TOR_RUNTIME_REPORT

## Current result

The APK contains the Tor Android dependency and app-owned Tor lifecycle/configuration code. The torrc renderer includes client-only mode, SafeLogging, isolated SOCKS ports, local ControlPort, cookie authentication, and optional bridge syntax. No real Tor bootstrap or onion request has been executed in this multi-cloud cycle.

| Test | Status | Evidence required |
|---|---|---|
| Tor service startup | `NETWORK_REQUIRED` | foreground service and native process logs |
| bootstrap progress | `NETWORK_REQUIRED` | ControlPort `GETINFO status/bootstrap-phase` reaching 100/DONE |
| SOCKS reachability | `NETWORK_REQUIRED` | authenticated/local SOCKS request |
| circuit/New Identity | `NETWORK_REQUIRED` | ControlPort command and observable circuit change |
| restart/retry | `DEVICE_REQUIRED + NETWORK_REQUIRED` | process/service lifecycle trace |
| network transition | `DEVICE_REQUIRED + NETWORK_REQUIRED` | Wi-Fi/mobile/loss/recovery trace |
| process death | `DEVICE_REQUIRED + NETWORK_REQUIRED` | kill and recovery evidence |
| `.onion` request | `NETWORK_REQUIRED` | real onion endpoint request, completion and failure behavior |
| obfs4 | `NOT IMPLEMENTED / BLOCKED` | no real PT binary/integration |
| Snowflake | `NOT IMPLEMENTED / BLOCKED` | no real WebRTC transport integration |

## Promotion rule

Tor becomes PASS only after a real bootstrap and protected SOCKS/.onion request. Presence of `libtor.so`, torrc syntax, or a service notification is not a bootstrap result. A bridge configuration without a working transport is configuration-supported only.
