# Dark Med — Master Full-Spectrum QA, Security & Software Audit

**التاريخ:** 2026-08-26  
**الحزمة:** `com.darkmed.app`  
**APK:** `deliverables/Dark Med f.apk`  
**APK SHA-256:** `e61ce5e72273f680888a4026a83846fd65b5066cdb7bc1d5ba71be4bcfbd276f`  
**القرار:** **NO-GO / QA ONLY / RELEASE BLOCKED**

## 1. Executive Summary

أُجريت مراجعة مستقلة End-to-End للمشروع، شملت المتطلبات الأصلية، المصدر، Manifest، APK، Gradle، اختبارات الوحدة، الاختبارات الساكنة، MobSFscan، الأدوات المضافة للـMaster Orchestrator، Cost Guard، adapters السحابية، smoke/device collectors، ومصفوفة Android 10–16. أُعيد تشغيل بوابة Android المحلية كاملة بعد التعديلات الأخيرة، ونجحت clean وunit tests وlint وassembleDebug وassembleDebugAndroidTest وassembleRelease، كما نجحت Python/Bash/agent/Cost Guard/icon/orchestrator preflight. هذه النجاحات محلية ومحدودة النطاق ولا تثبت مسار الحماية الشبكية أو توافق جهاز حقيقي.

لم تثبت المراجعة أن التطبيق يحقق حماية الجهاز الكاملة عبر VPN/TUN/Tor أو DNS/Kill Switch أو proxy chaining أو `.onion` أو عزل sessions على جهاز حقيقي. كما أن Firebase وAWS Device Farm وBrowserStack وKobiton وSauce Labs وPerfecto لم تنفذ أي run؛ كلها `BLOCKED` بسبب الاعتمادات أو المشاريع أو endpoints أو device pools. الـAPK الحالي Debug-signed، وProduction signing غير متاح. لذلك لا يجوز إصدار GO أو ادعاء أن التطبيق يعمل كما طلب المستخدم في سياق خصوصية عالي الخطورة.

## 2. Requirements Compliance Matrix

