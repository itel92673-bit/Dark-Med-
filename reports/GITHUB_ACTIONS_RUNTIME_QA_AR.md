# Dark Med — GitHub Actions Runtime QA

## الحالة

أُضيف workflow يدوي إلى `.github/workflows/android-runtime-qa.yml` باسم `Dark Med Android Runtime QA`. هذه الإضافة تغلق جزء إعداد بيئة اختبار قابلة لإعادة التنفيذ، لكنها لا تعني أن GitHub Actions شُغّل أو أن أي نتيجة runtime أصبحت PASS. المشروع الحالي لا يحتوي Git remote أو repository متصلًا من هذه البيئة، لذلك لا يمكن تشغيل workflow من هنا.

## ما ينفذه workflow

يبدأ workflow بـKVM preflight ويفشل صراحةً إذا لم يوجد `/dev/kvm`. بعد ذلك يجهز Java 21 وGradle، ويستخدم `reactivecircus/android-emulator-runner@v2` لإنشاء Android Emulator x86_64، ثم ينفذ `connectedDebugAndroidTest`. أثناء الجلسة يحفظ `getprop` وlogcat وpackage state وmemory وconnectivity، ويمكن تفعيل metadata الشبكة السلبية عبر input باسم `run_network_evidence`.

توجد matrix اختيارية عبر input `api_level` للقيم 29 إلى 36، مع default API 34 وGoogle APIs وPixel 7. بعد انتهاء الجلسة يرفع workflow تقارير instrumentation وملفات evidence كـGitHub artifact. كل failure في instrumentation يعيد exit code الفعلي بدل تحويله إلى PASS.

## التشغيل المطلوب من مالك المستودع

ينبغي وضع المشروع في GitHub repository خاص أو عام، ثم تشغيل `Actions → Dark Med Android Runtime QA → Run workflow` واختيار API level. لا توجد أسرار أو cloud credentials داخل workflow. يلزم أن يسمح runner بتقنية KVM؛ إذا فشل KVM preflight فلا يجوز تبديل الاختبار إلى software emulation ثم تسجيل PASS. يجب فحص artifact المرفوع يدويًا وربطه بــAPK SHA محدد، وعدم استخدام workflow لإثبات VPN/TUN أو DNS leak أو Kill Switch أو Tor أو proxy chaining ما لم تحتوي instrumentation suite نفسها على اختبار حقيقي يقيس هذه الآثار.

## حدود الدليل

نجاح emulator instrumentation يثبت فقط الاختبارات التي نفذتها suite فعلًا على emulator المحدد. لا يثبت سلوك Infinix SMART 9 أو XOS، ولا يعادل اختبار IPv6 على شبكة هاتف حقيقية، ولا يثبت bridges أو obfs4/Snowflake تحت تضييق شبكي حقيقي. كما أن workflow الحالي لا ينشئ endpoints أو proxies ولا يضيف credentials أو backend؛ لذلك تبقى وظائف الشبكة العميقة `NOT TESTED`, `BLOCKED`, أو `NOT IMPLEMENTED` حسب حالتها الفعلية.

## المرجع

تمت مواءمة بنية workflow مع README الرسمي لمشروع Android Emulator Runner، الذي يوثق تفعيل KVM و`reactivecircus/android-emulator-runner@v2` وتشغيل `connectedCheck` على Linux runner [1].

[1]: https://github.com/ReactiveCircus/android-emulator-runner "ReactiveCircus Android Emulator Runner"
