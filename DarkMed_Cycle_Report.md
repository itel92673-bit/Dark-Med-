# Dark Med — تقرير الدورة بعد إعادة البيئة وتنفيذ المهمة المرفقة

**التاريخ:** 24 أغسطس 2026 UTC  
**الجهاز المستهدف:** Infinix SMART 9 X6532 / Android 14 / XOS 14.0.1  
**القرار:** **NOT READY — RELEASE BLOCKED**

## Executive Summary

أُعيدت بيئة Android SDK وEmulator وJDK وGradle Wrapper، وأُعيد بناء المشروع من source الحالي بعد recovery للملفات التي حذفها reset. نجحت كل بوابات Gradle المحلية بعد clean، وأُضيفت PlatformSmokeTest instrumentation حقيقية تقرأ PackageManager وManifest من الجهاز. حاولنا تشغيل AVD API 34 عبر software acceleration لأن `/dev/kvm` غائب؛ ظهر المحاكي في ADB، لكنه لم يستقر بما يكفي لتثبيت APK. فشلت install وconnected test بسبب `PackageManagerInternal.freeStorage` NullPointerException و`Broken pipe` داخل system_server، وبدأت 0 اختبارات فعلية.

## PASS — local evidence

| المجال | النتيجة |
|---|---|
| SDK/API setup | SDK، platform-tools، Emulator، API 34/35، NDK 27.2.12479018 موجودة |
| JDK | OpenJDK 21 مع javac موجود |
| Gradle | Wrapper 9.5.1 مستعاد؛ 9.5.0 distribution غير متاح رسميًا |
| Unit | 16 tests، 0 failures، 0 errors |
| Lint | debug وrelease ناجحان |
| Debug/Release build | كلاهما ناجح بعد clean |
| AndroidTest compilation | ناجح |
| Static security audit | failures=0، warnings=3 في QA: debug signing وtransport assets؛ production-required يرفض debug signing |
| APK forensic | Manifest، native libraries، signing verification مفحوصة |
| Policy/failure | 24 scenario وfail-closed policy على مستوى unit |

## FIXED / RECOVERED

تم استرجاع root Gradle files و`local.properties` وwrapper، تثبيت SDK وJDK، استعادة الموارد `icon` و`styles` وbackup XML، واستعادة classes `SecurityState` و`BrowserSession` و`WebViewSessionInitializer` و`HevTun2Socks` و`DarkMedVpnService` من artifact السابق أو source upstream الموثق. استُعيد Hev مع submodules الثلاثة، وأعيد ربط JNI الحقيقي باستخدام `hev_socks5_tunnel_main_from_file` و`hev_socks5_tunnel_quit` و`hev_socks5_tunnel_stats`. أضيف `.rev-id` لتثبيت provenance.

أضيف `PlatformSmokeTest` بأربع اختبارات فعلية لفحص launcher/security flags/services/browser processes. لم تُنفذ الاختبارات على device لأن package install فشل قبل test runner.

## NOT VERIFIED / BLOCKED

| المجال | الحالة | الدليل |
|---|---|---|
| Infinix SMART 9 | DEVICE_REQUIRED | لا جهاز حقيقي في ADB |
| Emulator runtime | ENVIRONMENT_BLOCKED | `/dev/kvm` غائب، software AVD غير مستقر |
| APK installation | BLOCKED on AVD | PackageManagerInternal NPE/Broken pipe |
| connected instrumentation | BLOCKED | `Starting 0 tests` ثم InstallException |
| Tor/VPN/WireGuard/DNS/Proxy | NOT VERIFIED | لا runtime/network execution |
| Kill Switch | NOT VERIFIED | policy فقط، لا failure على network stack |
| Browser/.onion/isolation | NOT VERIFIED | لا route ولا WebView runtime |
| Biometric/XOS/battery/memory | DEVICE_REQUIRED | لا target device |
| Production signing | USER_KEY_REQUIRED | release debug certificate |

## Build commands

```text
./gradlew --no-daemon --max-workers=1 clean
./gradlew --no-daemon --max-workers=1 :app:testDebugUnitTest
./gradlew --no-daemon --max-workers=1 :app:lintDebug
./gradlew --no-daemon --max-workers=1 :app:lintRelease
./gradlew --no-daemon --max-workers=1 :app:assembleDebug
./gradlew --no-daemon --max-workers=1 :app:assembleDebugAndroidTest
./gradlew --no-daemon --max-workers=1 :app:assembleRelease
./gradlew --no-daemon --max-workers=1 :app:connectedDebugAndroidTest
```

الأوامر من clean حتى assembleRelease أعادت `BUILD SUCCESSFUL`. connected test أعاد `BUILD FAILED` لأن التثبيت لم يُعتمد؛ لا يوجد assertion فاشل مصنف كفشل تطبيق.

## Current artifacts

| الملف | SHA-256 | التصنيف |
|---|---|---|
| `deliverables/DarkMed_QA_Debug.apk` | `158e642cc081d98cb526b51102b9700a7e3785ac9bc53cbc2bd71ea43d21be1a` | QA/debug |
| `deliverables/DarkMed_QA_Release_DebugSigned.apk` | `0c365d2e4f68bc4e88c7ceb7c3708eeb3c376f9dfb76debcd24c9f21c67a87d3` | QA release variant |
| `deliverables/Dark Med f.apk` | `0c365d2e4f68bc4e88c7ceb7c3708eeb3c376f9dfb76debcd24c9f21c67a87d3` | requested name; QA/debug-signed |
| `deliverables/DarkMed_QA_AndroidTest.apk` | `73d7ca3ed079896628f2b391f91288161ebe40c92e7a60ac8c5297b12dc83070` | instrumentation |

## Required next step

لتحقيق الجزء المتبقي من الملف حرفيًا، يلزم توفير Infinix SMART 9 حقيقي مع USB debugging، أو تشغيل المشروع على host/VM يملك KVM/Hypervisor فعليًا. بعد ظهور الجهاز، يجب جمع device profile، تثبيت APK، تشغيل PlatformSmokeTest، ثم اختبارات Android 14 وXOS وVPN/TUN وTor وWireGuard وProxy وDNS/IPv6 وKill Switch وBrowser وBiometric وClear All Data وstress. لا يُرفع أي مجال إلى PASS قبل وجود evidence فعلي.

## References

[1]: https://developer.android.com/studio/run/emulator-commandline "Android Emulator command line"
[2]: https://developer.android.com/studio/test/command-line "Android command-line testing"
[3]: https://developer.android.com/reference/android/net/VpnService "Android VpnService API"
[4]: https://developer.android.com/develop/background-work/services/fgs/service-types "Android foreground service types"
[5]: https://developer.android.com/identity/sign-in/biometric-auth "Android biometric authentication"
[6]: https://github.com/heiher/hev-socks5-tunnel "HevSocks5Tunnel repository"
