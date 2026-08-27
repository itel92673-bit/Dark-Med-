from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
main = (ROOT / "app/src/main/java/com/darkmed/app/MainActivity.kt").read_text()
flow = (ROOT / "app/src/main/java/com/darkmed/app/core/ClearAllDataFlow.kt").read_text()
browser = (ROOT / "app/src/main/java/com/darkmed/app/core/BrowserSessionActivity.kt").read_text()
profiles = (ROOT / "app/src/main/java/com/darkmed/app/core/ProfileStore.kt").read_text()
manifest = (ROOT / "app/src/main/AndroidManifest.xml").read_text()

auth_index = main.index("BiometricGate(fragmentActivity).authenticate")
auth_success_index = main.index("ClearAllDataEvent.AuthenticationSucceeded")
confirmation_index = flow.index("ClearAllDataEvent.AuthenticationSucceeded ->")
confirmed_index = flow.index("ClearAllDataEvent.Confirmed ->")
wipe_completed_index = flow.index("ClearAllDataEvent.WipeCompleted ->")

checks = {
    "main_secure_flag": "FLAG_SECURE" in main,
    "main_obscured_touch_filter": "setFilterTouchesWhenObscured(true)" in main,
    "startup_retry": "retryRequest" in main and "R.string.retry" in main,
    "biometric_before_authorization": auth_index < auth_success_index,
    "authorization_before_confirmation": confirmation_index < confirmed_index,
    "confirmation_before_wipe": confirmed_index < wipe_completed_index,
    "wipe_success_relocks": "onWipeCompleted()" in main and "DarkMedApp(onWipeCompleted = { unlocked = false })" in main,
    "reducer_cancel_resets": "ClearAllDataEvent.Cancelled, ClearAllDataEvent.Dismissed -> ClearAllDataState(ClearAllDataPhase.Idle)" in flow,
    "reducer_wipe_failure_is_failed": "ClearAllDataEvent.WipeFailed -> ClearAllDataState(ClearAllDataPhase.Failed" in flow,
    "security_center_not_verified": "R.string.not_verified" in main,
    "browser_secure_flag": "FLAG_SECURE" in browser,
    "browser_obscured_touch_filter": "setFilterTouchesWhenObscured(true)" in browser,
    "browser_no_file_access": "settings.allowFileAccess = false" in browser and "settings.allowContentAccess = false" in browser,
    "browser_no_universal_file_access": "setAllowUniversalAccessFromFileURLs(false)" in browser,
    "browser_no_mixed_content": "MIXED_CONTENT_NEVER_ALLOW" in browser,
    "browser_no_third_party_cookies": "setAcceptThirdPartyCookies(webView, false)" in browser,
    "browser_url_policy": "WebViewSecurityPolicy.isAllowedNavigation" in browser,
    "profile_store_exists": "class ProfileStore" in profiles and "ProfileCatalog" in profiles,
    "profile_create_handler": "ProfileCatalog.create" in main and "ProfileStore(context)" in main,
    "profile_edit_handler": "ProfileCatalog.rename" in main,
    "profile_duplicate_handler": "ProfileCatalog.duplicate" in main,
    "profile_delete_handler": "ProfileCatalog.delete" in main,
    "profile_activation_stays_blocked": "profile_activation_blocked" in main,
    "launcher_task_affinity_empty": 'android:taskAffinity=""' in manifest,
    "launcher_single_instance": 'android:launchMode="singleInstance"' in manifest,
    "cleartext_disabled": 'android:usesCleartextTraffic="false"' in manifest,
    "backup_disabled": 'android:allowBackup="false"' in manifest,
}

failed = [name for name, passed in checks.items() if not passed]
for name in checks:
    print(f"{name}={'PASS' if checks[name] else 'FAIL'}")
if failed:
    raise SystemExit("UI/security contract failures: " + ", ".join(failed))
