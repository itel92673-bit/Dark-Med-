# Embedded Tor/VPN Research Notes

## Initial source candidates

| Component | Candidate | URL | Initial finding |
|---|---|---|---|
| Tor on Android | Guardian Project tor-android | https://github.com/guardianproject/tor-android | Native Android TorService and libtor.so; binaries may be used directly as a daemon. |
| Orbot reference | Guardian Project orbot-android | https://github.com/guardianproject/orbot-android | Reference implementation for Android Tor lifecycle and VPN routing. |
| TUN to SOCKS | hev-socks5-tunnel | https://github.com/heiher/hev-socks5-tunnel | Native tun2socks with IPv4/IPv6 and SOCKS5 support. |
| Android SOCKS VPN | sockstun | https://github.com/heiher/sockstun | Android VPN-over-SOCKS reference based on hev. |
| WireGuard | wireguard-android | https://github.com/WireGuard/wireguard-android | Official Android client and tunnel library ecosystem. |

## Constraints to verify

The existing project already contains a VpnService, native Hev wrapper, JNI socket protection callback, TorForegroundService, Tor configuration code, and WireGuard dependency. The next phase must determine whether the Tor service class and dependency are real and compatible, whether the embedded daemon is actually bootstrapped, and whether the TUN engine is started with a valid SOCKS endpoint. No runtime PASS is assigned without device or emulator evidence.

## Verified source findings

Guardian Project `tor-android` documents a native Android `TorService` backed by the Tor shared library and states that `libtor.so` can also be used directly as a daemon. Its README lists Tor 0.4.9.11, supports API 24+, and publishes ARM/Intel Android architectures through the Guardian Maven repository. Source: https://github.com/guardianproject/tor-android

Guardian Project `orbot-android` documents that Orbot uses `hev-socks5-tunnel` and provides Android lifecycle/VPN reference behavior. Its README also notes that recent Tor runs as a separate Android process, which is relevant to log collection and lifecycle isolation. Source: https://github.com/guardianproject/orbot-android

`hev-socks5-tunnel` documents IPv4/IPv6, TCP and UDP redirection, an Android build path, a YAML configuration, and native APIs for starting/stopping/stats. It explicitly describes the tunnel start call as blocking until quit or failure, so the JNI wrapper must run it off the main thread and must not treat native start as asynchronous unless the wrapper implements that contract. Source: https://github.com/heiher/hev-socks5-tunnel

The WireGuard Android repository documents that the tunnel library is available on Maven Central and that the project uses a userspace fallback when the kernel implementation is unavailable. Source: https://github.com/WireGuard/wireguard-android

## Current architectural risk

The project’s `TorForegroundService` starts and binds the external class `org.torproject.jni.TorService`, but the current dependency is `info.guardianproject:tor-android:0.4.9.11`; this must be verified against the actual AAR classes before claiming that the embedded Tor service is wired. The project’s Hev wrapper accepts a configuration path and a TUN file descriptor, but the actual config must point to a live local SOCKS5 listener. A TUN interface alone does not prove Tor routing. Runtime bootstrap, SOCKS reachability, packet forwarding, DNS behavior, and failure handling remain unverified until execution evidence exists.
