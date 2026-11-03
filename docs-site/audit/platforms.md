<!-- Detailed audit appendix. `refs/` are shallow clones of sibling InsertKoinIO repos at audit time; `live/md` is the published text split per page, neither is committed. -->

# Platforms audit: Android, Compose (Android angle), Ktor, quickstarts, koin-vs-hilt, KSP migration

Scope: live text in `$SCRATCH/live/md` (13 koin-android pages, 9 koin-compose pages, 5 koin-ktor pages, 10 quickstarts in scope, koin-vs-hilt, from-ksp-to-compiler-plugin) plus repo file `docs/reference/koin-android/r8-proguard.md`.
Source root: `projects` (abbreviated `P/`). Koin 4.2.2, plugin 1.2.1 (`P/gradle.properties:14`, plugin repo `gradle.properties:4`).

Overall: the reference pages are accurate. I grepped about 90 API names, packages, signatures and artifact names and found no invented API. Problems are mostly: one broken promise (Hilt side by side), one undocumented module, one 404 page that shipped code points to, one import split, and duplicated tutorial bodies.

## 1. Contradictions between pages

| # | Page A (quote) | Page B (quote) | Note |
|---|---|---|---|
| C1 | `/docs/intro/koin-vs-hilt`: "The migration guide has the full mapping, a step-by-step process, and **how to run both frameworks side by side during the move**" | `/docs/reference/koin-android/hilt-migration`: no coexistence section. Only "Migrate gradually with @Inject" (JSR-330) and a "Multi-module" paragraph. `koin-dagger-bridge` never appears | Broken promise. The one mechanism for side by side (`Scope.dagger<T>()`) is undocumented outside the gradle table. |
| C2 | `/docs/setup/gradle` (line 208): `koin-dagger-bridge` "Access Dagger components from Koin" | `/docs/support/releases` (line 124): "Inject Koin components from the Dagger side" | Opposite directions. Source: Koin reads Dagger, `P/android/koin-dagger-bridge/src/main/java/org/koin/android/dagger/DaggerBridge.kt:51` `inline fun <reified T> Scope.dagger(): T = EntryPoints.get(...)`. The gradle page is right. Neither page is in scope, but C1 depends on it. |
| C3 | `/docs/quickstart/android-compose`: `import org.koin.androidx.compose.koinViewModel` | `/docs/reference/koin-compose/compose-viewmodel`: `import org.koin.compose.viewmodel.koinViewModel`; `/docs/reference/koin-compose/compose`: "All Compose APIs live in the multiplatform packages" | Two non-deprecated overloads exist: `P/compose/koin-androidx-compose/src/main/java/org/koin/androidx/compose/ViewModel.kt:42` and `P/compose/koin-compose-viewmodel/src/commonMain/kotlin/org/koin/compose/viewmodel/ViewModel.kt:47`. `koin-androidx-compose` has `api` on both (`P/compose/koin-androidx-compose/build.gradle.kts:43-44`). Importing both gives ambiguity. No page says the `androidx` one exists or which to prefer. The getting-started sample (`android-compose/.../MainActivity.kt:27`) uses the androidx one, the KMP samples use the other. |
| C4 | `/docs/quickstart/android-annotations` and `/ktor-annotations`: "`koin-annotations` shares the Koin version"; `/docs/migration/from-ksp...` table: "Same version as `koin-core`" | `/docs/reference/koin-android/jsr330`: "`koin-jsr330` is published with Koin Annotations, not with the Koin BOM. Use the Koin Annotations version" and `$koin_annotations_version` | Implies a separate annotations version. Only one is true. `koin-jsr330` is not in this repo (no match in `P/settings.gradle.kts` or any build file), so I could not verify it exists or how it is versioned. Needs a check against the koin-annotations repo. |
| C5 | `/docs/quickstart/android` says "For the same app with annotations, see Android & Annotations" | `/docs/quickstart/android-annotations` says "same app with the Compiler Plugin DSL, see Android and Android ViewModel" | The two are not the same app. `android` uses a plain `UserPresenter` factory. `android-annotations` uses ViewModel plus a `@ViewModelScope` presenter plus `viewModelScopeFactory()`. `android-viewmodel` is the closer twin. `android-compose` points to `android-annotations` as its annotations variant, but that one is Views, not Compose. |
| C6 | `/docs/quickstart/junit-test` teaches `appModule.verify()`; `hilt-migration` checklist: "Without it, add a verify() test"; `instrumented-testing`: "use verify() in a JVM test" | `AGENTS.md` (repo): "No `checkModules()` / `verify()` tests"; `junit-test` itself: "The Compiler Plugin already runs this check at build time" | Docs hedge ("without the plugin"), AGENTS.md is absolute. The junit-test tutorial teaches the technique the plugin makes redundant, with no `Test` for the plugin path. Pick one stance. |
| C7 | `/docs/intro/koin-vs-hilt`: "Java support ... Koin's API is designed for Kotlin" | `/docs/reference/koin-android/viewmodel` "Java" section: `koin-android-compat`, `ViewModelCompat`, `SharedViewModelCompat`, `ScopeCompat` (exist: `P/android/koin-android-compat/src/main/java/org/koin/android/compat/`) | Not wrong, but the Hilt comparison omits the Java compat module. |
| C8 | `koin-vs-hilt` concept table: `@InstallIn(SingletonComponent::class)` to `@Configuration` | `hilt-migration` table: `@Module` + `@InstallIn` to `@Module` + `@ComponentScan` (and Step "Multi-module" needs all three) | Two different mappings for the same Hilt annotation. Use one. |
| C9 | `hilt-migration` Step 1 Gradle: only `koin-android` and `koin-androidx-compose`; no `koin-annotations` for annotations style | quickstarts annotations: `implementation(libs.koin.annotations)` required | Annotation users copying Step 1 lack `@Singleton`, `@KoinViewModel`. |
| C10 | `best-practices` "Errors you only see on Android" says the "Workers fail with 'Could not instantiate'" cause is the default initializer | `workmanager` says "It does nothing if WorkManager is already initialized ... Workers then fail at runtime because WorkManager can't create a class with extra constructor parameters" | Consistent in cause, different error text. Minor. Keep one phrase. |

