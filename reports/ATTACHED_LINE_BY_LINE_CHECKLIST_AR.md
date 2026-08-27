# Attached File — Line-by-Line Execution Checklist

> كل سطر أدناه محفوظ كما ورد في الملف المرفق. `PASS` يصف تحقق البند المحدد فقط؛ ولا يرفع أي runtime blocker إلى نجاح.

| Line | Instruction | Status | Current classification/evidence | Evidence |
|---:|---|---|---|---|
| 1 | إي، تقصد تحب Prompt شامل لـManus: موش فقط تسجيل الدخول للـemulator، بل يستعمل كل الحلول المتاحة ويكمل اختبار التطبيق بكل ما يمكنه. | `PARTIAL` | متطلب منهجي؛ تم تحويله إلى checklist | هذا الملف + خطة التنفيذ الحالية |
| 2 |  | `PARTIAL` | متطلب منهجي؛ تم تحويله إلى checklist | هذا الملف + خطة التنفيذ الحالية |
| 3 | أريدك الآن أن تتولى إكمال اختبار مشروع Dark Med بالكامل، ولا تكتفِ بالـstatic/unit/CI tests التي تم إنجازها. | `PARTIAL` | متطلب منهجي؛ تم تحويله إلى checklist | هذا الملف + خطة التنفيذ الحالية |
| 4 |  | `PARTIAL` | متطلب منهجي؛ تم تحويله إلى checklist | هذا الملف + خطة التنفيذ الحالية |
| 5 | استخدم كل قدرات Manus المتاحة لك، وكل بيئة مجانية أو مفتوحة المصدر تستطيع الوصول إليها، بالترتيب، واعتبر هدفك الأساسي هو الوصول إلى أقصى إثبات عملي ممكن قبل إصدار الحكم النهائي. | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 6 |  | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 7 | 1. Android Emulator / Runtime | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 8 |  | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 9 | ابحث أولًا عن إمكانية تشغيل Android Emulator حقيقي في بيئة Manus أو عبر GitHub Actions/CI. | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 10 |  | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 11 | جرّب بالترتيب: | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 12 |  | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 13 | - Android Emulator الرسمي في بيئة Linux/CI إذا كان متاحًا. | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 14 | - Android-x86 أو أي حل مفتوح المصدر مناسب. | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 15 | - Waydroid إذا كانت بيئة Linux تدعمه. | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 16 | - أي Android emulator مجاني آخر متاح فعليًا في البيئة. | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 17 | - إذا كانت خدمة emulator سحابية تتطلب تسجيل دخول، حاول استخدام حسابي الحالي بالطريقة الرسمية بدل إنشاء حساب جديد. | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 18 |  | `BLOCKED` | بيئة Android runtime/GitHub remote غير منفذة؛ KVM غير متاح محليًا | تقارير emulator وGITHUB_ACTIONS_RUNTIME_QA_AR.md |
| 19 | لا تنشئ حسابات جديدة ولا تطلب مني إنشاء حساب إلا إذا كان ذلك ضروريًا فعلًا. | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 20 |  | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 21 | 2. ADB | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 22 |  | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 23 | بعد توفير Emulator: | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 24 |  | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 25 | - تأكد من أن ADB يعمل. | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 26 | - تأكد من أن الجهاز يظهر كـ "device" وليس "offline" أو "unauthorized". | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 27 | - ثبّت APK الاختبار. | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 28 | - ثبّت APK التطبيق. | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 29 | - اجمع logcat بالكامل أثناء الاختبارات. | `BLOCKED` | لا يوجد جهاز ADB صحي متصل بعد | reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt |
| 30 | - احفظ كل logs وscreenshots وtest artifacts. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 31 |  | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 32 | 3. اختبارات التطبيق الأساسية | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 33 |  | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 34 | اختبر فعليًا: | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 35 |  | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 36 | - تثبيت التطبيق. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 37 | - أول تشغيل. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 38 | - permissions. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 39 | - إنشاء جلسة. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 40 | - UI workflows. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 41 | - start/stop VPN. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 42 | - إعادة تشغيل التطبيق. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 43 | - إعادة تشغيل الجهاز/Emulator إن أمكن. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 44 | - التعامل مع انقطاع الشبكة. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 45 | - التعامل مع VPN failure. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 46 | - عدم حدوث crash. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 47 | - lifecycle/background/foreground. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 48 | - persistence بعد إعادة التشغيل. | `NOT TESTED` | لا توجد نافذة runtime post-change صالحة | Evidence Trace Matrix E-010/E-012 |
| 49 | - أي workflow موجود في المشروع ولم يتم اختباره runtime. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 50 |  | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 51 | 4. VPN / TUN / Hev | `PASS` | تم توثيق أن contract وحده غير كافٍ | الملف المرفق + E-015 |
| 52 |  | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 53 | لا تعتبر "vpn_protect_contract PASS" دليلًا كافيًا. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 54 |  | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 55 | اختبر runtime فعليًا: | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 56 |  | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 57 | - إنشاء TUN interface. | `PARTIAL` | تم تنفيذ static/native bridge وbuild، لا runtime proof | vpn_protect_contract وfull CI |
| 58 | - تشغيل VPN service. | `PARTIAL` | تم تنفيذ static/native bridge وbuild، لا runtime proof | vpn_protect_contract وfull CI |
| 59 | - إنشاء Hev socket. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 60 | - "VpnService.protect(int)". | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 61 | - التأكد أن الـprotected socket يعمل فعليًا. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 62 | - تشغيل Tun2Socks. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 63 | - التأكد من أن traffic يدخل TUN ويخرج من المسار المتوقع. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 64 | - اختبار TCP. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 65 | - اختبار UDP إن كان مدعومًا. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 66 | - اختبار IPv4. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 67 | - اختبار IPv6. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 68 | - اختبار DNS. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 69 | - اختبار فشل socket protection. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 70 | - اختبار فشل إنشاء النفق. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 71 | - اختبار recovery/reconnect. | `BLOCKED` | لا يوجد جهاز أو مسار شبكة runtime صالح | E-011 وE-015 |
| 72 |  | `PASS` | قاعدة عدم تحويل BLOCKED إلى PASS مطبقة | Evidence Matrix وCI fail-closed contracts |
| 73 | إذا أمكن، استخدم packet capture أو network instrumentation لإثبات مسار الحزم بدل الاعتماد على logs فقط. | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 74 |  | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 75 | 5. Tor / Proxy | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 76 |  | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 77 | إذا كان المشروع يستخدم Tor أو proxy: | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 78 |  | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 79 | - اختبر Tor bootstrap فعليًا. | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 80 | - تحقق من أن الـproxy يستجيب. | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 81 | - تحقق من أن traffic يمر عبر المسار الصحيح. | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 82 | - اختبر فشل Tor. | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 83 | - اختبر reconnect. | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 84 | - اختبر proxy chaining إذا كان موجودًا. | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 85 | - تأكد من عدم وجود fallback غير محمي إلى الاتصال المباشر. | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 86 |  | `NOT TESTED` | Tor/proxy runtime غير منفذ، وبعض الوظائف غير موصولة | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 87 | لا تسجل أي PASS إلا إذا كان هناك دليل runtime حقيقي. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 88 |  | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 89 | 6. DNS / IP / Leak Protection | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 90 |  | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 91 | اختبر: | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 92 |  | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 93 | - DNS leak. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 94 | - IPv4 leak. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 95 | - IPv6 leak. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 96 | - DNS عند تشغيل VPN. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 97 | - DNS عند توقف VPN. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 98 | - IPv6 عند عدم دعمه. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 99 | - تسرب traffic أثناء reconnect. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 100 | - تسرب traffic عند crash/failure. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 101 | - تسرب traffic عند إيقاف proxy/Tor. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 102 | - أي fallback مباشر إلى الإنترنت. | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 103 |  | `BLOCKED` | لا توجد نافذة network runtime لجمع leak evidence | E-011 |
| 104 | استخدم أكثر من طريقة للتحقق إذا كانت البيئة تسمح بذلك، واحفظ النتائج. | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 105 |  | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 106 | 7. Kill Switch | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 107 |  | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 108 | اختبر سيناريوهات: | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 109 |  | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 110 | 1. VPN يعمل. | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 111 | 2. اقطع proxy/Tor. | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 112 | 3. اقطع النفق. | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 113 | 4. أعد الشبكة. | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 114 | 5. أوقف الخدمة بالقوة. | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 115 | 6. اقتل التطبيق. | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 116 | 7. أعد تشغيل التطبيق. | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 117 |  | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 118 | تحقق في كل حالة هل يستطيع أي traffic الخروج مباشرة أم لا. | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 119 |  | `BLOCKED` | Kill Switch غير قابل للإثبات دون جهاز/شبكة ومسار فعلي | DARKMED_QA_FINAL_DELIVERY_AR.md |
| 120 | إذا كان Kill Switch غير موجود أو غير قابل للإثبات، سجله BLOCKER ولا تعتبر الاختبار ناجحًا. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 121 |  | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 122 | 8. Browser / Session Isolation | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 123 |  | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 124 | إذا كان المشروع يحتوي browser/session isolation: | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 125 |  | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 126 | - افتح جلسة. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 127 | - اختبر cookies. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 128 | - اختبر storage. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 129 | - اختبر session persistence. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 130 | - أنشئ جلسة ثانية. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 131 | - تأكد من عدم انتقال بيانات الجلسة الأولى للثانية. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 132 | - اختبر logout/login. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 133 | - اختبر إعادة تشغيل التطبيق. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 134 | - اختبر حذف الجلسة. | `PARTIAL` | WebView policy/hardening وsession primitives موجودة؛ runtime isolation غير مختبر | WebViewSecurityPolicyTest وE-011 |
| 135 | - اختبر أي isolation/security boundary موجودة في المشروع. | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 136 |  | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 137 | 9. Security | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 138 |  | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 139 | أعد تشغيل كل الاختبارات الأمنية المتاحة: | `PASS` | الفحص الساكن المحلي نفذ ضمن نطاقه | MobSFscan/security audit APK evidence |
| 140 |  | `PASS` | الفحص الساكن المحلي نفذ ضمن نطاقه | MobSFscan/security audit APK evidence |
| 141 | - MobSF. | `PASS` | الفحص الساكن المحلي نفذ ضمن نطاقه | MobSFscan/security audit APK evidence |
| 142 | - APK inspection. | `PASS` | الفحص الساكن المحلي نفذ ضمن نطاقه | MobSFscan/security audit APK evidence |
| 143 | - manifest. | `PASS` | الفحص الساكن المحلي نفذ ضمن نطاقه | MobSFscan/security audit APK evidence |
| 144 | - exported components. | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 145 | - permissions. | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 146 | - native libraries. | `BLOCKED` | الشهادة Debug؛ production signing غير متوفر | production-required audit |
| 147 | - secrets/API keys داخل APK. | `PASS` | الفحص الساكن المحلي نفذ ضمن نطاقه | MobSFscan/security audit APK evidence |
| 148 | - certificate/signing. | `PASS` | الفحص الساكن المحلي نفذ ضمن نطاقه | MobSFscan/security audit APK evidence |
| 149 | - network security configuration. | `PASS` | الفحص الساكن المحلي نفذ ضمن نطاقه | MobSFscan/security audit APK evidence |
| 150 | - cleartext traffic. | `PASS` | الفحص الساكن المحلي نفذ ضمن نطاقه | MobSFscan/security audit APK evidence |
| 151 | - WebView configuration إن وجدت. | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 152 | - backup/debug flags. | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 153 | - storage/database security. | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 154 |  | `PARTIAL` | فحص محدود أو finding يحتاج قرار/بيئة إضافية | MOBSF_FULL_APK_ANALYSIS_CURRENT_AR.md |
| 155 | لا تعتبر Debug signing مشكلة أمنية في التطبيق نفسه فقط؛ سجله أيضًا كـrelease blocker. | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 156 |  | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 157 | 10. CI / Cloud Execution | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 158 |  | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 159 | نفّذ cloud execution إذا كان workflow موجودًا أو يمكن تفعيله. | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 160 |  | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 161 | لا تكتفِ بكتابة "cloud_execution=NOT_RUN". | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 162 |  | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 163 | إذا كان GitHub Actions متاحًا: | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 164 |  | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 165 | - أنشئ/استخدم workflow مناسب. | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 166 | - شغّل Android Emulator. | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 167 | - شغّل instrumentation tests. | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 168 | - ثبّت APK. | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 169 | - نفّذ runtime tests. | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 170 | - خزّن APK/test reports/logcat/screenshots/packet captures كـartifacts. | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 171 |  | `BLOCKED` | workflow/contract موجود محليًا؛ remote GitHub/cloud execution لم يحدث | github_actions_workflow PASS محليًا وcloud_execution=NOT_RUN |
| 172 | إذا فشل التشغيل بسبب صلاحيات أو قيود البيئة، وثّق السبب الدقيق. | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 173 |  | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 174 | 11. إصلاح تلقائي | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 175 |  | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 176 | إذا اكتشفت bug حقيقي أثناء الاختبارات: | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 177 |  | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 178 | - حدّد السبب. | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 179 | - أصلحه في الكود. | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 180 | - أضف regression test. | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 181 | - أعد build. | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 182 | - أعد full CI. | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 183 | - أعد الاختبار الذي فشل. | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 184 | - ثم أعد الاختبارات المرتبطة به. | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 185 |  | `PASS` | بروتوكول الإصلاح طُبق على عيوب محلية مع regression وfull CI | BUG_FIX_DATABASE_AR.md وProtocol 7 evidence |
| 186 | لا تقم بإخفاء الاختبار أو تعطيله فقط للحصول على PASS. | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 187 |  | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 188 | 12. Production Signing | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 189 |  | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 190 | تحقق من حالة signing. | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 191 |  | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 192 | إذا كان APK الحالي Debug-signed: | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 193 |  | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 194 | - لا تعتبره Release. | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 195 | - لا تنشئ production keystore عشوائيًا باسمي أو تضع secret داخل repository. | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 196 | - جهّز فقط ما يمكن تجهيزه بأمان. | `BLOCKED` | Production keystore مملوك للمستخدم غير متوفر، ولا يجوز إنشاؤه عشوائيًا | production-required audit |
| 197 | - اطلب مني التدخل فقط عندما يصبح إنشاء/تخزين Production keystore ضروريًا. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 198 |  | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 199 | 13. Final Evidence | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 200 |  | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 201 | في النهاية أنشئ تقريرًا واضحًا يحتوي على: | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 202 |  | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 203 | - Static tests. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 204 | - Unit tests. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 205 | - CI. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 206 | - MobSF. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 207 | - Emulator runtime. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 208 | - ADB status. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 209 | - VPN runtime. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 210 | - TUN. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 211 | - Hev. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 212 | - Tor. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 213 | - Proxy. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 214 | - DNS. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 215 | - IPv4. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 216 | - IPv6. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 217 | - Leak tests. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 218 | - Kill Switch. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 219 | - Proxy chaining. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 220 | - Browser/session isolation. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 221 | - Security. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 222 | - Signing. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 223 | - Cloud execution. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 224 |  | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 225 | لكل اختبار استخدم: | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 226 |  | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 227 | "PASS" = يوجد دليل فعلي. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 228 | "FAIL" = الاختبار نُفذ وفشل. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 229 | "BLOCKED" = لم يمكن تنفيذه بسبب قيد بيئي. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 230 | "NOT TESTED" = لم يتم تشغيله. | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 231 |  | `PARTIAL` | التقرير والمصفوفة موجودان؛ runtime sections تبقى blocked/not tested | DARKMED_QA_FINAL_DELIVERY_AR.md وEVIDENCE_TRACE_MATRIX.md |
| 232 | لا تحول "BLOCKED" إلى "PASS". | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 233 |  | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 234 | 14. Release Decision | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 235 |  | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 236 | أعطني في النهاية قرارًا صريحًا: | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 237 |  | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 238 | "GO" | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 239 | أو | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 240 | "NO-GO" | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 241 |  | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 242 | وإذا كان "NO-GO"، أنشئ قائمة BLOCKERS مرتبة حسب الخطورة، مع: | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 243 |  | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 244 | - السبب. | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 245 | - الدليل. | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 246 | - ما الذي تم اختباره. | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 247 | - ما الذي ما زال يحتاج جهازًا حقيقيًا أو صلاحيات إضافية. | `FAIL` | Release Decision الصحيح حاليًا NO-GO بسبب blockers الحرجة | RELEASE_READINESS_FINAL.md والتقرير النهائي |
| 248 | - الخطوة المطلوبة لإغلاق الـblocker. | `PASS` | قاعدة عدم تحويل BLOCKED إلى PASS مطبقة | Evidence Matrix وCI fail-closed contracts |
| 249 |  | `PASS` | قاعدة عدم تحويل BLOCKED إلى PASS مطبقة | Evidence Matrix وCI fail-closed contracts |
| 250 | مهم جدًا: | `PASS` | قاعدة عدم تحويل BLOCKED إلى PASS مطبقة | Evidence Matrix وCI fail-closed contracts |
| 251 |  | `PASS` | قاعدة عدم تحويل BLOCKED إلى PASS مطبقة | Evidence Matrix وCI fail-closed contracts |
| 252 | لا تقل إن التطبيق "مكتمل الحماية" لمجرد نجاح CI أو MobSF أو unit tests. | `PASS` | قاعدة عدم تحويل BLOCKED إلى PASS مطبقة | Evidence Matrix وCI fail-closed contracts |
| 253 |  | `PASS` | قاعدة عدم تحويل BLOCKED إلى PASS مطبقة | Evidence Matrix وCI fail-closed contracts |
| 254 | الهدف هو إثبات السلوك الحقيقي end-to-end بأكبر قدر ممكن باستخدام كل موارد Manus والـCI والـAndroid Emulator المتاحة، ثم ترك الاختبارات التي لا يمكن إثباتها فعليًا كـBLOCKED بدل التخمين. | `PASS` | قاعدة عدم تحويل BLOCKED إلى PASS مطبقة | Evidence Matrix وCI fail-closed contracts |

## Current verdict

The checklist is applied to the extent supported by the local environment. The artifact remains `QA ONLY`; full device, network, cloud, and production-signing evidence are not available, so the release decision remains `NO-GO / RELEASE BLOCKED`.