| ID | Requirement / acceptance criterion | Implementation | Test / evidence | Actual result | Status |
|---|---|---|---|---|---|
| R-001 | كل claim يحتاج Execution + Evidence + Independent Verification | EvidenceRecord وmerge rules وno-false-PASS | `agent_contract_tests.txt`, `merged_results_v2.json` | PASS بلا metadata يُرفض | `PASS` محليًا |
| R-002 | قراءة المتطلبات وتحويلها إلى matrix | تقارير compliance وmaster report | هذا التقرير و`ATTACHMENTS_COMPLIANCE_MATRIX.md` | المتطلبات موثقة وقابلة للتتبع | `PASS` للتوثيق |
| R-003 | startup/lifecycle/state/persistence/error/recovery | MainActivity وstate/coordinator | build/source review؛ device smoke فشل بيئيًا | code موجود جزئيًا؛ runtime post-change غير مثبت | `PARTIAL / DEVICE_REQUIRED` |
| R-004 | Master task routing/dependencies/retry/timeout/cancel/recovery | `master_orchestrator.py` وEngineeringContracts | contract tests؛ لا يوجد تشغيل swarm متزامن طويل | evidence gate موجود؛ state swarm الكامل غير مثبت | `PARTIAL` |
| R-005 | Agent selection والـinternal agents | ستة مسارات agents project-scoped | `agent_discovery.json` | internal agents متاحة محليًا | `PASS` للنطاق المحلي |
| R-006 | Claude Code adapter | adapter رسمي المسار، read-only default | discovery/preflight | CLI/auth غير متوفران محليًا | `BLOCKED` |
| R-007 | OX Alpha adapter | adapter fail-closed بلا endpoint مخمّن | discovery report | لا يوجد تكامل رسمي موثق | `BLOCKED` |
| R-008 | External adapter separation | Discovery→Capability→Auth→Execution→Validation | `EXTERNAL_AGENTS_REPORT.md` وpreflight | الفصل موجود؛ لا execution خارجي | `PASS` معماريًا / `NOT TESTED` خارجيًا |
| R-009 | Cost Guard يمنع upload/schedule/Appium بلا موافقة | `cost_guard.py` و`verify_cost_approval.py` وكل adapters | 3 contract tests وdirect adapter preflight | waiting/mismatch/placeholder مرفوضة | `PASS` محليًا |
| R-010 | Android 10–16 وOEM matrix | JSON matrix، 18 OEM، 126 target rows | `planned_device_matrix.csv` | كل الصفوف `NOT_TESTED` | `NOT TESTED` |
| R-011 | Infinix SMART 9 أولوية قصوى | target model/weight في config | 7 planned rows | لا يوجد X6532 runtime evidence | `DEVICE_REQUIRED` |
| R-012 | Arabic-first RTL/LTR وواجهة premium dark/red | Compose/resources/MainActivity | lint/build/source review | code/resources موجودة؛ visual device post-change غير مثبتة | `PARTIAL` |
| R-013 | Biometric strong/fingerprint policy | BiometricGate | compile/source review | OEM fingerprint behavior غير مثبت | `DEVICE_REQUIRED` |
| R-014 | Clear All Data biometric→confirm→stop→wipe→verify→lock | coordinators/storage cleanup | source/unit review | residual storage/device proof غير منفذ | `DEVICE_REQUIRED` |
| R-015 | Secure storage وredaction وعدم وصول AI للأسرار | SecureStore/Assistant registry/redaction | source/static/unit evidence | local policy مثبتة؛ device residual غير مثبت | `PARTIAL` |
| R-016 | Tor local service/bootstrap/SOCKS/ControlPort/NEWNYM | Tor service/config/controller/dependencies | renderer/unit/static only | bootstrap/circuit/control runtime غير منفذ | `NETWORK_REQUIRED` |
| R-017 | obfs4 binary/process/bootstrap | لا asset/ABI/process حقيقي | APK audit | غير موجود | `NOT IMPLEMENTED` |
| R-018 | Snowflake binary/WebRTC/bootstrap | لا integration حقيقي | APK audit | غير موجود | `NOT IMPLEMENTED` |
| R-019 | VPN→TUN→route كل الجهاز | VpnService/Hev/JNI structure | native/build/source only | packet flow وone-VPN arbitration غير مثبتان | `DEVICE_REQUIRED` |
| R-020 | WireGuard import/activation/handshake | official tunnel dependency/parser | code/config review | handshake/provider runtime غير منفذ | `NETWORK_REQUIRED` |
| R-021 | Proxy 1/2 وSOCKS/HTTP/HTTPS/auth/chaining | compiler/planning pieces | source review | real forwarder وchain runtime غير مكتمل | `NOT IMPLEMENTED / BLOCKED` |
| R-022 | DNS over Tor/DNSCrypt وIPv4/IPv6 leak prevention | planner/policy | no real packet/leak test | غير مثبت | `NETWORK_REQUIRED` |
| R-023 | Kill Switch fail-closed | policy/guard/failure matrix | unit/policy only | Android-wide traffic block غير مثبت | `DEVICE_REQUIRED` |
| R-024 | Browser `.onion` عبر route موثق | isolated WebView processes/gate | compile/source review | browser remains unavailable before verified route؛ `.onion` غير منفذ runtime | `BLOCKED` |
| R-025 | أربع sessions معزولة | dedicated processes/data suffix | source review | cross-session cookies/cache runtime غير مثبت | `DEVICE_REQUIRED` |
| R-026 | Profiles create/edit/delete/activate | status UI/profile pieces | source review | full backend workflow غير مكتمل | `PARTIAL / BLOCKED` |
| R-027 | Security Center truth/no fake protected state | explicit NOT VERIFIED states | source/build review | status صادق، protection locked | `PASS` للصدق |
| R-028 | offline/online/network switching/recovery | policy/collectors | no controlled real network profile | غير منفذ | `NOT TESTED` |
| R-029 | crash/ANR/low memory/force stop/kill/restart | collectors وfailure matrix | device collector | الجهاز/AVD blocked؛ لا runtime data صالح | `DEVICE_REQUIRED` |
| R-030 | performance RAM/CPU/battery/thermal | architecture/checklist | no measurements on target device | غير مثبت | `NOT TESTED` |
| R-031 | static security/Android Manifest/APK/signing | audit script/MobSFscan | audit وMobSF وapksigner | failures=0 static؛ 6 INFO؛ Debug certificate | `PARTIAL / RELEASE BLOCKED` |
| R-032 | reproducible build/CI | Gradle gate و`run_darkmed_ci.sh` | latest CI run | كل local gates PASS؛ cloud NOT_RUN | `PASS` محليًا |
| R-033 | independent provider runs | six adapters | provider preflight | 0 cloud runs | `BLOCKED` |
| R-034 | final certification percentages with methodology | readiness reports | confidence dimensions | لا توجد بيانات كافية لنسب؛ N/A صحيح | `N/A` |

