# Dependency Pins

| Dependency | Version | Scope |
|---|---:|---|
| Java | 25 target | Production compiler target |
| JUnit Jupiter | 5.14.4 | Unit/security test suite |
| jqwik | 1.9.3 | Property tests |
| ArchUnit | 1.4.2 | Architecture rules |

The production Money and Ledger modules have no third-party runtime dependencies. Test dependencies are centrally pinned in the root POM.

Exact artifact/release records were checked during the security review. The active environment could not perform Maven dependency resolution, so availability was verified from current repository/release metadata rather than by a local `mvn` download. A JDK 25 `mvn clean verify` remains an external CI gate.
