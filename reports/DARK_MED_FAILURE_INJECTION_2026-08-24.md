# Dark Med Failure Injection Matrix — 2026-08-24

The current matrix contains 24 directive scenarios covering Tor startup/crash/timeout/control/SOCKS failures; VPN permission/crash/TUN failures; invalid WireGuard configuration and handshake timeout; DNS, IPv6, and network disconnect; service restart and process death; biometric failure/cancellation; storage/config/dependency/input failures; route compilation; proxy; and browser failures.

## Policy evidence

For all component, route, storage, dependency, and network failure scenarios except biometric failure/cancellation, the policy evaluator returns `SecurityState.Lockdown`, marks direct fallback as blocked, and records the consequence as unprotected traffic blocked with no direct fallback. Biometric failure and cancellation remain `SecurityState.Locked` and do not execute the protected action. Unit tests verify all 24 scenarios are present, the expected policy states are returned, and non-policy runtime statuses are classified as `REAL_DEVICE_REQUIRED` or `NETWORK_REQUIRED`.

## Evidence boundary

This matrix is a deterministic policy test, not a live crash, packet, or leak test. It does not prove that Tor, VPN, WireGuard, DNS, proxy, browser, or process recovery actually fail and recover correctly on Android. Those tests require a real Android 10+ device and, for route/leak/handshake assertions, a controlled network and endpoints. No test was deleted, skipped, mocked, or relabeled as runtime PASS.

## Result

`PASS — UNIT/POLICY ONLY` for matrix coverage and fail-closed decision policy. `REAL_DEVICE_REQUIRED` for service/process/biometric/VPN/browser failure execution. `NETWORK_REQUIRED` for disconnect, IPv6, DNS, proxy, handshake, route, and leak execution.