## 3. Functional Audit

| Area | Evidence-backed finding | Verdict |
|---|---|---|
| Core UI/startup | build/lint/source checks pass; post-change healthy-device launch unavailable | `PARTIAL` |
| Profiles/Security Center/Settings | screens and truthful unavailable states exist; complete real connection workflows do not | `PARTIAL` |
| Assistant | local command/tool/consent/redaction architecture exists; model execution intentionally deferred | `PARTIAL` |
| Tor/VPN/WireGuard/Proxy/DNS | static/integration pieces exist, but required live route/handshake/bootstrap evidence absent | `BLOCKED / NETWORK_REQUIRED` |
| Browser/sessions | isolation architecture exists; protected `.onion` route and cross-session proof absent | `DEVICE_REQUIRED` |
| Clear All Data | coordinator and categories exist; post-wipe residual verification absent | `DEVICE_REQUIRED` |
| Offline/recovery | policies and failure matrix exist; runtime recovery untested | `NOT TESTED` |

## 4. Security Audit

### Confirmed static findings

| Finding | Severity | Evidence | Status |
|---|---|---|---|
| APK uses Android Debug certificate | `HIGH` release blocker | `security_audit_production_required.log`, apksigner | Open; user-owned production key required |
| obfs4 absent | `HIGH` requirement blocker | APK audit warning; no asset in APK | Open; no fake asset added |
| Snowflake absent | `HIGH` requirement blocker | APK audit warning; no asset in APK | Open; no fake asset added |
| MobSFscan informational findings | `INFO` | 6 INFO, 0 ERROR after launcher fix | Reviewed; no ERROR remains |

لم تُثبت المراجعة الحالية وجود hardcoded secret أو private key أو cleartext network configuration أو exported component غير مقصود في static scan. هذا لا يثبت غياب كل ثغرة ممكنة، ولا يعوّض runtime/network testing أو dependency CVE service scan. لا توجد هجمات على أنظمة خارجية؛ كل الاختبارات الهجومية المحلية كانت non-destructive.

### Android security

Manifest يعلن `usesCleartextTraffic=false` و`allowBackup=false`، ويستخدم FGS/VPN declarations والـnotification permission المناسبة للنطاق الحالي. تم تشديد launcher task handling بعد MobSF task-hijacking finding وإعادة البناء/الفحص. ما زالت WebView route binding، deep-link abuse، clipboard/notification leakage، exported behavior على OEM، وresidual deletion تحتاج جهازًا صالحًا.

## 5. Code Audit and Logical Errors

