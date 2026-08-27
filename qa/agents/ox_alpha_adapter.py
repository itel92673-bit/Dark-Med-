from __future__ import annotations

from pathlib import Path

from external_agent_adapter import AgentResult, AgentSpec, ExternalAgentAdapter, project_scope


class OxAlphaAdapter(ExternalAgentAdapter):
    def __init__(self, root: Path):
        spec = AgentSpec(
            provider="OX Alpha",
            connection_method="UNKNOWN",
            authentication_method="UNKNOWN",
            capabilities=("architecture_review", "code_review", "alternative_implementation", "debugging", "test_generation", "security_reasoning"),
            model="OX Alpha",
            context_limits="UNKNOWN",
            execution_permissions=("read_project", "propose_patch"),
            filesystem_permissions=("read_project", "write_only_after_master_review"),
            network_permissions=("none_by_default",),
            cost="UNKNOWN",
            availability="BLOCKED_NO_OFFICIAL_EXECUTABLE_OR_INTEGRATION_CONFIRMED",
            timeout_seconds=600,
            failure_behavior="BLOCKED; never guess provider endpoint, credentials, model access, or connection state",
        )
        super().__init__(spec)
        self.root = root.resolve()

    def read_only_request(self, operation: str, prompt: str) -> AgentResult:
        if not self.root.exists():
            return AgentResult(self.spec.provider, "BLOCKED", operation, "project_root_not_found")
        return AgentResult(self.spec.provider, "BLOCKED", operation, "no_official_integration_or_authenticated_runtime_available")
