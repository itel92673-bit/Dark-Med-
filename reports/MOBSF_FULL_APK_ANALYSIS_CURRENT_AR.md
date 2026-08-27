# Dark Med — تقرير MobSF/APK الحالي

## القرار التنفيذي

**الحالة: PARTIAL / RELEASE BLOCKED.** تم تنفيذ MobSFscan الساكن على مصدر التطبيق، كما تم تنفيذ فحص APK مستقل للهوية والـmanifest والتوقيع والمكتبات الأصلية. هذا الدليل لا يساوي Full MobSF APK Server Analysis ولا Dynamic Analysis. لا يوجد في البيئة الحالية Docker أو أمر `mobsf` أو جهاز ADB متصل، لذلك لا يجوز تحويل النتيجة إلى PASS شامل أو اعتبارها إثباتًا لسلوك VPN/TUN/Tor/Proxy/DNS أو عزل WebView.

## artifact المفحوص

| الحقل | القيمة المثبتة |
|---|---|
| المسار | `deliverables/Dark Med f.apk` |
| SHA-256 | `e61ce5e72273f680888a4026a83846fd65b5066cdb7bc1d5ba71be4bcfbd276f` |
| package | `com.darkmed.app` |
| version | `0.1.0` / `versionCode 1` |
| compile/target/min | `37` / `35` / `29` |
| certificate | Android Debug |
| certificate SHA-256 | `a4b04f78c7aefba840f4775336fda984720f1811830f907f05d8516143843d1f` |
| APK signature verification | PASS محليًا للتوقيع الموجود |
| production signing | FAIL: Debug certificate |

المصدر المباشر لهذه القيم هو `reports/ui_audit/20260827_v17/final_artifact/identity.txt` و`hashes.txt` و`aapt_badging.txt` و`apksigner_certs.txt`.

## MobSFscan الساكن

| النتيجة | العدد | التفسير |
|---|---:|---|
| ERROR | 0 | لا توجد قاعدة ERROR في نتيجة MobSFscan الحالية |
| INFO | 4 | Certificate Transparency، Root Detection، SafetyNet، SSL Pinning |

النتيجة الخام موجودة في `reports/ui_audit/20260827_v17/final_artifact/mobsfscan/mobsfscan_source.json`. هذه القواعد ليست كلها إصلاحات صحيحة لهذا التطبيق المحلي في مرحلته الحالية: Certificate Transparency وpinning يتطلبان نطاقات HTTPS مملوكة واتصالًا شبكيًا محددًا؛ SafetyNet قديم معماريًا ولا يمكن استبداله بادعاء integrity بلا backend؛ وroot detection قرار threat-model لا ينبغي إضافته شكليًا لإخفاء INFO. لذلك بقيت الأربعة موثقة كـINFO/قرار معماري، وليس كإخفاء أو fake remediation.

## إصلاحات أمنية محلية مثبتة

تم تثبيت `FLAG_SECURE` وobscured-touch filtering في MainActivity وBrowserSessionActivity. تم جعل launcher `singleInstance` مع task affinity فارغة، وتعطيل backup وcleartext traffic، وتقييد WebView إلى HTTP/HTTPS، وتعطيل third-party cookies وfile/content access وuniversal file access وmixed content، وتفعيل Safe Browsing وتعطيل multiple windows. كما أضيفت سياسة URL قابلة للاختبار واختبارات reducer لدورة Clear All Data.

تظل هذه النتائج **static/contract evidence**. لا تثبت أن `setFilterTouchesWhenObscured` يحمي كل حدث Compose على جهاز OEM محدد، ولا تثبت أن Safe Browsing أو WebView storage يعملان كما هو متوقع أثناء runtime دون instrumentation على جهاز.

## APK inventory

يحتوي APK على مكتبات native لكل من `arm64-v8a` و`armeabi-v7a` و`x86` و`x86_64` تشمل `libtor.so` و`libwg-go.so` و`libwg.so` و`libwg-quick.so` و`libhev-socks5-tunnel.so` و`libdarkmed-tun2socks-jni.so`. وجود المكتبة يثبت packaging فقط، ولا يثبت process bootstrap أو handshake أو route أو leak protection.

لم توجد ملفات APK تحمل اسم obfs4 أو snowflake، ولم يثبت inventory وجود binary مستقل أو ABI/process/bootstrap لأي منهما. وجود كلمات مشابهة داخل DEX أو native strings ليس دليل integration، لذلك يبقى obfs4 وSnowflake **NOT IMPLEMENTED** حسب blocker matrix.

## Full MobSF APK Server Analysis

**الحالة: BLOCKED / NOT AVAILABLE.** فحص capability المحلي أظهر أن `docker` و`mobsf` غير متوفرين، بينما `mobsfscan` فقط متوفر. لم يتم تشغيل MobSF server بديلًا أو الادعاء بأن MobSFscan هو Full APK Analyzer. المطلوب لإغلاق هذا البند هو بيئة MobSF server موثوقة تسمح برفع نفس SHA وتحفظ report identifier وmanifest/permission/component findings قابلة لإعادة التحقق.

## Dynamic Analysis

**الحالة: BLOCKED / DEVICE_REQUIRED.** `adb devices -l` لم يعرض أي جهاز. البيئة لا تحتوي `/dev/kvm` لمحاكي x86_64 السليم، كما أن محاولات البيئة السابقة سجلت system responsiveness dialog وlaunch timeout، لذلك لا تستخدم كـPASS post-change. المطلوب هو جهاز حقيقي أو cloud run مصادق عليه مع cost approval، ثم تسجيل startup، biometric، WebView، services، crashes/ANRs، network state، وscreenshots/logs مع SHA المطابق.

## حدود الاستنتاج

لا يثبت هذا التقرير full-device VPN/TUN routing، DNS أو IPv6 leak absence، Kill Switch عند سقوط Tor/WireGuard، Tor bootstrap أو control/NEWNYM أو `.onion`، proxy chaining، WireGuard handshake، عزل الجلسات الأربعة، Clear All Data residual scan، fingerprint-only behavior على Infinix SMART 9، obfs4 أو Snowflake. هذه البنود تبقى `BLOCKED`, `NOT TESTED`, أو `NOT IMPLEMENTED` حسب المصفوفة ولا تمنح Release GO.

## الأدلة المرتبطة

| الدليل | الحالة |
|---|---|
| `reports/ci/20260827T_final_tun_config/status.txt` | PASS لكل local gates، و`cloud_execution=NOT_RUN` |
| `reports/ui_audit/20260827_v17/final_artifact/security_audit_qa.log` | PASS محدود، 3 warnings |
| `reports/ui_audit/20260827_v17/final_artifact/security_audit_production_required.log` | FAIL متوقع بسبب Debug certificate |
| `reports/ui_audit/20260827_v17/final_artifact/mobsfscan/status.txt` | MobSFscan source result |
| `reports/ui_audit/20260827_v17/final_artifact/apk_inventory/` | APK package/native/marker inventory |
| `reports/ui_audit/20260827_v17/final_artifact/adb_devices.txt` | لا يوجد جهاز متصل |

## الحكم النهائي

**NO-GO / QA ONLY / RELEASE BLOCKED.** الـAPK الحالي قابل للتسليم كـQA artifact فقط بعد توضيح أنه Debug-signed ويفتقد إثباتات runtime الحرجة. لا توجد نتيجة Full MobSF APK Server أو Dynamic Analysis يمكنها تغيير الحكم في هذه البيئة.
