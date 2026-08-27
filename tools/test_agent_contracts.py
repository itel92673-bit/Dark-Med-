from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "qa" / "agents"))
from external_agent_adapter import AgentSpec, ExternalAgentAdapter
from master_orchestrator import EvidenceRecord


def adapter():
    spec = AgentSpec(
        provider="Test Agent",
        connection_method="local",
        authentication_method="none",
        capabilities=("read",),
        model="test",
        context_limits="bounded",
        execution_permissions=("read_project",),
        filesystem_permissions=("read_project",),
        network_permissions=("none",),
        cost="none",
        availability="AVAILABLE",
        timeout_seconds=30,
        failure_behavior="BLOCKED",
    )
    return ExternalAgentAdapter(spec)


def main():
    invalid = EvidenceRecord("C1", "QA Agent", (), "test", "before", "after", (), (), "PASS")
    errors = invalid.validate()
    assert "missing_apk_sha256" in errors
    assert "missing_device" in errors
    assert "missing_android_version" in errors
    assert "missing_timestamp_utc" in errors
    assert "pass_without_evidence" in errors
    blocked = EvidenceRecord("C2", "Cloud Execution Agent", (), "blocked", "before", "after", (), (), "BLOCKED")
    assert blocked.validate() == ()
    not_tested = EvidenceRecord("C3", "Device Compatibility Agent", (), "not run", "before", "after", (), (), "NOT_TESTED")
    assert not_tested.validate() == ()
    valid = EvidenceRecord("C4", "QA Agent", (), "verified", "before", "after", ("test.xml",), ("log.txt",), "PASS", "sha", "device", "34", "2026-08-26T00:00:00Z")
    assert valid.validate() == ()
    agent = adapter()
    assert agent.request("", "input").reason == "malformed_operation"
    assert agent.request("review", None).reason == "malformed_prompt"
    assert agent.request("review", "").reason == "empty_prompt"
    assert agent.request("review", "x" * 65537).reason == "prompt_too_large"
    assert agent.request("review", "input").status == "READY_TO_RUN"
    agent.spec = AgentSpec(**{**agent.spec.__dict__, "timeout_seconds": 0})
    assert agent.preflight().reason == "invalid_timeout"
    print("agent_contract_tests=10")
    print("invalid_pass_rejected=true")
    print("blocked_and_not_tested_preserved=true")
    print("valid_pass_requires_evidence=true")
    print("malformed_empty_huge_input_rejected=true")
    print("invalid_timeout_rejected=true")


if __name__ == "__main__":
    main()
