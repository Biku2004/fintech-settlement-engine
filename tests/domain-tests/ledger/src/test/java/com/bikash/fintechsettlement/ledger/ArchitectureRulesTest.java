package com.bikash.fintechsettlement.ledger;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.bikash.fintechsettlement",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureRulesTest {

    @ArchTest
    static final ArchRule domain_must_not_depend_on_frameworks = noClasses()
            .that().resideInAPackage("..ledger.domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..", "org.jooq..", "jakarta.persistence..");

    @ArchTest
    static final ArchRule money_must_not_depend_on_ledger = noClasses()
            .that().resideInAPackage("..shared.money..")
            .should().dependOnClassesThat()
            .resideInAPackage("..ledger..");
}