## 2. Docs vs source discrepancies (copyable things first)

Verified correct (no action): `androidContext/androidLogger(level=INFO)/androidFileProperties`, `MissingAndroidContextException`, `KoinStartup` (`org.koin.androix.startup`, `@KoinExperimentalAPI`), `koinNavGraphViewModel`, `viewModelForClass`, `activityViewModel/getActivityViewModel`, `activityScope/activityRetainedScope/fragmentScope` (delegates and `Module.` DSL in `org.koin.androidx.scope.dsl`), `ScopeService`, `serviceScope()/destroyServiceScope()`, `onCloseScope`, `viewModelScope {}` and `viewModelScopeFactory()` (`P/core/koin-core/.../option/KoinOption.kt:39`), `@KoinWorker` (`org.koin.android.annotation`), `@ActivityScope/@ActivityRetainedScope/@FragmentScope`, `worker<T>()` plugin DSL (`P/android/koin-androidx-workmanager/.../plugin/module/dsl/WorkerDSLExt.kt`), `workerOf`, `workManagerFactory()`, `fragmentFactory()`, `fragmentOf`, `fragment {}`, `setupKoinFragmentFactory`, `create(::fn)` and `single<T>()/factory<T>()/scoped<T>()/viewModel<T>()` (`P/core/koin-core/src/commonMain/kotlin/org/koin/plugin/module/dsl/`), `KoinApplication(configuration, logLevel)`, `KoinApplicationPreview(application, content)`, `KoinScope` (3 forms), `UnboundKoinScope`, `rememberKoinScope`, `rememberKoinModules`, `KoinIsolatedContext`, `navigationScope` (`org.koin.module.dsl`), `KoinNavigationScope`, `sharedKoinViewModel`, `koinActivityViewModel/koinActivityInject`, `KoinActivityScope/KoinFragmentScope`, `koinNavViewModel` deprecated in both artifacts, `KoinContext/KoinAndroidContext` deprecated, `navigation<T>` (`org.koin.dsl.navigation3`), `entryProvider/getEntryProvider` (`org.koin.androidx.compose.navigation3`), Ktor `Koin`, `KoinIsolated`, `koinModule(s)`, `requestScope`, `call.scope`, `ApplicationCall.inject/get/getProperty`, three lifecycle events, `slf4jLogger`, request scope id `request_` + counter, "Koin Request Scope is not ready" and "Koin plugin is not installed properly" strings, `KoinIsolated` ignoring `bridge{}` (`KoinIsolatedContextPlugin.kt:30-35` never calls the bridge setup), `androidVerify`, `userLogs` Gradle option, `@KoinViewModel` in `org.koin.core.annotation` (`P/core/koin-annotations/.../CoreAnnotations.kt:313`).

Findings:

| # | Doc quote | Page | Source evidence | Severity |
|---|---|---|---|---|
| S1 | (r8 page) "`koin-androidx-startup` additionally keeps its manifest-referenced initializer. None of these keep your application classes" | `docs/reference/koin-android/r8-proguard.md` (repo only) | `P/android/koin-androidx-startup/consumer-rules.pro:6` has `-keep class * implements org.koin.androix.startup.KoinStartup`, which does keep the user's `Application`. Also that file has no `-dontwarn org.koin.**` while the page says all AARs silence warnings. | Medium |
| S2 | Shipped consumer rules link to `https://insert-koin.io/docs/reference/koin-android/r8-proguard` | live site | `P/compose/koin-compose-viewmodel/consumer-rules.pro:8` (the link, the page is 404 live). Page was added in commit 1270c9ae. Every app on R8 that opens the rule file hits a 404. | High, easy fix: publish the page |
| S3 | r8 page contains 9 em dash characters and `---\ntitle:` legacy front matter | repo file | A grep for the em dash character returns 9 lines. Violates the no em dash rule if ported as is. Rewrite when porting. Content otherwise matches `consumer-rules.pro` in the 5 AARs (`koin-android`, `koin-core-viewmodel`, `koin-compose-viewmodel`, `koin-androidx-workmanager`, `koin-androidx-startup`). | Low |
| S4 | `jsr330`: "`koin-jsr330` ... provides the `jakarta.inject` annotations for every Kotlin target" | `docs/reference/koin-android/jsr330` | Artifact not defined anywhere in `P/` (see C4). Unverifiable here. | Check |
| S5 | `jsr330`: "`@Inject` on a constructor to `@Factory`" | same | Matches compiler plugin `docs/TRANSFORMATIONS.md:742-743` in `$SCRATCH/refs/koin-compiler-plugin`. OK. But `@Singleton` and `@Inject` are only tested in the plugin repo, not in `P/`. | Info |
| S6 | `compose-testing`: `KoinApplicationPreview(application = { ... })` | `compose-testing` | Matches `KoinApplication.kt:303`. OK. But `compose` page says `KoinApplication(application = ...)` is deprecated while `KoinApplicationPreview` keeps the old `application` param name. Not an error, worth a sentence. | Info |
| S7 | `ktor-scopes` example `call.scope.get<RequestLogger>()` has no `import org.koin.ktor.plugin.scope` | `ktor-scopes`, `ktor`, `ktor-isolated`, `ktor-testing` | `scope` is a top-level extension in `org.koin.ktor.plugin` (`KoinPlugin.kt:115`). A reader pasting gets an unresolved reference. Same for `getKoin()` (`org.koin.ktor.ext`). | Medium |
| S8 | `ktor-testing` Annotations tab: `withConfiguration<MyApp>()` | `ktor-testing` | `MyApp` is never defined on the page; the Ktor pages call the class `KoinUserApplication`. Name drift. | Low |
| S9 | `instrumented-testing` Annotations tab: `startKoin<MyApp> { modules(instrumentedTestModule) }` | same | Same undefined `MyApp`. | Low |
| S10 | `hilt-migration` Compiler Plugin tab, Step 4: `single<UserRepositoryImpl>() bind UserRepository::class` with no import | `hilt-migration`, `multi-module`, `binding-patterns` | `bind` lives in `org.koin.dsl.bind` (and `org.koin.plugin.module.dsl` has a `KoinDefinition<*>.bind(KClass)` in `ModuleExt.kt:87`). The quickstarts show `import org.koin.dsl.bind`, the reference pages never do. | Low |
| S11 | `fragment-factory`: "The `fragment` definition is part of `io.insert-koin:koin-android`" | `fragment-factory` | `P/android/koin-android/src/main/java/org/koin/androidx/fragment/dsl/`. OK. Also correct that no plugin `fragment<T>()` exists. Good example of a page that states a gap honestly. | Info |
| S12 | `scope`: `ScopeViewModel` / "extend `ScopeViewModel`" | `scope` | Two classes: `P/android/koin-android/.../androidx/scope/ScopeViewModel.kt` and `P/core/koin-core-viewmodel/.../viewmodel/scope/ScopeViewModel.kt:49`. Page doesn't say which package to import. | Low |
| S13 | quickstarts list `testImplementation(libs.koin.android.test)` | `android`, `android-viewmodel`, `android-compose` | The tutorials never use it (no test section, no `androidVerify`). `koin-android-test` only exposes `Module.androidVerify` (`P/android/koin-android-test/src/main/java/org/koin/android/test/verify/AndroidVerify.kt:39`). A dead dependency with no explanation. | Low |
| S14 | `from-ksp...` Step 2: example "after" uses `koin = "4.2.0"` | migration | Current is 4.2.2. Page says "4.2 or later", so fine, but copyable text is already stale. Use a placeholder as the quickstarts do (`<KOIN_VERSION>`). | Low |
| S15 | `from-ksp...`: `@KoinApplication(modules = [AppModule::class])`; `startKoin<MyApp>()` | migration | Matches `CoreAnnotations.kt:295` (`modules: Array<KClass<*>>`) and `plugin/module/dsl/ApplicationDSLExt.kt:32`. OK. | Info |

