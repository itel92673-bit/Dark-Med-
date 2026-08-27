# تقرير MobSF / MobSFscan — Dark Med

**آخر فحص مؤكد:** 2026-08-27 06:50:20 UTC  
**الأداة:** MobSFscan `1.0.0`  
**نطاق الإدخال:** `app/src/main` كمصدر Android  
**APK المرتبط:** `deliverables/Dark Med f.apk`  
**APK SHA-256:** `e61ce5e72273f680888a4026a83846fd65b5066cdb7bc1d5ba71be4bcfbd276f`  
**نتيجة الأداة:** `scanner_exit_code=0`, `error_findings=0`, `info_findings=4`  
**حد مهم:** `full_mobsf_apk_analysis=NOT_AVAILABLE`; هذا التقرير هو **MobSFscan source scan** وليس تحليل MobSF Server الكامل للـAPK ولا Dynamic Analysis.

## الخلاصة

الفحص الحالي لم يُصدر findings بدرجة `ERROR`. بقيت أربعة findings بدرجة `INFO`: Certificate Transparency، Root Detection، SafetyNet/Integrity، وTLS Certificate/Public-Key Pinning. لا تعني `INFO` تلقائيًا وجود استغلال مباشر، ولا تعني أن التطبيق آمن بالكامل. في تطبيق خصوصية محلي لا يملك Backend حاليًا، بعض هذه العناصر ليست حلولًا مطلقة ويمكن أن تضر التوافق أو تفرض بنية غير موجودة؛ لذلك يجب علاجها وفق نموذج تهديد Dark Med، لا بإضافة كود شكلي فقط.

تم إغلاق findingين عمليًا عبر تعديل الكود وإعادة البناء والفحص: finding منع screenshots اختفى بعد إضافة `FLAG_SECURE` إلى MainActivity وBrowserSessionActivity، وfinding tapjacking اختفى بعد إضافة `setFilterTouchesWhenObscured(true)` إلى النوافذ نفسها. كما كان finding سابق بدرجة `ERROR` متعلقًا بـStrandHogg/Task Hijacking، وقد عولج قبل ذلك بإضافة `launchMode="singleInstance"` و`taskAffinity=""` إلى MainActivity، مع target SDK حديث، ثم اختفى من الفحص اللاحق.

## Findings الحالية

| ID | MobSF key | Severity | CWE / MASVS | ماذا يعني | وضع Dark Med الحالي | الإجراء |
|---|---|---|---|---|---|---|
| MOBS-001 | `android_certificate_transparency` | `INFO` | CWE-295 / MSTG-NETWORK-4 | لا يفرض التطبيق Certificate Transparency وفق قاعدة MobSF | لم يُفعّل؛ Android يتيح CT من API 36، ولا تتوفر domain endpoints محددة لتطبيق محلي | لا نضيف إعدادًا عامًا قبل تحديد domains؛ نفعّل CT لكل domain HTTPS ثابت عند وجود backend/endpoint موثوق، ونختبر Android 16+ |
| MOBS-002 | `android_root_detection` | `INFO` | CWE-919 / MSTG-RESILIENCE-1 | لا توجد آلية لاكتشاف root أو بيئة غير موثوقة | غير منفذ؛ root detection وحده قابل للتجاوز وليس ضمان anonymity | أضف تحذيرًا/قفلًا متدرجًا فقط إذا كان ضمن threat model، مع عدم تسميته حماية مطلقة؛ اختبر false positives على OEMs |
| MOBS-003 | `android_safetynet` | `INFO` | CWE-353 / MSTG-RESILIENCE-1 | لا توجد attestation لسلامة التطبيق/الجهاز | لا يوجد Backend للتحقق من verdict؛ إضافة SafetyNet الآن ليست إصلاحًا مناسبًا | عند إضافة Backend رسمي استخدم Play Integrity مع server-side verification وnonce/request binding؛ لا تحفظ verdict كحقيقة محلية ولا تضف SafetyNet قديمًا شكليًا |
| MOBS-004 | `android_ssl_pinning` | `INFO` | CWE-295 / MSTG-NETWORK-4 | لا توجد certificate/public-key pinning | لا توجد حاليًا endpoints ثابتة يملكها Dark Med لتثبيت مفاتيحها؛ التطبيق يمنع cleartext على مستوى Manifest | لا تضف pinning عامًّا أو شهادات مخترعة؛ عند تحديد backend ثابت أضف backup pins واختبر rotation، أو استخدم Network Security Config لكل domain مملوك فقط |

## MOBS-001 — Certificate Transparency

### الدليل

