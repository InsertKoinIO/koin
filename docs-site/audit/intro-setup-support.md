<!-- Detailed audit appendix. `refs/` are shallow clones of sibling InsertKoinIO repos at audit time; `live/md` is the published text split per page, neither is committed. -->

# Audit: intro, setup, tooling, support (live text vs source)

Scope: docs/intro (6 pages + koin-vs-hilt read for context), docs/setup (4), docs/tooling (5), docs/support (4).
Ground truth: koin repo @ main (koinVersion=4.2.2, Kotlin 2.3.20, AGP 8.13.2), koin-compiler-plugin shallow clone (pluginVersion=1.2.1).
Short names below: L = live md page (docs__a__b.md = /docs/a/b).

## 1. Contradictions between pages

| # | Page A says | Page B says | Verdict |
|---|---|---|---|
| C1 | /docs/intro/choosing-your-style, "Which one to pick": Compiler Plugin DSL "keeps DI out of your classes" | same page, Tradeoffs: "Parameter annotations: qualifiers and injected parameters are declared on the class (`@Named`, `@InjectedParam`), so the class depends on `koin-annotations`". /docs/intro/core-concepts (Qualifier, Compiler Plugin DSL tab) puts `@Named("auth")` on `AuthClient`. /docs/intro/koin-compiler-plugin shows `@InjectedParam val userId` in DSL style. Repo llms.txt/AGENTS.md: "Koin DSL keeps DI out of the code (no annotations on classes)" | Real contradiction. Truth: DSL keeps definitions out of classes, but qualifiers/injected params in `single<T>()` style need annotations (verified: `@Named` lives in koin-annotations; `@InjectedParam`/`@Provided` in koin-core-annotations). Fix wording everywhere, and in llms.txt. |
| C2 | /docs/setup, Versions table: Koin 4.2.x needs Kotlin "2.3.20+" (Compiler Plugin column "Yes") | /docs/intro/choosing-your-style Requirements: Constructor/Classic DSL "Koin version: Any", "Kotlin: Any version supported by your Koin version". /docs/support: "Koin 4.2.x LTS, Kotlin baseline: Kotlin 2.x" | Setup column "Kotlin" mixes meanings: current row = "Built with 2.3.20", older rows = minimum ("2.1+", "1.9+", "1.8+"). Releases table says 4.1.0 built with 2.1.20, 4.0.4 with 2.0.21, 3.5.6 with 1.9.22. Same column header, three meanings (built with, minimum, baseline). Nowhere says the minimum Kotlin a Koin 4.2.x runtime consumer needs. |
| C3 | /docs/support/releases table: 4.2.2 "Stable", 4.2.1/4.2.0/4.1.x/4.0.4 "Previous stable", 3.5.6 "LTS" | /docs/support: "Koin 4.2.x LTS" is a maintained line in parallel with 3.5.6 | Status column ignores the 4.2.x LTS line. |
| C4 | /docs/intro/choosing-your-style + what-is-koin heading: "Two styles, one container" | what-is-koin table under that heading has 4 rows in a column titled "Style"; setup/choosing/core-concepts use 3 tabs; brief says 3 styles | See section 5. Counts used: 2 styles, 3 DSL forms, 4 declaration forms, 3 tabs. |
| C5 | /docs/setup/gradle (Compose Multiplatform + KMP): "In multiplatform source sets, use catalog entries that carry the version (see Without the BOM)" | /docs/reference/koin-core/kmp-setup: `implementation(project.dependencies.platform(libs.koin.bom))` inside `commonMain.dependencies`. /docs/reference/koin-compose/compose: `$koin_version` string versions, no BOM. /docs/reference/koin-android/hilt-migration: BOM with `$koin_version` | Three conventions for KMP/Compose. Pick one (BOM in commonMain works on Gradle 8+; verify then document it once in setup/gradle). |
| C6 | /docs/intro and /docs/intro/what-is-koin: Compiler Plugin is "optional" / "(optional)"; first code sample uses `single<ApiService>()` | /docs/setup/compiler-plugin and /docs/intro/choosing-your-style: `single<T>()` are stubs that throw `NotImplementedError` without the plugin; plugin "required" for Compiler Plugin DSL | The welcome and what-is-koin samples need the plugin and omit `import org.koin.plugin.module.dsl.*`, so they do not compile as pasted (without import, `single<T>()` has no match; with Constructor imports it is a different function). |
| C7 | /docs/setup/gradle + setup/compiler-plugin: "`koin-annotations` follows the Koin version" (4.2.2) | /docs/reference/koin-android/jsr330: "`koin-jsr330` is published with Koin Annotations, not with the Koin BOM. Use `$koin_annotations_version`". /docs/setup/annotations-ksp: `$koin_ksp_version` (2.x) | Koin Annotations still has a separate 2.x stream (KSP era, separate repo). `koin-jsr330` is not in this repo's settings.gradle.kts or BOM (see section 2). Unverifiable here: ask maintainers whether it exists for 4.2.x. |
| C8 | /docs/setup/gradle "All artifacts": `koin-test` platforms "All", description "Test API: KoinTest, verify()" | Source: `verify()` is in `core/koin-test/src/jvmMain/.../verify`; commonMain only has the deprecated `checkModules` | `verify()` is JVM only. |
| C9 | /docs/reference/koin-core/kmp-setup: "`koin-core` and the Compiler Plugin support every Kotlin target"; /docs/setup, choosing: plugin verified on 2.3.20 | koin-compiler-plugin README "Compatibility": "Annotation-based (`@Module`/`@ComponentScan`) WASM/JS requires Kotlin 2.4.0 (KT-82395)" | Undocumented limit anywhere in L (grep KT-82395: 0 hits). Docs imply Annotations on wasmJs/JS work from 2.3.20. |
| C10 | /docs/setup/gradle + setup: current version 4.2.2 / plugin 1.2.1 | /docs/migration/from-ksp-to-compiler-plugin: `koin = "4.2.0"`, `koin-plugin = "1.2.1"`; all 11 quickstarts use `<KOIN_VERSION>`, `<KOIN_PLUGIN_VERSION>` placeholders | Not wrong (4.2.0 is the minimum) but inconsistent; hardcoded 4.2.2 appears in 6 pages (setup 1, setup/gradle 4, setup/compiler-plugin 1, ai-agents 1, releases 4, embedded 4) with no single source of truth. |
| C11 | /docs/support/releases 4.2.0: `@KoinApplication`, `@Configuration`, `@PropertyValue`, `@ScopeId`, `@Monitor` listed under `koin-core-annotations` | Source: those classes are in `core/koin-annotations/.../CoreAnnotations.kt` and `MonitoringAnnotations.kt`; `koin-core-annotations` only contains `@InjectedParam` and `@Provided` (the 4.1.0 note is right) | Releases wrong module name. |
| C12 | /docs/tooling/ide-plugin: tree "reads the Constructor and Classic DSL, the Ktor DSL, the Compose API and Koin Annotations" | Recommended style everywhere else is the Compiler Plugin DSL `single<T>()` | IDE page never says whether `single<T>()` is supported. Verify with plugin team. |
| C13 | /docs/tooling: "Tests: `verify()`... If you use the Compiler Plugin, compile-time safety already covers most of it" | AGENTS.md/llms.txt: "No `checkModules()`/`verify()` tests" (as a rule for new code with the plugin) | Soft tension: docs keep `verify()` as the answer for Constructor/Classic without plugin. Align wording on one rule: with plugin none, without plugin `verify()`. |
| C14 | /docs/setup "Versions" table lists plugin as "Verified on 2.3.20, 2.4.0, 2.4.10, 2.4.20" | /docs/setup/compiler-plugin compatibility table splits by plugin version (2.4.20 needs 1.2.1+) | Consistent with supported-kotlin-versions.txt; just duplicated (see 4). |

