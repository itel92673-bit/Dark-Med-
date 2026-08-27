# Dark Med — Complete UI / Action / State / Evidence Inventory

**المصدر:** مراجعة MainActivity.kt وBrowserSessionActivity.kt وAndroidManifest.xml وموارد strings الحالية.  
**قاعدة التصنيف:** وجود العنصر في Compose أو Manifest لا يساوي تنفيذًا فعليًا. لا يصبح العنصر `PASS` إلا مع evidence مناسب لنطاقه.

## 1. Application Map

| Screen / Surface | كيف تُفتح | العناصر الموجودة | الحالة الحالية |
|---|---|---|---|
| Locked startup | أول تشغيل لـMainActivity | Lock icon، brand، biometric prompt غير مرئي، Retry button | `PARTIAL`: strong biometric gate وRetry موجودان، لكن لا runtime proof على جهاز حقيقي |
| Dashboard | bottom navigation: Dashboard | brand، privacy subtitle، protection card، Route card، four layer rows، fail-closed card | `PARTIAL / HONEST`: معلومات وحالات غير محمية؛ connect/disconnect ليس Button ولا ينفذ خدمة |
| Browser | bottom navigation: Browser | title، disclaimer، unavailable/protected-route card | `BLOCKED / HONEST`: لا يوجد address bar أو navigation أو new/close session UI؛ route غير موثّق |
| Profiles | bottom navigation: Profiles | default profiles، Add، Edit، Duplicate، Delete، confirmation dialogs | `PARTIAL`: CRUD محلي محفوظ ومختبر؛ لا activate/deactivate/switch route workflow |
| Security Center | bottom navigation: Privacy Center | 11 status rows، why-not-protected note، fail-closed message | `PASS limited UI truth`: كل status يعرض NOT VERIFIED؛ لا يوجد runtime proof |
| Settings | bottom navigation: Settings | Device Compatibility card، Clear All Data card/button | `PARTIAL`: wipe workflow backend موجود؛ compatibility قراءة محلية؛ لا settings toggles حقيقية |
| Clear confirmation dialog | Settings → Authenticate and Clear بعد biometric success | title، description، Cancel، Confirm، outside dismiss، back dismiss | `PARTIAL / SECURITY-GATED`: biometric يسبق الحوار، ثم التأكيد يستدعي wipe backend؛ runtime residual scan غير شامل لكل Android state |
| Browser session activities | Manifest/internal intent only | WebView لكل session process، private cleanup، HTTP/HTTPS allowlist، file/content isolation controls، Safe Browsing | `IMPLEMENTED MECHANICS / NOT TESTED`: process/suffix isolation وstatic hardening موجودان؛ UI route وruntime cross-access غير مثبتين |
| UnavailableScreen | لا يوجد call path في MainActivity الحالية | title، unavailable message | `UNREACHABLE / NOT TESTED`: لا يظهر في current navigation |

## 2. Bottom Navigation Inventory

| Element ID | Label AR/EN | Action | Next state | Persistence | Security | Status / Evidence |
|---|---|---|---|---|---|---|
| NAV-DASHBOARD | لوحة التحكم / Dashboard | `selected = "dashboard"` | Dashboard rendered | In-memory only | No sensitive action | `PASS limited`: Compose handler exists; device tap not executed post-final change |
| NAV-BROWSER | المتصفح / Browser | `selected = "browser"` | Browser unavailable state | In-memory only | Does not claim protection | `PASS limited`: route selection works in code; no browser route |
| NAV-PROFILES | الملفات الشخصية / Profiles | `selected = "profiles"` | Profiles CRUD list | SharedPreferences local store | Activation remains blocked by copy | `PASS limited`: navigation and local CRUD handlers; route activation not implemented |
| NAV-PRIVACY | مركز الخصوصية / Privacy Center | `selected = "privacy"` | Security Center | In-memory only | Displays NOT VERIFIED | `PASS limited UI truth` |
| NAV-SETTINGS | الإعدادات / Settings | `selected = "settings"` | Settings | In-memory only | Contains sensitive wipe control | `PASS limited`: handler exists; device navigation not executed |

## 3. Startup and Biometric Elements

