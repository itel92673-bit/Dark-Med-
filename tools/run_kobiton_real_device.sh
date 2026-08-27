#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APK="${DARKMED_APK:-$ROOT/deliverables/Dark Med f.apk}"
if [[ ! -f "$APK" ]]; then echo "BLOCKED: APK not found" >&2; exit 20; fi
if [[ "${CLOUD_TEST_EXECUTE:-0}" != "1" ]]; then echo "WAITING_FOR_COST_APPROVAL: set CLOUD_TEST_EXECUTE=1 only after review" >&2; exit 20; fi
if [[ "${CLOUD_COST_APPROVED:-0}" != "1" ]]; then echo "WAITING_FOR_COST_APPROVAL: run cost_guard and set CLOUD_COST_APPROVED=1" >&2; exit 20; fi
for name in KOBITON_USERNAME KOBITON_API_KEY KOBITON_APPIUM_SERVER_URL; do
  if [[ -z "${!name:-}" ]]; then echo "BLOCKED: missing $name" >&2; exit 20; fi
done
API_BASE="${KOBITON_API_BASE_URL:-https://api.kobiton.com/v2}"
FILE_NAME="$(basename "$APK")"
RUN_ID="${DARKMED_RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
OUT="${DARKMED_CLOUD_OUT:-$ROOT/reports/multicloud_qa/kobiton/$RUN_ID}"
mkdir -p "$OUT"
APPROVAL_FILE="${DARKMED_COST_APPROVAL_FILE:-}"
if [[ -z "$APPROVAL_FILE" ]]; then echo "WAITING_FOR_COST_APPROVAL: DARKMED_COST_APPROVAL_FILE is not set" >&2; exit 20; fi
python3 "$ROOT/tools/verify_cost_approval.py" --file "$APPROVAL_FILE" --provider "Kobiton" | tee "$OUT/cost_approval_check.txt" || exit 20
SHA="$(sha256sum "$APK" | awk '{print $1}')"
printf 'provider=Kobiton\napk=%s\napk_sha256=%s\nstarted_at=%s\n' "$APK" "$SHA" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" > "$OUT/manifest.txt"
AUTH=(-u "$KOBITON_USERNAME:$KOBITON_API_KEY")
UPLOAD_JSON="$(curl --fail-with-body --silent --show-error "${AUTH[@]}" -H 'Content-Type: application/json' -d "{\"file_name\":\"$FILE_NAME\"}" "$API_BASE/apps/upload-url")"
printf '%s\n' "$UPLOAD_JSON" > "$OUT/upload_url_response.json"
APP_PATH="$(printf '%s' "$UPLOAD_JSON" | python3 -c 'import json,sys; print(json.load(sys.stdin)["app_path"])')"
UPLOAD_URL="$(printf '%s' "$UPLOAD_JSON" | python3 -c 'import json,sys; print(json.load(sys.stdin)["url"])')"
curl --fail-with-body --silent --show-error -X PUT -H 'x-amz-tagging: unsaved=true' -H 'Content-Type: application/octet-stream' --data-binary "@$APK" "$UPLOAD_URL" > "$OUT/s3_upload_response.txt"
APP_JSON="$(curl --fail-with-body --silent --show-error "${AUTH[@]}" -H 'Content-Type: application/json' -d "{\"file_name\":\"$FILE_NAME\",\"app_path\":\"$APP_PATH\"}" "$API_BASE/apps")"
printf '%s\n' "$APP_JSON" > "$OUT/app_create_response.json"
APP_ID="$(printf '%s' "$APP_JSON" | python3 -c 'import json,sys; print(json.load(sys.stdin).get("app_id", ""))')"
VERSION_ID="$(printf '%s' "$APP_JSON" | python3 -c 'import json,sys; print(json.load(sys.stdin).get("version_id", ""))')"
if [[ -z "$VERSION_ID" ]]; then echo "FAIL: Kobiton did not return version_id" >&2; exit 1; fi
PARSE_JSON="$(curl --fail-with-body --silent --show-error "${AUTH[@]}" "$API_BASE/apps/parsing-status?appVersionId=$VERSION_ID")"
printf '%s\n' "$PARSE_JSON" > "$OUT/parsing_status.json"
STATE="$(printf '%s' "$PARSE_JSON" | python3 -c 'import json,sys; print(json.load(sys.stdin).get("state", ""))')"
if [[ "$STATE" != "OK" ]]; then echo "BLOCKED: Kobiton app parsing state is $STATE; no Appium run started" >&2; exit 20; fi
export APPIUM_SERVER_URL="$KOBITON_APPIUM_SERVER_URL"
export KOBITON_APP_REF="${KOBITON_APP_REF:-$APP_ID}"
export DARKMED_CLOUD_OUT="$OUT/appium"
exec "$ROOT/tools/run_universal_appium_suite.sh" kobiton
