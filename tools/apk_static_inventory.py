from pathlib import Path
import hashlib
import re
import sys
import zipfile

apk = Path(sys.argv[1])
out = Path(sys.argv[2])
out.mkdir(parents=True, exist_ok=True)
data = apk.read_bytes()
with zipfile.ZipFile(apk) as archive:
    names = archive.namelist()
    records = []
    for name in names:
        payload = archive.read(name)
        urls = sorted(set(re.findall(rb'https?://[^\x00\x20\x22\x27<>]+', payload)))
        marker_hits = [marker for marker in (b'obfs4', b'snowflake', b'tor', b'wireguard', b'hev-socks5') if marker.lower() in payload.lower()]
        if urls or marker_hits:
            records.append({"name": name, "size": len(payload), "urls": [u.decode("utf-8", "replace") for u in urls], "markers": [m.decode() for m in marker_hits]})
    (out / "zip_entries.txt").write_text("\n".join(f"{name}\t{info['size']}" for name, info in sorted(((n, {"size": archive.getinfo(n).file_size}) for n in names))) + "\n")
    (out / "native_entries.txt").write_text("\n".join(sorted(n for n in names if n.startswith("lib/") and n.endswith((".so", ".dat")))) + "\n")
    (out / "marker_and_url_entries.txt").write_text("\n".join(str(record) for record in records) + "\n")
manifest = next((n for n in names if n == "AndroidManifest.xml"), None)
(out / "identity.txt").write_text(f"path={apk}\nsize={len(data)}\nsha256={hashlib.sha256(data).hexdigest()}\nmanifest_entry={manifest}\n")
