package com.darkmed.app.core

enum class EngineeringAgentId {
    BUILD_TOOLCHAIN,
    CORE_ARCHITECTURE,
    TOR_ENGINE,
    VPN_ROUTING,
    WIREGUARD,
    DNS_LEAKS,
    KILL_SWITCH,
    BIOMETRIC,
    STORAGE_SECURITY,
    BROWSER_ONION,
    TOR_ADVANCED_NETWORK,
    AI_ASSISTANT,
    AI_MODEL,
    SECURITY_AUDITOR,
    TEST_ENGINEER,
    RELEASE_ENGINEER,
    ANDROID_COMPATIBILITY,
    UI_UX_BRANDING,
    FAILURE_INJECTION,
    FORENSIC_CODE_AUDITOR,
    DOCUMENTATION_EVIDENCE,
    RESEARCH
}

enum class EngineeringArea {
    BUILD,
    ARCHITECTURE,
    TOR,
    VPN,
    WIREGUARD,
    DNS,
    KILL_SWITCH,
    BIOMETRIC,
    STORAGE,
    BROWSER,
    TOR_ADVANCED,
    ASSISTANT,
    AI_MODEL,
    SECURITY,
    TESTING,
    RELEASE,
    ANDROID_COMPATIBILITY,
    UI_UX,
    FAILURE_INJECTION,
    FORENSICS,
    DOCUMENTATION,
    RESEARCH
}

enum class EngineeringStatus {
    NOT_TESTED,
    PASS,
    FAIL,
    BLOCKED,
    UNKNOWN,
    REAL_DEVICE_REQUIRED,
    NETWORK_REQUIRED,
    NOT_IMPLEMENTED
}

enum class EngineeringTool {
    READ_SOURCE,
    MODIFY_OWN_SCOPE,
    RUN_UNIT_TESTS,
    RUN_BUILD,
    RUN_LINT,
    RUN_APK_FORENSICS,
    RUN_SECURITY_AUDIT,
    RUN_ANDROID_TEST,
    RUN_NETWORK_TEST,
    RUN_DEVICE_TEST,
    WRITE_REPORT,
    RESEARCH_OFFICIAL_SOURCE
}

data class AgentDefinition(
    val id: EngineeringAgentId,
    val area: EngineeringArea,
    val allowedTools: Set<EngineeringTool>,
    val dependencies: Set<EngineeringAgentId>
)

object AgentRegistry {
    private val common = setOf(
        EngineeringTool.READ_SOURCE,
        EngineeringTool.MODIFY_OWN_SCOPE,
        EngineeringTool.RUN_UNIT_TESTS,
        EngineeringTool.WRITE_REPORT
    )
    private val runtime = setOf(EngineeringTool.RUN_DEVICE_TEST, EngineeringTool.RUN_NETWORK_TEST)
    private val research = setOf(EngineeringTool.RESEARCH_OFFICIAL_SOURCE)