تم البحث صراحةً عن التحويلات `FAIL→PASS` و`BLOCKED→APPROVED` و`NOT_TESTED→PASS` و`TIMEOUT→SUCCESS`. كُشف خطآن حقيقيان وأُصلحا مع regression tests: سجل smoke قديم كان يعلن PASS رغم system ANR/launch timeout، وCost Guard كان يعكس منطق حالة الموافقة. كما صُححت تسمية production-signing التي كانت توحي خطأً بأن Debug certificate إنتاجية. بعد الإصلاح، local CI وagent contracts وCost Guard contracts أعادت النجاح.

| ID | المشكلة | السبب | الإصلاح | Regression |
|---|---|---|---|---|
| BUG-001 | false PASS في device smoke | فحص exit code بلا system dialog/timeout validation | collector يرفض system ANR وtimeout؛ status القديم صُحح إلى FAIL | `collect_device_smoke.sh` وevidence review |
| BUG-002 | `WAITING_FOR_COST_APPROVAL` صُنّفت APPROVED | شرط منطقي معكوس | APPROVED هي الحالة الوحيدة المقبولة؛ placeholders/zero duration مرفوضة | `test_cost_guard.py`، 3 tests |
| BUG-003 | production signing label غير دقيق | رسالة auditor لا تطابق debug certificate | صياغة Debug/non-production مع إبقاء failure | QA وproduction-required audit |
| BUG-004 | adapters لا تتحقق من approval artifact | environment flag وحده غير كاف | ملف approval متطابق provider مطلوب قبل upload/schedule/Appium | syntax + preflight + verifier tests |

## 6. Crash, Chaos and Stability Testing

تم تنفيذ chaos محلي آمن على مستوى policy/contracts: missing configuration، malformed approval، provider mismatch، duplicate/invalid states، وdirect adapter preflight. نجحت قواعد الرفض. أما process kill وforce stop وnetwork loss/recovery وlow memory وstorage pressure وreal ANR وrotation وupgrade/reinstall فلم يمكن تنفيذها بدليل صالح لأن AVD الحالي غير مستقر والمضيف بلا `/dev/kvm`، ولا يوجد جهاز حقيقي متصل.

أول smoke attempt كشف system dialog `Process system isn't responding` و`am start -W` timeout. بعد ذلك فشلت محاولات clean/wipe/TCG وARM64 بسبب بيئة emulator/translation، لا بسبب stack trace مثبت من Dark Med. النتيجة الصحيحة هي `FAIL` للاختبار البيئي و`DEVICE_REQUIRED` للتوافق، وليست PASS للتطبيق.

## 7. Android Audit

تم فحص Manifest، components، permissions، services، processes، backup policy، cleartext policy، WebView process isolation، native libraries، package/version، APK signature، وicon pixels. أحدث Android build gate نجح. لا يزال runtime install/launch/post-change instrumentation على جهاز سليم غير مثبتًا. Emulator-only لا يُعامل كبديل عن real device، وInfinix SMART 9/XOS ما زال `DEVICE_REQUIRED`.

## 8. Network Audit

لا يوجد evidence صالح لـWi-Fi أو4G أو5G أوdual-stack أوDNS leak أوIPv4/IPv6 leak أوVPN/TUN packet flow أوTor bootstrap/SOCKS/ControlPort أوWireGuard handshake أوproxy hop/chaining أوKill Switch under failure. أدوات network/crash collectors تسجل `BLOCKED` عند غياب الجهاز/profile، ولا تسجل PASS من عدم الملاحظة. لا توجد endpoints أو credentials أو bridges أو transports مخترعة.

## 9. Performance Audit

لا توجد measurements موثقة للـRAM/CPU/battery/thermal/startup latency على Infinix أو real devices. يوجد source/build review فقط. لذلك لا يمكن إعطاء نسبة أداء أو القول إن التطبيق خفيف أو لا يسبب ANR. الأداء readiness هو `N/A`.

## 10. Data Integrity Audit