Repo-internal (outside the published docs, FYI): README.md links `https://insert-koin.io/docs/setup/koin` (page does not exist, live is /docs/setup/gradle). projects/CLAUDE.md says docs live in `../docs/`, AGENTS.md says `docs/` is a legacy unmaintained copy.

## 2. Docs vs source

### 2a. Canonical artifact table (group `io.insert-koin`, all 4.2.2)

Source: projects/settings.gradle.kts (26 `include`s), each build.gradle.kts, projects/bom/koin-bom. "Doc" = listed in L docs__setup__gradle.md "All artifacts". KMP target shorthand: FULL = jvm, js, wasmJs, iosX64/Arm64/Sim, macosX64/Arm64, watchos (5), tvos (3), mingwX64, linuxX64/Arm64.

| Artifact | Module path | Type / targets (from build file) | BOM | Doc |
|---|---|---|---|---|
| koin-bom | bom/koin-bom | java-platform. Also constrains `koin-core-jvm` and `koin-ktor-jvm` (hardcoded "Check later KMP Bom") | n/a | yes |
| koin-core | core/koin-core | KMP FULL | yes | yes |
| koin-core-annotations | core/koin-core-annotations | KMP FULL. Only `@InjectedParam`, `@Provided`. Transitive via koin-annotations and koin-test | yes | NO (only mentioned in verify page and releases) |
| koin-annotations | core/koin-annotations | KMP FULL (+ jvmMain `org.koin.android.annotation.*` e.g. KoinWorker). `api` on koin-core-annotations | yes | yes ("All") |
| koin-core-coroutines | core/koin-core-coroutines | KMP FULL minus wasmJs | yes | yes, says "All" (wrong: no wasmJs) |
| koin-core-viewmodel | core/koin-core-viewmodel | KMP: android, jvm, js, wasmJs, ios x3, macos x2, tvos x3. No watchOS, Linux, Windows | yes | yes, says "All" (wrong) |
| koin-test | core/koin-test | KMP FULL; `verify()` JVM only | yes | yes |
| koin-test-coroutines | core/koin-test-coroutines | JVM only (KMP plugin, `jvm()` only) | yes | yes |
| koin-test-junit4 | core/koin-test-junit4 | JVM | yes | yes |
| koin-test-junit5 | core/koin-test-junit5 | JVM | yes | yes |
| benchmark | core/benchmark | internal, no publish script | no | no (correct, internal) |
| koin-ktor | ktor/koin-ktor | KMP FULL (docs list "JVM, JS, Wasm, iOS, macOS": understates; also watchOS, tvOS, Linux, Windows) | yes | yes |
| koin-logger-slf4j | ktor/koin-logger-slf4j | JVM | yes | yes |
| koin-android | android/koin-android | Android AAR, `api` koin-core + koin-core-viewmodel, minSdk 23 | yes | yes |
| koin-android-compat | android/koin-android-compat | Android AAR | yes | yes |
| koin-androidx-navigation | android/koin-androidx-navigation | Android AAR | yes | yes |
| koin-androidx-workmanager | android/koin-androidx-workmanager | Android AAR | yes | yes |
| koin-androidx-startup | android/koin-androidx-startup | Android AAR, `api` koin-android | yes | yes |
| koin-dagger-bridge | android/koin-dagger-bridge | Android AAR | yes | yes |
| koin-android-test | android/koin-android-test | Android AAR | yes | yes |
| koin-compose | compose/koin-compose | KMP: android, jvm, js, wasmJs, ios x3, macos x2. On Android `api` koin-android (docs right) | yes | yes |
| koin-compose-viewmodel | compose/koin-compose-viewmodel | same targets; `api` koin-compose + koin-core-viewmodel | yes | yes |
| koin-compose-viewmodel-navigation | compose/koin-compose-viewmodel-navigation | same targets | yes | yes |
| koin-compose-navigation3 | compose/koin-compose-navigation3 | same targets, minSdk 23 | yes | yes |
| koin-androidx-compose | compose/koin-androidx-compose | Android; `api` koin-compose + koin-compose-viewmodel (docs right) | yes | yes |
| koin-androidx-compose-navigation | compose/koin-androidx-compose-navigation | Android; `api` koin-androidx-compose | yes | yes |
| koin-gradle-plugin | plugins/koin-gradle-plugin | JVM Gradle plugin, id `koin` (META-INF/gradle-plugins/koin.properties). Legacy: registers `checkModules` / `checkAndroidModules` tasks that run JUnit category `CheckModuleTest` (the deprecated checkModules API). NOT the Compiler Plugin | no | NO. grep for `koin-gradle-plugin`/`checkAndroidModules` in L: 0 hits in intro/setup/tooling/support |

