#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD="$ROOT/build/offline-verification"
rm -rf "$BUILD"
mkdir -p "$BUILD/money" "$BUILD/identity" "$BUILD/ledger" "$BUILD/selftest"

mapfile -t MONEY_SOURCES < <(find "$ROOT/libraries/money/src/main/java" -name '*.java' | sort)
mapfile -t IDENTITY_SOURCES < <(find "$ROOT/libraries/identity/src/main/java" -name '*.java' | sort)
mapfile -t LEDGER_SOURCES < <(find "$ROOT/applications/fintech-application/ledger-domain/src/main/java" -name '*.java' | sort)
mapfile -t SELFTEST_SOURCES < <(find "$ROOT/tests/selftest/src/main/java" -name '*.java' | sort)

javac --release 21 -Xlint:all,-serial -Werror -d "$BUILD/money" "${MONEY_SOURCES[@]}"
javac --release 21 -Xlint:all,-serial -Werror -d "$BUILD/identity" "${IDENTITY_SOURCES[@]}"
javac --release 21 -Xlint:all,-serial -Werror -cp "$BUILD/money:$BUILD/identity" -d "$BUILD/ledger" "${LEDGER_SOURCES[@]}"
javac --release 21 -Xlint:all,-serial -Werror -cp "$BUILD/money:$BUILD/identity:$BUILD/ledger" -d "$BUILD/selftest" "${SELFTEST_SOURCES[@]}"

java -ea -cp "$BUILD/money:$BUILD/identity:$BUILD/ledger:$BUILD/selftest" \
  com.bikash.fintechsettlement.selftest.Batch1SelfTest | tee "$BUILD/selftest-output.txt"

# Compile all Maven test sources against minimal API stubs. This validates test syntax and
# domain API usage without pretending to execute unavailable external test engines offline.
STUB_SRC="$BUILD/test-api-stubs-src"
STUB_CLASSES="$BUILD/test-api-stubs"
TEST_CLASSES="$BUILD/test-classes"
mkdir -p \
  "$STUB_SRC/org/junit/jupiter/api/function" \
  "$STUB_SRC/org/junit/jupiter/api" \
  "$STUB_SRC/net/jqwik/api/constraints" \
  "$STUB_SRC/net/jqwik/api" \
  "$STUB_SRC/com/tngtech/archunit/core/importer" \
  "$STUB_SRC/com/tngtech/archunit/junit" \
  "$STUB_SRC/com/tngtech/archunit/lang/syntax" \
  "$STUB_SRC/com/tngtech/archunit/lang" \
  "$STUB_CLASSES" "$TEST_CLASSES"

cat > "$STUB_SRC/org/junit/jupiter/api/Test.java" <<'JAVA'
package org.junit.jupiter.api; public @interface Test {}
JAVA
cat > "$STUB_SRC/org/junit/jupiter/api/BeforeEach.java" <<'JAVA'
package org.junit.jupiter.api; public @interface BeforeEach {}
JAVA
cat > "$STUB_SRC/org/junit/jupiter/api/function/Executable.java" <<'JAVA'
package org.junit.jupiter.api.function; @FunctionalInterface public interface Executable { void execute() throws Throwable; }
JAVA
cat > "$STUB_SRC/org/junit/jupiter/api/Assertions.java" <<'JAVA'
package org.junit.jupiter.api;
import org.junit.jupiter.api.function.Executable;
public final class Assertions {
  private Assertions() {}
  public static void assertEquals(long expected, long actual) {}
  public static void assertEquals(int expected, long actual) {}
  public static void assertEquals(Object expected, Object actual) {}
  public static <T extends Throwable> T assertThrows(Class<T> type, Executable executable) { return null; }
}
JAVA
cat > "$STUB_SRC/net/jqwik/api/Property.java" <<'JAVA'
package net.jqwik.api; public @interface Property { int tries() default 1000; String seed() default ""; }
JAVA
cat > "$STUB_SRC/net/jqwik/api/ForAll.java" <<'JAVA'
package net.jqwik.api; public @interface ForAll {}
JAVA
cat > "$STUB_SRC/net/jqwik/api/constraints/LongRange.java" <<'JAVA'
package net.jqwik.api.constraints; public @interface LongRange { long min(); long max(); }
JAVA
cat > "$STUB_SRC/com/tngtech/archunit/core/importer/ImportOption.java" <<'JAVA'
package com.tngtech.archunit.core.importer; public interface ImportOption { final class DoNotIncludeTests implements ImportOption {} }
JAVA
cat > "$STUB_SRC/com/tngtech/archunit/junit/AnalyzeClasses.java" <<'JAVA'
package com.tngtech.archunit.junit;
import com.tngtech.archunit.core.importer.ImportOption;
public @interface AnalyzeClasses { String[] packages(); Class<? extends ImportOption>[] importOptions() default {}; }
JAVA
cat > "$STUB_SRC/com/tngtech/archunit/junit/ArchTest.java" <<'JAVA'
package com.tngtech.archunit.junit; public @interface ArchTest {}
JAVA
cat > "$STUB_SRC/com/tngtech/archunit/lang/ArchRule.java" <<'JAVA'
package com.tngtech.archunit.lang; public interface ArchRule {}
JAVA
cat > "$STUB_SRC/com/tngtech/archunit/lang/syntax/RuleBuilder.java" <<'JAVA'
package com.tngtech.archunit.lang.syntax;
import com.tngtech.archunit.lang.ArchRule;
public final class RuleBuilder implements ArchRule {
  public RuleBuilder that() { return this; }
  public RuleBuilder resideInAPackage(String value) { return this; }
  public RuleBuilder should() { return this; }
  public RuleBuilder dependOnClassesThat() { return this; }
  public RuleBuilder resideInAnyPackage(String... values) { return this; }
}
JAVA
cat > "$STUB_SRC/com/tngtech/archunit/lang/syntax/ArchRuleDefinition.java" <<'JAVA'
package com.tngtech.archunit.lang.syntax;
public final class ArchRuleDefinition {
  private ArchRuleDefinition() {}
  public static RuleBuilder noClasses() { return new RuleBuilder(); }
}
JAVA

mapfile -t STUB_SOURCES < <(find "$STUB_SRC" -name '*.java' | sort)
mapfile -t TEST_SOURCES < <(find "$ROOT/tests/domain-tests/ledger/src/test/java" -name '*.java' | sort)
javac --release 21 -Xlint:all,-serial -Werror -d "$STUB_CLASSES" "${STUB_SOURCES[@]}"
javac --release 21 -Xlint:all,-serial -Werror \
  -cp "$BUILD/money:$BUILD/identity:$BUILD/ledger:$STUB_CLASSES" \
  -d "$TEST_CLASSES" "${TEST_SOURCES[@]}"
echo "TEST SOURCE COMPILATION PASSED: ${#TEST_SOURCES[@]} files" | tee "$BUILD/test-source-compilation.txt"

python3 "$ROOT/scripts/validate_batch1.py" | tee "$BUILD/static-validation.txt"

echo "OFFLINE VERIFICATION PASSED"
