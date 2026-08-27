#!/usr/bin/env bash
set -euo pipefail

PROJECT="${GOOGLE_CLOUD_PROJECT:-${GCLOUD_PROJECT:-}}"
GCLOUD_BIN="${GCLOUD_BIN:-/home/ubuntu/cloud-tools/google-cloud-sdk/bin/gcloud}"
APK="${DARKMED_APK:-deliverables/Dark Med f.apk}"
TEST_APK="${DARKMED_TEST_APK:-deliverables/DarkMed_QA_AndroidTest.apk}"
OUT="${DARKMED_CLOUD_OUT:-reports/multicloud_qa/firebase}"
DEVICE_SPECS="${FIREBASE_DEVICE_SPECS:-}"

mkdir -p "$OUT"
if [[ ! -x "$GCLOUD_BIN" ]]; then
  printf 'BLOCKED Firebase: gcloud CLI not found at %s\n' "$GCLOUD_BIN" | tee "$OUT/status.txt"
  exit 20
fi
if [[ -z "$PROJECT" ]]; then
  printf 'BLOCKED Firebase: GOOGLE_CLOUD_PROJECT or GCLOUD_PROJECT is not set\n' | tee "$OUT/status.txt"
  exit 20
fi
if [[ ! -f "$APK" || ! -f "$TEST_APK" ]]; then
  printf 'BLOCKED Firebase: APK or instrumentation APK missing\n' | tee "$OUT/status.txt"
  exit 20
fi
if ! "$GCLOUD_BIN" auth list --filter=status:ACTIVE --format='value(account)' | grep -q . && [[ -z "${GOOGLE_APPLICATION_CREDENTIALS:-}" ]]; then
  printf 'BLOCKED Firebase: no active gcloud account or ADC credential\n' | tee "$OUT/status.txt"
  exit 20
fi
if [[ "${CLOUD_TEST_EXECUTE:-0}" != "1" ]]; then
  printf 'WAITING_FOR_COST_APPROVAL Firebase: set CLOUD_TEST_EXECUTE=1 only after review\n' | tee "$OUT/status.txt"
  exit 20
fi
if [[ "${CLOUD_COST_APPROVED:-0}" != "1" ]]; then
  printf 'WAITING_FOR_COST_APPROVAL Firebase: run cost_guard and set CLOUD_COST_APPROVED=1\n' | tee "$OUT/status.txt"
  exit 20
fi
if [[ -z "$DEVICE_SPECS" ]]; then
  printf 'BLOCKED Firebase: FIREBASE_DEVICE_SPECS is empty; no device matrix is selected\n' | tee "$OUT/status.txt"
  exit 20
fi
APPROVAL_FILE="${DARKMED_COST_APPROVAL_FILE:-}"
if [[ -z "$APPROVAL_FILE" ]]; then
  printf 'WAITING_FOR_COST_APPROVAL Firebase: DARKMED_COST_APPROVAL_FILE is not set\n' | tee "$OUT/status.txt"
  exit 20
fi
python3 "$(dirname "$0")/verify_cost_approval.py" --file "$APPROVAL_FILE" --provider "Firebase Test Lab" | tee "$OUT/cost_approval_check.txt" || exit 20

APK_SHA256="$(sha256sum "$APK" | awk '{print $1}')"
TEST_SHA256="$(sha256sum "$TEST_APK" | awk '{print $1}')"
printf 'apk_sha256=%s\ntest_apk_sha256=%s\nproject=%s\nstarted_at=%s\n' "$APK_SHA256" "$TEST_SHA256" "$PROJECT" "$(date -u +%FT%TZ)" > "$OUT/run_manifest.txt"

ARGS=(firebase test android run --project "$PROJECT" --type instrumentation --app "$APK" --test "$TEST_APK" --timeout 45m --record-video --performance-metrics --use-orchestrator --results-dir "darkmed/$(date -u +%Y%m%dT%H%M%SZ)-$APK_SHA256")
IFS=';' read -r -a SPECS <<< "$DEVICE_SPECS"
for spec in "${SPECS[@]}"; do
  ARGS+=(--device "$spec")
done
"$GCLOUD_BIN" "${ARGS[@]}" 2>&1 | tee "$OUT/gcloud_run.log"
printf 'PASS_OR_PROVIDER_RESULT Firebase: inspect matrix output above; no local PASS is inferred\n' | tee "$OUT/status.txt"