MobSFscan أبلغ أن التطبيق لا يفرض CT. المرجع في JSON هو `android_certificate_transparency`، ودرجته `INFO`. توضح وثائق Android أن CT في Network Security Configuration مرتبط بإصدارات Android الحديثة، وأن الدعم لا يتوفر بالطريقة نفسها على الإصدارات الأقدم؛ كما أن CT ليس متاحًا للتطبيقات التي يجب أن تدعم Android 15 وما دونه بالطريقة المذكورة في الوثيقة الرسمية [1].

### هل هو ثغرة مباشرة؟

ليس finding قابلًا للإغلاق بمجرد إضافة XML عام. Dark Med حاليًا local-first ولا يملك domain backend ثابتًا يحتاج CT. كما أن إضافة `certificateTransparency enabled="true"` بلا domains مملوكة وبلا اختبار API 36+ قد لا تعالج المسار الحقيقي وقد تكسر اتصالات مشروعة أو لا تؤثر على Tor/VPN/Proxy transport الذي لا يمر بالضرورة عبر نفس سياسة Android.

### الإصلاح الصحيح

إذا أضيف backend HTTPS ثابت لاحقًا، يجب تعريف domain مملوك صراحةً في `network_security_config.xml`، تفعيل CT فقط لذلك النطاق، ثم اختبار شهادة صالحة وشهادة غير مستوفية للسياسة على Android 16+. لا يجوز استخدام CT كبديل عن Tor أو Kill Switch أو packet-route evidence.

**الحالة:** `INFO / NOT APPLICABLE TO CURRENT LOCAL-ONLY ENDPOINT MODEL`, مع إعادة تقييم عند إضافة backend.

## MOBS-002 — Root Detection

### الدليل

MobSFscan أبلغ `android_root_detection` بدرجة `INFO`. لا توجد في المصدر آلية root/integrity detection. هذا ينسجم مع وضع التطبيق الحالي الذي لا يملك server-side trust decision.

### المخاطر

على جهاز rooted أو بيئة tampered قد تتعرض بيانات التطبيق أو WebView أو مسار VPN للتلاعب. لكن root detection المحلي ليس boundary أمنيًا قويًا؛ يمكن تجاوزه، وقد ينتج false positives على أجهزة OEM أو بيئات اختبار. لذلك لا يجب تحويل نتيجة check محلية إلى claim بأن الجهاز آمن أو أن anonymity مضمونة.

### الإصلاح الممكن

إذا كان threat model يتطلب رفض الأجهزة المعدلة، يمكن إضافة فحص متعدد الإشارات مع قرار متدرج: تحذير، تعطيل الوظائف الحساسة، أو طلب إعادة تحقق. يجب أن يكون القرار fail-closed للوظيفة الحساسة وأن تُختبر حالات false positive وfalse negative على Android 10–16 وOEMs المختلفة. لا يُحفظ أي secret داخل الفحص، ولا يُعرض للمستخدم كضمان مطلق.

**الحالة:** `INFO / OPEN DESIGN DECISION`, وليست ثغرة مثبتة بحد ذاتها.

## MOBS-003 — SafetyNet / Integrity

### الدليل

MobSFscan يستخدم المفتاح `android_safetynet` ويبلغ غياب SafetyNet Attestation بدرجة `INFO`. هذا اسم rule قديم نسبيًا. وثائق Google الحالية تصف Play Integrity باعتباره خدمة لفحص أن user action أو server request صادر من تطبيق أصلي على جهاز Android موثوق، مع verdicts للتطبيق والجهاز [2].

### الإصلاح الصحيح

لا نضيف SafetyNet أو Play Integrity داخل APK المحلي فقط ثم نسجل PASS؛ القيمة الأمنية الحقيقية تحتاج Backend يتحقق من verdict وrequest binding وnonce أو request hash. إذا دخل Dark Med مستقبلًا في نموذج cloud/backend، يكون المسار: Play Console configuration، Play Integrity standard request، إرسال token إلى backend، server-side decode/verify، tiered decision، وعدم تخزين verdict قديم كدليل دائم. يجب أن تبقى الوظائف المحلية قابلة للعمل أو تُقفل صراحة عند فشل attestation وفق threat model.

**الحالة:** `INFO / NOT CONFIGURED BY DESIGN`, ولا يُعد إصلاحًا محليًا مستقلًا دون Backend.

## MOBS-004 — TLS Certificate/Public-Key Pinning

### الدليل

MobSFscan أبلغ `android_ssl_pinning` بدرجة `INFO`. في المقابل، Manifest الحالي يحدد `android:usesCleartextTraffic="false"`، ولا توجد شهادة أو public-key hash مخترعة. هذا يمنع cleartext opt-in العام لكنه لا يساوي certificate pinning.

### هل يجب إضافة pinning الآن؟

ليس بأمان قبل معرفة endpoints ثابتة يملكها المشروع. Android يحذر من أن pinning قد يسبب توقف الاتصالات عند certificate/CA rotation، ويوصي بوجود backup pins إذا استُخدم [3]. كما أن Browser/Tor destinations العامة لا يمكن تثبيت شهاداتها مسبقًا دون كسر التصفح، وpinning ليس بديلًا عن TLS hostname validation.