تمت مراجعة coordinators وسياسة Clear All Data وstorage categories وstate contracts. لم يُنفذ بعد crash أثناء الكتابة أو concurrent writes أو migration/upgrade/rollback أو residual-data scan على جهاز حقيقي. لذلك consistency وatomicity وsafe recovery غير مثبتة runtime، ولا يجوز الادعاء بأن الحذف الفيزيائي الآمن تحقق.

## 11. Agent / Orchestrator Audit

Master Orchestrator يعرّف agents الداخلية وخدمات evidence، ويسجل provider/model/capability/permissions/cost/timeout/failure behavior. External agents منفصلون عن execution؛ Claude Code `BLOCKED` لغياب CLI/auth، وOX Alpha `BLOCKED` لغياب تكامل رسمي موثق. `EvidenceRecord.validate()` يرفض PASS ناقص SHA/device/Android/timestamp/evidence. لم يُشغّل swarm خارجي متزامن لأن integrations غير متاحة؛ لا يتم الادعاء بوجود independent external review لم يحدث.

## 12. Cost Guard Audit

القاعدة المنفذة هي:

> لا توجد موافقة تكلفة صالحة = لا Cloud Execution، لا Upload، لا Schedule، لا Appium Session.

اختبارات contract غطت waiting approval، approved matching provider، provider mismatch، zero duration، placeholders، وdirect verifier behavior. كل adapters الستة تتطلب execution flag، cost flag، وapproval file متطابقًا. لم يتم تنفيذ upload أو schedule أو session. Race/concurrent approval على real cloud لم تُنفذ، لكن bypass المباشر المحلي الذي يمكن اختباره دون خدمة خارجية يظل مرفوضًا عند غياب الملف أو mismatch.

## 13. Test Quality Audit

الاختبارات الحالية ليست كلها runtime tests؛ التقرير يفصل unit/policy/static/device/network. كُشف false positive في smoke وصُحح. contract tests تحتوي assertions ذات معنى، و`PASS` يتطلب evidence. ما زالت هناك فروع غير مختبرة مثل network/provider malformed responses، real service death، concurrency على الجهاز، وfull UI flows. لذلك test quality محليًا `PASS` للـgates المضافة و`PARTIAL` للتغطية الشاملة.

## 14. Dependency / Supply-Chain Audit

تم فحص package/native presence وGradle/build configuration وMobSFscan. توجد مكتبات Tor/WireGuard/Hev/native حقيقية في APK، لكن وجودها لا يثبت تشغيلها. لم تُجرَ خدمة CVE حديثة كاملة على كل transitive dependency في هذه الجولة؛ لذلك لا يُعطى Supply-Chain PASS شامل. لا توجد حزمة مشبوهة أو secret pattern مثبت في الفحص المحلي الحالي.

## 15. Bugs Found

| Severity | Open | Fixed in this audit | Notes |
|---|---:|---:|---|
| CRITICAL | 0 confirmed static | 1 logic bug (Cost Guard) | كان يمكن أن يرفع الحالة خطأً؛ أُصلح واختُبر |
| HIGH | 3 release blockers | 2 | Debug signing، obfs4، Snowflake؛ لا تزال المتطلبات غير متاحة |
| MEDIUM | 0 confirmed | 0 | runtime unavailable prevents stronger classification |
| LOW | 0 confirmed | 1 wording bug | signing message corrected |
| INFO | 6 MobSF INFO + 3 audit warnings | reviewed | warnings retained honestly |

هذه الأرقام تفصل bugs/findings المؤكدة عن كل الوظائف غير المثبتة. الوظائف الأساسية غير المثبتة تظهر في `Blockers` ولا تُخفى داخل severity count.

## 16. Fixes Applied

