from __future__ import annotations

import json
from pathlib import Path
import subprocess
import sys
import tempfile


ROOT = Path(__file__).resolve().parents[1]


def run_verifier(payload: dict, provider: str) -> int:
    with tempfile.TemporaryDirectory() as directory:
        approval = Path(directory) / "approval.json"
        approval.write_text(json.dumps(payload), encoding="utf-8")
        result = subprocess.run(
            [sys.executable, str(ROOT / "tools" / "verify_cost_approval.py"), "--file", str(approval), "--provider", provider],
            text=True,
            capture_output=True,
            check=False,
        )
        return result.returncode


def main() -> None:
    base = {
        "provider": "Firebase Test Lab",
        "device": "catalog-device",
        "estimated_duration_minutes": 10,
        "estimated_cost": "provider-quote-to-be-reviewed",
        "quota": "current-quota-to-be-reviewed",
        "runs": 1,
    }
    waiting = {"status": "WAITING_FOR_COST_APPROVAL", "plan": base}
    approved = {"status": "APPROVED", "plan": {**base, "approval": "APPROVED"}}
    wrong_provider = {"status": "APPROVED", "plan": {**base, "provider": "AWS Device Farm", "approval": "APPROVED"}}
    assert run_verifier(waiting, "Firebase Test Lab") == 20
    assert run_verifier(approved, "Firebase Test Lab") == 0
    assert run_verifier(wrong_provider, "Firebase Test Lab") == 20
    print("cost_guard_contract_tests=3")
    print("waiting_rejected=true")
    print("approved_matching_provider_accepted=true")
    print("provider_mismatch_rejected=true")


if __name__ == "__main__":
    main()
