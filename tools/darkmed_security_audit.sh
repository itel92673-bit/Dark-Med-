#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APK="${1:-}"
FAILURES=0
WARNINGS=0

pass() { printf 'PASS  %s\n' "$1"; }
fail() { printf 'FAIL  %s\n' "$1"; FAILURES=$((FAILURES + 1)); }
warn() { printf 'WARN  %s\n' "$1"; WARNINGS=$((WARNINGS + 1)); }

require_text() {
    local label="$1" file="$2" pattern="$3"
    if grep -Fq -- "$pattern" "$file"; then pass "$label"; else fail "$label"; fi
}

forbidden_source() {
    local label="$1" pattern="$2"
    local matches
    matches="$(grep -RInE --exclude-dir=hev-socks5-tunnel --exclude='*.xml' "$pattern" "$ROOT/app/src/main/java" "$ROOT/app/src/main/jni" 2>/dev/null || true)"
    if [[ -n "$matches" ]]; then
        fail "$label"
        printf '%s\n' "$matches"
    else
        pass "$label"
    fi
}

MANIFEST="$ROOT/app/src/main/AndroidManifest.xml"
require_text "cleartext traffic disabled" "$MANIFEST" 'android:usesCleartextTraffic="false"'
require_text "backup disabled" "$MANIFEST" 'android:allowBackup="false"'
require_text "VPN binding declared" "$MANIFEST" 'android.permission.BIND_VPN_SERVICE'
require_text "special-use FGS declared" "$MANIFEST" 'android:foregroundServiceType="specialUse"'
require_text "notification permission declared" "$MANIFEST" 'android.permission.POST_NOTIFICATIONS'
require_text "official icon referenced" "$MANIFEST" '@drawable/dark_med_icon'

forbidden_source "no hardcoded private key material" 'BEGIN[[:space:]]+[A-Z ]*PRIVATE KEY|PRIVATE_KEY[[:space:]]*=|privateKey[[:space:]]*='
forbidden_source "no obvious API key literals" '(api[_-]?key|authorization)[[:space:]]*=[[:space:]]*"[A-Za-z0-9_./+=-]{16,}"'
forbidden_source "no direct sensitive logging" 'Log\.[divew]\([^\n]*(password|private|secret|credential|token)'
forbidden_source "no placeholder screen in app-owned source" 'PlaceholderScreen|coming soon|fake success|mock proxy|stub proxy'
forbidden_source "no misleading Tor running notification" 'Local Tor engine is running'

if [[ -n "$APK" ]]; then
    if [[ ! -f "$APK" ]]; then
        fail "APK exists: $APK"
    else
        pass "APK exists: $APK"
        APKSIGNER=""
        if command -v apksigner >/dev/null 2>&1; then
            APKSIGNER="$(command -v apksigner)"
        else
            for SDK_ROOT in "${ANDROID_HOME:-}" "${ANDROID_SDK_ROOT:-}" "$ROOT/../android-sdk" "/home/ubuntu/android-sdk" "/home/ubuntu/android-sd"; do
                if [[ -n "$SDK_ROOT" ]] && compgen -G "$SDK_ROOT/build-tools/*/apksigner" >/dev/null; then
                    APKSIGNER="$(compgen -G "$SDK_ROOT/build-tools/*/apksigner" | sort -V | tail -1)"
                    break
                fi
            done
        fi
        if [[ -n "$APKSIGNER" ]]; then
            CERT_INFO="$($APKSIGNER verify --print-certs "$APK" 2>/dev/null || true)"
            if grep -Fq 'CN=Android Debug' <<<"$CERT_INFO"; then
                if [[ "${DARKMED_REQUIRE_PRODUCTION_SIGNING:-0}" == "1" ]]; then
                    fail "APK is signed with Android Debug certificate; production signing is required"
                else
                    warn "APK is debug-signed; production signing is not verified"
                fi
            else
                pass "APK is not signed with Android Debug certificate"
            fi
        else
            if [[ "${DARKMED_REQUIRE_PRODUCTION_SIGNING:-0}" == "1" ]]; then
                fail "apksigner unavailable; production signing cannot be verified"
            else
                warn "apksigner unavailable; APK certificate type not checked"
            fi
        fi
        ZIP_LIST="$(mktemp)"
        trap 'rm -f "$ZIP_LIST"' EXIT
        unzip -Z1 "$APK" > "$ZIP_LIST"
        for lib in libtor.so libwg-go.so libwg.so libwg-quick.so libhev-socks5-tunnel.so libdarkmed-tun2socks-jni.so; do
            if grep -Fxq -- "lib/arm64-v8a/$lib" "$ZIP_LIST"; then pass "APK contains arm64-v8a/$lib"; else warn "APK missing arm64-v8a/$lib"; fi
        done
        if grep -Fq -- 'assets/obfs4' "$ZIP_LIST" || grep -Fq -- 'bin/obfs4' "$ZIP_LIST"; then
            pass "APK contains obfs4 asset"
        else
            warn "No obfs4 asset found; pluggable transport remains unverified"
        fi
        if grep -Fq -- 'assets/snowflake' "$ZIP_LIST" || grep -Fq -- 'bin/snowflake' "$ZIP_LIST"; then
            pass "APK contains snowflake asset"
        else
            warn "No snowflake asset found; pluggable transport remains unverified"
        fi
    fi
else
    warn "No APK argument; APK archive checks not executed"
fi

printf 'SUMMARY failures=%d warnings=%d\n' "$FAILURES" "$WARNINGS"
if (( FAILURES > 0 )); then exit 1; fi
