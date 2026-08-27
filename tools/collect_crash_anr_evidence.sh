#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SERIAL="${ANDROID_SERIAL:-emulator-5554}"
APK="${DARKMED_APK:-$ROOT/deliverables/Dark Med f.apk}"
OUT="${DARKMED_CRASH_OUT:-$ROOT/reports/crash_anr/$(date -u +%Y%m%dT%H%M%SZ)}"
mkdir -p "$OUT"
if ! adb -s "$SERIAL" get-state >/dev/null 2>&1; then
  printf 'status=BLOCKED\nblocker=no healthy adb device at %s\n' "$SERIAL" > "$OUT/status.txt"
  exit 20
fi
SHA="$(sha256sum "$APK" | awk '{print $1}')"
START="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
adb -s "$SERIAL" logcat -c >/dev/null 2>&1 || true
adb -s "$SERIAL" shell am force-stop com.darkmed.app >/dev/null 2>&1 || true
adb -s "$SERIAL" shell am start -W -n com.darkmed.app/.MainActivity > "$OUT/launch.log" 2>&1 || true
sleep "${DARKMED_OBSERVE_SECONDS:-10}"
adb -s "$SERIAL" logcat -d -b all -v threadtime > "$OUT/logcat_all.txt" 2>&1 || true
adb -s "$SERIAL" shell dumpsys activity processes > "$OUT/activity_processes.txt" 2>&1 || true
adb -s "$SERIAL" shell dumpsys meminfo com.darkmed.app > "$OUT/meminfo.txt" 2>&1 || true
adb -s "$SERIAL" shell dumpsys dropbox --print data_app_crash > "$OUT/dropbox_app_crash.txt" 2>&1 || true
adb -s "$SERIAL" shell dumpsys dropbox --print data_app_anr > "$OUT/dropbox_app_anr.txt" 2>&1 || true
if grep -Eq 'FATAL EXCEPTION.*com.darkmed.app|ANR in com.darkmed.app' "$OUT/logcat_all.txt" "$OUT/launch.log"; then APP_EVENT=1; else APP_EVENT=0; fi
if [[ "$APP_EVENT" -eq 1 ]]; then STATUS=FAIL; BLOCKER='Dark Med crash or ANR observed in collection window'; else STATUS=NOT_OBSERVED; BLOCKER='no Dark Med crash/ANR observed during this window; this is not proof of crash-free operation'; fi
printf 'status=%s\nblocker=%s\nserial=%s\napk_sha256=%s\nstarted_at=%s\nfinished_at=%s\ncrash_or_anr_observed=%s\n' "$STATUS" "$BLOCKER" "$SERIAL" "$SHA" "$START" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$APP_EVENT" > "$OUT/status.txt"
if [[ "$STATUS" == FAIL ]]; then exit 1; else exit 20; fi
