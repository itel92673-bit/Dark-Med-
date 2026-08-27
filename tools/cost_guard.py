from __future__ import annotations

import argparse
import json
from pathlib import Path


REQUIRED = ("provider", "device", "estimated_duration_minutes", "estimated_cost", "quota", "runs")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--plan", required=True)
    parser.add_argument("--output", required=True)
    args = parser.parse_args()
    plan_path = Path(args.plan)
    output = Path(args.output)
    plan = json.loads(plan_path.read_text(encoding="utf-8"))
    missing = [key for key in REQUIRED if key not in plan or plan[key] in (None, "", [])]
    if missing:
        result = {"status": "BLOCKED", "reason": "missing_cost_plan_fields", "missing": missing}
        output.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
        print(json.dumps(result))
        return 20
    if plan["runs"] < 1:
        result = {"status": "BLOCKED", "reason": "runs_must_be_positive"}
        output.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
        print(json.dumps(result))
        return 20
    if not isinstance(plan["estimated_duration_minutes"], (int, float)) or plan["estimated_duration_minutes"] <= 0:
        result = {"status": "BLOCKED", "reason": "estimated_duration_must_be_positive"}
        output.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
        print(json.dumps(result))
        return 20
    placeholders = [key for key in ("provider", "device", "estimated_cost", "quota") if str(plan[key]).startswith("REPLACE_") or str(plan[key]).startswith("PENDING_")]
    if placeholders:
        result = {"status": "BLOCKED", "reason": "placeholder_cost_plan_values", "fields": placeholders}
        output.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
        print(json.dumps(result))
        return 20
    if plan.get("approval") != "APPROVED":
        result = {"status": "WAITING_FOR_COST_APPROVAL", "reason": "human_cost_approval_required", "plan": plan}
        output.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
        print(json.dumps(result))
        return 20
    result = {"status": "APPROVED", "plan": plan, "reason": "cost_plan_explicitly_approved"}
    output.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(result))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
