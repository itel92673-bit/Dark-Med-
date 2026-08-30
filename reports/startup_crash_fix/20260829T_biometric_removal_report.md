# Dark Med — Startup Biometric Removal Report

## Requested change
The startup biometric gate was removed. The application now renders `DarkMedApp()` directly from `MainActivity.onCreate()` and no longer displays a biometric prompt or requires fingerprint authentication to enter.

## Security boundary preserved
`BiometricGate` remains in the Clear All Data confirmation path. The source scan shows one production call site, inside the Clear All Data flow. No biometric bypass was added to that sensitive operation.

## Files changed
`app/src/main/java/com/darkmed/app/MainActivity.kt`

## Verification evidence
The post-change source scan found no `LockedDarkMedApp`, `LockedScreen`, or startup `BiometricGate` reference. `git diff --check` passed. Debug unit tests, debug lint, debug APK assembly, and release APK assembly completed successfully.

The release APK was rebuilt and copied to `deliverables/Dark Med f.apk`.

## APK
SHA-256 is recorded in `20260829T_biometric_removal_apk_sha256.txt`.

## Runtime status
No Android device or emulator was visible through ADB during the available runtime gate. Therefore, this change is `CODE_BUILT_RUNTIME_UNVERIFIED`; the removal of the prompt is not marked as runtime PASS until the APK is launched on a real device or emulator and the Main UI is observed with logcat evidence.
