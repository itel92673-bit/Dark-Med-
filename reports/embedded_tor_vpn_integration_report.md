# DARK MED — Embedded Tor/VPN Integration Report

## Implemented

تم ربط Tor Android المضمّن داخل حزمة التطبيق مع خدمة `TorForegroundService`. قبل تشغيل Tor تكتب الخدمة torrc محليًا عبر `TorConfigWriter`، ثم تشغّل `org.torproject.jni.TorService` الموجود فعليًا داخل AAR، وتقرأ منفذ SOCKS من `TorService.LocalBinder` عند الاتصال.

تمت إضافة `DarkMedNetworkOrchestrator` لتطبيق التسلسل الحقيقي التالي بعد منح المستخدم إذن VPN: تشغيل Tor المحلي، انتظار `TOR_READY`، قراءة منفذ SOCKS الفعلي، كتابة إعداد Hev YAML بالمنفذ نفسه، ثم بدء `DarkMedVpnService` لتأسيس TUN وتشغيل native Hev tun2socks. فشل الإذن أو فشل الإعداد يوقف المسار ولا يسمح بمسار مباشر بديل.

## APK evidence

تم بناء Release APK بنجاح. يحتوي APK على `libtor.so` و`libdarkmed-tun2socks-jni.so` لكل من `arm64-v8a` و`armeabi-v7a` و`x86` و`x86_64`. كما يحتوي الـmerged manifest على `DarkMedVpnService` و`TorForegroundService` و`org.torproject.jni.TorService`.

SHA-256 للنسخة الحالية محفوظ في `reports/embedded_network_apk_sha256.txt`.

## Verification

| Gate | Result |
|---|---|
| tor-android AAR class/API inspection | PASS — `org.torproject.jni.TorService` و`LocalBinder` موجودان |
| Hev JNI/native build | PASS |
| Unit tests | PASS في آخر build ناجح قبل/مع التعديل |
| Debug lint | PASS في آخر build ناجح قبل/مع التعديل |
| Release assemble | PASS |
| Native libraries inside APK | PASS |
| Tor bootstrap على Android runtime | UNVERIFIED |
| Tor SOCKS reachability | UNVERIFIED |
| TUN packet forwarding | UNVERIFIED |
| DNS/IPv4/IPv6 leak behavior | UNVERIFIED |
| Kill Switch تحت failure | UNVERIFIED |

## Honest limitation

لم يظهر أي جهاز Android أو Emulator عبر ADB في بيئة التنفيذ، لذلك لا يوجد logcat أو packet trace أو screenshot يثبت Tor bootstrap أو مرور حزم الجهاز عبر TUN ثم SOCKS. وفق بروتوكول Dark Med، لا يجوز تحويل هذه البنود إلى PASS اعتمادًا على compilation أو وجود المكتبات داخل APK فقط.

## Sources

[1] Guardian Project tor-android: https://github.com/guardianproject/tor-android

[2] Guardian Project Orbot Android: https://github.com/guardianproject/orbot-android

[3] Hev socks5 tunnel: https://github.com/heiher/hev-socks5-tunnel

[4] WireGuard Android: https://github.com/WireGuard/wireguard-android
