#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APK="${DARKMED_APK:-$ROOT/deliverables/Dark Med f.apk}"
if [[ ! -f "$APK" ]]; then echo "BLOCKED: APK not found" >&2; exit 20; fi
if [[ "${CLOUD_TEST_EXECUTE:-0}" != "1" ]]; then echo "WAITING_FOR_COST_APPROVAL: set CLOUD_TEST_EXECUTE=1 only after review" >&2; exit 20; fi
if [[ "${CLOUD_COST_APPROVED:-0}" != "1" ]]; then echo "WAITING_FOR_COST_APPROVAL: run cost_guard and set CLOUD_COST_APPROVED=1" >&2; exit 20; fi
for name in BROWSERSTACK_USERNAME BROWSERSTACK_ACCESS_KEY BROWSERSTACK_APPIUM_SERVER_URL; do
  if [[ -z "${!name:-}" ]]; then echo "BLOCKED: missing $name" >&2; exit 20; fi
done
RUN_ID="${DARKMED_RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
OUT="${DARKMED_CLOUD_OUT:-$ROOT/reports/multicloud_qa/browserstack/$RUN_ID}"
mkdir -p "$OUT"
APPROVAL_FILE="${DARKMED_COST_APPROVAL_FILE:-}"
if [[ -z "$APPROVAL_FILE" ]]; then echo "WAITING_FOR_COST_APPROVAL: DARKMED_COST_APPROVAL_FILE is not set" >&2; exit 20; fi
python3 "$ROOT/tools/verify_cost_approval.py" --file "$APPROVAL_FILE" --provider "BrowserStack" | tee "$OUT/cost_approval_check.txt" || exit 20
SHA="$(sha256sum "$APK" | awk '{print $1}')"
printf 'provider=BrowserStack\napk=%s\napk_sha256=%s\nstarted_at=%s\n' "$APK" "$SHA" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" > "$OUT/manifest.txt"
curl --fail-with-body --silent --show-error -u "$BROWSERSTACK_USERNAME:$BROWSERSTACK_ACCESS_KEY" -X POST "https://api-cloud.browserstack.com/app-automate/upload" -F "file=@$APK" -F 'custom_id=DarkMed-QA' > "$OUT/upload_response.json"
APP_REF="$(cat "$OUT/upload_response.json" | python3 -c 'import json,sys; x=json.load(sys.stdin); print(x.get("app_url", ""))')"
if [[ -z "$APP_REF" ]]; then echo "FAIL: BrowserStack upload did not return app_url" >&2; exit 1; fi
export APPIUM_SERVER_URL="$BROWSERSTACK_APPIUM_SERVER_URL"
export BROWSERSTACK_APP_REF="$APP_REF"
export DARKMED_CLOUD_OUT="$OUT/appium"
exec "$ROOT/tools/run_universal_appium_suite.sh" browserstack
