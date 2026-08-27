from __future__ import annotations

from pathlib import Path

from external_agent_adapter import AgentResult, AgentSpec, ExternalAgentAdapter, project_scope


class ClaudeCodeAdapter(ExternalAgentAdapter):
    def __init__(self, root: Path):
        spec = AgentSpec(
            provider="Claude Code",
            connection_method="official Claude Code CLI",
            authentication_method="Claude.ai/Console OAuth or ANTHROPIC_API_KEY, according to official configuration",
            capabilities=("review_files", "analyze_failure", "propose_patch", "write_tests", "review_architecture", "analyze_logs"),
            model="provider-selected",
            context_limits="provider-defined; verify at runtime",
            execution_permissions=("read_project", "propose_patch", "run_read_only_analysis"),
            filesystem_permissions=("read_project", "write_only_after_master_review"),
            network_permissions=("none_by_default",),
            cost="account/subscription or API billing; verify before use",
            availability="DISCOVERED_OFFICIAL_NOT_LOCAL",
            timeout_seconds=600,
            failure_behavior="BLOCKED on missing CLI/auth; no unrestricted write; preserve evidence",
        )
        super().__init__(spec, executable="claude", required_env=())
        self.root = root.resolve()

    def read_only_request(self, operation: str, prompt: str) -> AgentResult:
        if not self.root.exists():
            return AgentResult(self.spec.provider, "BLOCKED", operation, "project_root_not_found")
        result = self.request(operation, prompt, allow_write=False)
        if result.status == "READY":
            return AgentResult(result.provider, result.status, result.operation, result.reason, (project_scope(self.root)["policy"],))
        return result
