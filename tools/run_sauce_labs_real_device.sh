#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APK="${DARKMED_APK:-$ROOT/deliverables/Dark Med f.apk}"
if [[ ! -f "$APK" ]]; then echo "BLOCKED: APK not found" >&2; exit 20; fi
if [[ "${CLOUD_TEST_EXECUTE:-0}" != "1" ]]; then echo "WAITING_FOR_COST_APPROVAL: set CLOUD_TEST_EXECUTE=1 only after review" >&2; exit 20; fi
if [[ "${CLOUD_COST_APPROVED:-0}" != "1" ]]; then echo "WAITING_FOR_COST_APPROVAL: run cost_guard and set CLOUD_COST_APPROVED=1" >&2; exit 20; fi
for name in SAUCE_USERNAME SAUCE_ACCESS_KEY SAUCE_APPIUM_SERVER_URL; do
  if [[ -z "${!name:-}" ]]; then echo "BLOCKED: missing $name" >&2; exit 20; fi
done
BASE_URL="${SAUCE_STORAGE_API_BASE_URL:-https://api.us-west-1.saucelabs.com/v1/storage/upload}"
RUN_ID="${DARKMED_RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
OUT="${DARKMED_CLOUD_OUT:-$ROOT/reports/multicloud_qa/sauce/$RUN_ID}"
mkdir -p "$OUT"
APPROVAL_FILE="${DARKMED_COST_APPROVAL_FILE:-}"
if [[ -z "$APPROVAL_FILE" ]]; then echo "WAITING_FOR_COST_APPROVAL: DARKMED_COST_APPROVAL_FILE is not set" >&2; exit 20; fi
python3 "$ROOT/tools/verify_cost_approval.py" --file "$APPROVAL_FILE" --provider "Sauce Labs" | tee "$OUT/cost_approval_check.txt" || exit 20
SHA="$(sha256sum "$APK" | awk '{print $1}')"
printf 'provider=Sauce Labs\napk=%s\napk_sha256=%s\nstarted_at=%s\n' "$APK" "$SHA" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" > "$OUT/manifest.txt"
curl --fail-with-body --silent --show-error -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY" --location --request POST "$BASE_URL" --form "payload=@$APK" --form "name=$(basename "$APK")" > "$OUT/upload_response.json"
APP_REF="$(cat "$OUT/upload_response.json" | python3 -c 'import json,sys; x=json.load(sys.stdin); item=x.get("item",x); print("storage:" + str(item.get("id", "")))')"
if [[ "$APP_REF" == "storage:" || -z "$APP_REF" ]]; then echo "FAIL: Sauce Labs upload did not return a file id" >&2; exit 1; fi
export APPIUM_SERVER_URL="$SAUCE_APPIUM_SERVER_URL"
export SAUCE_APP_REF="$APP_REF"
export DARKMED_CLOUD_OUT="$OUT/appium"
exec "$ROOT/tools/run_universal_appium_suite.sh" sauce
