#!/usr/bin/env python3
import argparse
import csv
import hashlib
import json
import os
import shutil
import subprocess
import sys
from datetime import datetime, timezone
from pathlib import Path

PROVIDER_DEFINITIONS = {
    "firebase": {
        "name": "Firebase Test Lab",
        "required": ("GOOGLE_CLOUD_PROJECT",),
        "mode": "instrumentation",
    },
    "aws": {
        "name": "AWS Device Farm",
        "required": ("AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY", "AWS_DEFAULT_REGION", "AWS_DEVICE_FARM_PROJECT_ARN", "AWS_DEVICE_FARM_DEVICE_POOL_ARN"),
        "mode": "instrumentation",
    },
    "browserstack": {
        "name": "BrowserStack Real Device Cloud",
        "required": ("BROWSERSTACK_USERNAME", "BROWSERSTACK_ACCESS_KEY", "BROWSERSTACK_APPIUM_SERVER_URL"),
        "mode": "appium",
    },
    "kobiton": {
        "name": "Kobiton Real Device Cloud",
        "required": ("KOBITON_USERNAME", "KOBITON_API_KEY", "KOBITON_APPIUM_SERVER_URL"),
        "mode": "appium",
    },
    "sauce": {
        "name": "Sauce Labs Real Device Cloud",
        "required": ("SAUCE_USERNAME", "SAUCE_ACCESS_KEY", "SAUCE_APPIUM_SERVER_URL"),
        "mode": "appium",
    },
    "perfecto": {
        "name": "Perfecto Real Device Cloud",
        "required": ("PERFECTO_CLOUD_NAME", "PERFECTO_SECURITY_TOKEN"),
        "mode": "appium",
    },
}
PROVIDERS = tuple(item["name"] for item in PROVIDER_DEFINITIONS.values())
MATRIX_FIELDS = ("device_id", "provider", "manufacturer", "model", "android_version", "sdk", "abi", "ram", "screen", "resolution", "dpi", "test_suite", "test_id", "status", "duration", "crashes", "anr", "logs", "screenshots", "video", "timestamp", "apk_sha256", "blocker")
TESTS = ("INSTALL", "LAUNCH", "PERMISSIONS", "NAVIGATION", "PROFILES", "SECURITY_CENTER", "COMPATIBILITY_CENTER", "VPN", "TUN", "DNS", "IPv4", "IPv6", "Tor", ".onion", "Kill Switch", "Restart", "Stress", "Crash", "ANR")


def sha256(path):
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def command_path(name):
    found = shutil.which(name)
    if found:
        return found
    if name == "gcloud":
        candidate = Path(os.environ.get("GCLOUD_BIN", "/home/ubuntu/cloud-tools/google-cloud-sdk/bin/gcloud"))
        if candidate.is_file() and os.access(candidate, os.X_OK):
            return str(candidate)
    return None


def command_exists(name):
    return command_path(name) is not None


def active_gcloud_account():
    gcloud = command_path("gcloud")
    if not gcloud:
        return None
    result = subprocess.run([gcloud, "auth", "list", "--filter=status:ACTIVE", "--format=value(account)"], capture_output=True, text=True)
    if result.returncode != 0:
        return None
    account = result.stdout.strip().splitlines()
    return account[0] if account else None


def provider_prerequisite(provider_id):
    definition = PROVIDER_DEFINITIONS[provider_id]
    name = definition["name"]
    if provider_id == "firebase":
        project = os.environ.get("GOOGLE_CLOUD_PROJECT") or os.environ.get("GCLOUD_PROJECT")
        missing = [] if project else ["GOOGLE_CLOUD_PROJECT or GCLOUD_PROJECT"]
        if not command_exists("gcloud"):
            missing.append("gcloud CLI")
        if not active_gcloud_account() and not os.environ.get("GOOGLE_APPLICATION_CREDENTIALS"):
            missing.append("active gcloud account or GOOGLE_APPLICATION_CREDENTIALS")
        if missing:
            return "BLOCKED", "missing prerequisite: " + ", ".join(missing)
        return "READY_TO_RUN", "Firebase project, CLI, and authentication detected; explicit execution gate remains required"
    missing = [name for name in definition["required"] if not os.environ.get(name)]
    if provider_id == "aws" and not command_exists("aws"):
        missing.append("aws CLI")
    if provider_id in {"browserstack", "kobiton", "sauce", "perfecto"} and not command_exists("curl"):
        missing.append("curl")
    if missing:
        return "BLOCKED", "missing prerequisite: " + ", ".join(missing)
    return "READY_TO_RUN", f"{name} credentials and endpoint prerequisites detected; explicit execution gate remains required"


def row(provider, apk_hash, status, blocker, timestamp):
    return {field: "" for field in MATRIX_FIELDS} | {
        "provider": provider,
        "test_suite": "preflight",
        "test_id": "provider_prerequisite",
        "status": status,
        "timestamp": timestamp,
        "apk_sha256": apk_hash,
        "blocker": blocker,
    }


