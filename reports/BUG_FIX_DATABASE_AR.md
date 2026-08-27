# Dark Med — Bug Fix Database وفق بروتوكول 7.0–7.8

هذا السجل يطبق التسلسل الإجباري: REPRODUCE → ISOLATE → CLASSIFY → ROOT-CAUSE FIX → REGRESSION → FULL MATRIX → DOCUMENTATION. لا يُصنف أي implementation gap كـbug مغلق، ولا يتحول أي اختبار static إلى runtime PASS.

| ID | النوع | إعادة الإنتاج والعزل | السبب الجذري | الإصلاح الجذري | اختبار الارتداد | المصفوفة المعاد تشغيلها | الحالة |
|---|---|---|---|---|---|---|---|
| BUG-001 | Security logic / false PASS | device smoke كان يقرأ exit code فقط رغم system ANR وlaunch timeout؛ عُزل في collector ونتيجة AVD API34 | collector لم يكن يتحقق من system dialog وtimeout وANR | رفض PASS عند ظهور system dialog أو app ANR أو launch timeout | device-smoke collector regression وlocal CI | local static gates؛ runtime device matrix بقيت DEVICE_REQUIRED | `FIXED LOCALLY / RUNTIME BLOCKED` |
| BUG-002 | Logic / cost safety | Cost Guard قبل خطأً waiting أو provider mismatch | approval logic معكوس ومطابقة provider غير مكتملة | fail-closed validation للمدة والحقول والـplaceholder والحالة والمزود وملف الموافقة | `test_cost_guard.py` | local CI وadapter preflight؛ cloud execution لم يُنفذ | `FIXED LOCALLY` |
| BUG-003 | State-machine logic | Task Orchestrator كان يقفز RUNNING→RETRY | transition لا يسجل FAILURE قبل retry | المسار أصبح RUNNING→FAILURE→RETRY أو FINAL_FAILURE | `test_task_orchestrator.py` | local CI | `FIXED LOCALLY` |
| BUG-004 | Release security audit | production-required كان قد يمر عند غياب apksigner من PATH | auditor اعتمد على PATH فقط وحوّل غياب الأداة إلى warning | اكتشاف SDK paths وfail-closed في production mode | production audit على APK الحالي | local CI/security audit | `FIXED LOCALLY`; APK نفسه ما زال Debug-signed |
| BUG-005 | Android task hijacking | MobSFscan أبلغ StrandHogg/Task Hijacking | launcher policy لم تكن مقيدة بما يكفي | `singleInstance` و`taskAffinity=""` ثم fresh build/scan | MobSFscan follow-up | static scan | `FIXED LOCALLY` |
| BUG-006 | UI security | MobSFscan أبلغ screenshot/tapjacking | النوافذ لم تكن تفرض FLAG_SECURE وobscured-touch filtering | إضافة الضوابط إلى MainActivity وBrowserSessionActivity | UI/security contracts وfresh scan | local CI | `FIXED LOCALLY`; runtime overlay يحتاج جهازًا |
| BUG-007 | Performance / lifecycle | Clear All Data كان ينفذ wipe synchronous على main thread | DataWiper استُدعي مباشرة من callback UI | reducer قابل للاختبار و`withContext(Dispatchers.IO)` مع lifecycle state | `ClearAllDataFlowTest` وUI/security contract | local CI؛ low-storage/real-device runtime مطلوب | `FIXED LOCALLY / RUNTIME PARTIAL` |
| BUG-008 | Data integrity | DataWiper لم يتحقق من commit ونتيجة حذف كل child، ونطاق storage لم يكن كاملًا | نجاح wipe كان يُستنتج من فحص محدود | التحقق من commit وdelete outcomes وإضافة code/external/device-protected app storage ضمن النطاق | DataWiper tests/contracts | local CI؛ residual scan/reboot يحتاج جهازًا | `FIXED LOCALLY / RUNTIME BLOCKED` |
| BUG-009 | WebView policy testability | unit JVM فشل عند استخدام Android Uri runtime | policy لم تكن مفصولة عن Android runtime | String policy pure path مع Android overload، وتطبيق allowlist فعلي | `WebViewSecurityPolicyTest` | local CI؛ WebView runtime يحتاج جهازًا | `FIXED LOCALLY / RUNTIME BLOCKED` |
| BUG-010 | CI harness | full CI توقف لأن output directory لم يُنشأ قبل redirection | wrapper اعتمد على مجلد غير موجود | إنشاء output dir قبل بدء CI وحفظ wrapper status | full CI إعادة التشغيل | local CI | `FIXED LOCALLY` |
| BUG-011 | CI contract regression | UI contract استخدم assertions قديمة بعد reducer refactor | test stale لا يطابق source الحالي | تحديث assertions إلى reducer الحالي مع إبقاء شروط الأمان | `test_ui_security_contracts.py` | local CI | `FIXED LOCALLY` |
| BUG-012 | UI functionality | Profiles كانت cards عرضية بلا CRUD | لا يوجد local profile model/store | إضافة `ProfileCatalog` و`ProfileStore` وcreate/rename/duplicate/delete dialogs مع validation | `ProfileCatalogTest` وUI/security contract | local CI؛ UI tap/persistence device evidence غير منفذ | `FIXED LOCALLY / RUNTIME PARTIAL` |
| BUG-013 | CI evidence logic | workflow contract فشل بعد تغيير pcap status إلى قيمة ديناميكية | assertion بحث عن literal `tcpdump=NOT_AVAILABLE` غير موجود في source | assertion أصبح يثبت `PCAP_MODE=NOT_AVAILABLE` وstatus file وtraffic artifact وترتيب بدء/إيقاف capture | `test_github_actions_workflow.py`؛ فشل أولًا ثم PASS | full CI Protocol 7؛ GitHub matrix نفسها لم تُشغّل | `FIXED LOCALLY / REMOTE RUNTIME NOT RUN` |
| BUG-014 | VPN/TUN integration | build كشف include chain ناقصًا عند محاولة ربط protect callback | JNI اعتمد على Hev public headers دون ترتيب dependencies، ثم احتاج setter إلى export/include path | أضيف callback native يمرر `VpnService.protect(int)` لكل socket ويغلق socket عند الرفض، مع JNI GlobalRef lifecycle وfail-closed start | `test_vpn_protect_contract.py` و`assembleRelease`؛ أول build فشل ثم أُصلح ونجح | full CI `20260827T_final_vpn_protect`؛ runtime matrix لم تُشغّل | `FIXED LOCALLY / RUNTIME UNVERIFIED` |
| BUG-015 | Test/build regression | الاختبار الجديد لـTun2SocksConfigRenderer فشل compile بسبب imports `kotlin.test` غير الموجودة | المشروع يستخدم JUnit وليس Kotlin test APIs في source set الحالي | تحويل الاختبار إلى JUnit مع assertions مكافئة دون حذف حالات التحقق | `Tun2SocksConfigRendererTest`؛ فشل أولًا ثم PASS في `tun_config_hardening_test.status` | full CI `20260827T_final_tun_config`؛ remote matrix لم تُشغّل | `FIXED LOCALLY` |

