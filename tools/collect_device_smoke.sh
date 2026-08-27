#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SERIAL="${ANDROID_SERIAL:-emulator-5554}"
APK="${DARKMED_APK:-$ROOT/app/build/outputs/apk/debug/app-debug.apk}"
OUT="${DARKMED_DEVICE_OUT:-$ROOT/reports/device_smoke/$(date -u +%Y%m%dT%H%M%SZ)}"
mkdir -p "$OUT"
if ! adb -s "$SERIAL" get-state >/dev/null 2>&1; then
  printf 'status=BLOCKED\nblocker=no healthy adb device at %s\n' "$SERIAL" > "$OUT/status.txt"
  exit 20
fi
if [[ ! -f "$APK" ]]; then
  printf 'status=BLOCKED\nblocker=APK missing: %s\n' "$APK" > "$OUT/status.txt"
  exit 20
fi
APK_SHA256="$(sha256sum "$APK" | awk '{print $1}')"
adb -s "$SERIAL" shell getprop > "$OUT/device_getprop.txt"
adb -s "$SERIAL" shell wm size > "$OUT/device_size.txt" 2>&1 || true
adb -s "$SERIAL" shell wm density > "$OUT/device_density.txt" 2>&1 || true
adb -s "$SERIAL" install -r "$APK" > "$OUT/install.log" 2>&1; INSTALL_RC=$?
adb -s "$SERIAL" shell pm clear com.darkmed.app > "$OUT/clear.log" 2>&1; CLEAR_RC=$?
adb -s "$SERIAL" shell am start -W -n com.darkmed.app/.MainActivity > "$OUT/launch.log" 2>&1; LAUNCH_RC=$?
sleep 8
adb -s "$SERIAL" exec-out screencap -p > "$OUT/launch.png"; SCREEN_RC=$?
adb -s "$SERIAL" shell uiautomator dump /sdcard/darkmed_window.xml > "$OUT/ui_dump_command.log" 2>&1; DUMP_RC=$?
adb -s "$SERIAL" shell cat /sdcard/darkmed_window.xml > "$OUT/window.xml" 2>&1 || true
adb -s "$SERIAL" logcat -d -b all -t 1500 -v threadtime > "$OUT/logcat_all.txt" 2>&1 || true
SYSTEM_DIALOG=""
for marker in "Process system isn't responding" "System UI isn't responding" "System server isn't responding"; do
  if grep -Fq "$marker" "$OUT/window.xml" "$OUT/logcat_all.txt"; then SYSTEM_DIALOG="$marker"; break; fi
done
if grep -Eq 'ANR in com.darkmed.app|FATAL EXCEPTION.*com.darkmed.app' "$OUT/logcat_all.txt"; then APP_FAILURE=1; else APP_FAILURE=0; fi
if [[ -n "$SYSTEM_DIALOG" ]]; then STATUS=FAIL; BLOCKER="Android system responsiveness failure: $SYSTEM_DIALOG"; elif [[ "$APP_FAILURE" -eq 1 ]]; then STATUS=FAIL; BLOCKER="Dark Med process crash/ANR detected"; elif grep -Eq 'Status: timeout|LaunchState: UNKNOWN' "$OUT/launch.log"; then STATUS=FAIL; BLOCKER="MainActivity launch timed out"; elif [[ "$INSTALL_RC" -eq 0 && "$CLEAR_RC" -eq 0 && "$LAUNCH_RC" -eq 0 && "$SCREEN_RC" -eq 0 && "$DUMP_RC" -eq 0 ]]; then STATUS=PASS; BLOCKER=""; else STATUS=FAIL; BLOCKER="one or more smoke commands failed"; fi
printf 'status=%s\nblocker=%s\nserial=%s\napk=%s\napk_sha256=%s\ninstall_rc=%s\nclear_rc=%s\nlaunch_rc=%s\nscreenshot_rc=%s\nui_dump_rc=%s\nsystem_dialog=%s\napp_failure=%s\n' "$STATUS" "$BLOCKER" "$SERIAL" "$APK" "$APK_SHA256" "$INSTALL_RC" "$CLEAR_RC" "$LAUNCH_RC" "$SCREEN_RC" "$DUMP_RC" "${SYSTEM_DIALOG:-none}" "$APP_FAILURE" > "$OUT/status.txt"
if [[ "$STATUS" == PASS ]]; then exit 0; else exit 1; fi