    val definitions: List<AgentDefinition> = listOf(
        AgentDefinition(EngineeringAgentId.BUILD_TOOLCHAIN, EngineeringArea.BUILD, common + EngineeringTool.RUN_BUILD + EngineeringTool.RUN_LINT, emptySet()),
        AgentDefinition(EngineeringAgentId.CORE_ARCHITECTURE, EngineeringArea.ARCHITECTURE, common + research, setOf(EngineeringAgentId.BUILD_TOOLCHAIN)),
        AgentDefinition(EngineeringAgentId.TOR_ENGINE, EngineeringArea.TOR, common + runtime + research, setOf(EngineeringAgentId.CORE_ARCHITECTURE)),
        AgentDefinition(EngineeringAgentId.VPN_ROUTING, EngineeringArea.VPN, common + runtime + research, setOf(EngineeringAgentId.CORE_ARCHITECTURE)),
        AgentDefinition(EngineeringAgentId.WIREGUARD, EngineeringArea.WIREGUARD, common + runtime + research, setOf(EngineeringAgentId.VPN_ROUTING)),
        AgentDefinition(EngineeringAgentId.DNS_LEAKS, EngineeringArea.DNS, common + runtime + research, setOf(EngineeringAgentId.VPN_ROUTING)),
        AgentDefinition(EngineeringAgentId.KILL_SWITCH, EngineeringArea.KILL_SWITCH, common + runtime + research, setOf(EngineeringAgentId.VPN_ROUTING, EngineeringAgentId.DNS_LEAKS)),
        AgentDefinition(EngineeringAgentId.BIOMETRIC, EngineeringArea.BIOMETRIC, common + runtime + research, emptySet()),
        AgentDefinition(EngineeringAgentId.STORAGE_SECURITY, EngineeringArea.STORAGE, common + runtime + research, emptySet()),
        AgentDefinition(EngineeringAgentId.BROWSER_ONION, EngineeringArea.BROWSER, common + runtime + research, setOf(EngineeringAgentId.VPN_ROUTING)),
        AgentDefinition(EngineeringAgentId.TOR_ADVANCED_NETWORK, EngineeringArea.TOR_ADVANCED, common + runtime + research, setOf(EngineeringAgentId.TOR_ENGINE)),
        AgentDefinition(EngineeringAgentId.AI_ASSISTANT, EngineeringArea.ASSISTANT, common, setOf(EngineeringAgentId.CORE_ARCHITECTURE)),
        AgentDefinition(EngineeringAgentId.AI_MODEL, EngineeringArea.AI_MODEL, common + research, setOf(EngineeringAgentId.AI_ASSISTANT)),
        AgentDefinition(EngineeringAgentId.SECURITY_AUDITOR, EngineeringArea.SECURITY, common + research + EngineeringTool.RUN_APK_FORENSICS + EngineeringTool.RUN_SECURITY_AUDIT, setOf(EngineeringAgentId.BUILD_TOOLCHAIN)),
        AgentDefinition(EngineeringAgentId.TEST_ENGINEER, EngineeringArea.TESTING, common + EngineeringTool.RUN_BUILD + EngineeringTool.RUN_LINT + EngineeringTool.RUN_ANDROID_TEST + EngineeringTool.RUN_APK_FORENSICS, setOf(EngineeringAgentId.BUILD_TOOLCHAIN)),
        AgentDefinition(EngineeringAgentId.RELEASE_ENGINEER, EngineeringArea.RELEASE, common + EngineeringTool.RUN_BUILD + EngineeringTool.RUN_APK_FORENSICS, setOf(EngineeringAgentId.TEST_ENGINEER, EngineeringAgentId.SECURITY_AUDITOR)),
        AgentDefinition(EngineeringAgentId.ANDROID_COMPATIBILITY, EngineeringArea.ANDROID_COMPATIBILITY, common + runtime + research, setOf(EngineeringAgentId.BUILD_TOOLCHAIN)),
        AgentDefinition(EngineeringAgentId.UI_UX_BRANDING, EngineeringArea.UI_UX, common, emptySet()),
        AgentDefinition(EngineeringAgentId.FAILURE_INJECTION, EngineeringArea.FAILURE_INJECTION, common + EngineeringTool.RUN_UNIT_TESTS + EngineeringTool.RUN_DEVICE_TEST + EngineeringTool.RUN_NETWORK_TEST, setOf(EngineeringAgentId.CORE_ARCHITECTURE)),
        AgentDefinition(EngineeringAgentId.FORENSIC_CODE_AUDITOR, EngineeringArea.FORENSICS, common + research, setOf(EngineeringAgentId.CORE_ARCHITECTURE)),
        AgentDefinition(EngineeringAgentId.DOCUMENTATION_EVIDENCE, EngineeringArea.DOCUMENTATION, common, emptySet()),
        AgentDefinition(EngineeringAgentId.RESEARCH, EngineeringArea.RESEARCH, setOf(EngineeringTool.READ_SOURCE, EngineeringTool.RESEARCH_OFFICIAL_SOURCE, EngineeringTool.WRITE_REPORT), emptySet())
    )

    fun definition(id: EngineeringAgentId): AgentDefinition = definitions.first { it.id == id }
}

data class EvidenceRecord(
    val id: String,
    val agent: EngineeringAgentId,
    val component: String,
    val before: String,
    val finding: String,
    val rootCause: String,
    val research: String,
    val proposedDesign: String,
    val change: String,
    val filesModified: List<String>,
    val tests: List<String>,
    val commands: List<String>,
    val result: EngineeringStatus,
    val evidence: List<String>,
    val limitations: List<String>,
    val nextAction: String
)

