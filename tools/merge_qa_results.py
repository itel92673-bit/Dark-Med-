#!/usr/bin/env python3
import argparse
import csv
import hashlib
import json
from collections import Counter
from pathlib import Path


def sha256(path):
    digest = hashlib.sha256()
    with path.open('rb') as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b''):
            digest.update(chunk)
    return digest.hexdigest()


def load_json(path):
    try:
        return json.loads(path.read_text(encoding='utf-8'))
    except Exception:
        return None


def parse_status(path):
    values = {}
    for line in path.read_text(encoding='utf-8', errors='replace').splitlines():
        if '=' in line:
            key, value = line.split('=', 1)
            values[key.strip()] = value.strip()
    if 'status' not in values:
        exit_code = values.get('exit_code', '')
        values['status'] = {'20': 'BLOCKED', '1': 'FAIL', '0': 'PASS'}.get(exit_code, 'NOT_TESTED')
    manifest = path.parent / 'manifest.txt'
    if not values.get('apk_sha256') and manifest.is_file():
        for line in manifest.read_text(encoding='utf-8', errors='replace').splitlines():
            if line.startswith('apk_sha256='):
                values['apk_sha256'] = line.split('=', 1)[1].strip()
    return values


def collect(root):
    records = []
    for path in root.rglob('result.json'):
        value = load_json(path)
        if isinstance(value, dict):
            records.append({'source': str(path), 'provider': value.get('provider', 'unknown'), 'status': value.get('status', 'NOT_TESTED'), 'apk_sha256': value.get('apk_sha256', ''), 'device_name': value.get('device_name', ''), 'platform_version': value.get('platform_version', ''), 'artifacts': value.get('artifacts', [])})
    for path in root.rglob('status.txt'):
        values = parse_status(path)
        if values:
            records.append({'source': str(path), 'provider': values.get('provider', path.parent.name), 'status': values.get('status', 'NOT_TESTED'), 'apk_sha256': values.get('apk_sha256', ''), 'device_name': values.get('device', values.get('serial', '')), 'platform_version': values.get('api', values.get('platform_version', '')), 'artifacts': []})
    return records


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--artifact', required=True)
    parser.add_argument('--input-root', required=True)
    parser.add_argument('--output', required=True)
    args = parser.parse_args()
    artifact = Path(args.artifact).resolve()
    root = Path(args.input_root).resolve()
    expected = sha256(artifact)
    records = collect(root)
    for record in records:
        if record['apk_sha256'] and record['apk_sha256'] != expected:
            record['status'] = 'FAIL'
            record['blocker'] = 'artifact SHA mismatch'
    counts = Counter(record['status'] for record in records)
    summary = {'artifact': str(artifact), 'apk_sha256': expected, 'record_count': len(records), 'status_counts': dict(counts), 'real_device_passes': sum(1 for record in records if record['status'] == 'PASS' and record['device_name']), 'records': records, 'no_false_pass_rule': 'PASS requires exact SHA and real device metadata; missing/unknown fields remain NOT_TESTED or BLOCKED'}
    Path(args.output).write_text(json.dumps(summary, indent=2, ensure_ascii=False) + '\n', encoding='utf-8')
    print(json.dumps({'apk_sha256': expected, 'record_count': len(records), 'status_counts': dict(counts)}, ensure_ascii=False))


if __name__ == '__main__':
    main()
