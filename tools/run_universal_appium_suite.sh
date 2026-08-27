#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROVIDER="${1:-}"
if [[ -z "$PROVIDER" ]]; then
  echo "BLOCKED: provider argument is required" >&2
  exit 20
fi
case "$PROVIDER" in
  browserstack|kobiton|sauce|perfecto) ;;
  *) echo "BLOCKED: unsupported Appium provider: $PROVIDER" >&2; exit 20 ;;
esac
APK="${DARKMED_APK:-$ROOT/deliverables/Dark Med f.apk}"
if [[ ! -f "$APK" ]]; then
  echo "BLOCKED: APK not found: $APK" >&2
  exit 20
fi
EXPECTED_SHA="${DARKMED_APK_SHA256:-}"
ACTUAL_SHA="$(sha256sum "$APK" | awk '{print $1}')"
if [[ -n "$EXPECTED_SHA" && "$ACTUAL_SHA" != "$EXPECTED_SHA" ]]; then
  echo "FAIL: APK SHA mismatch expected=$EXPECTED_SHA actual=$ACTUAL_SHA" >&2
  exit 1
fi
if [[ "${CLOUD_TEST_EXECUTE:-0}" != "1" ]]; then
  echo "BLOCKED: set CLOUD_TEST_EXECUTE=1 only after reviewing provider cost, device selection, and authorization" >&2
  exit 20
fi
SERVER_URL="${APPIUM_SERVER_URL:-}"
DEVICE_NAME="${APPIUM_DEVICE_NAME:-}"
PLATFORM_VERSION="${APPIUM_PLATFORM_VERSION:-}"
if [[ -z "$SERVER_URL" || -z "$DEVICE_NAME" || -z "$PLATFORM_VERSION" ]]; then
  echo "BLOCKED: APPIUM_SERVER_URL, APPIUM_DEVICE_NAME, and APPIUM_PLATFORM_VERSION are required" >&2
  exit 20
fi
RUN_ID="${DARKMED_RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
OUT="${DARKMED_CLOUD_OUT:-$ROOT/reports/multicloud_qa/$PROVIDER/$RUN_ID}"
mkdir -p "$OUT"
printf 'provider=%s\napk=%s\napk_sha256=%s\ndevice_name=%s\nplatform_version=%s\nstarted_at=%s\n' "$PROVIDER" "$APK" "$ACTUAL_SHA" "$DEVICE_NAME" "$PLATFORM_VERSION" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" > "$OUT/manifest.txt"
python3 "$ROOT/qa/appium/universal_appium_suite.py" --provider "$PROVIDER" --server-url "$SERVER_URL" --device-name "$DEVICE_NAME" --platform-version "$PLATFORM_VERSION" --output-dir "$OUT" --apk-sha256 "$ACTUAL_SHA" --build-id "${DARKMED_BUILD_ID:-dark-med-$RUN_ID}"