data class ProjectState(
    val statuses: Map<EngineeringArea, EngineeringStatus> = EngineeringArea.entries.associateWith { EngineeringStatus.NOT_TESTED },
    val evidence: List<EvidenceRecord> = emptyList(),
    val blockers: List<String> = emptyList()
)

enum class OrchestratorDecision {
    ACCEPTED,
    REJECTED_UNAUTHORIZED_TOOL,
    BLOCKED_DEPENDENCY,
    REJECTED_INVALID_EVIDENCE
}

data class AgentSubmission(val decision: OrchestratorDecision, val reason: String, val state: ProjectState)

enum class ReleaseDecision { RELEASE_ALLOWED, RELEASE_BLOCKED }

class MasterEngineeringOrchestrator(initialState: ProjectState = ProjectState()) {
    var state: ProjectState = initialState
        private set

    fun submit(agent: EngineeringAgentId, tool: EngineeringTool, evidence: EvidenceRecord): AgentSubmission {
        val definition = AgentRegistry.definition(agent)
        if (tool !in definition.allowedTools) {
            return AgentSubmission(OrchestratorDecision.REJECTED_UNAUTHORIZED_TOOL, "Tool is outside agent scope", state)
        }
        val requiredText = listOf(
            evidence.id,
            evidence.component,
            evidence.before,
            evidence.finding,
            evidence.rootCause,
            evidence.research,
            evidence.proposedDesign,
            evidence.change,
            evidence.nextAction
        )
        val completeEvidence = evidence.agent == agent &&
            requiredText.all { it.isNotBlank() } &&
            evidence.filesModified.isNotEmpty() &&
            evidence.tests.isNotEmpty() &&
            evidence.commands.isNotEmpty() &&
            evidence.evidence.isNotEmpty() &&
            evidence.limitations.isNotEmpty()
        val runtimePassWithoutProof = tool in setOf(EngineeringTool.RUN_DEVICE_TEST, EngineeringTool.RUN_NETWORK_TEST) &&
            evidence.result == EngineeringStatus.PASS &&
            evidence.evidence.none { item ->
                item.contains("real device", ignoreCase = true) ||
                    item.contains("network verified", ignoreCase = true) ||
                    item.contains("network test", ignoreCase = true)
            }
        if (!completeEvidence || runtimePassWithoutProof) {
            return AgentSubmission(OrchestratorDecision.REJECTED_INVALID_EVIDENCE, "Evidence contract is incomplete or runtime PASS is unproven", state)
        }
        val dependenciesReady = definition.dependencies.all { dependency ->
            state.statuses[AgentRegistry.definition(dependency).area] == EngineeringStatus.PASS
        }
        if (!dependenciesReady) {
            return AgentSubmission(OrchestratorDecision.BLOCKED_DEPENDENCY, "Dependencies are not PASS", state)
        }
        state = state.copy(
            statuses = state.statuses + (definition.area to evidence.result),
            evidence = state.evidence + evidence
        )
        return AgentSubmission(OrchestratorDecision.ACCEPTED, "Evidence accepted", state)
    }

    fun releaseGate(): ReleaseDecision {
        val allPass = EngineeringArea.entries.all { state.statuses[it] == EngineeringStatus.PASS }
        val completeEvidence = state.evidence.isNotEmpty() && state.evidence.all {
            it.tests.isNotEmpty() && it.commands.isNotEmpty() && it.evidence.isNotEmpty() && it.limitations.isNotEmpty()
        }
        val noBlockers = state.blockers.isEmpty()
        val signedArtifact = state.evidence.any {
            it.result == EngineeringStatus.PASS &&
                it.evidence.any { evidence -> evidence.contains("SHA-256", ignoreCase = true) } &&
                it.evidence.any { evidence -> evidence.contains("signature", ignoreCase = true) }
        }
        return if (allPass && noBlockers && completeEvidence && signedArtifact) ReleaseDecision.RELEASE_ALLOWED else ReleaseDecision.RELEASE_BLOCKED
    }
}
