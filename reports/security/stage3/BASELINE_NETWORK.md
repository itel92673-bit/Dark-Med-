# Dark Med Stage 3 — Baseline Network

## Status

**BLOCKED / NOT RUN**. لم يتوفر في البيئة الحالية Android runtime محلي أو ADB أو network observer مستقل قادر على التقاط traffic من جهاز Android.

## Required baseline observations

| Observation | Status | Evidence |
|---|---|---|
| Physical/ADB device identity | BLOCKED | `reports/security/stage3/runtime/preflight_20260905T182942Z.log`: `ADB_PATH=MISSING` |
| Public IPv4 before Dark Med | NOT RUN | No Android device/network observer |
| Public IPv6 before Dark Med | NOT RUN | No dual-stack Android runtime |
| System/libc DNS destination | NOT RUN | No external DNS observation |
| WebView/Chromium DNS | NOT RUN | No Android runtime |
| Raw UDP DNS | NOT RUN | No Android runtime or pcap |
| Independent packet capture | BLOCKED | `TCPDUMP_BINARY=MISSING`; no observation point |
| Timestamp correlation | BLOCKED | No device and observer clocks to correlate |

## Integrity rule

لا يتم استخدام public IP أو DNS أو pcap من بيئة sandbox العامة كبديل عن baseline لجهاز Android. لا توجد هنا قيم مصطنعة أو نتائج simulated. عند توفر جهاز Android ونقطة مراقبة مستقلة، يجب إنشاء baseline جديد مرتبط بـAPK SHA-256 وGit commit ووقت UTC، ولا يجوز إعادة استخدام هذا الملف كدليل PASS.
