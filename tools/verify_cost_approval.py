from __future__ import annotations

import argparse
import json
from pathlib import Path


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--file", required=True)
    parser.add_argument("--provider", required=True)
    args = parser.parse_args()
    path = Path(args.file)
    if not path.is_file():
        print("BLOCKED: cost approval file not found")
        return 20
    try:
        payload = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        print("BLOCKED: cost approval file is unreadable or invalid JSON")
        return 20
    if payload.get("status") != "APPROVED":
        print("WAITING_FOR_COST_APPROVAL: approval status is not APPROVED")
        return 20
    plan = payload.get("plan") or {}
    if plan.get("provider") != args.provider:
        print("BLOCKED: cost approval provider mismatch")
        return 20
    if not plan.get("device") or not plan.get("estimated_duration_minutes") or not plan.get("estimated_cost") or not plan.get("quota") or not plan.get("runs"):
        print("BLOCKED: approved cost plan is incomplete")
        return 20
    print(f"cost_approval=APPROVED provider={args.provider}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