Counts: 26 included modules, 25 published (benchmark is not). Docs table lists 24 (23 libraries + bom). Missing from docs: `koin-core-annotations`, `koin-gradle-plugin`. BOM undocumented extras: `koin-core-jvm`, `koin-ktor-jvm`.

### 2b. Artifacts named in L that do not exist in this repo

| Name | Where | Reality |
|---|---|---|
| `koin-android-workmanager` | reference/koin-annotations/annotations-inventory (line 125), reference/koin-compiler/compile-safety (KOIN-A002, line 252) | Wrong. Real: `koin-androidx-workmanager`. Real error: user follows the message and gets unresolved dependency. |
| `koin-jsr330` | reference/koin-android/jsr330 | Not in settings.gradle.kts, BOM or any build file here. Docs say it ships with Koin Annotations (other repo). Unverifiable; flag. |
| `koin-ksp-compiler` | setup/annotations-ksp, migration, others | Not in this repo (lives in koin-annotations repo), deprecated. Correctly flagged deprecated. |
| `koin-core-viewmodel-navigation`, `koin-viewmodel` | support/releases 4.0.4, 4.1.0 notes | Historical names; current module is `koin-compose-viewmodel-navigation`. Add a rename note. |
| `koin-android-annotation` | support/releases 4.2.0 Deprecated | Not a module. Package `org.koin.android.annotation` is inside koin-annotations jvmMain. |
| `koin-ktor3` | releases | Merged into koin-ktor (accurate). |

