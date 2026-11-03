# Koin

Koin is a pragmatic, lightweight dependency injection framework for Kotlin Multiplatform (Android, JVM, iOS/native, JS, WasmJS). This directory (`projects/`) is the Gradle build root.

Shared agent notes (layout, branches, build commands, contributing rules, never-do list, how to write Koin code): [`../AGENTS.md`](../AGENTS.md), loaded automatically through the root `../CLAUDE.md` import. This file adds the engine and contributor detail; keep both consistent.

## Doctrine

- **Evidence-based claims.** "fixed/verified/done" must cite file:line, test output, or a repro run — never "looks right + tests pass".
- **Falsify, don't confirm.** A test must try to BREAK the implementation. Prove RED before GREEN: a regression test must fail on the unfixed code.
- **Resolution behavior is a contract.** Koin's resolution order/matching semantics are user-observable API — treat any change there as a compatibility event, not an implementation detail.

> **SURPRISE RULE — mandatory.** If the project surprises you or contradicts these docs, STOP, tell the user, and record it here (or in the closest module doc). "Surprising" = not inferable from the code in one grep.

## Module Map

| Group | Modules | Notes |
|---|---|---|
| `core/` | **koin-core** (engine), koin-core-coroutines, koin-core-viewmodel, koin-core-annotations, koin-annotations, koin-test, koin-test-coroutines, koin-test-junit4/5, benchmark | KMP |
| `android/` | koin-android, koin-android-compat, koin-androidx-navigation, koin-androidx-workmanager, koin-androidx-startup, koin-dagger-bridge, koin-android-test | Android |
| `compose/` | koin-compose, koin-compose-viewmodel, koin-compose-navigation3, koin-compose-viewmodel-navigation, koin-androidx-compose(-navigation) | Compose MP + AndroidX |
| `ktor/` | koin-ktor (KMP: JVM, JS, wasmJs, native), koin-logger-slf4j (JVM) | Ktor server |
| `plugins/` | koin-gradle-plugin | |
| `bom/` | koin-bom | Aligns all artifact versions |

### Key engine areas (`core/koin-core/src/commonMain/kotlin/org/koin/`)

- `core/resolution/` — instance resolution. `CoreResolverV2.kt` is the 4.2+ engine: injected params → stacked params → registry (own scopeQualifier defs → own archetype defs → scope source *(unqualified only)* → linked scopes) → resolution extensions; see `resolveFromContextOrNull` / `resolveFromRegistry`
  - **Qualified lookups are registry-only.** A `get(named(...))` skips the stacked-parameter path entirely — parameters carry no qualifier (`ParametersHolder` matches by type only), so a stacked param can never satisfy a qualified request. Removing this guard re-opens #2370/#2408 (a `parametersOf` value shadowing a qualified dependency of the same type). Only unqualified resolution reads the param stack — that's the #2387 ViewModel path; don't break it.
- `core/registry/` — definition & instance registries (indexing by type + qualifier)
- `core/scope/` — `Scope`, scope archetypes, linked scopes
- `core/module/` — module DSL (`single`, `factory`, `scoped`, `bind`, `includes`)
- `mp/` — multiplatform abstractions (`KoinPlatform`, platform tools)

## Build & Test (run from `projects/`)

```bash
./gradlew :core:koin-core:jvmTest              # one module, JVM only (fast) — KMP modules have no plain `test` task
./gradlew :core:koin-core:allTests             # one module, all KMP targets
./test.sh                                      # clean JVM-side run: `test` (Android/JVM) + `jvmTest` (KMP modules); non-JVM targets need allTests / test-macos.sh
./test-macos.sh                                # koin-core on macosArm64 only
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
- **Stale root `.api` dumps** (as of 2026-10-01): `koin-core-viewmodel`, `koin-compose`, `koin-compose-viewmodel`, `koin-compose-viewmodel-navigation` keep an old root-level `api/<module>.api` beside the live `api/jvm` + `api/android` dumps (BCV ignores it with 2 JVM targets); `koin-ktor` has the live `koin-ktor.api` and a dead `koin-ktor3.api`. Don't edit the dead ones (the four root dumps and `koin-ktor3.api`).
- **AGP 9 silently disables `apiCheck` for Android code** (upstream, not yet hit here — repo is AGP 8.13 / Gradle 8.14.3): BCV 0.18 registers no tasks under AGP built-in Kotlin (BCV #312) and skips the Android target of `com.android.kotlin.multiplatform.library` (BCV #315) while staying green. KGP `abiValidation` covers the KMP Android target only from Kotlin 2.4.20 (KT-85950); Android-only modules have no path yet (KT-83410). Solve API validation before bumping AGP.
- **Removals or signature changes of public API are not acceptable in patch releases** — additions only. Breaking changes need a minor/major version and an explicit deprecation cycle (`@Deprecated` with `ReplaceWith` first).
- Behavioral compatibility counts as API: a PR that changes resolution order/matching semantics must state the old vs new behavior in its description, even if no signature changed.

### 2. New tests for every new case or fixed use-case
- **Bug fix** → a regression test that reproduces the issue: fails before the fix, passes after. Place it in `commonTest` whenever the code under test is common.
- **New case/feature** → tests covering the new behavior, including the edge that motivated it (qualifiers, scopes, params, platform-specific paths).
- A PR touching `commonMain` without an accompanying test change needs an explicit justification in the description.
- KMP: tests must run beyond JVM when the code is common (`allTests`, or at minimum compile-check one native target + `wasmJs`).

The PR template (`../.github/pull_request_template.md`) carries both guards as a checklist — fill it, don't delete it.

### Guardrails enforced by `.claude/settings.json` (repo root and `projects/`)
Claude Code reads shared settings only from the launch directory, so both files carry the deny/ask rules below — keep them in sync. Differences: only the root file denies edits to `docs/`, only `projects/` pre-allows the test scripts and `apiCheck`.
- **Denied:** `release.sh` / Central Portal publishing (releases are cut by the `release.yml` workflow, never by an agent), force-push, edits to the dead root `.api` dumps.
- **Asks first:** `git push`, `gh pr create|merge`, any Gradle run with `-x` exclusions.

## Conventions

- **KMP discipline:** `commonMain` changes must be verified beyond JVM — compile at least one native target and `wasmJs` before considering a change done. Several bug classes (qualifiedName, klib, JS) only surface off-JVM.
- **Every bug fix ships a regression test**, in `commonTest` whenever the code is common.
- **Commit style:** GitHub issue → `Fix #NNNN - description` (e.g. `Fix #2387 - CoreResolverV2 lost stacked-params lookup on linked scopes`); otherwise a plain description. No internal tracker IDs in commits, code or docs.
- **Public API changes:** run `./compare_apis.sh`; avoid breaking binary compatibility in patch releases.
- **Resolution-semantics changes** (anything in `core/resolution/`): describe the behavioral delta (old vs new resolution order/matching) in the PR description — resolver behavior changes have historically caused regressions.
- **User-visible changes need a docs touch** on https://insert-koin.io/docs — say so in the PR description. `../docs/` is a legacy, unmaintained copy: never edit it.

