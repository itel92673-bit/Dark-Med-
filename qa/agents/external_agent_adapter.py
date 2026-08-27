from __future__ import annotations

from dataclasses import asdict, dataclass
from pathlib import Path
import os
import shutil
from typing import Any


@dataclass(frozen=True)
class AgentSpec:
    provider: str
    connection_method: str
    authentication_method: str
    capabilities: tuple[str, ...]
    model: str
    context_limits: str
    execution_permissions: tuple[str, ...]
    filesystem_permissions: tuple[str, ...]
    network_permissions: tuple[str, ...]
    cost: str
    availability: str
    timeout_seconds: int
    failure_behavior: str


@dataclass(frozen=True)
class AgentResult:
    provider: str
    status: str
    operation: str
    reason: str
    evidence: tuple[str, ...] = ()


class ExternalAgentAdapter:
    def __init__(self, spec: AgentSpec, executable: str | None = None, required_env: tuple[str, ...] = ()):
        self.spec = spec
        self.executable = executable
        self.required_env = required_env

    def descriptor(self) -> dict[str, Any]:
        return asdict(self.spec)

    def preflight(self) -> AgentResult:
        if self.spec.timeout_seconds <= 0:
            return AgentResult(self.spec.provider, "BLOCKED", "preflight", "invalid_timeout")
        if self.spec.availability != "AVAILABLE":
            return AgentResult(self.spec.provider, "BLOCKED", "preflight", self.spec.availability)
        if self.executable and shutil.which(self.executable) is None:
            return AgentResult(self.spec.provider, "BLOCKED", "preflight", f"executable_not_found:{self.executable}")
        missing = tuple(name for name in self.required_env if not os.environ.get(name))
        if missing:
            return AgentResult(self.spec.provider, "BLOCKED", "preflight", "missing_credentials:" + ",".join(missing))
        return AgentResult(self.spec.provider, "READY", "preflight", "requirements_detected")

    def request(self, operation: str, prompt: str, allow_write: bool = False) -> AgentResult:
        if not isinstance(operation, str) or not operation.strip():
            return AgentResult(self.spec.provider, "BLOCKED", "", "malformed_operation")
        if not isinstance(prompt, str):
            return AgentResult(self.spec.provider, "BLOCKED", operation, "malformed_prompt")
        if len(prompt) > 65536:
            return AgentResult(self.spec.provider, "BLOCKED", operation, "prompt_too_large")
        gate = self.preflight()
        if gate.status != "READY":
            return AgentResult(self.spec.provider, "BLOCKED", operation, gate.reason)
        if not prompt.strip():
            return AgentResult(self.spec.provider, "BLOCKED", operation, "empty_prompt")
        if allow_write and "write_project" not in self.spec.execution_permissions:
            return AgentResult(self.spec.provider, "BLOCKED", operation, "write_permission_not_granted")
        if os.environ.get("AGENT_EXTERNAL_EXECUTE") != "1":
            return AgentResult(self.spec.provider, "READY_TO_RUN", operation, "human_execution_gate_required")
        return AgentResult(self.spec.provider, "BLOCKED", operation, "adapter_execution_not_implemented_until_provider_command_is_verified")


def project_scope(root: Path) -> dict[str, str]:
    resolved = root.resolve()
    return {"root": str(resolved), "policy": "read-only by default; proposed patches require Master review"}
