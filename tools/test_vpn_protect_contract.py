from pathlib import Path

root = Path(__file__).resolve().parents[1]
service = (root / "app/src/main/java/com/darkmed/app/core/DarkMedVpnService.kt").read_text()
bridge = (root / "app/src/main/java/com/darkmed/app/core/HevTun2Socks.kt").read_text()
native = (root / "app/src/main/jni/darkmed_tun2socks_jni.cpp").read_text()
socket_header = (root / "app/src/main/jni/hev-socks5-tunnel/third-part/hev-task-system/include/hev-task-io-socket.h").read_text()
socket_source = (root / "app/src/main/jni/hev-socks5-tunnel/third-part/hev-task-system/src/lib/io/socket/hev-task-io-socket.c").read_text()

checks = {
    "service_passes_protector": "candidate.start(config.path, fd, this)" in service,
    "bridge_uses_vpnservice": "fun start(configPath: String, tunFd: Int, protector: VpnService)" in bridge,
    "native_requires_protector": "jobject protector" in native and "protector == nullptr" in native,
    "native_calls_vpn_protect": '"protect", "(I)Z"' in native and "CallBooleanMethod" in native,
    "callback_registered": "hev_task_io_socket_set_protect_callback(protect_socket)" in native,
    "socket_rejected_when_unprotected": "!g_protect_callback (fd)" in socket_source and "close (fd)" in socket_source,
    "callback_api_declared": "HevTaskIOSocketProtect" in socket_header and "set_protect_callback" in socket_header,
    "callback_cleared_on_stop": "clear_protector(env)" in native,
}

for name, passed in checks.items():
    print(f"{name}={'PASS' if passed else 'FAIL'}")
failed = [name for name, passed in checks.items() if not passed]
if failed:
    raise SystemExit("VPN protect contract failures: " + ", ".join(failed))