### 2c. Versions, plugin, options

| Item | Docs | Source | Result |
|---|---|---|---|
| Koin | 4.2.2 | projects/gradle.properties koinVersion=4.2.2 | ok |
| Kotlin built with | 2.3.20 | libs.versions.toml kotlin=2.3.20 | ok |
| Compiler Plugin | 1.2.1 | koin-compiler-plugin gradle.properties pluginVersion=1.2.1 | ok |
| Verified Kotlin | 2.3.20, 2.4.0, 2.4.10, 2.4.20 | supported-kotlin-versions.txt same four | ok |
| Plugin id | `io.insert-koin.compiler.plugin` | koin-compiler-gradle-plugin build.gradle.kts `pluginId` same | ok |
| Compiler artifact | not named; Maven badge uses `io.insert-koin:koin-compiler-plugin` | BuildConfig KOTLIN_PLUGIN_NAME=`koin-compiler-plugin` | ok, but docs never say plugin id (Gradle) differs from artifact id (compiler) |
| Requires Koin | 4.2.0+ | plugin README: Koin 4.2.0+ | ok |
| "Gradle 8 or later" | setup/compiler-plugin | not stated in plugin repo | unverified |
| Extension block | `koinCompiler { }` | extension class KoinGradleExtension, 10 properties: userLogs, debugLogs, unsafeDslChecks, skipDefaultValues, compileSafety, strictSafety, strictSafetyForceOff, aiAssist, logSeverity, versionCheckSeverity | reference/koin-annotations/options lists exactly these 10 with matching defaults. ok. Note the options page lives under `koin-annotations` URL though options belong to the Compiler Plugin (naming smell). |
| Relation koin-gradle-plugin vs Compiler Plugin | not documented | koin-gradle-plugin = old `checkModules` task plugin (id `koin`); Compiler Plugin = separate repo, id `io.insert-koin.compiler.plugin`, not in BOM | Gap: docs say "The plugin is not part of the BOM" but never mention koin-gradle-plugin exists, so users searching "koin gradle plugin" find the wrong one. |
| Platform claims in releases for plugin | JVM, JS, Wasm, iOS, macOS, watchOS, tvOS, Linux, Windows | not verifiable here beyond README | see C9 |
| minSdk | releases 4.2.0: 21 -> 23 | androidMinSDK=23 | ok, but absent from setup/gradle (user installing needs it) |
| AGP/Gradle/JDK | absent in setup | AGP 8.13.2, JDK 17 for repo build, Java target 8 (JVM_1_8) for most libs; JVM_11 for core-viewmodel and compose jvm | Gap: no JVM target / minSdk requirement in setup. |

