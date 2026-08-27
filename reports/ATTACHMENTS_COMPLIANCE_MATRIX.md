# Dark Med — Attachments Compliance Matrix

**تاريخ التدقيق:** 2026-08-25

**نطاق المصدر:** `pasted_content.txt`، `pasted_content_2.txt`، `pasted_content_3.txt`. هذه المصفوفة تقارن المتطلبات بالمصدر الحالي للمشروع، ولا تحول وجود الكود أو نجاح البناء إلى إثبات runtime.

## قواعد التصنيف

| الحالة | المعنى |
|---|---|
| `IMPLEMENTED / CODE_VERIFIED` | الكود أو الموارد موجودة ومراجعة محليًا؛ لا تعني نجاح الجهاز أو الشبكة. |
| `INTEGRATION_VERIFIED` | الوصلات بين طبقات التطبيق موجودة ومراجعة، مع بقاء runtime مستقلًا. |
| `LOCALLY_VERIFIED` | أثبتها اختبار محلي أو static/APK audit محدد. |
| `DEVICE_REQUIRED` | تحتاج تنفيذًا على جهاز Android فعلي أو Emulator صالح. |
| `NETWORK_REQUIRED` | تحتاج endpoint أو شبكة أو handshake حقيقي. |
| `NOT_IMPLEMENTED` | لا يوجد تنفيذ حقيقي كافٍ، ولا يجوز إخفاء ذلك. |
| `BLOCKED` | مطلوب في المواصفة لكن الدليل أو مكوّن أساسي غير متاح. |

## Compliance Matrix

