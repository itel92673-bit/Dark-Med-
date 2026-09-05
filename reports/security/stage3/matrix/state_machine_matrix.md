# Dark Med Stage 3 — State and Failure Matrix

## State contract

| Current state | Event | Expected state | Network permission claim | Evidence status |
|---|---|---|---|---|
| STARTING | VPN establishment succeeds | TUN_ESTABLISHED | No protected egress claim yet | STATIC ONLY |
| STARTING | VPN establishment throws/returns null | FAILED/BLOCKED | Must not report protected | STATIC ONLY |
| TUN_ESTABLISHED | Hev starts | PROXY_READY only if upstream protection is verified | No READY claim while protection is false/unverified | STATIC ONLY |
| TUN_ESTABLISHED | Hev start fails | FAILED/BLOCKED | No protected egress | STATIC ONLY |
| PROXY_READY | upstream protection remains unverified | BLOCKED | Protected egress must not be reported | STATIC ONLY |
| PROXY_READY | Tor/SOCKS/bootstrap verification missing | BLOCKED/NOT READY | No READY transition permitted | STATIC ONLY |
| READY | Hev worker dies | FAILED/BLOCKED | Must prevent direct egress | Runtime direct-egress proof absent |
| READY | Tor dies | DEGRADED/FAILED/BLOCKED | Must prevent direct egress | Runtime direct-egress proof absent |
| READY | VPN revoked/service destroyed | STOPPED/FAILED/BLOCKED | System-level lockdown behavior not proven | Runtime device test absent |
| FAILED/DEGRADED | reconnect | STARTING | No temporary direct egress may occur | Runtime transition test absent |
| ANY | invalid config path | FAILED/BLOCKED | No network start | Static path validation |

## Failure injection matrix

| ID | Injection | Expected result | Current evidence |
|---|---|---|---|
| FI-001 | VPN establishment failure | FAILED/BLOCKED, no READY | Static only |
| FI-002 | TUN descriptor unavailable | FAILED/BLOCKED, no READY | Static only |
| FI-003 | Hev initialization failure | FAILED/BLOCKED | Static/regression only |
| FI-004 | Hev worker immediate exit | FAILED/BLOCKED | Static/regression only |
| FI-005 | SOCKS unavailable | No READY | NOT RUN |
| FI-006 | Tor startup failure | FAILED/BLOCKED | NOT RUN |
| FI-007 | Tor bootstrap timeout | No TOR_READY/READY | NOT RUN |
| FI-008 | Tor process death | FAILED/BLOCKED, no fallback | NOT RUN |
| FI-009 | Hev worker/process death | FAILED/BLOCKED, no fallback | NOT RUN |
| FI-010 | `protect(fd)` returns false | FD closes and no READY | Static semantics only; runtime injection NOT RUN |
| FI-011 | DNS subsystem failure | DNS blocked or chain fails closed | NOT RUN |
| FI-012 | IPv6 path failure | IPv6 blocked; no direct egress | NOT RUN |
| FI-013/014 | Wi-Fi/cellular transition | no direct egress during reconnect | NOT RUN |
| FI-015 | Airplane mode | no direct egress | NOT RUN |
| FI-016 | VPN revocation | blocked/locked down according to supported Android behavior | NOT RUN |
| FI-017/018 | Service recreation/background kill | no false READY; no direct egress | NOT RUN |

The matrix is not a runtime PASS list. Any row requiring direct-egress observation remains unproven until a real Android device/runtime and an independent network observer produce correlated evidence.
