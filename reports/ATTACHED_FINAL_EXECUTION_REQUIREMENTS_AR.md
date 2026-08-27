# Dark Med — متطلبات Final Execution Phase المرفقة

## قاعدة الحكم

لا تُعد أي ميزة PASS بسبب وجود class أو service أو dependency أو UI أو configuration أو نجاح compile/unit/static scan. معيار PASS الكامل هو: UI ثم Logic ثم State ثم Service ثم Runtime ثم Real effect ثم Verification ثم Evidence. غير ذلك يُصنف PARTIAL أو NOT TESTED أو BLOCKED أو NOT IMPLEMENTED.

## نطاق التنفيذ المطلوب

يتطلب الملف إنهاء inventory لكل شاشة وزر وToggle وSetting وDialog وInput وMenu وProfile وBrowser/Security control وحالات loading/error/success، مع توثيق function وstate والتخزين والتأثير الحقيقي والفشل وإعادة التشغيل وoffline وفشل الخدمة. كل UI-only control بلا runtime effect يعد bug.

يتطلب مسار Settings مساواة UI value وstored value وruntime value، واختبار ON/OFF/restart/force-stop/reboot/network-change/service-failure. كما يتطلب تنفيذًا واختبارًا فعليًا لـVPN/TUN والإنشاء والتوجيه ومعالجة الحزم والـupstream والقطع وإعادة الاتصال وتبديل الشبكة وموت الخدمة، مع packet evidence لا استنتاج من VpnService.

يتطلب Kill Switch اختبار traffic أثناء VPN ثم قتل العملية/الخدمة وإثبات توقف المرور ثم استعادة VPN وإثبات التعافي عبر Wi-Fi وMobile وIPv4 وIPv6. ويتطلب DNS/IP leak tests فعلية لـDNS وIPv4 وIPv6 في حالات VPN ON/OFF/failure/network switching/Tor/proxy مع تسجيل العناوين والخوادم المرصودة دون simulated results.

يتطلب Tor اختبار Start وBootstrap وSOCKS وControlPort وCircuit وNEWNYM والفشل والتعافي وربط المتصفح. وإذا كان obfs4/Snowflake مطلوبين، فإما integration حقيقي مع binary/ABI/process/bootstrap/failure evidence أو توثيق WHY/DEPENDENCY/REQUIRED INPUT/BLOCKER/NEXT ACTION دون fake implementation.

يتطلب Proxy دعم SOCKS/HTTP/HTTPS وauthentication وProxy 1/2 وchaining واختبارات valid/invalid/timeout/dead proxy/wrong auth/chain failure/recovery. ويتطلب Browser اختبار Open/Navigate/Back/Forward/Reload/URL validation/External URL/Cookies/Cache/LocalStorage/Downloads/JavaScript/Windows/Permissions/.onion، وأن يستخدم route محميًا فعليًا.

يتطلب الملف كسر عزل الجلسات الأربعة عمدًا وفحص cookies/cache/localStorage/WebView/history/downloads/authentication/storage بين A→B→C→D. ويتطلب Clear All Data تنفيذ biometric ثم confirmation ثم stop services ثم wipe ثم storage/Keystore cleanup وresidual verification وrestart، مع fault recovery أثناء wipe/crash/force-stop/low-storage/concurrent requests.

يتطلب Profiles تنفيذ Create/Edit/Duplicate/Activate/Deactivate/Delete/Switch وإثبات تأثيرها على VPN/Tor/DNS/Proxy/Browser/Sessions/Security Center. كما يفرض أن تبقى كل security status evidence-backed، وألا يتحول NOT_TESTED أو BLOCKED أو FAILED أو UI boolean إلى PASS/SECURE/ACTIVE/PROTECTED.

يتطلب Chaos على جهاز حقيقي: process/service/VPN/Tor/proxy kills، network loss/recovery، Wi-Fi↔Mobile، rapid toggling/profile switching، low-memory/storage، reboot/upgrade/reinstall، مع مراقبة crash/ANR/deadlock/race/state corruption/false security/data loss/bypass. ويتطلب Performance قياس startup/RAM/CPU/battery/thermal/ANR/crash rate/network overhead دون تقدير أرقام.

يتطلب Full MobSF APK Static + Dynamic Analysis عندما تسمح البيئة، مع مراجعة Manifest/components/permissions/WebView/storage/crypto/network/secrets/native/IPC/intents/deep links ثم runtime/network/storage/logs/components/behavior. كما يتطلب Real Device يبدأ بـInfinix SMART 9/X6532 ثم Android 10–16، وCloud execution فقط مع providers configured ومصرح بها وبعد Cost Guard وprovider approval وvalid credentials وdevice availability.

## شرط الإغلاق

لكل مشكلة: REPRODUCE ثم ROOT CAUSE ثم FIX ثم BUILD ثم TEST ثم REGRESSION ثم SECURITY RETEST. ممنوع حذف الاختبار أو تخفيف assertion أو تغيير expected result. في النهاية يجب إصدار جدول SCREEN/BUTTON/SETTING/FUNCTION/IMPLEMENTATION/RUNTIME/TEST/EVIDENCE/STATUS، وقرار NO-GO إذا بقيت core functionality أو VPN/TUN أو leaks أو Kill Switch أو Tor أو proxy chaining أو browser route أو isolation أو production signing أو critical/high issue غير مغلقة.

## تطبيق هذه المتطلبات على الحالة الحالية

تم تنفيذ ما يمكن محليًا سابقًا: Clear All Data reducer وDataWiper hardening وWebView URL policy وcontract tests وfull local CI وMobSFscan source وAPK inventory. ستُعاد مطابقة هذه النتائج مع الملف المرفق، ثم تُنفذ الإصلاحات المحلية الإضافية فقط حيث يوجد تأثير حقيقي قابل للإثبات. تبقى وظائف runtime التي تتطلب جهازًا أو شبكة أو اعتمادًا خارجيًا BLOCKED/DEVICE_REQUIRED/NETWORK_REQUIRED، وتبقى obfs4 وSnowflake وproxy forwarder/chaining وProfile CRUD وprotected browser route NOT IMPLEMENTED ما لم يُنفذ كود حقيقي ويُثبت أثره.