### KMP test traps (hard rules)

- **Never weaken `test.sh` to scoped tasks.** In KMP projects `./gradlew test` resolves only to Android/JVM unit tests and **silently skips** native, JS, and WASM tests. Full verification = `allTests` (or per-target tasks explicitly). Do not add `-x` exclusions to make a run green — fix the failing test at the root cause.
- **Prefer snake_case test names in new `commonTest` code** (style; backticks are fine in `jvmTest`). Backticks are not a proven hazard: they compile for `compileTestKotlinJs`/`compileTestKotlinWasmJs` on Kotlin 2.3.20 (36 `koin-core` `commonTest` files use them), and the native reporter crash below is unrelated to names (verified 2026-11-03). Don't mass-rename existing files.
- **`runTest {}` in `commonTest`: use block-body syntax** — `fun testX() { runTest { ... } }`. Expression-body (`= runTest { }`) was reported to fail on wasmJs with a return-type mismatch; it compiles today (`ConcurrencyTest.kt:39`) but is unverified at runtime, so don't spread it.

#### Known pre-existing off-JVM failures (not yours — don't chase them)

`allTests` is currently red on two counts unrelated to any recent resolver work (first verified by stash-comparison on the `4.2.2` branch, 2026-06-08; both re-checked on `4.3.0`, see dates below). If you touch `commonMain` and run `allTests`, expect these and confirm your diff doesn't change them:

- **`createdAtStart` on wasmJs and JS** (node and browser, re-verified on `4.3.0` 2026-11-02) — `CreateOnStart`, `OverrideAndCreateatStartTest` fail with `AssertionError: Expected value to be true`. Core `single(createdAtStart = true)` eager-init doesn't fire on JS/wasmJs. This is *core*, **not** the compiler-plugin `createdAtStart` drop (#2425/#2415) — same symptom family, different layer.
- **`macosArm64Test` reports FAILED although every test passes** — Gradle's result writer throws `IllegalArgumentException: Multiple entries with same key … TestOutputStore$Index` ("Could not write XML test results for <class>"), on a different class each run. It is a Gradle/KGP reporting bug, not test names: renaming `DynamicModulesTest` to snake_case changed nothing (3 runs, 2026-11-03). Read the per-class XML under `build/test-results/macosArm64Test/` for the real outcome (303 tests, 0 failures on `4.3.0`).

## Versioning

- Version is set in `gradle.properties` (`koinVersion`).
- Branch lines (as of 2026-11-02):
  - `main` — current stable line (`koinVersion=4.2.2`). Patch fixes target `main`, or the open patch release branch when one exists (4.2.2 fixes #2437–#2448 went to `4.2.2`). Regressions/bug fixes only — no API removals.
  - `4.3.0` — next minor (`4.3.0-Beta1`). Features and 4.3 work target `4.3.0`; topic branches (e.g. `4.3-perfs`) branch from it. `main` is merged into `4.3.0` to carry fixes forward.
  - Unsure which line a change belongs to? Ask — don't guess the base branch.
- `koin-bom` must stay aligned with all published modules.

### Release notes: Added vs Fixed

The test: **"Would this affect a user on the previous supported version range?"**
- Yes → `Fixed` (genuine regression or pre-existing bug)
- No, only relevant to a new Kotlin/AGP/library range → `Added` (new support story), as ONE umbrella entry with sub-bullets — users care that "Kotlin X works", not how many internal changes it took.
- Internal refactor with identical user-visible behavior → internal changes, not `Changed`.
