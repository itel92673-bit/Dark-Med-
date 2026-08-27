# Network Component Research — 2026-08-24

## Sources

1. [Guardian Project Tor Android](https://guardianproject.info/code/tor-android/)
2. [guardianproject/tor-android repository](https://github.com/guardianproject/tor-android)
3. [WireGuard/wireguard-android repository](https://github.com/WireGuard/wireguard-android)
4. [heiher/hev-socks5-tunnel repository](https://github.com/heiher/hev-socks5-tunnel)

## Findings and project classification

Guardian Project describes `tor-android` as a native Android `TorService` built around the Tor shared library, with included `libtor.so` binaries usable as a Tor daemon and support for Android architectures including `arm64-v8a`, `armeabi-v7a`, `x86`, and `x86_64`. The repository README documents the current Tor Android dependency versions and the `tor-android` plus `jtorctl` integration. Dark Med's dependency and APK native-library evidence is consistent with this integration. It still does not prove that the service bootstraps, opens a control connection, or carries application traffic in this environment.

The official WireGuard Android repository describes the Android client as opportunistically using the kernel implementation and falling back to the non-root userspace implementation. It documents the tunnel library as embeddable from Maven Central. Dark Med uses the tunnel library and GoBackend service declaration; this proves dependency and declared integration only. A real handshake, tunnel lifecycle, and interaction with the single Android VPN slot require a device and a reachable WireGuard peer.

The HevSocks5Tunnel README documents a tun2socks engine supporting IPv4/IPv6, TCP and UDP redirection, Android builds through NDK, a configuration file containing TUN and SOCKS5 parameters, and C/JNI start/stop/stats APIs. It also shows that routing the upstream proxy outside the TUN requires explicit platform routing exclusions and that the tunnel main function blocks until quit or error. Dark Med vendors the source, links it through its own JNI bridge, and supplies the mapped-DNS configuration. The current project does not have a demonstrated Android TUN packet loop, protected upstream socket, or live SOCKS5 endpoint, so this remains integration/configuration evidence rather than network PASS.

No pluggable-transport binary evidence was found in the current APK inventory. Tor bridge and transport syntax may be rendered and validated, but obfs4 or snowflake must remain unsupported/unverified until an actual binary and route test exist.

Classification: CONFIGURATION_SUPPORTED and native/APK evidence where stated; RUNTIME_UNVERIFIED, REAL_DEVICE_REQUIRED, and NETWORK_REQUIRED for bootstrap, handshake, packet flow, proxy reachability, DNS leak behavior, and `.onion` access.
