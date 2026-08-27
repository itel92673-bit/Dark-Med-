from __future__ import annotations

import argparse
import json
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "qa" / "agents"))
from master_orchestrator import MasterOrchestrator


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", required=True)
    args = parser.parse_args()
    output = Path(args.output)
    orchestrator = MasterOrchestrator(ROOT)
    orchestrator.write_discovery(output)
    print(json.dumps(orchestrator.discover(), indent=2, ensure_ascii=False))


if __name__ == "__main__":
    main()
