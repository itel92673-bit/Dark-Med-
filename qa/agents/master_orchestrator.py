from __future__ import annotations

from dataclasses import asdict, dataclass
from datetime import datetime, timezone
from pathlib import Path
import hashlib
import json
from typing import Any

from claude_code_adapter import ClaudeCodeAdapter
from ox_alpha_adapter import OxAlphaAdapter


VALID_STATUSES = {"PASS", "FAIL", "BLOCKED", "NOT_TESTED", "READY_TO_RUN"}


@dataclass(frozen=True)
class EvidenceRecord:
    change_id: str
    agent: str
    files_changed: tuple[str, ...]
    reason: str
    before: str
    after: str
    tests: tuple[str, ...]
    evidence: tuple[str, ...]
    result: str
    apk_sha256: str = ""
    device: str = ""
    android_version: str = ""
    timestamp_utc: str = ""

    def validate(self) -> tuple[str, ...]:
        errors = []
        if self.result not in VALID_STATUSES:
            errors.append("invalid_result")
        if self.result == "PASS":
            required = {"apk_sha256", "device", "android_version", "timestamp_utc"}
            values = {key: getattr(self, key) for key in required}
            errors.extend(f"missing_{key}" for key, value in values.items() if not value)
            if not self.evidence:
                errors.append("pass_without_evidence")
        return tuple(errors)


class MasterOrchestrator:
    def __init__(self, root: Path):
        self.root = root.resolve()
        self.agents = {
            "Code Agent": "internal",
            "Security Agent": "internal",
            "QA Agent": "internal",
            "Device Compatibility Agent": "internal",
            "Cloud Execution Agent": "internal",
            "Evidence Agent": "internal",
            "Claude Code": ClaudeCodeAdapter(self.root),
            "OX Alpha": OxAlphaAdapter(self.root),
        }

    def discover(self) -> dict[str, Any]:
        result = {}
        for name, agent in self.agents.items():
            if isinstance(agent, str):
                result[name] = {"type": "internal", "availability": "AVAILABLE", "permissions": "project-scoped"}
            else:
                result[name] = agent.descriptor()
                result[name]["preflight"] = asdict(agent.preflight())
        return {"generated_at": datetime.now(timezone.utc).isoformat(), "agents": result}

    def record(self, record: EvidenceRecord) -> dict[str, Any]:
        errors = record.validate()
        payload = asdict(record)
        payload["validation"] = "PASS" if not errors else "FAIL"
        payload["validation_errors"] = errors
        return payload

    def write_discovery(self, output: Path) -> None:
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(json.dumps(self.discover(), indent=2, ensure_ascii=False) + "\n", encoding="utf-8")

    def write_record(self, record: EvidenceRecord, output: Path) -> None:
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(json.dumps(self.record(record), indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def artifact_hash(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()