| Element ID | Type | Action / precondition | Actual result from source | Failure / recovery | Status |
|---|---|---|---|---|---|
| START-BIOMETRIC-GATE | `LaunchedEffect` + BiometricPrompt | Runs after Activity composition and Retry; allows `BIOMETRIC_STRONG` only | Success sets `unlocked=true`; failure updates locked message | Retry re-runs status/authentication; biometric unavailability remains locked | `PARTIAL / DEVICE_REQUIRED` |
| LOCK-ICON | Icon | None | Decorative lock | None | `PASS visual only` |
| LOCK-BRAND | Text | None | Shows DARK MED | None | `PASS visual only` |
| LOCK-MESSAGE | Text | None | Shows required/unavailable/failure message | Message plus visible Retry recovery action | `PASS code path / DEVICE_REQUIRED` |

## 4. Dashboard Inventory

| Element ID | Type | Action | Expected | Actual | Backend/service | Status |
|---|---|---|---|---|---|---|
| DASH-BRAND | Text | None | Brand shown | Brand shown | None | `PASS visual` |
| DASH-PROTECTION-STATUS | Card/Text | None | Honest protection status | Shows UNPROTECTED and no verified route | None | `PASS UI truth` |
| DASH-CONNECT-DISCONNECT | Text pair | No click handler | Should connect/disconnect if product requirement is active | It is explanatory text only; no operation | None | `NOT IMPLEMENTED` |
| DASH-ROUTE-CARD | Card/Text | None | Show verified current route | Shows DEVICE → BLOCKED until evidence | None | `PASS UI truth` |
| DASH-WIREGUARD-ROW | Card | None | Show and manage WireGuard | Display only, NOT CONFIGURED | No service call | `NOT IMPLEMENTED UI workflow` |
| DASH-PROXY1-ROW | Card | None | Show/manage Proxy 1 | Display only, NOT CONFIGURED | No proxy forwarder | `NOT IMPLEMENTED` |
| DASH-PROXY2-ROW | Card | None | Show/manage Proxy 2 | Display only, NOT CONFIGURED | No proxy forwarder | `NOT IMPLEMENTED` |
| DASH-TOR-ROW | Card | None | Show/manage Tor | Display only, NOT CONFIGURED | Tor service exists but not wired to UI | `PARTIAL / NOT IMPLEMENTED UI workflow` |
| DASH-FAIL-CLOSED-CARD | Card/Text | None | Explain fail-closed | Explains direct traffic is not reported protected | No verified kill-switch proof | `PASS UI truth only` |

## 5. Browser Inventory

| Element ID | Type | Action | Actual | Required evidence | Status |
|---|---|---|---|---|---|
| BROWSER-TITLE | Text | None | Browser title shown | None | `PASS visual` |
| BROWSER-DISCLAIMER | Text | None | Explicitly says anonymity and `.onion` unverified | None | `PASS UI truth` |
| BROWSER-REQUIRES-PROTECTION | Card/Text | None | Says available only after verified route | No route activation exists | `PASS fail-closed UI` |
| BROWSER-ADDRESS-BAR | Missing | None | Not present | N/A | `NOT IMPLEMENTED` |
| BROWSER-NAVIGATION | Missing | None | No back/forward/refresh/new session/close session controls | WebView runtime evidence required | `NOT IMPLEMENTED` |
| BROWSER-DOWNLOADS | Missing | None | No user workflow | Storage/download evidence required | `NOT IMPLEMENTED` |
| BROWSER-ONION | Missing | None | No `.onion` workflow; Tor route unverified | bootstrap/SOCKS/.onion evidence required | `BLOCKED / NOT IMPLEMENTED` |

## 6. Profiles Inventory

