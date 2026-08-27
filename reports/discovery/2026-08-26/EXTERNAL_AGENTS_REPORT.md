# Dark Med — External Agents Discovery

**Date:** 2026-08-26  
**Decision rule:** لا تُستخدم كلمة `CONNECTED` إلا بعد إنشاء اتصال حقيقي وتنفيذ operation موثقة.

## Discovery result

| Agent | Official path found | Local executable | Connector/integration | Current status | Allowed behavior |
|---|---|---|---|---|---|
| Claude Code | Official Claude Code CLI and authentication documentation | `claude` not found | no Claude connector configured; Anthropic built-in connector disabled | `BLOCKED — DISCOVERED_OFFICIAL_NOT_LOCAL` | read/analyze/propose only; no unrestricted project write |
| OX Alpha | no official OX Alpha API/CLI/SDK/MCP source verified | not found | no OX Alpha connector | `BLOCKED — NO_OFFICIAL_INTEGRATION_CONFIRMED` | no endpoint/model/credential assumptions |
| Internal Code Agent | project-scoped local analysis/build tools | available as project workflow | local | `AVAILABLE` | scope-limited |
| Internal Security Agent | source/manifest/APK/static-audit workflow | available as project workflow | local | `AVAILABLE` | scope-limited |
| Internal QA Agent | Gradle, instrumentation, smoke, evidence collectors | available as project workflow | local | `AVAILABLE` | scope-limited |
| Device Compatibility Agent | matrix and device evidence collectors | available as project workflow | local | `AVAILABLE` | scope-limited |
| Cloud Execution Agent | six provider adapters and preflight orchestrator | available as project workflow | provider credentials absent | `AVAILABLE / PROVIDERS_BLOCKED` | preflight only until authorization |
| Evidence Agent | SHA/evidence merge and PASS gate | available as project workflow | local | `AVAILABLE` | rejects incomplete PASS |

## Claude Code

Anthropic's official documentation describes Claude Code CLI installation and authentication using Claude.ai/Console login or supported cloud providers [1] [2]. The current environment contains no `claude` executable and no Claude connector. The adapter therefore returns `BLOCKED` and does not attempt authentication, install scripts, or unrestricted write access. If the user later installs and authenticates Claude Code locally, the approved workflow is `read → analyze → propose → review → patch → test`; a patch remains unaccepted until the Master Orchestrator reviews it and the relevant tests pass.

## OX Alpha

The discovery found third-party model listings but no official OX Alpha provider documentation, executable, API endpoint, SDK, or MCP server that could be verified as an authorized integration. The adapter is intentionally `BLOCKED`. It does not guess an OpenRouter/third-party endpoint, model identifier, credentials, pricing, or connection state. A future adapter may be enabled only after the user supplies an official provider reference and the authentication model is verified from that provider's documentation.

## Permission model

External agents receive no default project-wide write permission, no private-key access, no credential access, and no network permission. Any future proposal must include provider, connection method, authentication method, model, context limit, execution/filesystem/network permissions, cost, availability, timeout, failure behavior, files changed, tests, evidence, and result. The local contract test confirms that a PASS without APK SHA, device, Android version, timestamp, or evidence is rejected.

## References

[1]: https://code.claude.com/docs/en/authentication "Claude Code authentication"  
[2]: https://code.claude.com/docs/en/quickstart "Claude Code quickstart"  
[3]: https://platform.claude.com/docs/en/cli-sdks-libraries/cli/quickstart "Anthropic CLI quickstart"  
