#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
export JAVA_HOME="${JAVA_HOME:-$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")}" 
export PATH="$JAVA_HOME/bin:$PATH"
export CLOUD_TEST_EXECUTE=0
unset CLOUD_COST_APPROVED DARKMED_COST_APPROVAL_FILE
RUN_ID="${DARKMED_CI_RUN_ID:-$(date -u +%Y%m%dT%H%M%SZ)}"
OUT="${DARKMED_CI_OUT:-$ROOT/reports/ci/$RUN_ID}"
mkdir -p "$OUT"
run_gate() {
  local name="$1"
  shift
  "$@" > "$OUT/$name.log" 2>&1
  printf '%s=PASS\n' "$name" | tee -a "$OUT/status.txt"
}
: > "$OUT/status.txt"
run_gate clean ./gradlew clean --no-daemon
run_gate unit_test ./gradlew testDebugUnitTest --no-daemon
run_gate lint_debug ./gradlew lintDebug --no-daemon
run_gate lint_release ./gradlew lintRelease --no-daemon
run_gate assemble_debug ./gradlew assembleDebug --no-daemon
run_gate assemble_android_test ./gradlew assembleDebugAndroidTest --no-daemon
run_gate assemble_release ./gradlew assembleRelease --no-daemon
run_gate python_compile python3 -m py_compile qa/agents/*.py tools/*.py
for script in tools/*.sh; do bash -n "$script"; done
printf 'bash_syntax=PASS\n' | tee -a "$OUT/status.txt"
run_gate agent_contracts python3 tools/test_agent_contracts.py
run_gate cost_guard_contracts python3 tools/test_cost_guard.py
run_gate task_orchestrator_contracts python3 tools/test_task_orchestrator.py
run_gate ui_security_contracts python3 tools/test_ui_security_contracts.py
run_gate github_actions_workflow python3 tools/test_github_actions_workflow.py
run_gate vpn_protect_contract python3 tools/test_vpn_protect_contract.py
run_gate security_audit ./tools/darkmed_security_audit.sh "deliverables/Dark Med f.apk"
run_gate icon_pixels python3 tools/compare_icon_pixels.py
run_gate orchestrator_preflight python3 tools/multicloud_qa.py --apk "deliverables/Dark Med f.apk" --test-apk "deliverables/DarkMed_QA_AndroidTest.apk" --output-dir "$OUT/orchestrator" --plan-config config/multicloud_device_matrix_plan.json --provider all
printf 'cloud_execution=NOT_RUN\n' | tee -a "$OUT/status.txt"
printf 'ci_output=%s\n' "$OUT" | tee -a "$OUT/status.txt"