## 3. Gradle install snippets repeated across all 81 live pages

"Actual dependency lines" = lines with `implementation(`/`api(`/`testImplementation(`. Pages with 0 are omitted. Plugin application = `io.insert-koin.compiler.plugin` or `libs.plugins.koin.compiler` (prose mentions excluded).

| Page | dependency lines | BOM | plugin applied/declared | version used |
|---|---|---|---|---|
| setup/gradle | 25 (+22 `libs.koin.*`) | 8 mentions | no (links out) | 4.2.2 |
| setup/compiler-plugin | 10 | 1 (prose) | 4 snippets | 4.2.2 + 1.2.1 |
| setup/annotations-ksp | 5 | 1 | KSP | `$koin_version`, `$koin_ksp_version` |
| setup | 0 | 1 (prose) | prose | 4.2.2/1.2.1 table |
| migration/from-ksp-to-compiler-plugin | 6 | 0 | yes | 4.0.0 (before), 4.2.0 + 1.2.1 (after) |
| quickstart/kotlin | 3 | 0 | yes | placeholders |
| quickstart/kotlin-annotations | 4 | 0 | yes | placeholders |
| quickstart/android | 4 | 0 | yes | placeholders |
| quickstart/android-viewmodel | 4 | 0 | yes | placeholders |
| quickstart/android-annotations | 4 | 0 | yes | placeholders |
| quickstart/android-compose | 5 | 0 (compose.bom only) | yes | placeholders |
| quickstart/cmp | 2 | 0 | yes | placeholders |
| quickstart/compose-multiplatform-annotations | 6 | 0 | yes | placeholders |
| quickstart/ktor | 7 | 0 | yes | placeholders |
| quickstart/ktor-annotations | 8 | 0 | yes | placeholders |
| quickstart/junit-test | 3 | 0 | no | placeholder |
| reference/koin-core/kmp-setup | 7 | 1 (platform()) | yes | BOM (version via catalog) |
| reference/koin-compose/compose | 3 | 0 | no | `$koin_version` |
| reference/koin-compose/navigation3 | 2 | 0 | no | `$koin_version` |
| reference/koin-ktor/ktor | 3 | 1 | no | BOM + libs.koin |
| reference/koin-android/hilt-migration | 3 | 1 | 1 | `$koin_version` BOM |
| reference/koin-android/jsr330 | 3 | 0 | 2 | `$koin_annotations_version` |
| reference/koin-android/workmanager | 2 | 0 | no | no version |
| reference/koin-android/start, viewmodel, fragment-factory | artifact mentions, no BOM | 0 | no | mixed |
| reference/koin-android/instrumented-testing, koin-test/testing, koin-ktor/ktor-testing, koin-core/lazy-modules, koin-core/viewmodel | 1 to 3 each | 0 | no | no version |
| support/embedded | 9 | 0 | no | 3.5.6 / 4.2.2 / acme-koin-core (intentional) |

About 30 pages carry install snippets; 3 install patterns coexist: (a) BOM + versionless libs (setup/gradle, kmp-setup, ktor ref), (b) per-library version catalog with `version.ref` (quickstarts, setup/compiler-plugin), (c) inline `$koin_version` strings (compose ref, hilt-migration). setup/compiler-plugin Step 1 uses (b) then says "if you use the BOM, drop version.ref", while setup/gradle recommends (a) as primary.

Disagreements on names/versions found:
- `koin-android-workmanager` (wrong) vs `koin-androidx-workmanager` (right, setup/gradle + workmanager ref). 2 pages.
- Android Compose: setup/gradle (koin-android + koin-compose + koin-compose-viewmodel, mentions koin-androidx-compose as alternative) vs compose ref and android-compose quickstart (koin-androidx-compose only).
- migration page old block `koin = "4.0.0"` and `ksp = "2.0.0-1.0.22"` (intentional "before").
- Quickstarts do not use the BOM at all; setup/gradle says BOM is the recommended path.
- Plugin repository block (mavenCentral + gradlePluginPortal) only in setup/compiler-plugin.

