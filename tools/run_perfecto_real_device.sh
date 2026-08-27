#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APK="${DARKMED_APK:-$ROOT/deliverables/Dark Med f.apk}"
if [[ ! -f "$APK" ]]; then echo "BLOCKED: APK not found" >&2; exit 20; fi
if [[ "${CLOUD_TEST_EXECUTE:-0}" != "1" ]]; then echo "WAITING_FOR_COST_APPROVAL: set CLOUD_TEST_EXECUTE=1 only after review" >&2; exit 20; fi
if [[ "${CLOUD_COST_APPROVED:-0}" != "1" ]]; then echo "WAITING_FOR_COST_APPROVAL: run cost_guard and set CLOUD_COST_APPROVED=1" >&2; exit 20; fi
for name in PERFECTO_CLOUD_NAME PERFECTO_SECURITY_TOKEN PERFECTO_APP_REF; do
  if [[ -z "${!name:-}" ]]; then echo "BLOCKED: missing $name; upload the exact APK to the Perfecto repository and set its repository locator" >&2; exit 20; fi
done
RUN_ID="${DARKMED_RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
OUT="${DARKMED_CLOUD_OUT:-$ROOT/reports/multicloud_qa/perfecto/$RUN_ID}"
mkdir -p "$OUT"
APPROVAL_FILE="${DARKMED_COST_APPROVAL_FILE:-}"
if [[ -z "$APPROVAL_FILE" ]]; then echo "WAITING_FOR_COST_APPROVAL: DARKMED_COST_APPROVAL_FILE is not set" >&2; exit 20; fi
python3 "$ROOT/tools/verify_cost_approval.py" --file "$APPROVAL_FILE" --provider "Perfecto" | tee "$OUT/cost_approval_check.txt" || exit 20
SHA="$(sha256sum "$APK" | awk '{print $1}')"
printf 'provider=Perfecto\napk=%s\napk_sha256=%s\ncloud_name=%s\napp_ref=%s\nstarted_at=%s\n' "$APK" "$SHA" "$PERFECTO_CLOUD_NAME" "$PERFECTO_APP_REF" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" > "$OUT/manifest.txt"
export APPIUM_SERVER_URL="${PERFECTO_APPIUM_SERVER_URL:-https://$PERFECTO_CLOUD_NAME/nexperience/perfectomobile/wd/hub}"
export PERFECTO_APP_REF="$PERFECTO_APP_REF"
export DARKMED_CLOUD_OUT="$OUT/appium"
exec "$ROOT/tools/run_universal_appium_suite.sh" perfecto
