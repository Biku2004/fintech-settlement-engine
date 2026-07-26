#!/usr/bin/env python3
from pathlib import Path
import hashlib
import sys

root = Path(__file__).resolve().parents[1]
manifest = root / 'MANIFEST.md'
if not manifest.exists():
    print('MANIFEST.md missing')
    sys.exit(1)

expected = {}
for line in manifest.read_text(encoding='utf-8').splitlines():
    if line.startswith('- `') and '` — `' in line:
        path_part, hash_part = line[3:].split('` — `', 1)
        rel = path_part
        sha = hash_part.rstrip('`')
        expected[rel] = sha

errors = []
for rel, sha in expected.items():
    path = root / rel
    if not path.is_file():
        errors.append(f'missing {rel}')
        continue
    actual = hashlib.sha256(path.read_bytes()).hexdigest()
    if actual != sha:
        errors.append(f'hash mismatch {rel}')

actual_files = {
    str(path.relative_to(root))
    for path in root.rglob('*')
    if path.is_file() and 'build' not in path.parts and path.name != 'MANIFEST.md'
}
missing_from_manifest = sorted(actual_files - set(expected))
extra_in_manifest = sorted(set(expected) - actual_files)
errors.extend(f'unlisted file {path}' for path in missing_from_manifest)
errors.extend(f'manifest-only file {path}' for path in extra_in_manifest)

if errors:
    print('MANIFEST VALIDATION FAILED')
    for error in errors:
        print('-', error)
    sys.exit(1)
print(f'MANIFEST VALIDATION PASSED: {len(expected)} files')