## 3. Duplicated content

| Topic | Where | Canonical | Action |
|---|---|---|---|
| ViewModel retrieval, parameters, qualifier, SavedStateHandle | `koin-core/viewmodel`, `koin-android/viewmodel`, `koin-compose/compose-viewmodel` | Declaration, params, `SavedStateHandle`, scope: `koin-core/viewmodel`. Retrieval API: one page per platform. | Split is already sound (declare in core, retrieve per platform). Remaining overlap is small: "parameters used only at creation" appears in all three; `SavedStateHandle` paragraph in android page repeats core. Trim android and compose to one sentence + link. Add a 6 row "which function for which owner" table once in core, link from both. |
| Package/artifact tables | `koin-compose/compose` "Packages", `setup/gradle` (lines 100-125 and 200-215), `compose-viewmodel` API summary | `setup/gradle` | Keep a 2 line pointer in compose. Today three tables must be edited together. |
| "Start Koin in Application" snippet | `start`, `hilt-migration` Step 2, `multi-module`, `quickstart/android*` (x4), `compose` "Start outside Compose" | `koin-android/start` | Others link. Keep the quickstarts self contained, drop it from `hilt-migration` and `multi-module` (use a link). |
| Retrofit/Room/Json setup | `binding-patterns` and `hilt-migration` Step 3 | `binding-patterns` | Hilt page can show only the Hilt vs Koin before/after, without the full OkHttp module. |
| Activity scope vs Context | `start`, `scope`, `best-practices`, `advanced-patterns` | `scope` | Four places explain "Context resolves to Activity in an Activity scope". Keep in `scope`; the other three link. |
| Scope lifetime warnings (retained, fragment, ViewModel) | `scope`, `best-practices`, `viewmodel` | `scope` | `best-practices` "Scoped instances that outlive their owner" is a restatement. |
| Deprecated entry points | `compose` table, `compose-viewmodel` warning, `compose-testing` (`KoinContext`) | `compose` | Link only. |
| Isolated context | `compose/isolated-context`, `ktor/ktor-isolated`, `koin-core/context-isolation` | `koin-core/context-isolation` | Both platform pages already link to it; fine. |
| Tutorial body (User, Repository, Service, module, start) | 7 quickstarts; ~70% of each is identical | see below | see below |
| Test lifecycle and rule order | `instrumented-testing` (twice), `compose-testing` Pitfalls | `instrumented-testing` | Rule order is in both; keep one. |

### Quickstart pairs: could each pair be one page with synced tabs?

The page diff for `ktor` vs `ktor-annotations` is: title, one intro sentence, one extra dependency line, `@Singleton` on two classes, module (DSL vs `@Module/@ComponentScan/@Configuration`), and the `install(Koin)` block (`modules(appModule)` vs `withConfiguration<KoinUserApplication>()`). Everything else is identical, including the dependency chain, the data classes, the route and the run instructions. The same applies to `kotlin` and `kotlin-annotations` (same 8 headings, reordered by one section).

| Pair | Verdict |
|---|---|
| `ktor` + `ktor-annotations` | Merge into one page `quickstart/ktor` with a global style tab (Compiler Plugin DSL / Annotations). Shared code stays outside tabs. About 45% less text. |
| `kotlin` + `kotlin-annotations` | Merge the same way. `junit-test` hangs off the DSL version, so make its module snippet tabbed too (it currently has none). |
| `android` + `android-annotations` | Not clean pairs (see C5). First align the tutorials: make `android-viewmodel` the DSL twin of `android-annotations` (same Presenter/ViewModel structure). Then merge `android-viewmodel` + `android-annotations` into one tabbed page, keep `android` as the plain `inject()` first page. |
| `android-compose` | No annotation twin today. Add a tab instead of a new page. |
| `cmp` + `compose-multiplatform-annotations` | Out of scope here, same pattern (13 KB and 14 KB). |

