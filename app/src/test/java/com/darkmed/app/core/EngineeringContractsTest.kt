package com.darkmed.app.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EngineeringContractsTest {
    @Test
    fun registryContainsAllTwentyOneSpecializedAgents() {
        assertEquals(21, AgentRegistry.definitions.size)
        assertEquals(EngineeringAgentId.entries.toSet(), AgentRegistry.definitions.map { it.id }.toSet())
        assertEquals(EngineeringArea.entries.toSet(), AgentRegistry.definitions.map { it.area }.toSet())
        assertTrue(AgentRegistry.definitions.all { it.allowedTools.isNotEmpty() })
    }

    @Test
    fun unauthorizedToolIsRejected() {
        val orchestrator = MasterEngineeringOrchestrator()
        val result = orchestrator.submit(
            EngineeringAgentId.UI_UX_BRANDING,
            EngineeringTool.RUN_NETWORK_TEST,
            evidence(EngineeringAgentId.UI_UX_BRANDING, EngineeringStatus.PASS)
        )
        assertEquals(OrchestratorDecision.REJECTED_UNAUTHORIZED_TOOL, result.decision)
    }

    @Test
    fun dependencyMustBePassBeforeSubmission() {
        val orchestrator = MasterEngineeringOrchestrator()
        val result = orchestrator.submit(
            EngineeringAgentId.TOR_ENGINE,
            EngineeringTool.RESEARCH_OFFICIAL_SOURCE,
            evidence(EngineeringAgentId.TOR_ENGINE, EngineeringStatus.PASS)
        )
        assertEquals(OrchestratorDecision.BLOCKED_DEPENDENCY, result.decision)
    }

    @Test
    fun realDeviceRequiredDoesNotBecomePass() {
        val orchestrator = MasterEngineeringOrchestrator()
        submitPass(orchestrator, EngineeringAgentId.BUILD_TOOLCHAIN, EngineeringTool.RUN_BUILD)
        submitPass(orchestrator, EngineeringAgentId.CORE_ARCHITECTURE, EngineeringTool.READ_SOURCE)
        val result = orchestrator.submit(
            EngineeringAgentId.TOR_ENGINE,
            EngineeringTool.RUN_DEVICE_TEST,
            evidence(EngineeringAgentId.TOR_ENGINE, EngineeringStatus.REAL_DEVICE_REQUIRED)
        )
        assertEquals(OrchestratorDecision.ACCEPTED, result.decision)
        assertEquals(EngineeringStatus.REAL_DEVICE_REQUIRED, result.state.statuses[EngineeringArea.TOR])
        assertEquals(ReleaseDecision.RELEASE_BLOCKED, orchestrator.releaseGate())
    }

    @Test
    fun incompletePassEvidenceIsRejected() {
        val orchestrator = MasterEngineeringOrchestrator()
        val incomplete = evidence(EngineeringAgentId.BUILD_TOOLCHAIN, EngineeringStatus.PASS).copy(evidence = emptyList())
        val result = orchestrator.submit(EngineeringAgentId.BUILD_TOOLCHAIN, EngineeringTool.RUN_BUILD, incomplete)
        assertEquals(OrchestratorDecision.REJECTED_INVALID_EVIDENCE, result.decision)
    }

    @Test
    fun emptyProjectCannotPassReleaseGate() {
        assertEquals(ReleaseDecision.RELEASE_BLOCKED, MasterEngineeringOrchestrator().releaseGate())
    }

    @Test
    fun everyEvidenceSectionIsMandatory() {
        val orchestrator = MasterEngineeringOrchestrator()
        val result = orchestrator.submit(
            EngineeringAgentId.BUILD_TOOLCHAIN,
            EngineeringTool.RUN_BUILD,
            evidence(EngineeringAgentId.BUILD_TOOLCHAIN, EngineeringStatus.PASS).copy(rootCause = "")
        )
        assertEquals(OrchestratorDecision.REJECTED_INVALID_EVIDENCE, result.decision)
    }

    private fun submitPass(orchestrator: MasterEngineeringOrchestrator, agent: EngineeringAgentId, tool: EngineeringTool) {
        assertEquals(OrchestratorDecision.ACCEPTED, orchestrator.submit(agent, tool, evidence(agent, EngineeringStatus.PASS)).decision)
    }

    private fun evidence(agent: EngineeringAgentId, status: EngineeringStatus) = EvidenceRecord(
        id = "test-${agent.name}",
        agent = agent,
        component = agent.name,
        before = "not tested",
        finding = "test finding",
        rootCause = "test root cause",
        research = "test research",
        proposedDesign = "test design",
        change = "test change",
        filesModified = listOf("test"),
        tests = listOf("test"),
        commands = listOf("test command"),
        result = status,
        evidence = if (status == EngineeringStatus.PASS) listOf("test evidence SHA-256 signature") else listOf("runtime unavailable"),
        limitations = listOf("unit test only"),
        nextAction = "continue"
    )
}