## ليست bugs مغلقة

| ID | التصنيف | السبب | الحالة |
|---|---|---|---|
| GAP-001 | Core implementation gap | لا يوجد route engine موثق ومثبت يمرر كل الجهاز عبر VPN/TUN→Tor/Proxy | `NOT IMPLEMENTED / BLOCKED` |
| GAP-002 | Security logic blocker | DNS/IPv4/IPv6 leak وKill Switch لم تُقاس على جهاز/شبكة فعلية | `NOT TESTED / DEVICE_REQUIRED / NETWORK_REQUIRED` |
| GAP-003 | Transport implementation gap | لا توجد obfs4/Snowflake binaries/integration/bootstrap evidence | `NOT IMPLEMENTED` |
| GAP-004 | Proxy implementation gap | لا يوجد proxy forwarder/chaining فعلي مع hop order/auth/failure recovery مثبت | `NOT IMPLEMENTED` |
| GAP-005 | Browser implementation gap | لا توجد address bar/navigation/route binding مثبتة | `NOT IMPLEMENTED / BLOCKED` |
| GAP-006 | Runtime isolation blocker | process/suffix primitive موجود لكن cookies/cache/storage cross-session لم تُختبر فعليًا | `NOT TESTED / DEVICE_REQUIRED` |
| GAP-007 | Environment blocker | لا يوجد `/dev/kvm` محليًا ولا ADB device؛ GitHub workflow أُضيف لكنه لم يُشغل من repository متصل | `BLOCKED` |
| GAP-008 | Release blocker | artifact الحالي Android Debug certificate | `FAIL / RELEASE BLOCKED` |
| GAP-009 | Security analysis blocker | Full MobSF APK Server/Dynamic Analysis غير متاح محليًا | `NOT_AVAILABLE / BLOCKED` |

## قاعدة الإغلاق

العطل لا يعود إلى `FIXED/CLOSED` في قرار Release إلا بعد إصلاح جذري واختبار ارتداد أخضر ومصفوفة كاملة خضراء وتوثيق مكتمل. العناصر ذات `RUNTIME BLOCKED` أو `NOT IMPLEMENTED` لا تُغلق بواسطة local CI وحده.
