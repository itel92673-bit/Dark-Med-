#!/usr/bin/env bash
set -u
ROOT="$(cd "$(dirname "$0")/../../../.." && pwd)"
OUT="$ROOT/reports/security/stage3/runtime/preflight_$(date -u +%Y%m%dT%H%M%SZ).log"
mkdir -p "$(dirname "$OUT")"
{
  echo "STAGE3_PREFLIGHT_UTC=$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  echo "ROOT=$ROOT"
  echo "GIT_HEAD=$(git -C "$ROOT" rev-parse HEAD 2>/dev/null || echo UNKNOWN)"
  echo "APK_DEBUG_SHA=$(sha256sum "$ROOT/deliverables/stage3/Dark_Med_Stage3_debug.apk" 2>/dev/null || echo MISSING)"
  echo "APK_RELEASE_SHA=$(sha256sum "$ROOT/deliverables/stage3/Dark_Med_Stage3_release_unsigned_or_debugsigned.apk" 2>/dev/null || echo MISSING)"
  echo "ADB_PATH=$(command -v adb || echo MISSING)"
  if command -v adb >/dev/null 2>&1; then
    adb start-server >/dev/null 2>&1 || true
    echo 'ADB_DEVICES_BEGIN'
    adb devices -l || true
    echo 'ADB_DEVICES_END'
  else
    echo 'ADB_STATUS=BLOCKED_NO_ADB_BINARY'
  fi
  if command -v tcpdump >/dev/null 2>&1; then echo 'TCPDUMP_BINARY=present'; else echo 'TCPDUMP_BINARY=MISSING'; fi
  if command -v emulator >/dev/null 2>&1; then echo 'EMULATOR_BINARY=present'; else echo 'EMULATOR_BINARY=MISSING'; fi
  echo 'VERDICT=BLOCKED_UNLESS_REAL_ANDROID_AND_INDEPENDENT_NETWORK_OBSERVER_APPEAR'
} | tee "$OUT"
echo "$OUT"
