#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SERIAL="${ANDROID_SERIAL:-emulator-5554}"
OUT="${DARKMED_NETWORK_OUT:-$ROOT/reports/network_runtime/$(date -u +%Y%m%dT%H%M%SZ)}"
mkdir -p "$OUT"
if ! adb -s "$SERIAL" get-state >/dev/null 2>&1; then
  printf 'status=BLOCKED\nblocker=no healthy adb device at %s\n' "$SERIAL" > "$OUT/status.txt"
  exit 20
fi
if [[ -z "${DARKMED_NETWORK_PROFILE:-}" ]]; then
  printf 'status=NOT_TESTED\nblocker=DARKMED_NETWORK_PROFILE is required; no Wi-Fi/4G/5G/IPv4/IPv6 profile was authorized\nserial=%s\n' "$SERIAL" > "$OUT/status.txt"
  exit 20
fi
PROFILE="$DARKMED_NETWORK_PROFILE"
adb -s "$SERIAL" shell getprop > "$OUT/getprop.txt"
adb -s "$SERIAL" shell ip addr > "$OUT/ip_addr.txt" 2>&1 || true
adb -s "$SERIAL" shell ip route > "$OUT/ip_route.txt" 2>&1 || true
adb -s "$SERIAL" shell ip -6 route > "$OUT/ip6_route.txt" 2>&1 || true
adb -s "$SERIAL" shell dumpsys connectivity > "$OUT/connectivity.txt" 2>&1 || true
adb -s "$SERIAL" shell settings get global airplane_mode_on > "$OUT/airplane_mode.txt" 2>&1 || true
adb -s "$SERIAL" shell dumpsys netd > "$OUT/netd.txt" 2>&1 || true
printf 'status=NOT_TESTED\nblocker=metadata captured only; active network profile execution and leak assertions require controlled Wi-Fi/4G/5G and DNS endpoints\nprofile=%s\nserial=%s\nseparate_gates=IPv4,IPv6,DNS,VPN,TUN,Tor,Kill Switch,network_switch,airplane_mode,recovery\n' "$PROFILE" "$SERIAL" > "$OUT/status.txt"
exit 20