| Requirement | Source file / section | Current implementation | Missing / broken | Needs modification | Test required | Final status |
|---|---|---|---|---|---|---|
| Golden rule: no false PASS | الملفات الثلاثة §0 | Evidence contracts وRelease Gate وFailure Matrix ترفض PASS بلا دليل | لا يوجد runtime device evidence | إبقاء gate صارمًا | Contract tests وfinal evidence review | `LOCALLY_VERIFIED`; runtime claims blocked |
| Full repository forensic audit | الملف 1 §1؛ الملف 2 §1؛ الملف 3 §1 | Baseline وتقارير APK/security موجودة، وaudit script قابل لإعادة التشغيل | يلزم توليد التقارير الأربعة الجديدة من الحالة النهائية | نعم، تقارير نهائية | source scan، dependency scan، APK forensic | `IMPLEMENTED / CODE_VERIFIED` |
| Feature Truth Matrix | الملف 1 §1؛ الملف 2 §31؛ الملف 3 §1 و§9 و§11 | UI/Backend/Evidence contracts موجودة جزئيًا | يلزم ربط كل زر/خدمة بحالة runtime صريحة | نعم، إنشاء `FINAL_FEATURE_TRUTH_AUDIT.md` | no-dead-feature scan ومراجعة يدويّة | `BLOCKED` لبعض الميزات |
| Android API-level compatibility 29–34 | الملف 1 §3؛ الملف 2 §2 | minSdk 29، targetSdk 35، compileSdk 37، API feature detection جزئي | لا يوجد matrix runtime API 29/31/33/34 | نعم، عند توفر Emulator/Firebase | install/launch/instrumentation لكل API | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| Infinix SMART 9 / XOS checklist | الملف 1 §3؛ الملف 2 §27 | متطلبات checklist موثقة في التقارير | لا يوجد XOS runtime أو dumpsys من X6532 | نعم، بعد توصيل الهاتف | battery/background/permission/OEM tests | `DEVICE_REQUIRED` |
| Premium Dark Med UI | الملف 1 §4–§6؛ الملف 2 §3–§4؛ الملف 3 §3 | Compose UI داكنة، Arabic/English resources، حالة غير محمية صادقة | بعض الأقسام ليست full product flows | نعم، إكمال الشاشات فقط بما له backend | screenshot/navigation/accessibility audit | `CODE_VERIFIED`; `DEVICE_REQUIRED` للمرئيات |
| No placeholder/dead/fake success | الملفات الثلاثة UI rules | Browser disabled قبل route موثق، unavailable messaging، auditor scan | remaining non-vendor markers تحتاج triage | نعم، إزالة/تعليم أي dead UI | source scan + click-path review | `LOCALLY_VERIFIED` جزئيًا |
| Self Diagnostic Engine | الملف 1 §7؛ الملف 2 §5؛ الملف 3 §19 | contracts/failure matrix وcomponent evidence policy موجودة | لا توجد قياسات runtime لكل component | نعم، إضافة diagnostic aggregation إن لم تكن موجودة | unit/integration/device diagnostic | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| Tor real implementation | الملف 1 §8–§9 و§11؛ الملف 2 §7؛ الملف 3 §5 | tor-android/jtorctl dependency، TorService wrapper، config writer، controller | bootstrap/SOCKS/ControlPort runtime غير مثبت | lifecycle retry/recovery عند الحاجة | bootstrap, SOCKS, ControlPort, shutdown | `INTEGRATION_VERIFIED`; `NETWORK_REQUIRED` |
| Tor notification truth | الملف 1 §8؛ الملف 2 §7 | notification corrected to STARTING/bootstrap unverified | no device evidence | none locally unless regression | runtime notification check | `LOCALLY_VERIFIED`; `DEVICE_REQUIRED` |
| Tor SafeLogging/DataDirectory/NEWNYM | الملف 1 §8؛ الملف 2 §7 | SafeLogging، local data directory، ControlPort/NEWNYM paths in writer/controller | actual control command untested | runtime verification only | Tor control test | `CODE_VERIFIED`; `NETWORK_REQUIRED` |
| Automatic Tor recovery/backoff | الملف 1 §11؛ الملف 2 §6؛ الملف 3 §20 | failure policy and failure injection matrix | verified restart/backoff on service not run | implement only if source audit finds missing runtime loop | crash/timeout/network transition | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| obfs4 | الملف 1 §10؛ الملف 2 §8؛ الملف 3 §6 | bridge/transport configuration syntax only; no PT asset in APK | real binary/library, ABI, license, process and bridge test missing | legitimate implementation required; no fake asset | PT process→bridge→bootstrap→SOCKS→route | `NOT IMPLEMENTED / BLOCKED` |
| Snowflake | same sections | no Snowflake runtime asset or WebRTC integration | broker/relay/WebRTC/native lifecycle missing | legitimate supported integration required | real Snowflake bootstrap path | `NOT IMPLEMENTED / BLOCKED` |
| Protected routing architecture | الملف 1 §11 و§13؛ الملف 2 §10؛ الملف 3 §4 و§7 | VpnService/TUN/Hev/JNI structure exists | full packet flow and one-VPN arbitration not proven | architectural hardening may be required | packet flow, loop, protected socket, route | `INTEGRATION_VERIFIED`; `DEVICE_REQUIRED` |
| One authoritative VPN orchestration | الملف 1 §7؛ الملف 2 §10؛ الملف 3 §4 | DarkMedVpnService and WireGuard GoBackend both exist | explicit runtime arbitrator/one-active-interface evidence missing | implement/verify orchestration before Protected | start/stop conflict test | `BLOCKED` |
| TUN/Hev/JNI lifecycle | الملف 1 §12–§13؛ الملف 2 §10؛ الملف 3 §7 | real Hev source/submodules and JNI start/stop/isRunning/stats | no successful device TUN/packet run | device-only validation | TUN creation, fd cleanup, JNI failure | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| IPv4/IPv6 policy | الملف 1 §16؛ الملف 2 §10–§12 | mapped DNS/routes and explicit policy exist | leak proof and device behavior missing | ensure no direct IPv6 fallback | IPv4/IPv6 packet/leak tests | `CODE_VERIFIED`; `NETWORK_REQUIRED` |
| Kill Switch / fail closed | الملف 1 §14؛ الملف 2 §11؛ الملف 3 §10 | security state machine, RouteActivationGuard, failure matrix | Android-wide always-on blocking and runtime crash tests unproven | no `setBlocking` shortcut; complete real route before claim | Tor/VPN/TUN/network/process failure | `POLICY_VERIFIED`; `DEVICE_REQUIRED` |
| DNS security | الملف 1 §15؛ الملف 2 §12؛ الملف 3 §9 | DnsRoutePlanner/config policy | real resolver and leak test not proven | implement resolver or keep protected blocked | DNS path/IPv4/IPv6/Private DNS | `NOT VERIFIED / NETWORK_REQUIRED` |
| WireGuard | الملف 1 §17؛ الملف 2 §13؛ الملف 3 §8 | official tunnel dependency, config parser/controller | profile/handshake/route lifecycle not device-tested | no fake handshake | import, activation, handshake, failure | `CODE_VERIFIED`; `NETWORK_REQUIRED` |
| Proxy chain | الملف 1 §18؛ file 3 §4 | ProxyChainCompiler validation/planning | actual proxy client/forwarder absent | implement real forwarder or disable UI | each hop, auth, timeout, DNS, chaining | `NOT IMPLEMENTED / BLOCKED` |
| Basic protected browser/.onion | الملف 1 §20–§21؛ الملف 2 §9؛ الملف 3 §11 | WebView processes/suffix isolation and protected-route gate | WebView does not itself bind Tor; onion runtime absent | keep browser unavailable without verified route | protected route + safe onion endpoint | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| Browser isolation/cleanup | الملف 1 §20؛ الملف 2 §15؛ الملف 3 §12 | four processes, suffix policy, cleanup broadcast and WebView cleanup | cross-session runtime proof absent | device test and residual verification | cookies/cache/storage/download/process | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| WebRTC/JS/download/file policy | الملف 1 §20؛ الملف 2 §9 | some WebView settings hardened; JS remains policy-dependent | full WebRTC/download/history test absent | review per profile and route | WebView behavior test | `CODE_VERIFIED` partial; `DEVICE_REQUIRED` |
| Profiles | الملف 2 §14؛ الملف 3 §21 | profile/domain pieces exist | full create/validate/save/activate/verify UI flow may be incomplete | implement only backed actions | persistence/restart/invalid config | `PARTIAL / BLOCKED` |
| Panic / LOCKDOWN NOW | الملف 1 §25؛ الملف 2 §16؛ الملف 3 §24 | fail-closed state and clear coordinator pieces exist | device immediacy not measured | verify full stop order | panic action with services/sessions | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| Biometric | الملف 1 §40؛ الملف 2 §?؛ الملف 3 §14 | BIOMETRIC_STRONG, no credential fallback, localized prompts | fingerprint-only/OEM behavior cannot be guaranteed by API | no claim beyond runtime | success/cancel/failure/background/restart/change | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| Keystore/secure storage | الملف 2 §17 و§21؛ الملف 3 §13–§14 | AES/GCM AndroidKeyStore SecureStore and redaction | device residual/backup verification absent | audit secrets and backup | keystore, logs, APK resources, reset | `STATIC_VERIFIED`; `DEVICE_REQUIRED` |
| Clear All Data / Secure Reset | الملف 1 §21؛ الملف 2 §17؛ الملف 3 §15 | confirmation→biometric→stop services/browser→wipe→postcheck coordinator | hardware erasure cannot be claimed; device residual test absent | device validation and report | data categories, keystore alias, restart | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| Assistant local-first / OpenAI optional | الملف 2 §21–§24؛ الملف 3 §16–§17 | LocalAiEngine/AssistantModel, tool registry, consent/redaction, LOCAL COMMANDS | OpenAI settings/API integration not configured in current personal build | optional feature must remain unavailable without key | tool permissions, redaction, API only if intentionally enabled | `LOCALLY_VERIFIED` local mode; remote `NOT CONFIGURED` |
| AI translation / Arabic RTL | الملف 2 §23؛ الملف 3 §17 | Arabic/English resources and RTL intent | selected-text translation flow may be absent | add only with explicit user consent | locale, redaction, translation consent | `CODE_VERIFIED` partial |
| Local code sandbox | الملف 2 §20؛ الملف 3 §18 | no unrestricted shell should be exposed | dedicated sandbox UI/runner may be absent | implement only bounded safe runner | timeout/memory/fs/network limits | `NOT IMPLEMENTED / BLOCKED` if UI absent |
| Security timeline | الملف 1 §39؛ الملف 2 §25 | redaction/action audit concepts exist | complete runtime event timeline may be incomplete | add local redacted event store if missing | event order and secret scan | `PARTIAL` |
| Traffic monitor | الملف 2 §18؛ الملف 3 §23 | Hev stats interface and policy concepts exist | runtime TX/RX/session metrics absent on device | lifecycle-aware monitor | low-power stats and transitions | `CODE_VERIFIED`; `DEVICE_REQUIRED` |
| Battery/thermal/performance modes | الملف 1 §22–§23؛ الملف 2 §26؛ الملف 3 §25 | low-memory-aware architecture partly present | Helio G81 measurements and modes absent | implement/measure without weakening security | meminfo/cpu/battery/thermal/OOM | `NOT VERIFIED / DEVICE_REQUIRED` |
| Defensive Security Test Lab | الملف 2 §19 | unit/failure policies exist | internal runtime lab UI/endpoints absent or partial | implement controlled local tests only | local/localhost/safe endpoints | `PARTIAL / DEVICE_REQUIRED` |
| Crash/observability | الملف 2 §30؛ الملف 3 §29 | logs/evidence files and failure reports exist | device crash persistence/ANR collection absent | local crash report if missing; no browsing data | crash/process death/ANR/OOM | `PARTIAL / DEVICE_REQUIRED` |
| Emulator matrix API 29/31/33/34 | الملف 2 §28؛ الملف 3 §27 | SDK/API 34/35 installed; API 34 AVD attempts made | API 34 system_server/PackageManager failure; others not executed | use KVM/healthy host or device | clean boot/install/launch/instrumentation | `BLOCKED` |
| Firebase Test Lab | الملف 2 §29؛ الملف 3 §28 | no credentials/project evidence available | tests not run | requires user-owned Firebase project/auth | Robo/instrumentation on selected models | `NOT RUN / CONFIGURATION_REQUIRED` |
| Final security auditor | الملف 1 §27؛ الملف 2 §1؛ الملف 3 §13 و§31 | `tools/darkmed_security_audit.sh` rerunnable; debug-signing gate added | warnings obfs4/Snowflake and debug signing remain | legitimate PT/key integration only | source/APK/manifest/native/signature | `LOCALLY_VERIFIED`; warnings honest |
| Final build/QA gate | الملف 1 §28؛ الملف 3 §12 | clean/unit/lint/debug/androidTest compile/release and APK forensic evidence | instrumentation blocked before test execution | rerun after any code change | exact Gradle sequence and audit | `LOCALLY_VERIFIED`; device gate blocked |
| Final reports | الملف 2 §1 and §31; file 3 §13 | prior Master reports and current evidence exist | required new named reports need synchronization | create/update all required reports | report cross-check against artifacts | `IN PROGRESS` |
| Production signing | الملف 1 §12؛ الملف 2 §?؛ الملف 3 §30 | current `Dark Med f.apk` is debug-signed v2 QA artifact | user-owned keystore absent | configure key without chat secrets | apksigner cert/fingerprint/update test | `USER_KEY_REQUIRED` |