| Element ID | Type | Action | Actual | Persistence | Status |
|---|---|---|---|---|---|
| PROFILE-MAX-PRIVACY | Card | Edit/Duplicate/Delete handlers | Local profile record; route remains NOT VERIFIED | SharedPreferences | `PARTIAL: CRUD implemented; activation blocked` |
| PROFILE-TOR | Card | Edit/Duplicate/Delete handlers | Local profile record; route remains NOT VERIFIED | SharedPreferences | `PARTIAL: CRUD implemented; activation blocked` |
| PROFILE-WIREGUARD | Card | Edit/Duplicate/Delete handlers | Local profile record; route remains NOT VERIFIED | SharedPreferences | `PARTIAL: CRUD implemented; activation blocked` |
| PROFILE-CUSTOM | Card | Edit/Duplicate/Delete handlers | Local profile record; route remains NOT VERIFIED | SharedPreferences | `PARTIAL: CRUD implemented; activation blocked` |
| PROFILE-CREATE | Button/dialog | `ProfileCatalog.create` + `ProfileStore.save` | Validated local create and persistence path | SharedPreferences | `PASS local model / DEVICE_REQUIRED UI` |
| PROFILE-EDIT | TextButton/dialog | `ProfileCatalog.rename` + `ProfileStore.save` | Validated local rename and persistence path | SharedPreferences | `PASS local model / DEVICE_REQUIRED UI` |
| PROFILE-DELETE | TextButton/dialog | `ProfileCatalog.delete` + `ProfileStore.save` | Confirmation then local delete; route unaffected | SharedPreferences | `PASS local model / DEVICE_REQUIRED UI` |
| PROFILE-ACTIVATE | Missing | None | Activation explicitly blocked because no verified route engine is connected | VPN/Tor/DNS/route evidence required | `NOT IMPLEMENTED / BLOCKED` |

## 7. Security Center Inventory

| Element ID | Status shown | Evidence behind it | Actual behavior | Status |
|---|---|---|---|---|
| SEC-VPN | VPN / NOT VERIFIED | No device VPN proof | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-TOR | Tor / NOT VERIFIED | No bootstrap proof | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-SOCKS | SOCKS / NOT VERIFIED | No SOCKS proof | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-DNS | DNS / NOT VERIFIED | No leak/runtime proof | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-IPV4 | IPv4 / NOT VERIFIED | No route/packet proof | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-IPV6 | IPv6 / NOT VERIFIED | No route/packet proof | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-ROUTING | Routing / NOT VERIFIED | No full-device route proof | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-KILL-SWITCH | Kill Switch / NOT VERIFIED | No terminate-and-block proof | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-BROWSER-ISOLATION | Browser Isolation / NOT VERIFIED | No cross-session test | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-BIOMETRIC | Biometric / NOT VERIFIED | Strong biometric API only; no real device test | Honest NOT VERIFIED | `PASS UI truth` |
| SEC-STORAGE | Storage / NOT VERIFIED | Static wipe code only; no residual runtime scan | Honest NOT VERIFIED | `PASS UI truth` |

## 8. Settings Inventory

| Setting / control | Default | UI control | Backend variable/effect | Persistence | Status |
|---|---|---|---|---|---|
| Strong biometric unlock | Enabled/required | No toggle | `BiometricGate` with `BIOMETRIC_STRONG` | System biometric state | `PARTIAL / DEVICE_REQUIRED` |
| Fingerprint-only intent | Requested | API strong biometric; no face-specific universal API guarantee | `BiometricPrompt` allowed authenticator | System | `PARTIAL`: cannot claim fingerprint-only on every OEM without device-specific verification |
| Clear All Data | Available | Button + confirmation dialog | `ClearAllDataCoordinator` → stop services/broadcast sessions → `DataWiper` | Destructive local action | `IMPLEMENTED BACKEND / DEVICE_REQUIRED` |
| Device Compatibility | Read-only | Card | `DeviceCompatibilityCenter.snapshot()` | In-memory snapshot | `PASS local readout / NOT runtime proof` |
| History | No UI setting | Missing | No exposed preference | N/A | `NOT IMPLEMENTED UI` |
| Downloads | No UI setting | Missing | Browser workflow absent | N/A | `NOT IMPLEMENTED UI` |
| Persistent/private session mode | No UI setting | Missing | Browser base supports private cleanup metadata only | N/A | `NOT IMPLEMENTED UI` |
| Kill Switch | No UI toggle | Missing | No verified runtime behavior | N/A | `NOT IMPLEMENTED UI / BLOCKED` |
| DNS mode | No UI control | Missing | No configured user workflow | N/A | `NOT IMPLEMENTED UI` |
| Proxy 1/2 | No UI control | Missing | No forwarder/config workflow | N/A | `NOT IMPLEMENTED UI` |
| WireGuard import | No UI control | Missing | WireGuard dependency/service exists but no import workflow | N/A | `NOT IMPLEMENTED UI` |
| Tor bridges/transports | No UI control | Missing | Tor config primitives exist but no settings workflow | N/A | `NOT IMPLEMENTED UI` |

## 9. Clear All Data Journey