## 4. Duplicated content (canonical in bold)

| Topic / snippet | Where duplicated | Canonical |
|---|---|---|
| ApiService / UserRepository `module { single<ApiService>(); single<UserRepository>() }` + startKoin | intro, what-is-koin, core-concepts, choosing-your-style, DI page (variant) | **core-concepts** (intro/what-is-koin keep 1 line teaser, add imports) |
| Style comparison (3 tabs + table) | what-is-koin "Two styles" table, choosing-your-style "side by side" + "Requirements", core-concepts tabs, koin-compiler-plugin "What it does" | **choosing-your-style** |
| Requirements block (Kotlin 2.3.20 K2, Koin 4.2.0+, verified list 2.3.20/2.4.0/2.4.10/2.4.20, plugin id) | setup, setup/compiler-plugin (Requirements + Kotlin compat table), choosing-your-style, koin-compiler-plugin, releases (Compiler Plugin section), ai-agents "Limits" | **setup/compiler-plugin**; elsewhere link only |
| Current versions 4.2.2 / 1.2.1 | 6 pages (see C10) | **setup** (Versions) + Maven badges |
| `NotImplementedError` stub warning | choosing-your-style warning, setup/compiler-plugin Troubleshooting | **setup/compiler-plugin** |
| Imports (plugin.module.dsl vs core.module.dsl vs org.koin.dsl) | choosing-your-style table row, setup/compiler-plugin "Wrong imports", koin-compiler-plugin last para | **setup/compiler-plugin**, or a DSL reference page |
| KSP deprecated notice | intro, what-is-koin ("Annotations status"), choosing-your-style, koin-compiler-plugin, setup, setup/annotations-ksp, releases, monitor | **setup/annotations-ksp** + migration; others 1 sentence |
| KOIN-D001 error block | core-concepts, koin-compiler-plugin (identical) | **reference/koin-compiler/compile-safety** |
| Limits of compile-time checks (loadKoinModules, library modules, Kotlin coupling, incremental) | what-is-koin "costs", koin-compiler-plugin "Limits", setup/compiler-plugin "Stale results" | **koin-compiler-plugin** (explanation) ; troubleshooting stays in setup |
| Options block (`koinCompiler { compileSafety..userLogs }`) | setup/compiler-plugin, tooling/monitor (logSeverity), production-monitoring (aiAssist), options page | **reference/koin-annotations/options** (rename path to koin-compiler) |
| Constructor vs service-locator explanation | what-is-koin "Constructor injection and service locator", DI page "Service locator and DI", core-concepts "Injection" | **what-is-dependency-injection** |
| Tooling table (which tool for which need) | tooling, intro "Production & Tooling" list, ai-agents | **tooling** |
| "Tabs on every page" note | choosing-your-style | keep, move to a docs conventions note |
| What each pre-release/semver means | support ("Version numbers"), api-stability ("Release types", links back to support for semver), releases header | **api-stability** for API contract and pre-release; **support** for cadence and support only. Move "Version numbers" to api-stability or vice versa, link once |
| LTS lines | support (table + note), releases (note box), embedded (Embedded LTS), setup? no | **support** |
| Embedded LTS pitch | support "Koin Embedded LTS" + tiers, support/embedded "full Embedded LTS offer" | **support/embedded** (support keeps 2 lines + table) |
| Compiler Plugin changelog | releases "Koin Compiler Plugin" section (1.1.0, 1.2.0, 1.2.1) only, but version/compat in setup/compiler-plugin too | **releases** for history, setup for compat table |
| Upgrade notes | releases per-version "Breaking", migration/from-ksp page, setup "Moving to the Compiler Plugin" section (also in setup/compiler-plugin and annotations-ksp) | one **migration** page; the 3 "Moving to" stubs link only |

## 5. Terminology inventory (declaration styles)