Result: 10 in-scope quickstart pages become 6 (android, android-viewmodel/annotations, android-compose, kotlin, ktor, junit-test). The docs already use "Compiler Plugin DSL / Annotations / Constructor / Classic DSL" tabs in every reference page, so the pattern exists. Also make the tab choice sticky across pages.

## 4. Gaps by persona

**Android dev new to DI**
- No Android page explains why DI at all; `intro/what-is-dependency-injection` exists (out of scope) but is not linked from `quickstart/android`.
- First tutorial teaches a "presenter" with `factory`, which is not what a new Android dev writes in 2026. `android-viewmodel` should be the first Android tutorial.
- No Fragment tutorial (only `fragment-factory` as reference). Many apps are still View based.
- Nothing about using Koin with a Navigation component beyond `koinNavGraphViewModel` (a single code block in `viewmodel`).
- Nothing about R8 for the whole docs (S2). The only R8 text is not published.
- No "common errors" first-aid page, only a 5 row table inside `best-practices`.
- `Context`: good. `Service` / `BroadcastReceiver` / `ContentProvider`: good (entry-points).

**Android dev migrating from Hilt / Dagger**
- Hilt: strong (`hilt-migration` 18 KB, concept and scope tables, steps, checklist, troubleshooting). Gaps: no side by side / incremental strategy, no `koin-dagger-bridge`, no mention that the bridge uses Hilt `EntryPoints` (`DaggerBridge.kt:3`, dependency `hilt-core`, `P/gradle/libs.versions.toml:99`), so it needs an `@EntryPoint` interface on the Dagger/Hilt side. No `@Assisted`/`@AssistedFactory` code beyond a summary. No `@IntoSet`/`@IntoMap` multibinding mapping (Koin: `getAll<T>()` / lists). No mention of Hilt `@DefineComponent`, `@BindsInstance`. No WorkManager `HiltWorkerFactory` before/after code (only one line).
- Dagger without Hilt: no content. `koin-vs-hilt` says "and Dagger" but the code is Hilt only. The `di-migration` skill in koin-skills handles Dagger (`/docs/tooling/ai-agents`), the docs do not.
- KSP to plugin: complete. Missing: Koin 3.x to 4.x pointer, and a Koin KSP specific `ksp` JVM-only note.
- Missing in `koin-vs-hilt`: build time impact (KSP/kapt vs plugin), app startup cost numbers, APK size, Gradle module boundaries (Hilt aggregating task), test story (`@TestInstallIn`).

**Ktor backend dev**
- Good: plugin install, injection in routes/handlers, feature functions, events, request scope, isolated, bridge, testing.
- Missing: Ktor with annotations beyond the quickstart (no `@Single` in a route example), configuration properties (`getProperty` mentioned in one sentence, no `application.conf`/`koin.properties` example), Koin + Ktor `Authentication` / `Sessions` pattern, Koin with Exposed/database transactions and coroutines context (`call.scope` and background coroutines get one sentence in Pitfalls), a deployment shape (Netty vs EngineMain `application.yaml` `modules` list; the quickstart only shows `embeddedServer`), production health: shutdown, `ApplicationStopping`. Ktor client DI not mentioned. Ktor 2 users: "targets Ktor 3" is the only statement; no upgrade note.
- `examples/ktor-di-sample`, `hello-ktor`, `multimodule-ktor` exist in the repo; none linked from the Ktor reference pages.

## 5. Undocumented / thinly documented modules

