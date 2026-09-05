# Dark Med — Stage 3 Socket Inventory

## Scope and evidence rule

هذا inventory مبني على مراجعة source الحالية. لا يُحوّل أي مسار إلى PASS شبكي؛ أي مسار لا يملك runtime packet evidence مستقلًا يبقى **UNKNOWN** أو **NOT PROVEN**.

| Owner | Creation / path | Protocol | `protect()` evidence | Failure behavior visible in source | Status |
|---|---|---|---|---|---|
| `DarkMedVpnService` | `VpnService.Builder.establish()` creates the TUN descriptor | IP/TUN | Not applicable to TUN descriptor itself | Service state machine is intended to move to failed/blocked on worker failure | STATIC ONLY |
| `HevTun2Socks` | JNI `startNative(configPath, tunFd, protector)` starts native tunnel | TCP/UDP through Hev | Protector callback is passed into native layer; independent runtime verification absent | Native start result and worker lifecycle are observed by Kotlin layer | UNKNOWN_UNPROVEN |
| Hev upstream sockets | Hev native socket/task-I/O implementation under `hev-socks5-tunnel` | TCP and potentially UDP | JNI protector callback is the intended boundary; every actual FD path is not externally observed | Depends on Hev error/close paths and callback return values | UNKNOWN_UNPROVEN |
| Hev DNS mapping | Hev DNS mapping / `getaddrinfo` and resolver path | DNS / TCP/UDP depending implementation | No independent DNS capture available | Configuration and resolver failures are not equivalent to leak proof | UNKNOWN_UNPROVEN |
| Tor embedded service | `TorForegroundService` starts the bundled Tor component and exposes local SOCKS metadata | TCP SOCKS control path | Upstream Tor sockets are not independently enumerated at Android runtime | Tor errors are converted to service/orchestrator error state | UNKNOWN_UNPROVEN |
| Local SOCKS listener | Orchestrator consumes Tor SOCKS port and writes tunnel configuration | TCP SOCKS5 | No runtime probe artifact | Invalid/unavailable port causes startup failure | UNKNOWN_UNPROVEN |
| Tor ControlPort | Configuration/source references only; no authenticated runtime transcript | TCP local control | Not proven | No authenticated response artifact | NOT_RUN |
| Java/Kotlin HTTP stack | No direct application-level `Socket`, `DatagramSocket`, `HttpURLConnection`, or OkHttp creation found outside the VPN/Tor path in the scanned app source | HTTP/TCP | N/A | WebView and platform networking remain external runtime surfaces | UNKNOWN_UNPROVEN |
| Browser/WebView | Browser session activities use WebView; platform/Chromium sockets are not visible from app source inventory | TCP/UDP/QUIC possible | No independent observation | WebView process isolation is tested, network path is not | UNKNOWN_UNPROVEN |
| WireGuard service declaration | Manifest contains the backend service declaration; no runtime WireGuard handshake evidence in Stage 3 | VPN | Not tested | Not part of current proven network path | NOT_RUN |

## Native scan observations

The native tree contains Hev task-I/O and SOCKS implementation calls such as `hev_task_io_socket_recv` and `hev_task_io_socket_send`, plus DNS resolution through the Hev layer. These are implementation observations, not packet-level proof. The complete scan is retained in `socket_inventory_scan.txt` and `socket_inventory_deep.txt`.

## Required evidence to close UNKNOWN

A socket row can become PASS only after a real Android runtime test ties the exact APK hash and commit to an independently captured flow, including interface, source/destination, protocol, timestamp, and failure behavior. In particular, `protect()` presence is not sufficient to prove that every upstream FD is protected or that no direct egress exists.