Counts across all 81 pages (case-insensitive; "Compiler Plugin DSL" figure includes "plugin DSL" overlap):
- Tab labels (bold, exact): "Compiler Plugin DSL" 78, "Annotations" 78, "Constructor / Classic DSL" 78. So 3 tabs everywhere, no page has a 4th.
- "Classic DSL" ~123 hits/52 pages; "Constructor DSL" 18/9; "Koin DSL" only 7/7 (choosing, what-is-koin, llms.txt, dsl ref and few); "Koin Annotations" 25/10; "DSL form" 5, "DSL style" 5 (used for the forms, e.g. "DSL style" in migration, compose-scopes, "DSL form" in monitor and dsl ref).
- Phrases for the same concept: "style" (Annotations, DSL), "style" also used for each DSL form ("DSL style", "your style"), "form", "mode" absent. "Two styles" appears in 2 pages; "three forms" in 2; "three DSL forms" in dsl ref. Nobody says "three styles" (0 hits) or "four styles" (0).
- llms.txt: "Two styles, one container: Koin DSL or Koin Annotations", DSL has "three forms": Compiler Plugin DSL `single<T>()` / `single { create(::buildT) }`, Constructor DSL `singleOf(::T)`, Classic DSL `single { T(get()) }`. Repo and L agree on this model.
- Mismatch inside L: what-is-koin heading "Two styles" over a 4-row table whose column is named "Style".
- The third tab merges two different APIs with different imports (`org.koin.core.module.dsl.*` vs `org.koin.dsl.*`). core-concepts third tab shows only Constructor DSL except in Qualifier (Classic). Reader cannot tell which they are looking at.

What is really there: 2 styles (Koin DSL, Koin Annotations), 3 DSL forms, 4 declaration forms, 3 tabs.

Recommended glossary (canonical):
- **Style** = Koin DSL or Koin Annotations. (2) Pick one per project.
- **DSL form** = Compiler Plugin DSL, Constructor DSL, Classic DSL. (3) Mixable in one module.
- **Declaration form** (umbrella, optional) = the 4 above.
- Name the tabs exactly: "Compiler Plugin DSL", "Annotations", "Constructor / Classic DSL" (keep), or split into 4 tabs only on pages where the two differ.
- Never write "plugin DSL", "Koin Plugin DSL", "DSL style", "Kotlin DSL" (Gradle) next to these.
- Plugin names: **Koin Compiler Plugin** (product, first mention), **Compiler Plugin** (short), "Gradle plugin `io.insert-koin.compiler.plugin`" (install), never "Koin Gradle plugin" (collides with `koin-gradle-plugin`). Library: **Koin Annotations** / `koin-annotations`. Deprecated: **KSP processor** `koin-ksp-compiler` (not "Koin KSP" in docs; llms.txt uses "Koin KSP").
- "Compile-time safety" (reference page title) vs "compile-time checks" vs "compile safety" (option `compileSafety`, URL `compile-safety`, llms.txt "Compile safe"): pick "compile-time safety" for prose, backticks for the option.

## 6. Gaps

Upgrade 4.0/4.1 to 4.2:
- No upgrade guide page. releases has per-version "Breaking" bullets but no checklist: min Kotlin, minSdk 21 to 23, `Module.getAll` unsorted, `koin-ktor3` merged, `KoinContext` deprecated, `CoreResolverV2` behavior, "upgrade to 4.2.2 not 4.2.0/4.2.1".
- Kotlin floor for the Koin 4.2.x runtime is never stated (C2). Does a project on Kotlin 2.1 compile against 4.2.2 artifacts? Unanswered.
- No "should I move to the Compiler Plugin now" decision tied to Kotlin version. setup/annotations-ksp says keep KSP only if Kotlin < 2.3.20, but Koin 4.1.x supports Kotlin 2.1/2.2: path "stay on 4.1 + KSP vs jump to 4.2 + plugin" not drawn.
- JVM target / Java level for libraries (JVM_1_8, but core-viewmodel and compose jvm target JVM_11): not in setup.
- Android: minSdk 23, AGP, compileSdk 36 not in setup/gradle; only in releases.
- Wasm/JS + Annotations requires Kotlin 2.4.0 (C9).
- BOM limits: plugin and `koin-jsr330`/KSP not in BOM, `koin-core-jvm`/`koin-ktor-jvm` constraint; BOM + KMP guidance conflicts (C5).
- `koin-gradle-plugin` / `checkModules` task: users upgrading from 3.x/4.0 with `id("koin")` get no guidance (not in migration, not in releases).
- Deprecated `checkModules` removal timeline vs `verify()` and plugin: only in 4.0.4 release notes.
- Replacing `koin-ksp-compiler` options: `KOIN_CONFIG_CHECK` documented in annotations-ksp; no table mapping KSP args to `koinCompiler {}` options.
- Koin Annotations version mapping (2.x vs Koin 4.x): setup/annotations-ksp table stops at "2.3 / KSP 2.3 / Independent"; no "which Koin Annotations equals Koin 4.2.x".