## Conflicts and resolution decisions

| Conflict | Resolution |
|---|---|
| الملف الأول يطلب أربع مراحل لا تبدأ التالية دون runtime evidence، بينما الملف الثاني/الثالث يطلبان تعظيم العمل المحلي حتى مع غياب الجهاز | أُبقيت Phase Gate صارمة للميزات، ونُفذت الأعمال المحلية المستقلة مع تصنيف صريح `DEVICE_REQUIRED` بدل الانتقال الوهمي. |
| الملف الثاني يذكر OpenAI API، بينما قرارات المشروع السابقة اختارت تأجيل model والإبقاء على local commands | لم تُضف API key أو اتصالًا افتراضيًا؛ الحالة `LOCAL COMMANDS`، والاتصال البعيد اختياري وغير مهيأ. |
| الملف الأول يطلب حماية كل الجهاز، لكن Android لا يثبت ذلك بمجرد وجود VpnService | لا تُستخدم عبارة system-wide protected؛ يلزم runtime packet/leak evidence وone-VPN arbitration. |
| الملف الأول/الثاني يطلبان obfs4/Snowflake لكنهما يمنعان الملفات الوهمية | بقيت التحذيرات؛ لا binary أو mock غير موثق. يلزم implementation حقيقي وترخيص وABI وbootstrap. |
| release APK مطلوب اسميًا، لكن مفتاح الإنتاج غير متاح | أُنشئ `Dark Med f.apk` كـQA/debug-signed فقط، ويظل production release `USER_KEY_REQUIRED`. |

## Master decision

المشروع قابل للبناء والتدقيق المحلي، لكن **ليس Release Ready**. أي انتقال إلى `PROTECTED`, `TOR_READY`, `CONNECTED`, `ONION_VERIFIED`, `NETWORK_VERIFIED`, `TARGET-DEVICE VERIFIED` يحتاج دليلًا من Emulator صالح أو Infinix SMART 9 وشبكة اختبار حقيقية.
