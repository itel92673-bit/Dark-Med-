# Android VPN Runtime Findings

## المصادر

تمت مراجعة وثائق Android الرسمية لـ`VpnService` و`VpnService.Builder` ومتطلبات foreground service على Android 14.

## النتائج التنفيذية

`VpnService` ينشئ virtual network interface ويعيد file descriptor؛ قراءة descriptor تستقبل outgoing IP packets وكتابة descriptor تحقن incoming packets. الوثيقة الرسمية تفصل بين `prepare(Context)` لموافقة المستخدم و`Builder.establish()` لإنشاء الواجهة، وتذكر أن التطبيق يجب أن يحمي upstream sockets عبر `protect()` ثم يعالج packets عبر descriptor. لذلك وجود `establish()` أو route declarations وحده لا يكفي لإثبات traffic forwarding.

`Builder.addRoute()` يضيف routes إلى VPN interface ويجعل address family قابلة للتوجيه، بينما `addAddress()` و`addDnsServer()` يؤثران أيضًا في family allowance. `allowBypass()` يسمح للتطبيقات بتجاوز VPN، ولذلك لا يجوز استدعاؤه في مسار fail-closed المطلوب. `setBlocking()` يغير وضع file descriptor فقط، ولا يمثل وحده Kill Switch. `establish()` قد يعيد null إذا لم تكن الموافقة موجودة أو رُفعت، ويجب إغلاق descriptor عند إنهاء الخدمة.

توضح وثيقة Android 14 أن foreground-service type يجب أن يكون معلنًا ومطابقًا للاستخدام، وإلا يمكن أن يظهر `MissingForegroundServiceTypeException` أو `SecurityException`. المشروع الحالي يستخدم `specialUse`، ويجب إبقاء هذا القرار موثقًا ومراجعته مع manifest وruntime؛ لا يجوز اعتبار foreground notification دليلًا على أن route أو Tor يعمل.

## أثر المراجعة على Dark Med

الكود الحالي يضيف IPv4 وIPv6 routes وDNS داخليًا ويسلم descriptor إلى native `HevTun2Socks`. لكن لا يوجد بعد دليل runtime على أن native engine يمرر traffic إلى upstream حقيقي، ولا أن `protect()` استُخدم على sockets upstream، ولا أن DNS server الافتراضي يُحجب، ولا أن Kill Switch يبقى فعالًا عند موت الخدمة. لذلك تظل VPN/TUN وDNS وKill Switch `UNVERIFIED` حتى تشغيل instrumented/device evidence فعلي.

## المراجع

1. https://developer.android.com/reference/android/net/VpnService
2. https://developer.android.com/reference/android/net/VpnService.Builder
3. https://developer.android.com/about/versions/14/changes/fgs-types-required
