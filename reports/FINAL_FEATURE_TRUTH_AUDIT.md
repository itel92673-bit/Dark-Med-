# Dark Med — Final Feature Truth Audit

## Rule

Every visible action must map to a controller/backend, an observable state, an error state, and an evidence requirement. A disabled or unavailable feature is recorded as such; it is not treated as a successful feature.

## UI truth matrix

| Screen/action | UI behavior | Backend/integration | Runtime state | Failure behavior | Status |
|---|---|---|---|---|---|
| Unlock on launch | Strong biometric prompt; locked screen on failure/unavailable | `BiometricGate` with `BIOMETRIC_STRONG` | OEM authentication not executed in current environment | remains locked and reports failure | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| Dashboard protection status | Shows `UNPROTECTED` and no verified route | no direct-start action exposed as protected | no runtime route evidence | remains unprotected/blocked | `TRUTHFUL` |
| Dashboard layer rows | Shows WireGuard, Proxy 1, Proxy 2, Tor as not configured | no fake activation | no handshake/bootstrap | no silent fallback | `TRUTHFUL`; incomplete backend for Proxy |
| Browser tab | Shows disclaimer and non-clickable protection requirement card | WebView host exists separately; no verified route | browser opening is blocked from main UI | remains unavailable | `TRUTHFUL`; route `DEVICE_REQUIRED` |
| Profiles tab | Unavailable module message | no complete profile repository/UI flow found | no activation possible | explicitly unavailable | `BLOCKED`, not dead success |
| Privacy tab | Unavailable module message | no complete privacy center flow found | no runtime claims | explicitly unavailable | `BLOCKED`, not dead success |
| Settings compatibility card | Reads API/OEM/model/ABI/RAM/WebView and checklist states | `DeviceCompatibilityCenter` | local capability readout only | warnings/actions shown | `CODE_VERIFIED`; runtime values device-specific |
| Clear All Data | confirmation dialog then strong biometric, then coordinator | `ClearAllDataCoordinator` stops services/sessions and calls `DataWiper` | residual deletion untested on device | reports failed/not deleted | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| Browser session close broadcast | private session clears WebView state and finishes | package-restricted internal broadcast | process behavior untested | cleanup is best-effort and reported by wipe path | `CODE_VERIFIED`; `DEVICE_REQUIRED` |

## No-dead-feature result

The only previous empty browser button was replaced by a non-interactive status card. The auditor scan found no app-owned `PlaceholderScreen`, fake success, mock proxy, stub proxy, or empty `onClick` pattern. Vendored Hev/lwIP/yaml comments are excluded from the app-owned source truth scan and are not treated as Dark Med features.

## Remaining feature gaps

Profiles, Privacy Center, real proxy forwarding/chaining, DNS resolver/leak detector, full Security Test Lab UI, traffic monitor UI, local code sandbox, and OpenAI settings are not fully wired in the current source. They remain explicitly unavailable or not configured and must not be presented as working. Tor/VPN/WireGuard/Browser are structurally present but require runtime evidence before any protected status.