| Artifact | Documented where | Gap |
|---|---|---|
| `koin-android` | start, entry-points, scope, viewmodel, fragment-factory | none |
| `koin-android-compat` | one section in `viewmodel` (Java) | Java only for ViewModel and scope; `GetViewModelCompat` (`P/android/koin-android-compat/.../GetViewModelCompat.kt`) not mentioned |
| `koin-android-test` | only the `androidVerify` mention in `/docs/reference/koin-test/verify`; listed as a dead dependency in 3 quickstarts (S13) | no page on it from the Android side |
| `koin-androidx-navigation` | one code block in `viewmodel` | no Navigation component guide, no Safe Args example |
| `koin-androidx-startup` | `start` (App Startup section) | fine; also lacks the `-keep` note (S1) |
| `koin-androidx-workmanager` | workmanager | fine |
| `koin-dagger-bridge` | gradle table (2 lines), releases (1 line, wrong direction, C2) | **no page**. It is the only module with zero usage documentation, and it is the core of the Hilt/Dagger coexistence story. Source docs live only in the KDoc of `DaggerBridge.kt:21-45`. |
| `koin-compose` | compose, lifecycle, scopes, modules, testing, isolated | none |
| `koin-compose-viewmodel` | compose-viewmodel | none, but see C3 |
| `koin-compose-viewmodel-navigation` | compose-viewmodel, compose-scopes | OK |
| `koin-compose-navigation3` | navigation3 | OK (supports android, jvm, js, wasm, ios per `build.gradle.kts`) |
| `koin-androidx-compose` | compose (packages), compose-scopes | its own `koinViewModel` (C3) and `KoinAndroidContext` are not described |
| `koin-androidx-compose-navigation` | one deprecated mention | OK (only `koinNavViewModel`, deprecated) |
| `koin-ktor` | five pages | see persona gaps |
| `koin-logger-slf4j` | one line in `ktor` setup + quickstart | no options page (`slf4jLogger(level)`) |
| `koin-jsr330` | jsr330 | unverified here (S4, C4) |

## 6. Page disposition (Diataxis)

Legend: T tutorial, H how-to, R reference, E explanation. "Mixed" marks pages that need a split or a clear internal boundary.

| Page | Category | Disposition |
|---|---|---|
| quickstart/android | T | keep, rewrite lead (see Section 4), becomes "first Android steps" |
| quickstart/android-annotations | T | merge into android-viewmodel as a tabbed page |
| quickstart/android-viewmodel | T | keep as the main Android tutorial, merge with annotations |
| quickstart/android-compose | T | keep, add Annotations tab, fix import (C3) |
| quickstart/junit-test | T (mixes H on `verify()`) | keep, rewrite after C6 decision |
| quickstart/kotlin, kotlin-annotations | T | merge into one tabbed page |
| quickstart/ktor, ktor-annotations | T | merge into one tabbed page |
| intro/koin-vs-hilt | E (mixes R in the concept table) | keep, fix C1, C7, C8; move the concept table to `hilt-migration` and link |
| migration/from-ksp-to-compiler-plugin | H | keep, rewrite version numbers (S14) |
| koin-android/start | H + R (mixed) | keep; split "Start with AndroidX App Startup" into its own page if it grows |
| koin-android/entry-points | R + E (mixed) | keep |
| koin-android/viewmodel | R | keep, trim duplicate paragraphs |
| koin-android/scope | R | keep (canonical for Android scopes) |
| koin-android/fragment-factory | H | keep |
| koin-android/workmanager | H | keep |
| koin-android/binding-patterns | H | keep, rename "Android library integration" (title already says it); drop redundant Hilt copy elsewhere |
| koin-android/advanced-patterns | H | keep or merge into binding-patterns (both 4-8 KB, both "recipes") |
| koin-android/multi-module | H + E (mixed) | keep |
| koin-android/best-practices | H + E (mixed) | split: "Avoid leaks" (E) and "Debugging errors" (R) ; the Secrets section moves to a how-to |
| koin-android/hilt-migration | H (with R tables) | keep, add "run side by side" and dagger-bridge section |
| koin-android/jsr330 | R + H (mixed) | keep, resolve C4/S4 first |
| koin-android/instrumented-testing | H | keep |
| koin-android/r8-proguard (repo only) | E + R (mixed) | publish, rewrite without em dashes, fix S1; link from `start` and from the consumer rule files |
| koin-compose/compose | R + T (mixed: start options, packages) | keep, shrink Packages table (point to gradle) |
| koin-compose/compose-viewmodel | R | keep |
| koin-compose/compose-lifecycle | E | keep |
| koin-compose/compose-scopes | R | keep |
| koin-compose/compose-modules | R | merge into compose-scopes (4 KB, both "bind koin to the composition") or keep as is |
| koin-compose/compose-testing | H | keep |
| koin-compose/isolated-context | H | keep |
| koin-compose/navigation3 | R + H (mixed) | keep, split migration table "From Navigation 2" into its own how-to |
| koin-ktor/ktor | R + H | keep, add imports (S7) |
| koin-ktor/ktor-scopes | R | keep |
| koin-ktor/ktor-isolated | H | keep |
| koin-ktor/ktor-bridge | H | keep |
| koin-ktor/ktor-testing | H | keep |

Missing pages to add: `koin-android/dagger-bridge` (H), `koin-android/navigation` (H) for `koin-androidx-navigation`, a short "first aid" page (R) for the Android errors table, `koin-ktor` configuration and properties (H).