| Step | Source path | Expected | Actual / evidence | Status |
|---|---|---|---|---|
| Open Settings | bottom nav | Settings appears | Compose navigation handler exists | `PASS limited` |
| Press Authenticate and Clear | Button | Strong biometric opens before confirmation | `BiometricGate.authenticate` called; no wipe before success | `PASS code path / DEVICE_REQUIRED` |
| Cancel/outside/back | Dialog | No wipe | reducer يعيد Confirmation إلى Idle ويلغي authorization المؤقتة | `PASS code path` |
| Biometric success | Prompt callback | Confirmation opens | `ClearAllDataReducer` ينتقل من Authenticating إلى Confirmation مع authorization مؤقتة ضمن state؛ no wipe before success | `PASS code path / DEVICE_REQUIRED` |
| Confirm | Dialog | Wipe runs only after prior biometric success | reducer ينتقل إلى Wiping مرة واحدة؛ coordinator يعمل عبر `Dispatchers.IO`؛ successful wipe يحدّث state ثم يعيد قفل التطبيق | `IMPLEMENTED / DEVICE_REQUIRED` |
| Biometric failure | Prompt | No confirmation/wipe, error shown | `wipeNotDeleted` callback; button can be retried | `PASS code path / DEVICE_REQUIRED` |
| Residual scan | Backend | Prove all required residuals absent | Directory/alias verification only; not full device residual scan | `PARTIAL` |
| Restart after wipe | Runtime | Locked state and no data | Not executed on valid device | `DEVICE_REQUIRED` |

## 10. Dialog and Error Inventory

| Dialog/error | Open | Cancel/back | Confirm | Invalid/empty | Status |
|---|---|---|---|---|---|
| Clear confirmation | Biometric success | Cancel/outside/back resets reducer to Idle | Confirm then one Wiping transition | No input fields; reducer blocks repeated confirmation; runtime tap race remains untested | `PARTIAL / DEVICE_REQUIRED` |
| Biometric unavailable | Startup | No dialog cancel | No bypass | Locked message only | `PARTIAL UX` |
| Biometric failure | Startup/Clear | System prompt behavior | No bypass | Message callback only | `PARTIAL / DEVICE_REQUIRED` |
| FragmentActivity unavailable | Clear path | N/A | No wipe | Error text, safe failure | `PASS code safety` |
| Wipe failure/not deleted | Clear path | N/A | No false success | Error string includes reason | `PASS code safety` |

## 11. User-level actual journey today

بعد تثبيت التطبيق وفتحه، يبدأ MainActivity ويطلب strong biometric authentication. إذا نجحت المصادقة ينتقل المستخدم إلى Dashboard؛ إذا لم تتوفر أو فشلت، يبقى في شاشة القفل مع رسالة وزر Retry، دون مسار تجاوز. في Dashboard يرى أن التطبيق غير محمي وأن المسار محجوب، ويرى الطبقات الأربع بحالة غير مهيأة. يمكنه التنقل إلى Browser وProfiles وPrivacy Center وSettings، لكن Browser وProfiles يعرضان حالات صادقة غير مكتملة، وPrivacy Center يعرض NOT VERIFIED لكل طبقة، وSettings يعرض قراءة توافق الجهاز ومسار Clear All Data المحمي بالمصادقة. لا توجد حاليًا واجهة تشغيل فعلية للـVPN أوTor أوWireGuard أوProxy أوDNS أوKill Switch أوBrowser navigation، لذلك لا يجوز وصف النسخة الحالية كتطبيق حماية مكتمل.

## 12. Summary

| Category | Count / state |
|---|---|
| Current bottom-nav destinations | 5 |
| Current functional sensitive button | 1: Authenticate and Clear |
| Current dialogs | 1 explicit clear dialog + system biometric prompt |
| Current profile cards | 4 display-only |
| Current Security Center status rows | 11 honest NOT VERIFIED rows |
| Missing required workflows | connect, profile activation/switch, browser navigation, VPN/TUN, Tor, proxy/chaining, DNS, kill switch, persistent/private settings; local Profile CRUD is implemented |
| Runtime evidence available | No valid post-final-change device runtime evidence; `adb devices -l` empty |
| Current UI truth risk | Low for shown protection statuses; high if future controls expose success without underlying evidence |
| Latest local CI evidence | `reports/ci/20260827T_final_tun_config/status.txt`: all local gates PASS, `cloud_execution=NOT_RUN` | 
| Release interpretation | `NO-GO / QA ONLY / RELEASE BLOCKED` |
