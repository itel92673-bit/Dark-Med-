#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APK="${DARKMED_APK:-$ROOT/deliverables/Dark Med f.apk}"
OUT="${DARKMED_MOBSF_OUT:-$ROOT/reports/mobsf/$(date -u +%Y%m%dT%H%M%SZ)}"
mkdir -p "$OUT"
if [[ ! -f "$APK" ]]; then printf 'status=BLOCKED\nblocker=APK missing: %s\n' "$APK" > "$OUT/status.txt"; exit 20; fi
SHA="$(sha256sum "$APK" | awk '{print $1}')"
printf 'apk=%s\napk_sha256=%s\nstarted_at=%s\n' "$APK" "$SHA" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" > "$OUT/manifest.txt"
if ! command -v mobsfscan >/dev/null 2>&1; then
  printf 'status=BLOCKED\nblocker=MobSFscan is not installed; full MobSF dynamic/static server is also not available\n' > "$OUT/status.txt"
  exit 20
fi
mobsfscan --type android --json -o "$OUT/mobsfscan_source.json" "$ROOT/app/src/main" > "$OUT/console.log" 2>&1; SCAN_RC=$?
python3 - "$OUT/mobsfscan_source.json" "$OUT/status.txt" "$SCAN_RC" <<'PY'
import json,sys
report=json.load(open(sys.argv[1],encoding='utf-8'))
errors=sum(1 for x in report.get('results',{}).values() if x.get('metadata',{}).get('severity')=='ERROR')
infos=sum(1 for x in report.get('results',{}).values() if x.get('metadata',{}).get('severity')=='INFO')
status='PASS' if errors==0 else 'FAIL'
open(sys.argv[2],'w',encoding='utf-8').write(f'status={status}\nscanner=MobSFscan\nscanner_exit_code={sys.argv[3]}\nerror_findings={errors}\ninfo_findings={infos}\nfull_mobsf_apk_analysis=NOT_AVAILABLE\nblocker=full MobSF APK server analysis was not executed locally; this result is source MobSFscan only\n')
PY
if [[ "$SCAN_RC" -ne 0 ]]; then
  exit 1
fi
exit 0
