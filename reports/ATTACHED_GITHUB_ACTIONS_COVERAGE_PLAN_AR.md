# Dark Med — خطة GitHub Actions والمصفوفة المرفقة

## نطاق المصفوفة

المطلوب هو تشغيل API 29 و30 و31 و33 و34 على صور `google_apis`، مع x86_64 يوميًا وarm64-v8a كمسار أبطأ للـrelease candidate، وبروفايلات RAM منخفضة ومتوسطة وعالية. يحدد الملف محاكاة شاشة 720×1600 وكثافة تقارب 260dpi كقرب بصري من Infinix SMART 9، مع التنبيه إلى أن AVD لا يقلد XOS 14 وPhone Manager وإدارة البطارية العدوانية.

## قواعد الدليل

كل PR/build يجب أن يستخدم `fail-fast: false` ويجمع logcat وscreenshots عند الفشل وbugreport وpcap عند توفر capture حقيقي. نجاح emulator لا يساوي نجاح جهاز Infinix أو شبكة IPv6 أو XOS. أي بند لا يشغّل effect حقيقيًا يبقى NOT TESTED أو BLOCKED.

## تسلسل runtime المطلوب

يجب أن تغطي instrumentation، عند توفر بيئة KVM، VPN consent وTUN وroute وtraffic وservice death وrecovery؛ DNS وIPv4 وIPv6 leak؛ Kill Switch مع Wi-Fi وMobile وairplane/network loss؛ Tor bootstrap/SOCKS/ControlPort/NEWNYM/`.onion`؛ proxy chaining؛ protected browser route؛ four-session isolation؛ Profile CRUD عبر المصفوفة؛ Clear All Data تحت ضغط ذاكرة؛ وWebView URL policy على كل API.

## حدود لا يجوز إخفاؤها

لا تثبت بيئة GitHub Actions وحدها XOS أو cellular NAT أو captive portals أو IPv6 الحقيقي أو obfs4/Snowflake تحت تضييق شبكي. يلزم جهاز Transsion حقيقي لإغلاق Kill Switch/Tor في الخلفية بعد قفل الشاشة وBattery Saver. وتبقى الوظائف غير المنفذة NOT IMPLEMENTED حتى يتم ربطها بمسار حقيقي.

## ما طُبق في المشروع

أضيف workflow يدوي/تلقائي في `.github/workflows/android-runtime-qa.yml` باستخدام KVM fail-closed و`reactivecircus/android-emulator-runner@v2`، API input من 29 إلى 36، Google APIs، instrumentation، وجمع getprop/logcat/memory/package/connectivity وmetadata الشبكة الاختيارية. أضيف له contract audit داخل local CI. لم يُشغّل GitHub Actions من هذه البيئة، لأن المشروع ليس Git repository متصلًا ولا توجد عملية remote execution.

## المرجع

تمت مراجعة README الرسمي لـAndroid Emulator Runner قبل إنشاء workflow: https://github.com/ReactiveCircus/android-emulator-runner
