# AGENTS.md

Notes for AI coding agents (Claude Code, Codex, Cursor, Copilot, Junie…) working in this repository — the Koin framework sources.
Building an app **with** Koin rather than changing Koin itself? Read "Using Koin" below and skip the rest.

## Repository layout

| Path | Content |
|---|---|
| `projects/` | Gradle build root — every Koin library module (see `projects/settings.gradle.kts`) |
| `projects/core/` | `koin-core` (engine), `koin-core-coroutines`, `koin-core-viewmodel`, `koin-core-annotations`, `koin-annotations`, `koin-test*`, `benchmark` — KMP |
| `projects/android/` | `koin-android`, `koin-android-compat`, `koin-androidx-navigation`, `koin-androidx-workmanager`, `koin-androidx-startup`, `koin-dagger-bridge`, `koin-android-test` |
| `projects/compose/` | `koin-compose`, `koin-compose-viewmodel`, `koin-compose-navigation3`, `koin-compose-viewmodel-navigation`, `koin-androidx-compose(-navigation)` |
| `projects/ktor/` | `koin-ktor`, `koin-logger-slf4j` |
| `projects/plugins/`, `projects/bom/` | `koin-gradle-plugin`, `koin-bom` |
| `docs/` | Legacy copy of the docs, not maintained. The documentation is https://insert-koin.io/docs (agent index: https://insert-koin.io/llms.txt) |
| `examples/` | Sample apps |

The Koin Compiler Plugin lives in its own repository: https://github.com/InsertKoinIO/koin-compiler-plugin

## Build and test

JDK 17. Run every command from `projects/`. Version: `koinVersion` in `projects/gradle.properties`; Kotlin and AGP in `projects/gradle/libs.versions.toml`.

```bash
./gradlew :core:koin-core:jvmTest              # one KMP module, JVM only (KMP modules have no plain `test` task)
./gradlew :core:koin-core:allTests             # one KMP module, all targets
./gradlew :core:koin-core:compileKotlinWasmJs  # quick non-JVM compile check
./test.sh                                      # what CI runs: clean `test` + `jvmTest` on all modules
./test-macos.sh                                # Apple / native targets
./gradlew apiCheck                             # public API compatibility (CI runs it too)
./gradlew apiDump                              # regenerate `.api` dumps after an intended API addition
./compare_apis.sh                              # review `.api` changes against main
./install.sh                                   # publish everything to Maven Local
```

## Contributing rules

- **Public API is a contract** — `apiCheck` must pass. Patch releases only add API; removals need a deprecation cycle (`@Deprecated` with `ReplaceWith`).
- **Resolution behavior is API** — any change in `core/resolution/` states the old vs new resolution order / matching in the PR description.
- **Every bug fix ships a regression test** that fails before the fix, in `commonTest` when the code is common.
- **KMP discipline** — a `commonMain` change is verified beyond the JVM (`allTests`, or at least one native target and `wasmJs`). `./gradlew test` silently skips native, JS and Wasm tests.
- In `commonTest`: no backtick test names (they break JS / Wasm / native), and `runTest { }` in block-body form.
- Commit style: `Fix #NNNN - description`. User-visible changes need a docs update on https://insert-koin.io/docs: mention it in the PR, never edit `docs/`.

Detailed contributor notes (engine internals, known failing off-JVM tests, release notes policy): `projects/CLAUDE.md`.

## Using Koin

Write Koin code the way the official Koin skills do. Current versions: **Koin 4.2.2**, **Koin Compiler Plugin 1.2.1**, **Kotlin 2.3.20** or later.

- **Two styles, one container** — Koin DSL or Koin Annotations. Choose one explicitly and keep to it; don't mix unless asked.
  - **Koin DSL** keeps DI out of the code (no annotations on classes). Three forms:
    - **Compiler Plugin DSL** `single<T>()` — recommended with the Compiler Plugin.
    - **Constructor DSL** `singleOf(::T)`.
    - **Classic DSL** `single { T(get()) }` — custom expressions only.
  - **Koin Annotations** `@Singleton`, `@Factory`, `@KoinViewModel` — JSR-330 compatible.
- **Compile safe in both styles** with the Koin Compiler Plugin (`id("io.insert-koin.compiler.plugin")`). No KSP, no kapt: the Koin KSP processor is deprecated. No `checkModules()` / `verify()` tests.
- **Skills** — official Koin skills for setup, everyday code, migration (Hilt, Dagger, Koin KSP, Koin 3.x) and diagnosis: https://github.com/InsertKoinIO/koin-skills.
  Claude Code: `/plugin marketplace add InsertKoinIO/koin-skills` then `/plugin install koin@koin-skills`.
- **Runtime debugging** (graph behavior, scopes, memory, performance) — Kotzilla MCP at `https://mcp.kotzilla.io/mcp`.
  Claude Code: `claude mcp add kotzilla --transport http https://mcp.kotzilla.io/mcp`.
- **Docs** — https://insert-koin.io/docs/setup/gradle, https://insert-koin.io/docs/setup/compiler-plugin, https://insert-koin.io/docs/reference/koin-compiler/compile-safety. Index for LLMs: https://insert-koin.io/llms.txt (full text: https://insert-koin.io/llms-full.txt), and this repo's `llms.txt`.