شملت الإصلاحات تصحيح false PASS في smoke evidence، تقوية Cost Guard وإضافة approval-file/provider matching، إضافة external-agent discovery adapters، إضافة agent/evidence contracts، إضافة local CI gate، تصحيح تسمية debug signing، إعادة build كاملة، إعادة static/MobSF/icon checks، ومزامنة التقارير وSHA والحزمة المضغوطة.

## 17. Regression Tests

| Test | Result | Evidence |
|---|---|---|
| Gradle clean | `PASS` | `reports/discovery/2026-08-26/final_build_gate/clean.log` |
| Unit tests | `PASS` | `.../unit_test.log`؛ baseline الحالي 16 tests |
| Lint debug/release | `PASS` | `.../lint_debug.log`, `lint_release.log` |
| Assemble debug/androidTest/release | `PASS` | corresponding final build logs |
| Python compile | `PASS` | local CI status |
| Bash syntax | `PASS` | local CI status |
| Agent evidence contracts | `PASS` | `agent_contracts.log`؛ 4 tests |
| Cost Guard contracts | `PASS` | `cost_guard_contracts.log`؛ 3 tests |
| Security audit QA | `PASS` with 3 warnings | `final_static_evidence/security_audit_qa.log` |
| Production-required signing audit | expected `FAIL` | Debug certificate correctly rejected |
| Icon pixel comparison | `PASS` | source/supplied/packaged pixels equal |
| Orchestrator preflight | `PASS` as preflight only | 6 provider rows BLOCKED; 126 planned rows NOT_TESTED |
| Cloud execution | `NOT_RUN` | no upload/schedule/session |

## 18. Remaining Risks

المخاطر الأهم هي أن بناء APK ووجود native libraries قد يعطي إحساسًا زائفًا بأن routing محمي، بينما لم يُقَس packet flow. كذلك Android OEM background restrictions وVPN arbitration وIPv6/DNS behavior وWebView storage isolation وbiometric modality قد تختلف على Infinix/XOS. Debug signing غير مناسب للتوزيع، وغياب obfs4/Snowflake وproxy forwarder وlive Tor/browser route يمنع مطابقة المواصفات الأساسية.

## 19. Blockers

| Blocker | Required evidence/action |
|---|---|
| Production signing | user-owned keystore configured locally; never send key/password in chat |
| Healthy device | real Infinix SMART 9 preferred, or healthy authorized Android device |
| Cloud providers | account/project/auth/device pool/endpoint for each provider; no secrets in chat |
| Network profiles | controlled authorized VPN/WireGuard/proxy/Tor endpoints and IPv4/IPv6 test network |
| Runtime route | VPN/TUN packet capture, DNS/IP leak tests, Tor bootstrap/SOCKS/NEWNYM, Kill Switch failure tests |
| Browser/sessions | real `.onion` route and cross-session cookie/cache/storage isolation evidence |
| Release features | real proxy forwarder/chaining and required PT implementations if they remain in scope |
| Performance/data integrity | measurements and crash/interruption/residual-data evidence on device |

## 20. Evidence / Trace Matrix

| Evidence ID | Command/artifact | What it proves | What it does not prove | Verifier |
|---|---|---|---|---|
| E-001 | final local CI status | reproducible local build/static/contract pipeline passed | no real device/network | Master local review |
| E-002 | APK SHA and package/signature | exact artifact identity and v2 technical signing | production readiness | Release audit |
| E-003 | icon pixel report | official icon pixels unchanged | UI quality/runtime | icon comparator |
| E-004 | MobSFscan output | 6 INFO/0 ERROR for scan scope | full dynamic security/CVE coverage | static audit |
| E-005 | agent discovery JSON | internal availability and external blocked states | external agent opinions | Master Orchestrator |
| E-006 | Cost Guard contract logs | invalid approval paths rejected locally | real cloud race behavior | contract tests |
| E-007 | provider preflight and planned CSV | six provider prerequisites and 126 planned targets | device runs or device availability | orchestrator |
| E-008 | UI smoke screenshot/window/logcat | system ANR/timeout occurred in emulator attempt | app crash on real device | QA review |
| E-009 | production-required audit | Debug certificate blocks production gate | signed production APK | security auditor |