### الإصلاح الصحيح عند وجود Backend

أضف Network Security Configuration domain-specific فقط للنطاقات التي يملكها Dark Med، استخدم SPKI SHA-256 pins صحيحة من شهادات حقيقية، أضف backup key، ضع rotation procedure، واختبر valid chain وwrong certificate وexpired certificate وkey rotation على API 29–36. لا تستخدم `TrustManager` يقبل أي شهادة، ولا `HostnameVerifier` يعيد true، ولا شهادات test في release.

**الحالة:** `INFO / OPEN UNTIL OWNED FIXED DOMAINS EXIST`.

## Findings المعالجة سابقًا

| Finding السابق | الدليل السابق | الإصلاح | التحقق اللاحق |
|---|---|---|---|
| `android_task_hijacking2` / StrandHogg 2.0 | `mobsfscan_project.json`، severity `ERROR`، Manifest launcher | `launchMode="singleInstance"` و`taskAffinity=""` وtarget SDK حديث | اختفى من JSON بعد الإصلاح؛ build وscan لاحقا نجحا |
| `android_prevent_screenshot` | source scan سابق، severity `INFO` | `FLAG_SECURE` في MainActivity وكل BrowserSessionActivity | اختفى من scan بعد build/fresh scan |
| `android_tapjacking` | source scan سابق، severity `INFO` | `setFilterTouchesWhenObscured(true)` في MainActivity وكل BrowserSessionActivity | اختفى من scan بعد تحويل setter إلى استدعاء صريح وإعادة build/scan |

## حدود التقرير

هذا الفحص لا يثبت عدم وجود hardcoded secrets في كل حالات runtime، ولا يثبت DNS/IP leak absence، ولا يثبت VPN/TUN/Tor/Kill Switch، ولا يثبت WebView `.onion` route أو session isolation على جهاز حقيقي. لا يثبت أيضًا أن APK Debug-signed مناسب للإنتاج؛ production audit ما زال يرفض الشهادة الحالية.

MobSFscan الحالي source-oriented ويعطي rule findings وفق الملفات التي يقرأها. لتشغيل **Full MobSF APK analysis** يلزم MobSF Server/CLI الرسمي وتحليل APK فعليًا، ثم Dynamic Analyzer على جهاز/Emulator صالح. لم يتم ادعاء أن source MobSFscan بديل كامل لذلك.

## قرار الإغلاق

| الفئة | النتيجة |
|---|---|
| MobSFscan ERROR findings | `0` في الفحص الحالي |
| MobSFscan INFO findings | `4`، كلها موثقة أعلاه |
| Screenshot/tapjacking findings | أُغلقت في الفحص الحالي بعد hardening وإعادة build |
| StrandHogg/task hijacking | أُغلق في الفحص اللاحق بعد Manifest fix |
| Full MobSF APK/server analysis | `NOT_AVAILABLE` |
| Dynamic/device verification | `NOT TESTED / DEVICE_REQUIRED` |
| Production release | `BLOCKED` بسبب Debug signing وبقية runtime requirements |

## المراجع الرسمية

[1]: https://developer.android.com/privacy-and-security/security-config "Android Network Security Configuration"  
[2]: https://developer.android.com/google/play/integrity/overview "Google Play Integrity API overview"  
[3]: https://developer.android.com/privacy-and-security/security-ssl "Android security with network protocols and certificate pinning"  
[4]: https://github.com/MobSF/mobsfscan "MobSFscan source scanner"  

## Regression إضافي: Production signing gate

خلال تحديث الـartifact ظهر خلل حقيقي في security auditor: عند غياب `apksigner` من PATH كان نمط `DARKMED_REQUIRE_PRODUCTION_SIGNING=1` يعيد exit code صفر مع تحذير فقط. تم إصلاح ذلك بإضافة اكتشاف تلقائي لمسارات Android SDK وبجعل غياب `apksigner` فشلًا صريحًا في production-required mode. أُعيد الاختبار على الـAPK الحالي: QA audit `exit=0` مع تحذيرات Debug signing/obfs4/Snowflake، بينما production-required audit `exit=1` بسبب Debug certificate، وهو الحكم الصحيح.

كما أُعيد تشغيل local CI بعد إصلاحات UI ونجحت gates المحلية، ثم وُحّد artifact مع ناتج CI الأخير. الفحص النهائي المرتبط بالـAPK الحالي هو الموجود في `reports/ui_audit/20260827_v17/final_artifact/mobsfscan/` ويحتوي `error_findings=0` و`info_findings=4`، مع SHA `e61ce5e72273f680888a4026a83846fd65b5066cdb7bc1d5ba71be4bcfbd276f`.