def write_json(path, value):
    path.write_text(json.dumps(value, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def load_plan(path):
    if not path:
        return None
    return json.loads(Path(path).read_text(encoding="utf-8"))


def write_planned_matrix(output, plan, timestamp, apk_hash):
    if not plan:
        return 0
    weights = plan["priority_weights"]
    target_models = set(plan.get("target_models", []))
    versions = plan["scope"]["android_versions"]
    sdk_by_android = {10: 29, 11: 30, 12: 31, 13: 33, 14: 34, 15: 35, 16: 36}
    tiers = (
        ("tier_1_oem", plan.get("tier_1_oems", []), weights["tier_1_oem"]),
        ("tier_2_oem", plan.get("tier_2_oems", []), weights["tier_2_oem"]),
        ("tier_3_oem", plan.get("tier_3_oems", []), weights["tier_3_oem"]),
    )
    rows = []
    for tier, manufacturers, weight in tiers:
        for manufacturer in manufacturers:
            for version in versions:
                model = plan["target_models"][0] if manufacturer == "Infinix" else ""
                priority = weights["target_model"] if model in target_models else weight
                rows.append({
                    "device_id": "",
                    "provider": "UNASSIGNED",
                    "manufacturer": manufacturer,
                    "model": model,
                    "android_version": str(version),
                    "sdk": str(sdk_by_android.get(version, "")),
                    "abi": "",
                    "ram": "",
                    "screen": "",
                    "resolution": "",
                    "dpi": "",
                    "test_suite": "planned_matrix",
                    "test_id": "coverage_target",
                    "status": "NOT_TESTED",
                    "duration": "",
                    "crashes": "",
                    "anr": "",
                    "logs": "",
                    "screenshots": "",
                    "video": "",
                    "timestamp": timestamp,
                    "apk_sha256": apk_hash,
                    "blocker": f"planned target only; tier={tier}; priority_weight={priority}; provider catalog lookup required",
                })
    with (output / "planned_device_matrix.csv").open("w", newline="", encoding="utf-8") as handle:
        writer = csv.DictWriter(handle, fieldnames=MATRIX_FIELDS)
        writer.writeheader()
        writer.writerows(rows)
    return len(rows)


def selected_provider_ids(value):
    if value == "all":
        return tuple(PROVIDER_DEFINITIONS)
    return (value,)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--apk", required=True)
    parser.add_argument("--test-apk")
    parser.add_argument("--output-dir", required=True)
    parser.add_argument("--plan-config")
    parser.add_argument("--provider", choices=("all",) + tuple(PROVIDER_DEFINITIONS), default="all")
    parser.add_argument("--execute", action="store_true")
    args = parser.parse_args()
    apk = Path(args.apk).resolve()
    if not apk.is_file():
        print(f"ERROR APK not found: {apk}", file=sys.stderr)
        return 2
    test_apk = Path(args.test_apk).resolve() if args.test_apk else None
    if test_apk and not test_apk.is_file():
        print(f"ERROR test APK not found: {test_apk}", file=sys.stderr)
        return 2
    output = Path(args.output_dir).resolve()
    output.mkdir(parents=True, exist_ok=True)
    timestamp = datetime.now(timezone.utc).isoformat()
    apk_hash = sha256(apk)
    selected_ids = selected_provider_ids(args.provider)
    selected_names = tuple(PROVIDER_DEFINITIONS[item]["name"] for item in selected_ids)
    results = []
    for provider_id in selected_ids:
        status, blocker = provider_prerequisite(provider_id)
        if args.execute and status == "READY_TO_RUN":
            status = "READY_TO_RUN_NOT_STARTED"
            blocker = "execution adapter is prepared; no cloud run was started by this safe orchestrator preflight"
        results.append(row(PROVIDER_DEFINITIONS[provider_id]["name"], apk_hash, status, blocker, timestamp))
    plan = load_plan(args.plan_config)
    planned_rows = write_planned_matrix(output, plan, timestamp, apk_hash)
    manifest = {
        "created_at": timestamp,
        "apk": str(apk),
        "apk_sha256": apk_hash,
        "test_apk": str(test_apk) if test_apk else None,
        "test_apk_sha256": sha256(test_apk) if test_apk else None,
        "providers": list(selected_names),
        "provider_ids": list(selected_ids),
        "execution_requested": args.execute,
        "plan_config": str(Path(args.plan_config).resolve()) if args.plan_config else None,
        "planned_matrix_rows": planned_rows,
        "source": "Dark Med current artifact; no simulated test results; planned rows are not tested",
    }
    write_json(output / "run_manifest.json", manifest)
    with (output / "device_matrix.csv").open("w", newline="", encoding="utf-8") as handle:
        writer = csv.DictWriter(handle, fieldnames=MATRIX_FIELDS)
        writer.writeheader()
        writer.writerows(results)
    with (output / "cross_provider_results.csv").open("w", newline="", encoding="utf-8") as handle:
        writer = csv.writer(handle)
        writer.writerow(("test",) + selected_names + ("overall",))
        for test in TESTS:
            writer.writerow((test,) + ("BLOCKED_NOT_RUN",) * len(selected_names) + ("BLOCKED_NOT_RUN",))
    print(json.dumps({"apk_sha256": apk_hash, "results": results, "planned_matrix_rows": planned_rows, "output_dir": str(output)}, indent=2, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