## 21. Final Release Decision

| Dimension | Result | Methodology |
|---|---|---|
| Functional readiness | `N/A / NOT PROVEN` | required live routes and full workflows not executed |
| Security readiness | `N/A` for privacy runtime; static local scan limited PASS | static evidence only; no leak/runtime attack proof |
| Software quality | `PASS` for local gate, `PARTIAL` overall | build/contracts pass; runtime breadth unavailable |
| Performance readiness | `N/A` | no target-device measurements |
| Android readiness | `DEVICE_REQUIRED` | no healthy post-change device run |
| Network readiness | `N/A` | no authorized controlled network profile |
| Agent system readiness | `PASS` local contracts; external `BLOCKED` | no external authenticated execution |
| Release readiness | `NO-GO` | debug signing plus core required features unproven |

**CRITICAL:** 0 confirmed static; 1 historical logic issue fixed.  
**HIGH:** 3 open release blockers: Debug signing, obfs4 absent, Snowflake absent.  
**MEDIUM:** 0 confirmed in current static scope.  
**LOW:** 0 open; one wording issue fixed.  
**OPEN BLOCKERS:** production key, healthy device/cloud auth, controlled network, runtime route/browser/session evidence.  
**UNTESTED:** real device matrix, Android 10–16 runtime, Infinix SMART 9, network/security flows, performance/data-integrity chaos.  
**BLOCKED:** six cloud providers, current emulator path, external agents, protected runtime features.  
**FIXES APPLIED:** false-PASS smoke correction, Cost Guard logic and approval-file gate, signing wording, agent/evidence/CI infrastructure.  
**REGRESSION STATUS:** local regression gates PASS; runtime regression unavailable.  
**PRODUCTION REQUIREMENTS:** not satisfied; APK is Android Debug-signed.  
**CONFIDENCE:** no arbitrary percentage; unsupported dimensions remain `N/A`.

> **السؤال الحاسم:** لا أستطيع إثبات أن التطبيق يعمل كما طلب المستخدم في مسار الحماية الكامل. أستطيع إثبات أن النسخة الحالية قابلة للبناء والتدقيق المحلي، وأن الأدوات ترفض false PASS والعمليات السحابية غير المصرح بها. أما VPN/TUN/Tor/DNS/Kill Switch/proxy/browser isolation/Infinix SMART 9 فتظل غير مثبتة أو محجوبة حتى تتوفر أدلة runtime فعلية.

## تحديث أخير بعد Orchestrator adversarial test

كشفت اختبارات state machine أن implementation الأولى كانت تحاول الانتقال من `RUNNING` إلى `RETRY` مباشرة، مع أن المسار المطلوب هو `RUNNING → FAILURE → RETRY` أو `FINAL_FAILURE`. تم إصلاح المنطق، ثم كُشف أن assertion الأول لا يثبت dependency gate فعليًا لأنه أنشأ المهمة التابعة بعد نجاح dependency؛ تم إصلاح test fixture نفسه ليختبر queue قبل اكتمال dependency. بعد ذلك نجحت أربعة مسارات: retry ثم success، dependency gate، timeout ثم recovery، وcancel/final failure، مع منع duplicate start والانتقالات غير المعرفة. أُدخل هذا الاختبار في local CI، وأعيد تشغيل CI كاملًا بنجاح.

تم إنشاء `reports/EVIDENCE_TRACE_MATRIX.md` مستقلًا بالحقول Test ID وTimestamp وAgent وComponent وInput وEnvironment وCommand وExpected وActual وArtifact وLog/Trace وResult وVerifier. يظل هذا السجل صريحًا في الفصل بين PASS المحلي وBLOCKED/NOT TESTED للـruntime.