Missing FAQ page (none in L; grep "FAQ" 0). Candidate questions with canonical answer location:
1. Which style should I pick? (choosing-your-style)
2. Do I need the Compiler Plugin? Which Kotlin versions? (setup/compiler-plugin)
3. Can I stay on KSP? Until when? (annotations-ksp)
4. Why `NotImplementedError` at runtime / "No definition found"? (troubleshooting + setup/compiler-plugin)
5. Is `koin-annotations` deprecated? (what-is-koin "Annotations status")
6. Do I still need `verify()` / `checkModules()`? (verify + compile-safety)
7. Which artifact for Android / Compose / KMP / Ktor? (setup)
8. BOM or not in KMP?
9. Can I mix styles? (choosing-your-style)
10. Koin vs Hilt/Dagger, Kodein, Koin 3.x LTS (support)
11. Is Koin reflection based / service locator? (what-is-koin)
12. Proguard/R8 (4.2.2 note exists only in releases).

## 7. Page disposition (Diataxis)

| Page | Disposition | Diataxis |
|---|---|---|
| /docs/intro | Keep as site map; fix sample (imports, "optional"), drop duplicate of what-is-koin sample | Navigation / index |
| /docs/intro/what-is-dependency-injection | Keep, canonical for DI-vs-locator; trim Koin tabs | Explanation |
| /docs/intro/what-is-koin | Merge "how it works" into core-concepts; keep costs/limits + project overview; fix "Two styles" heading | Explanation |
| /docs/intro/core-concepts | Keep, canonical example; becomes the tutorial-like overview | Explanation (with tutorial flavor) |
| /docs/intro/choosing-your-style | Keep, canonical for styles and requirements; fix C1; remove duplicated requirements | Explanation / decision guide |
| /docs/intro/koin-compiler-plugin | Keep as the single explanation of the plugin; drop duplicated install bits | Explanation |
| /docs/setup | Keep as the install hub; single source for versions | How-to / reference |
| /docs/setup/gradle | Keep; fix target column, add koin-core-annotations, koin-gradle-plugin note, minSdk/JVM target, resolve BOM-in-KMP | Reference + how-to |
| /docs/setup/compiler-plugin | Keep; canonical for requirements, compat, troubleshooting; add wasm/JS annotations limit | How-to + reference |
| /docs/setup/annotations-ksp | Keep as deprecated stub, shrink "Moving to" to link | How-to (legacy) |
| /docs/tooling | Keep as hub | Navigation / explanation |
| /docs/tooling/ai-agents | Keep | How-to |
| /docs/tooling/ide-plugin | Keep; clarify `single<T>()` support | Reference + how-to |
| /docs/tooling/monitor | Keep; shorten KSP section | Reference |
| /docs/tooling/production-monitoring | Keep | Explanation / how-to |
| /docs/support | Keep; split policy (LTS) from versioning; remove semver here or in api-stability | Explanation / policy |
| /docs/support/api-stability | Keep; absorb semver + release types | Reference |
| /docs/support/releases | Keep as changelog; add 4.x upgrade checklist and fix module names (C11, 2b) | Reference |
| /docs/support/embedded | Keep | How-to + explanation |
| (new) FAQ, Upgrade 4.x to 4.2 guide | Create | How-to / reference |
