from pathlib import Path
import csv, re, sys

root=Path(__file__).resolve().parents[1]
files=[p for p in root.rglob('*') if p.is_file() and p.name!='MANIFEST.md']
manifest=(root/'MANIFEST.md').read_text(encoding='utf-8')
manifest_entries={}
for line in manifest.splitlines():
    if line.startswith('| `'):
        parts=line.split('`')
        rel=parts[1]; digest=parts[3]
        try:
            size=int(line.rsplit('|',2)[1].strip())
        except Exception:
            size=-1
        manifest_entries[rel]=(digest,size)
manifest_paths=set(manifest_entries)
actual={str(p.relative_to(root)).replace('\\','/') for p in files}
errors=[]
if manifest_paths != actual:
    errors.append(f'manifest mismatch missing={sorted(actual-manifest_paths)} extra={sorted(manifest_paths-actual)}')
for p in files:
    rel=str(p.relative_to(root)).replace('\\','/')
    if rel in manifest_entries:
        import hashlib
        data=p.read_bytes(); digest=hashlib.sha256(data).hexdigest(); size=len(data)
        if manifest_entries[rel] != (digest,size):
            errors.append(f'manifest checksum/size mismatch {rel}')
ids=[]
for p in (root/'docs/domain').glob('*invariants.md'):
    ids += re.findall(r'\|\s*([A-Z]+-\d{3})\s*\|', p.read_text(encoding='utf-8'))
if len(ids)!=len(set(ids)): errors.append('duplicate invariant IDs')
with (root/'docs/test-strategy/invariant-test-traceability.csv').open(encoding='utf-8') as f:
    rows=list(csv.DictReader(f))
trace={r['Invariant ID'] for r in rows}
if trace != set(ids): errors.append(f'trace mismatch missing={set(ids)-trace} orphan={trace-set(ids)}')
for r in rows:
    s=r['Concrete Given/When/Then Scenario']
    if not all(x in s for x in ('Given','when','then')): errors.append(f'non-concrete scenario {r["Invariant ID"]}')
required=[
'docs/domain/ledger-account-model-and-posting-policies.md',
'docs/api/api-behaviour-contract.md',
'docs/architecture/relational-model-and-constraints.md',
'docs/observability/observability-specification.md',
'docs/domain/settlement-and-payout-state-machines.md',
'docs/domain/reconciliation-state-machines.md']
for r in required:
    if not (root/r).exists(): errors.append(f'missing required file {r}')
text=(root/'docs/domain/payment-state-machine.md').read_text(encoding='utf-8')
for token in ['CANCELLATION_UNKNOWN','REFUND_UNKNOWN','preUnknownState','PARTIALLY_REFUNDED']:
    if token not in text: errors.append(f'payment state spec missing {token}')
print(f'files={len(actual)+1} invariants={len(ids)} trace_rows={len(rows)}')
if errors:
    print('\n'.join(errors)); sys.exit(1)
print('Batch 0 structural/content consistency checks: PASS')
