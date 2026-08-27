#!/usr/bin/env python3
import argparse
import base64
import json
import os
import sys
import time
import urllib.error
import urllib.request
from datetime import datetime, timezone
from pathlib import Path

APP_PACKAGE = "com.darkmed.app"
APP_ACTIVITY = "com.darkmed.app.MainActivity"


def now():
    return datetime.now(timezone.utc).isoformat()


def request_json(url, method="GET", payload=None, headers=None, timeout=90):
    body = None
    request_headers = {"Accept": "application/json", "Content-Type": "application/json"}
    if headers:
        request_headers.update(headers)
    if payload is not None:
        body = json.dumps(payload).encode("utf-8")
    request = urllib.request.Request(url, data=body, headers=request_headers, method=method)
    with urllib.request.urlopen(request, timeout=timeout) as response:
        raw = response.read()
        if not raw:
            return response.status, {}
        return response.status, json.loads(raw.decode("utf-8"))


def unwrap(response):
    if not isinstance(response, dict):
        return response
    if "value" in response:
        return response["value"]
    return response


def save_json(path, value):
    path.write_text(json.dumps(value, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def save_screenshot(path, value):
    path.write_bytes(base64.b64decode(value))


def normalized_base(url):
    return url.rstrip("/")


def app_reference(provider):
    env_name = {
        "browserstack": "BROWSERSTACK_APP_REF",
        "sauce": "SAUCE_APP_REF",
        "kobiton": "KOBITON_APP_REF",
        "perfecto": "PERFECTO_APP_REF",
    }.get(provider)
    if env_name and os.environ.get(env_name):
        return os.environ[env_name]
    return None


def build_capabilities(args):
    app_ref = app_reference(args.provider)
    if not app_ref:
        raise RuntimeError(f"missing provider app reference for {args.provider}; upload must complete before Appium execution")
    caps = {
        "platformName": "Android",
        "appium:automationName": "UiAutomator2",
        "appium:deviceName": args.device_name,
        "appium:platformVersion": args.platform_version,
        "appium:app": app_ref,
        "appium:appPackage": APP_PACKAGE,
        "appium:appActivity": APP_ACTIVITY,
        "appium:autoGrantPermissions": False,
        "appium:noReset": False,
        "appium:newCommandTimeout": 180,
    }
    if args.provider == "perfecto":
        caps["perfecto:securityToken"] = os.environ.get("PERFECTO_SECURITY_TOKEN", "")
        caps["perfecto:appiumVersion"] = os.environ.get("PERFECTO_APPIUM_VERSION", "latest")
        caps["perfecto:enableAppiumBehavior"] = True
        caps["perfecto:recordVideo"] = True
        caps["perfecto:screenshotOnFailure"] = True
    if args.provider == "sauce":
        caps["sauce:options"] = {
            "name": "Dark Med Universal Appium Suite",
            "build": args.build_id,
            "appiumVersion": os.environ.get("SAUCE_APPIUM_VERSION", "latest"),
            "recordVideo": True,
            "capturePerformance": True,
        }
    if args.provider == "browserstack":
        caps["bstack:options"] = {
            "projectName": "Dark Med",
            "buildName": args.build_id,
            "sessionName": "Universal Appium Suite",
            "video": True,
            "debug": True,
        }
    if args.provider == "kobiton":
        caps["kobiton:sessionName"] = "Dark Med Universal Appium Suite"
    return {"capabilities": {"alwaysMatch": caps, "firstMatch": [{}]}}


def run(args):
    output = Path(args.output_dir)
    output.mkdir(parents=True, exist_ok=True)
    started = now()
    result = {
        "provider": args.provider,
        "device_name": args.device_name,
        "platform_version": args.platform_version,
        "started_at": started,
        "status": "NOT_TESTED",
        "tests": [],
        "artifacts": [],
        "apk_sha256": args.apk_sha256,
        "execution": "real Appium session only; no simulated fallback",
    }
    if not args.server_url:
        result["status"] = "BLOCKED"
        result["blocker"] = "APPIUM_SERVER_URL is missing"
        save_json(output / "result.json", result)
        return 20
    try:
        status_code, status_payload = request_json(normalized_base(args.server_url) + "/status")
        save_json(output / "server_status.json", status_payload)
        result["tests"].append({"id": "APPIUM_SERVER_STATUS", "status": "PASS" if status_code == 200 else "FAIL", "http_status": status_code})
        if status_code != 200:
            result["status"] = "FAIL"
            result["blocker"] = "Appium server status did not return HTTP 200"
            save_json(output / "result.json", result)
            return 1
        capabilities = build_capabilities(args)
        status_code, session_payload = request_json(normalized_base(args.server_url) + "/session", method="POST", payload=capabilities, headers=args.auth_headers)
        save_json(output / "session_create.json", session_payload)
        session_value = unwrap(session_payload)
        session_id = session_value.get("sessionId") if isinstance(session_value, dict) else None
        if not session_id and isinstance(session_payload, dict):
            session_id = session_payload.get("sessionId")
        if not session_id:
            result["status"] = "FAIL"
            result["blocker"] = "Appium session was not created"
            result["tests"].append({"id": "SESSION_CREATE", "status": "FAIL", "http_status": status_code})
            save_json(output / "result.json", result)
            return 1
        result["session_id"] = session_id
        session_url = normalized_base(args.server_url) + "/session/" + session_id
        status_code, session_details = request_json(session_url)
        save_json(output / "session_details.json", session_details)
        session_data = unwrap(session_details)
        if isinstance(session_data, dict):
            result["capabilities_returned"] = session_data
        result["tests"].append({"id": "SESSION_CREATE", "status": "PASS", "http_status": status_code})
        status_code, window_size = request_json(session_url + "/window/size")
        save_json(output / "window_size.json", window_size)
        result["tests"].append({"id": "WINDOW_SIZE", "status": "PASS" if status_code == 200 else "FAIL", "http_status": status_code})
        status_code, source = request_json(session_url + "/source")
        save_json(output / "page_source.json", source)
        result["tests"].append({"id": "PAGE_SOURCE", "status": "PASS" if status_code == 200 else "FAIL", "http_status": status_code})
        status_code, screenshot = request_json(session_url + "/screenshot")
        screenshot_value = unwrap(screenshot)
        if status_code == 200 and isinstance(screenshot_value, str):
            save_screenshot(output / "screenshot.png", screenshot_value)
            result["artifacts"].append("screenshot.png")
        result["tests"].append({"id": "SCREENSHOT", "status": "PASS" if status_code == 200 else "FAIL", "http_status": status_code})
        for label, query in (("DASHBOARD", "//*[contains(@text,'Dashboard') or contains(@content-desc,'Dashboard') or contains(@text,'لوحة') or contains(@content-desc,'لوحة')]"), ("SECURITY_CENTER", "//*[contains(@text,'Security') or contains(@content-desc,'Security') or contains(@text,'الأمان') or contains(@content-desc,'الأمان')]"), ("SETTINGS", "//*[contains(@text,'Settings') or contains(@content-desc,'Settings') or contains(@text,'الإعدادات') or contains(@content-desc,'الإعدادات')]") ):
            status_code, element_payload = request_json(session_url + "/element", method="POST", payload={"using": "xpath", "value": query})
            save_json(output / f"element_{label.lower()}.json", element_payload)
            element_value = unwrap(element_payload)
            found = status_code == 200 and isinstance(element_value, dict) and bool(element_value.get("element-6066-11e4-a52e-4f735466cecf") or element_value.get("ELEMENT"))
            result["tests"].append({"id": f"UI_{label}", "status": "PASS" if found else "FAIL", "http_status": status_code, "query": query})
        request_json(session_url, method="DELETE")
        result["tests"].append({"id": "SESSION_CLOSE", "status": "PASS"})
        result["status"] = "PASS" if all(test["status"] == "PASS" for test in result["tests"]) else "FAIL"
    except urllib.error.HTTPError as error:
        result["status"] = "FAIL"
        result["blocker"] = f"HTTP {error.code} from Appium endpoint"
        result["error"] = error.read().decode("utf-8", errors="replace")[:2000]
    except Exception as error:
        result["status"] = "FAIL"
        result["blocker"] = str(error)
    result["finished_at"] = now()
    save_json(output / "result.json", result)
    return 0 if result["status"] == "PASS" else 1


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--provider", required=True, choices=("browserstack", "kobiton", "sauce", "perfecto"))
    parser.add_argument("--server-url", default=os.environ.get("APPIUM_SERVER_URL", ""))
    parser.add_argument("--device-name", default=os.environ.get("APPIUM_DEVICE_NAME", ""))
    parser.add_argument("--platform-version", default=os.environ.get("APPIUM_PLATFORM_VERSION", ""))
    parser.add_argument("--output-dir", required=True)
    parser.add_argument("--apk-sha256", required=True)
    parser.add_argument("--build-id", default=os.environ.get("DARKMED_BUILD_ID", "dark-med"))
    args = parser.parse_args()
    if not args.device_name or not args.platform_version:
        print("BLOCKED: APPIUM_DEVICE_NAME and APPIUM_PLATFORM_VERSION are required", file=sys.stderr)
        return 20
    args.auth_headers = {}
    if args.provider == "browserstack":
        username = os.environ.get("BROWSERSTACK_USERNAME", "")
        access_key = os.environ.get("BROWSERSTACK_ACCESS_KEY", "")
        if not username or not access_key:
            print("BLOCKED: BrowserStack credentials are missing", file=sys.stderr)
            return 20
        import base64 as _base64
        args.auth_headers["Authorization"] = "Basic " + _base64.b64encode(f"{username}:{access_key}".encode()).decode()
    elif args.provider == "sauce":
        username = os.environ.get("SAUCE_USERNAME", "")
        access_key = os.environ.get("SAUCE_ACCESS_KEY", "")
        if not username or not access_key:
            print("BLOCKED: Sauce Labs credentials are missing", file=sys.stderr)
            return 20
        import base64 as _base64
        args.auth_headers["Authorization"] = "Basic " + _base64.b64encode(f"{username}:{access_key}".encode()).decode()
    elif args.provider == "kobiton":
        username = os.environ.get("KOBITON_USERNAME", "")
        api_key = os.environ.get("KOBITON_API_KEY", "")
        if not username or not api_key:
            print("BLOCKED: Kobiton credentials are missing", file=sys.stderr)
            return 20
        import base64 as _base64
        args.auth_headers["Authorization"] = "Basic " + _base64.b64encode(f"{username}:{api_key}".encode()).decode()
    elif args.provider == "perfecto" and not os.environ.get("PERFECTO_SECURITY_TOKEN"):
        print("BLOCKED: Perfecto security token is missing", file=sys.stderr)
        return 20
    return run(args)


if __name__ == "__main__":
    raise SystemExit(main())
