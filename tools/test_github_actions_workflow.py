from pathlib import Path

root = Path(__file__).resolve().parents[1]
workflow = (root / ".github/workflows/android-runtime-qa.yml").read_text()

checks = {
    "workflow_exists": workflow.startswith("name: Dark Med Android Runtime QA"),
    "manual_dispatch": "workflow_dispatch:" in workflow,
    "matrix_fail_fast_disabled": "fail-fast: false" in workflow,
    "api_matrix": "api-level: [29, 30, 31, 33, 34]" in workflow,
    "google_apis_target": "target: google_apis" in workflow,
    "low_ram_profile": "ram-size: 3072M" in workflow,
    "contents_read_only": "contents: read" in workflow,
    "kvm_fail_closed": "test -e /dev/kvm" in workflow,
    "emulator_runner": "reactivecircus/android-emulator-runner@v2" in workflow,
    "instrumentation_command": "./gradlew connectedDebugAndroidTest" in workflow,
    "runtime_artifacts": "actions/upload-artifact@v4" in workflow,
    "failure_screenshot": "failure.png" in workflow,
    "failure_bugreport": "adb bugreport" in workflow,
    "pcap_is_explicit": "PCAP_MODE=NOT_AVAILABLE" in workflow and "traffic.pcap" in workflow and "pcap_status.txt" in workflow,
    "pcap_before_instrumentation": workflow.index("PCAP_MODE=NOT_AVAILABLE") < workflow.index("./gradlew connectedDebugAndroidTest"),
    "pcap_stopped_after_instrumentation": workflow.index("pidof tcpdump") > workflow.index("./gradlew connectedDebugAndroidTest"),
    "test_exit_preserved": "exit \"$TEST_RC\"" in workflow,
    "no_provider_secrets": all(token not in workflow for token in ("AWS_ACCESS_KEY", "BROWSERSTACK_ACCESS_KEY", "SAUCE_ACCESS_KEY", "PERFECTO_TOKEN")),
    "no_paid_cloud_action": all(token not in workflow for token in ("CLOUD_TEST_EXECUTE=1", "gcloud firebase test android run", "aws devicefarm")),
}

for name, passed in checks.items():
    print(f"{name}={'PASS' if passed else 'FAIL'}")
failed = [name for name, passed in checks.items() if not passed]
if failed:
    raise SystemExit("GitHub Actions workflow contract failures: " + ", ".join(failed))
