#!/usr/bin/env bash
set -euo pipefail

AWS_BIN="${AWS_BIN:-aws}"
REGION="${AWS_DEFAULT_REGION:-}"
PROJECT_ARN="${AWS_DEVICE_FARM_PROJECT_ARN:-}"
DEVICE_POOL_ARN="${AWS_DEVICE_FARM_DEVICE_POOL_ARN:-}"
APK="${DARKMED_APK:-deliverables/Dark Med f.apk}"
TEST_APK="${DARKMED_TEST_APK:-deliverables/DarkMed_QA_AndroidTest.apk}"
OUT="${DARKMED_CLOUD_OUT:-reports/multicloud_qa/aws}"
mkdir -p "$OUT"

missing=()
for name in AWS_ACCESS_KEY_ID AWS_SECRET_ACCESS_KEY AWS_DEFAULT_REGION AWS_DEVICE_FARM_PROJECT_ARN AWS_DEVICE_FARM_DEVICE_POOL_ARN; do
  [[ -n "${!name:-}" ]] || missing+=("$name")
done
if [[ "${#missing[@]}" -gt 0 ]]; then
  printf 'BLOCKED AWS Device Farm: missing %s\n' "$(IFS=,; echo "${missing[*]}")" | tee "$OUT/status.txt"
  exit 20
fi
if ! command -v "$AWS_BIN" >/dev/null 2>&1; then
  printf 'BLOCKED AWS Device Farm: aws CLI not found\n' | tee "$OUT/status.txt"
  exit 20
fi
if [[ ! -f "$APK" || ! -f "$TEST_APK" ]]; then
  printf 'BLOCKED AWS Device Farm: APK or instrumentation APK missing\n' | tee "$OUT/status.txt"
  exit 20
fi
if [[ "${CLOUD_TEST_EXECUTE:-0}" != "1" ]]; then
  printf 'WAITING_FOR_COST_APPROVAL AWS Device Farm: set CLOUD_TEST_EXECUTE=1 only after review\n' | tee "$OUT/status.txt"
  exit 20
fi
if [[ "${CLOUD_COST_APPROVED:-0}" != "1" ]]; then
  printf 'WAITING_FOR_COST_APPROVAL AWS Device Farm: run cost_guard and set CLOUD_COST_APPROVED=1\n' | tee "$OUT/status.txt"
  exit 20
fi
APPROVAL_FILE="${DARKMED_COST_APPROVAL_FILE:-}"
if [[ -z "$APPROVAL_FILE" ]]; then
  printf 'WAITING_FOR_COST_APPROVAL AWS Device Farm: DARKMED_COST_APPROVAL_FILE is not set\n' | tee "$OUT/status.txt"
  exit 20
fi
python3 "$(dirname "$0")/verify_cost_approval.py" --file "$APPROVAL_FILE" --provider "AWS Device Farm" | tee "$OUT/cost_approval_check.txt" || exit 20

APK_SHA256="$(sha256sum "$APK" | awk '{print $1}')"
TEST_SHA256="$(sha256sum "$TEST_APK" | awk '{print $1}')"
STAMP="$(date -u +%Y%m%dT%H%M%SZ)"
printf 'apk_sha256=%s\ntest_apk_sha256=%s\nregion=%s\nstarted_at=%s\n' "$APK_SHA256" "$TEST_SHA256" "$REGION" "$(date -u +%FT%TZ)" > "$OUT/run_manifest.txt"

upload_app="$OUT/upload_app.json"
"$AWS_BIN" devicefarm create-upload --region "$REGION" --project-arn "$PROJECT_ARN" --name "Dark Med f.apk" --type ANDROID_APP > "$upload_app"
app_url="$(python3 - "$upload_app" <<'PY'
import json
import sys
print(json.load(open(sys.argv[1], encoding='utf-8'))['upload']['url'])
PY
)"
app_arn="$(python3 - "$upload_app" <<'PY'
import json
import sys
print(json.load(open(sys.argv[1], encoding='utf-8'))['upload']['arn'])
PY
)"
curl --fail --retry 3 -T "$APK" "$app_url"
"$AWS_BIN" devicefarm get-upload --region "$REGION" --arn "$app_arn" > "$OUT/upload_app_final.json"

upload_test="$OUT/upload_test.json"
"$AWS_BIN" devicefarm create-upload --region "$REGION" --project-arn "$PROJECT_ARN" --name "DarkMed_QA_AndroidTest.apk" --type INSTRUMENTATION_TEST_PACKAGE > "$upload_test"
test_url="$(python3 - "$upload_test" <<'PY'
import json
import sys
print(json.load(open(sys.argv[1], encoding='utf-8'))['upload']['url'])
PY
)"
test_arn="$(python3 - "$upload_test" <<'PY'
import json
import sys
print(json.load(open(sys.argv[1], encoding='utf-8'))['upload']['arn'])
PY
)"
curl --fail --retry 3 -T "$TEST_APK" "$test_url"
"$AWS_BIN" devicefarm get-upload --region "$REGION" --arn "$test_arn" > "$OUT/upload_test_final.json"

"$AWS_BIN" devicefarm schedule-run \
  --region "$REGION" \
  --project-arn "$PROJECT_ARN" \
  --app-arn "$app_arn" \
  --device-pool-arn "$DEVICE_POOL_ARN" \
  --name "Dark Med multi-cloud $STAMP" \
  --test "type=INSTRUMENTATION,testPackageArn=$test_arn" \
  > "$OUT/schedule_run.json"
run_arn="$(python3 - "$OUT/schedule_run.json" <<'PY'
import json
import sys
print(json.load(open(sys.argv[1], encoding='utf-8'))['run']['arn'])
PY
)"
printf 'run_arn=%s\n' "$run_arn" | tee "$OUT/status.txt"
"$AWS_BIN" devicefarm get-run --region "$REGION" --arn "$run_arn" > "$OUT/run_initial.json"
