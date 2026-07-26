#!/usr/bin/env python3
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
money = root / 'libraries/money/src/main/java'
identity = root / 'libraries/identity/src/main/java'
ledger = root / 'applications/fintech-application/ledger-domain/src/main/java'
tests = root / 'tests/domain-tests/ledger/src/test/java'

errors = []
java_files = sorted(list(money.rglob('*.java')) + list(identity.rglob('*.java')) + list(ledger.rglob('*.java')))
test_files = sorted(tests.rglob('*.java'))
if not java_files:
    errors.append('no Java source files found')

for path in java_files:
    text = path.read_text(encoding='utf-8')
    if path.is_relative_to(ledger):
        for forbidden in ('org.springframework', 'org.jooq', 'jakarta.persistence'):
            if forbidden in text:
                errors.append(f'{path.relative_to(root)} imports forbidden framework {forbidden}')
    if path.is_relative_to(money) and 'ledger.' in text:
        errors.append(f'{path.relative_to(root)} depends on ledger')
    if re.search(r'\b(?:TODO|FIXME|XXX)\b', text):
        errors.append(f'{path.relative_to(root)} contains an unresolved marker')

# Reject floating-point fields/parameters in the money and ledger domain. Comments are stripped lightly.
for path in java_files:
    text = re.sub(r'/\*.*?\*/', '', path.read_text(encoding='utf-8'), flags=re.S)
    text = re.sub(r'//.*', '', text)
    if re.search(r'\b(?:double|float)\b', text):
        errors.append(f'{path.relative_to(root)} contains floating-point money/domain type')

required = [
    root / 'pom.xml',
    root / 'libraries/money/pom.xml',
    root / 'libraries/identity/pom.xml',
    root / 'applications/fintech-application/ledger-domain/pom.xml',
    root / 'tests/domain-tests/ledger/pom.xml',
    root / 'docs/batch1/invariant-coverage.md',
    root / 'docs/batch1/deferred-work.md',
    root / 'SECURITY.md',
    root / 'CONTRIBUTING.md',
    root / 'docs/security-review/codex-security-review.md',
    root / 'docs/security-review/validation-and-rating.md',
]
for path in required:
    if not path.exists():
        errors.append(f'missing required file {path.relative_to(root)}')

for pom in sorted(root.rglob('pom.xml')):
    try:
        ET.parse(pom)
    except ET.ParseError as exception:
        errors.append(f'invalid XML in {pom.relative_to(root)}: {exception}')

root_pom = (root / 'pom.xml').read_text(encoding='utf-8')
if '<version>0.3.1</version>' not in root_pom:
    errors.append('root POM is not version 0.3.1')
for expected in ('<maven.compiler.release>25</maven.compiler.release>',
                 '<junit.version>5.14.4</junit.version>',
                 '<jqwik.version>1.9.3</jqwik.version>',
                 '<archunit.version>1.4.2</archunit.version>'):
    if expected not in root_pom:
        errors.append(f'root POM missing expected pin {expected}')

if '1.10.1' in root_pom or '1.10.1' in (root / 'README.md').read_text(encoding='utf-8'):
    errors.append('jqwik 1.10.1 must not be pinned in this AI-assisted project')


# Security boundary assertions. These are intentionally source-level companions to the
# executable and negative-compilation gates in verify-batch1.sh.
security_expectations = {
    ledger / 'com/bikash/fintechsettlement/ledger/domain/policy/PostingPolicyValidator.java':
        ('PAYMENT_CAPTURE_CONFIRMED', 'PAYMENT_REFUND_CONFIRMED', 'SETTLEMENT_CONFIRMED'),
    ledger / 'com/bikash/fintechsettlement/ledger/domain/kernel/LedgerKernel.java':
        ('public static final class Access', 'private Access()', 'policyValidator.validate(command)',
         'LedgerKernel(PlatformId platformId'),
    ledger / 'com/bikash/fintechsettlement/ledger/domain/transaction/LedgerTransaction.java':
        ('private LedgerTransaction(', 'createForKernel('),
    ledger / 'com/bikash/fintechsettlement/ledger/domain/transaction/LedgerTransactionFactory.java':
        ('LedgerKernel.Access access', 'ValidatedPostingCommand validatedCommand'),
}
for path, fragments in security_expectations.items():
    if not path.exists():
        errors.append(f'missing security control {path.relative_to(root)}')
        continue
    text = path.read_text(encoding='utf-8')
    for fragment in fragments:
        if fragment not in text:
            errors.append(f'{path.relative_to(root)} missing security boundary fragment {fragment!r}')

legacy_access = ledger / 'com/bikash/fintechsettlement/ledger/domain/kernel/LedgerKernelAccess.java'
if legacy_access.exists():
    errors.append('legacy forgeable LedgerKernelAccess class must not exist')

if errors:
    print('STATIC VALIDATION FAILED')
    for error in errors:
        print('-', error)
    sys.exit(1)

print(f'STATIC VALIDATION PASSED: {len(java_files)} main Java files')
print(f'Maven test sources found: {len(test_files)}')
print('POM XML parsing: passed')
print('Dependency pins: passed')
print('Framework independence: passed')
print('No floating-point domain types: passed')
print('No unresolved source markers: passed')
print('Required project structure: passed')
