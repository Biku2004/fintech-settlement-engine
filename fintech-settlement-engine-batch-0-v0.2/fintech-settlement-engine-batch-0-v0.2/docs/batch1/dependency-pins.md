# Batch 1 Dependency Pins

| Tool/library | Version | Purpose |
|---|---:|---|
| Java | 25 target | Production project baseline |
| Maven | 3.9+ | Multi-module build |
| JUnit Jupiter | 5.14.4 | Example/unit test suite |
| jqwik | 1.9.3 | Property-test source |
| ArchUnit | 1.4.2 | Domain dependency rules |

## Important verification distinction

The container provides JDK 21 but no Maven or external dependency cache. Therefore:

- all production sources compile with `javac --release 21`, warnings as errors;
- the code is source-compatible with the Java 25 target because it uses no preview or post-21 language feature;
- JUnit/jqwik/ArchUnit test source is syntax-compiled against minimal API stubs;
- the real Maven dependency-engine execution remains a developer/CI gate under JDK 25.

jqwik remains on the 1.9.x line for this AI-assisted repository. The 1.10.x project guidance explicitly discourages coding-agent usage.
