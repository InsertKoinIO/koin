# Koin

Koin is a pragmatic, lightweight dependency injection framework for Kotlin Multiplatform (Android, JVM, iOS/native, JS, WasmJS). This directory (`projects/`) is the Gradle build root.

## Doctrine

- **Evidence-based claims.** "fixed/verified/done" must cite file:line, test output, or a repro run — never "looks right + tests pass".
- **Falsify, don't confirm.** A test must try to BREAK the implementation. Prove RED before GREEN: a regression test must fail on the unfixed code.
- **Resolution behavior is a contract.** Koin's resolution order/matching semantics are user-observable API — treat any change there as a compatibility event, not an implementation detail.

> **SURPRISE RULE — mandatory.** If the project surprises you or contradicts these docs, STOP, tell the user, and record it here (or in the closest module doc). "Surprising" = not inferable from the code in one grep.

## Module Map

| Group | Modules | Notes |
|---|---|---|
| `core/` | **koin-core** (engine), koin-core-coroutines, koin-core-viewmodel, koin-core-annotations, koin-annotations, koin-test, koin-test-junit4/5, benchmark | KMP |
| `android/` | koin-android, koin-android-compat, koin-androidx-navigation, koin-androidx-workmanager, koin-androidx-startup, koin-dagger-bridge, koin-android-test | Android |
| `compose/` | koin-compose, koin-compose-viewmodel, koin-compose-navigation3, koin-compose-viewmodel-navigation, koin-androidx-compose(-navigation) | Compose MP + AndroidX |
| `ktor/` | koin-ktor, koin-logger-slf4j | JVM server |
| `plugins/` | koin-gradle-plugin | |
| `bom/` | koin-bom | Aligns all artifact versions |

### Key engine areas (`core/koin-core/src/commonMain/kotlin/org/koin/`)

- `core/resolution/` — instance resolution. `CoreResolverV2.kt` is the 4.2+ engine: injected params → stacked params → registry (scope source → linked scopes → archetype)
- `core/registry/` — definition & instance registries (indexing by type + qualifier)
- `core/scope/` — `Scope`, scope archetypes, linked scopes
- `core/module/` — module DSL (`single`, `factory`, `scoped`, `bind`, `includes`)
- `mp/` — multiplatform abstractions (`KoinPlatform`, platform tools)

## Build & Test (run from `projects/`)

```bash
./gradlew :core:koin-core:jvmTest              # one module, JVM only (fast) — KMP modules have no plain `test` task
./gradlew :core:koin-core:allTests             # one module, all KMP targets
./test.sh                                      # clean JVM-side run: `test` (Android/JVM) + `jvmTest` (KMP modules); non-JVM targets need allTests / test-macos.sh
./test-macos.sh                                # Apple/native targets
./gradlew :core:koin-core:compileKotlinWasmJs  # quick non-JVM compile check
./install.sh                                   # publish all to Maven Local
./benchmark.sh                                 # core benchmarks (core/benchmark)
./compare_apis.sh                              # public API diff (binary compatibility)
./clean.sh                                     # clean all builds
```

## PR Guards — check on EVERY pull request

Before opening (or approving) any PR, both guards must pass:

### 1. Don't break the current API
- `./gradlew apiCheck` must pass (kotlinx binary-compatibility-validator, applied at the root — `.api` dumps per module, e.g. `core/koin-core/api/koin-core.api`).
- If the PR intentionally **adds** public API: regenerate dumps with `./gradlew apiDump`, commit the `.api` diff, and review it with `./compare_apis.sh` (diffs `.api` files vs `main`).
- **Removals or signature changes of public API are not acceptable in patch releases** — additions only. Breaking changes need a minor/major version and an explicit deprecation cycle (`@Deprecated` with `ReplaceWith` first).
- Behavioral compatibility counts as API: a PR that changes resolution order/matching semantics must state the old vs new behavior in its description, even if no signature changed.

### 2. New tests for every new case or fixed use-case
- **Bug fix** → a regression test that reproduces the issue: fails before the fix, passes after. Place it in `commonTest` whenever the code under test is common.
- **New case/feature** → tests covering the new behavior, including the edge that motivated it (qualifiers, scopes, params, platform-specific paths).
- A PR touching `commonMain` without an accompanying test change needs an explicit justification in the description.
- KMP: tests must run beyond JVM when the code is common (`allTests`, or at minimum compile-check one native target + `wasmJs`).

## Conventions

- **KMP discipline:** `commonMain` changes must be verified beyond JVM — compile at least one native target and `wasmJs` before considering a change done. Several bug classes (qualifiedName, klib, JS) only surface off-JVM.
- **Every bug fix ships a regression test**, in `commonTest` whenever the code is common.
- **Commit style:** `Fix #NNNN - description` referencing the GitHub issue (e.g. `Fix #2387 - CoreResolverV2 lost stacked-params lookup on linked scopes`).
- **Public API changes:** run `./compare_apis.sh`; avoid breaking binary compatibility in patch releases.
- **Resolution-semantics changes** (anything in `core/resolution/`): describe the behavioral delta (old vs new resolution order/matching) in the PR description — resolver behavior changes have historically caused regressions.
- **User-visible changes need a docs touch** — documentation lives in `../docs/` (published to insert-koin.io).

### KMP test traps (hard rules)

- **Never weaken `test.sh` to scoped tasks.** In KMP projects `./gradlew test` resolves only to Android/JVM unit tests and **silently skips** native, JS, and WASM tests. Full verification = `allTests` (or per-target tasks explicitly). Do not add `-x` exclusions to make a run green — fix the failing test at the root cause.
- **No backtick test names in `commonTest`** — backtick names (`` fun `my test`() ``) break `compileTestKotlinJs`/`compileTestKotlinWasmJs` (invalid JS identifiers). Use snake_case in `commonTest`; backticks are fine in `jvmTest` only.
- **`runTest {}` in `commonTest` must use block-body syntax** — on wasmJs, expression-body forms (`fun foo() = runTest { }`) fail with return-type mismatch. Use `fun testX() { runTest { ... } }`.

## Versioning

- Version is set in `gradle.properties` (`koinVersion`).
- Branch lines: `4.2.x` = stable maintenance (regressions/bug fixes only), next minor in development on `main`.
- `koin-bom` must stay aligned with all published modules.

### Release notes: Added vs Fixed

The test: **"Would this affect a user on the previous supported version range?"**
- Yes → `Fixed` (genuine regression or pre-existing bug)
- No, only relevant to a new Kotlin/AGP/library range → `Added` (new support story), as ONE umbrella entry with sub-bullets — users care that "Kotlin X works", not how many internal changes it took.
- Internal refactor with identical user-visible behavior → internal changes, not `Changed`.
