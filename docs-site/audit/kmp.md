<!-- Detailed audit appendix. `refs/` are shallow clones of sibling InsertKoinIO repos at audit time; `live/md` is the published text split per page, neither is committed. -->

# KMP documentation audit (Koin 4.2.2, repo HEAD d486a5e8)

Scope: live text in `$SCRATCH/live/md` (page names below are URL paths). Ground truth: `projects` (written `P/`). Sibling refs under `$SCRATCH/refs` (written `refs/`).

Legend: CONFIRMED = grepped in source. UNVERIFIED = could not be proven from the repo or refs (needs a build).

---

## 1. Contradictions between pages

| # | Page A (quote) | Page B (quote) | Truth / note |
|---|---|---|---|
| C1 | `/docs/reference/koin-core/kmp-setup`: `commonMain.dependencies { implementation(project.dependencies.platform(libs.koin.bom)); implementation(libs.koin.core) ... }` | `/docs/setup/gradle` (Compose Multiplatform and Kotlin Multiplatform sections): "In multiplatform source sets, use catalog entries that carry the version (see Without the BOM)" | Three-way: `/docs/setup` says "Recommended setup: Koin BOM ... For a new project". BOM source (`P/bom/koin-bom/build.gradle.kts`) is a plain `java-platform` with `api("io.insert-koin:koin-core-jvm:$version") //Check later KMP Bom` (same for `koin-ktor-jvm`). No KMP-aware BOM. Both official samples use no BOM (`refs/koin-getting-started/gradle/libs.versions.toml`). BOM in `commonMain` is UNVERIFIED. |
| C2 | `/docs/reference/koin-core/kmp-setup` iOS: `fun initKoinIos() = initKoin()`, Swift `KoinIosKt.doInitKoinIos()`, "The helper avoids passing a Kotlin lambda (`config`) from Swift." | `/docs/quickstart/cmp`: "Kotlin exports `initKoin()` as `doInitKoin()`", Swift `KoinKt.doInitKoin()`, where `initKoin(configuration: KoinAppDeclaration? = null)` | Two iOS patterns for the same function. Samples (`refs/koin-getting-started/compose/iosApp/iosApp/iOSApp.swift:7`, `refs/KMP-App-Template/iosApp/iosApp/iOSApp.swift:7`) call `doInitKoin()` with NO argument on a function that has a default parameter. Kotlin/Native ObjC export does not expose default values, so whether this compiles is UNVERIFIED (the template iOS CI only runs in the Kotlin org: `refs/koin-getting-started/compose/.github/workflows/build-ios.yml` has `if: ... repository_owner == 'Kotlin'`). Build on macOS before a new section claims either. |
| C3 | `/docs/reference/koin-core/starting-koin`: "Compose: start Koin from a composable with `KoinApplication` or `KoinMultiplatformApplication`." | `/docs/reference/koin-compose/compose`: "`KoinMultiplatformApplication(config)` Deprecated -> `KoinApplication(configuration)`" | Source: `P/compose/koin-compose/src/commonMain/kotlin/org/koin/compose/KoinApplication.kt:213` is `@Deprecated(... ReplaceWith("KoinApplication(configuration = config ..."))` (WARNING). starting-koin is stale. |
| C4 | `/docs/reference/koin-core/kmp-setup`: "you can start Koin from the root Composable with `KoinApplication` instead of calling `initKoin` on each platform" | `/docs/quickstart/cmp` (initKoin from Application and Swift) vs `/docs/quickstart/compose-multiplatform-annotations` ("there is no separate start code in `Application` or in the iOS app") | Both valid, but the two flagship tutorials teach opposite start strategies for the same app, with no comparison. The annotations sample has `initKoin` and the Swift call commented out (`refs/koin-getting-started/compose-annotations/composeApp/src/commonMain/kotlin/com/jetbrains/kmpapp/di/Koin.kt:30-42`, `.../iosApp/iosApp/iOSApp.swift:7`, `.../androidMain/.../MuseumApp.kt`). Koin-skills `skills/koin/references/entry-points.md` says the opposite default: "KMP with a common start function: That function, called by each native entry point. No `KoinApplication()`". |
| C5 | `/docs/reference/koin-core/kmp-setup` androidMain: `implementation(libs.koin.android)` next to `koin.compose` in commonMain | `/docs/setup/gradle`: "On Android, `koin-compose` brings `koin-android`, so you don't need to add it." | Source: `P/compose/koin-compose/build.gradle.kts:50-53` androidMain `api(project(":android:koin-android"))`. The androidMain line is redundant when `koin-compose` is used. |
| C6 | `/docs/setup/gradle` artifact table: `koin-test` "All", "Test API: `KoinTest`, `verify()`" | `/docs/reference/koin-test/verify`: "`verify()` ... available on the JVM in `koin-test`" | Source: `P/core/koin-test/src/jvmMain/kotlin/org/koin/test/verify/VerifyModule.kt:30`. `verify()` is jvmMain only. `checkModules` is commonMain but deprecated (`P/core/koin-test/src/commonMain/kotlin/org/koin/test/check/CheckModules.kt:43-117`). |
| C7 | `/docs/reference/koin-core/kmp-shared-modules` Limits: "Tests: shared modules can be tested in `commonTest`. See Testing." | `/docs/reference/koin-test/testing` line 5: "This guide shows how to write JVM unit tests with Koin" (JUnit4/5, `KoinTestRule`, `@JvmField`) | The linked page has no commonTest recipe. The only one is under "Multiplatform UI tests" in `/docs/reference/koin-compose/compose-testing`. |
| C8 | `/docs/setup/gradle` table: `koin-compose-navigation3` "All Compose targets"; `koin-core-viewmodel` "All"; `koin-core-coroutines` "All" | `/docs/reference/koin-compose/navigation3`: "`koin-compose-navigation3` targets Android, iOS, JVM desktop, JS and Wasm." | Source target lists (5a): navigation3 also has macOS; `koin-core-viewmodel` has no watchOS/Linux/mingw; `koin-core-coroutines` has no wasmJs. |
| C9 | `/docs/reference/koin-core/kmp-setup`: "`koin-core` and the Compiler Plugin support every Kotlin target"; `/docs/setup/compiler-plugin`: "Kotlin 2.3.20 or later" | `refs/koin-compiler-plugin/README.md` Compatibility: "Annotation-based (`@Module` / `@ComponentScan`) WASM/JS requires Kotlin 2.4.0 (KT-82395); JVM/Android, iOS/Native, and DSL-based WASM/JS work across the range." | Caveat missing from every doc page (grep KT-82395 in live md: no hit). |
| C10 | `/docs/setup/gradle` Versions: Koin 4.2.2 "Built with 2.3.20"; quickstarts "needs Kotlin 2.3.20 or later" | Sample catalog `refs/koin-getting-started/gradle/libs.versions.toml`: kotlin 2.4.10, compose-multiplatform 1.9.3, nav-compose 2.9.1. Koin repo `P/gradle/libs.versions.toml`: jb-compose 1.10.2, jb-navigation 2.9.2, jb-lifecycle 2.9.6 | Sample uses an older Compose MP than Koin 4.2.2 is built against. No page states supported Compose MP, lifecycle or navigation versions. |
| C11 | `/docs/reference/koin-android/jsr330`: "`koin-jsr330` ... is published with Koin Annotations, not with the Koin BOM. Use the Koin Annotations version." | `/docs/setup/gradle`: "All Koin libraries, including `koin-annotations`, share one version"; artifact list has no `koin-jsr330` | `koin-jsr330` is not in `P/settings.gradle.kts`, not in the BOM, not anywhere in `projects/` (only legacy `docs/`). Existence and targets UNVERIFIED. |
| C12 | `/docs/reference/koin-mp/kmp` Desktop: "Start Koin in `main()`, before the first window:" then `fun main() = application { initKoin { ... }; Window(...) }` (same code on kmp-setup) | n/a | Text says main(), code puts `initKoin` inside the `application { }` composable lambda, which re-executes on recomposition. A second `startKoin` throws `KoinApplicationAlreadyStartedException` (`P/core/koin-core/src/jvmMain/kotlin/org/koin/core/context/GlobalContext.kt:44`). Real bug in the snippet. |
| C13 | cmp quickstart: `// nativeMain` for `NativeComponent` actuals, `// iosMain` for `platformModule` | kmp-setup/kmp-shared-modules use `iosMain` only | Both source sets exist in the sample (`refs/koin-getting-started/compose/composeApp/src/nativeMain/...` and `iosMain/...`); never explained. |
| C14 | `/docs/support/releases` line 420: "now lives in `koin-core-viewmodel` and `koin-core-viewmodel-navigation`" | Gradle page: `koin-compose-viewmodel-navigation` | No `koin-core-viewmodel-navigation` in `P/settings.gradle.kts:33-68`. Historic, but readers will search for it. |

---

## 2. Docs vs source discrepancies

"OK" rows are kept where an exact signature matters for section 5.

| # | Doc claim (page) | Source evidence | Verdict |
|---|---|---|---|
| D1 | "`koin-core` supports JVM, Android, Apple targets (iOS, macOS, tvOS, watchOS), Linux, Windows, JS and Wasm" (gradle) | `P/core/koin-core/build.gradle.kts:10-38`: jvm, js(IR) browser+node, wasmJs (node), iosX64/Arm64/SimArm64, macosX64/Arm64, watchos x5, tvos x3, mingwX64, linuxX64, linuxArm64. No `androidTarget` (Android uses the jvm variant). | OK, "Android" is not a separate target. |
| D2 | `koin-core-viewmodel` "All" (gradle table) | `P/core/koin-core-viewmodel/build.gradle.kts:12-52`: android, jvm, js, wasmJs, ios x3, macos x2, tvos x3. No watchOS, Linux, mingw. | Imprecise |
| D3 | `koin-core-coroutines` "All" | `P/core/koin-core-coroutines/build.gradle.kts:10-33`: no `wasmJs` | Wrong for wasmJs |
| D4 | compose artifacts "All Compose targets" | `P/compose/koin-compose/build.gradle.kts:14-42` and the three siblings: android, jvm (JVM_11), js, wasmJs, iosX64/Arm64/SimArm64, macosX64/Arm64. No tvOS/watchOS/Linux/mingw. | OK, but kmp-setup never lists them |
| D5 | `koin-test-coroutines` "JVM" | `P/core/koin-test-coroutines/build.gradle.kts:10` only `jvm()` | OK |
| D6 | `koin-ktor` "JVM, JS, Wasm, iOS, macOS" | `P/ktor/koin-ktor/build.gradle.kts:9-37`: also watchOS, tvOS, mingwX64, linux x2 | Understated (outside KMP client scope) |
| D7 | `koin-test` "All" | `P/core/koin-test/build.gradle.kts:11-38` all native + js + wasm + jvm. jvmMain adds `kotlin("reflect")` and `verify/*`. `koin-test-junit4/5` are `kotlin("jvm")` (`P/core/koin-test-junit4/build.gradle.kts:4`). | OK for KoinTest/declareMock; wrong for `verify()` |
| D8 | `startKoin` throws `KoinApplicationAlreadyStartedException` (dsl, starting-koin, troubleshooting) | CONFIRMED on all contexts: jvm `GlobalContext.kt:44`, native `:44`, js `:41`, wasmJs `:41` (all under `P/core/koin-core/src/<set>Main/kotlin/org/koin/core/context/`). Native uses stately `Lock` (`nativeMain/.../GlobalContext.kt:30`), JS/wasm have no lock. | OK, but no KMP page mentions it |
| D9 | compose: "if Koin is already started, `KoinApplication` attaches to the running instance and ignores its own configuration" | `P/compose/koin-compose/src/commonMain/kotlin/org/koin/compose/application/RememberKoinApplication.kt:28-31` (`if (KoinPlatform.getKoinOrNull() == null) koinApplication(...) else null`) plus `CompositionKoinApplicationLoader.start()` re-attach branch. KDoc at `KoinApplication.kt:~171` still says `@throws KoinApplicationAlreadyStartedException`. | Docs correct, KDoc stale |
| D10 | compose: Android calls `androidContext()` + `androidLogger()`, others `printLogger()`, `logLevel` default `Level.INFO` | `KoinApplication.android.kt` (`androidContext(appContext); androidLogger(loggerLevel)`); `.jvm.kt/.native.kt/.wasmJS.kt/.js.kt` use `printLogger(loggerLevel)`; signature `KoinApplication.kt:182` | OK |
| D11 | "Koin is not stopped when `KoinApplication` leaves the composition ... stopped only if the composition is abandoned" | `CompositionKoinApplicationLoader.kt`: `onForgotten` nulls `koin`; `onAbandoned` calls `stop()` -> `stopKoin()` | OK |
| D12 | Deprecated table: `KoinApplication(application=...)`, `KoinMultiplatformApplication`, `KoinContext`, `KoinAndroidContext` | `KoinApplication.kt:128` (WARNING), `:213` (WARNING, `@KoinExperimentalAPI`), `:252/:265`, `P/compose/koin-androidx-compose/src/main/java/org/koin/androidx/compose/KoinAndroidContext.kt:41` | OK |
| D13 | `org.koin.dsl.koinConfiguration { }`, `includes(config)` | `P/core/koin-core/src/commonMain/kotlin/org/koin/dsl/KoinConfiguration.kt:57` (`koinConfiguration(declaration: KoinAppDeclaration): KoinConfiguration`), `:66` `includes(vararg KoinAppDeclaration?)`, `:77` `includes(vararg KoinConfiguration?)` | OK |
| D14 | `koinConfiguration<KoinApp>()` and `startKoin<KoinApp>` from `org.koin.plugin.module.dsl` | `P/core/koin-core/src/commonMain/kotlin/org/koin/plugin/module/dsl/ApplicationDSLExt.kt` (`startKoin<T>(appDeclaration: KoinAppDeclaration? = null)`, `startKoinWith:45`, `koinApplication<T>`, `koinConfiguration<T>:99`, `withConfiguration<T>:133`, `module<T>:169`, `modules(vararg KClass):193`); stubs throw `NotImplementedError` without the plugin (`BaseDSLExt.kt:7`) | OK |
| D15 | "annotation and Composable are both named `KoinApplication`" | annotation `P/core/koin-annotations/src/commonMain/kotlin/org/koin/core/annotation/CoreAnnotations.kt:295` (`KoinApplication(val configurations: Array<String> = [], val modules: Array<KClass<*>> = [Unit::class])`); composable `KoinApplication.kt:182` | OK |
| D16 | `viewModel<T>()` from `org.koin.plugin.module.dsl` | `P/core/koin-core-viewmodel/src/commonMain/kotlin/org/koin/plugin/module/dsl/ViewModelDSLExt.kt:10` (`Module.viewModel<T: ViewModel>()`), `:13` (`ScopeDSL`). Lives in `koin-core-viewmodel`, not `koin-core`. | OK |
| D17 | `viewModelOf(::X)`, `viewModel { }` from `org.koin.core.module.dsl` | `P/core/koin-core-viewmodel/src/commonMain/kotlin/org/koin/core/module/dsl/ModuleExt.kt:33` (`Module.viewModel(qualifier, definition)` = `factory`), `ViewModelOf.kt`, `ScopeViewModelOf.kt:41-185` | OK |
| D18 | `@KoinViewModel` "is in `org.koin.core.annotation` and works in common code" | `P/core/koin-annotations/src/commonMain/kotlin/org/koin/core/annotation/CoreAnnotations.kt:313` (`KoinViewModel(val binds: Array<KClass<*>> = [])`, targets CLASS, FUNCTION) | OK |
| D19 | viewmodel page shows `@InjectedParam`; Dependencies lists only `koin-core-viewmodel` | `@InjectedParam` is in `koin-core-annotations` (`P/core/koin-core-annotations/src/commonMain/kotlin/org/koin/core/annotation/KoinCoreAnnotations.kt:27`); `koin-core-viewmodel` only has `api(koin-core)` (`build.gradle.kts:56`). Needs `koin-annotations` (api to core-annotations, `P/core/koin-annotations/build.gradle.kts:42`). injection-parameters page says so; viewmodel page does not. | Missing dependency note |
| D20 | "`viewModelScope` and `viewModelScopeFactory()` require opting in to `@KoinViewModelScopeApi` and `@KoinExperimentalAPI`" | `Module.viewModelScope` `P/core/koin-core-viewmodel/src/commonMain/kotlin/org/koin/viewmodel/scope/ViewModelScopeArchetypeDSL.kt:41` (both); `viewModelScopeFactory()` `P/core/koin-core/src/commonMain/kotlin/org/koin/core/option/KoinOption.kt:39` (`@KoinViewModelScopeApi`); opt-in WARNING level (`KoinAnnotations.kt:~100`) | OK. Name clash: androidx `ViewModel.viewModelScope` (CoroutineScope, cmp quickstart `stateIn(viewModelScope, ...)`) vs Koin `Module.viewModelScope { }`; never disambiguated. |
| D21 | SavedStateHandle auto-created; error text | `P/core/koin-core-viewmodel/src/commonMain/kotlin/org/koin/viewmodel/factory/AndroidParametersHolder.kt:25-55` (error at `:47`) | OK. Page says "On Android, navigation arguments ... are in the SavedStateHandle"; nothing for iOS/desktop/web. Code is commonMain; behavior with Nav Compose on iOS UNVERIFIED. |
| D22 | `koinViewModel` params | `P/compose/koin-compose-viewmodel/src/commonMain/kotlin/org/koin/compose/viewmodel/ViewModel.kt:47-54` (`qualifier, viewModelStoreOwner, key, extras, scope, parameters`) | OK |
| D23 | `sharedKoinViewModel(navController, navGraphRoute)` | `P/compose/koin-compose-viewmodel-navigation/src/commonMain/kotlin/org/koin/compose/viewmodel/NavViewModel.kt:81-84`; falls back to `koinViewModel()` when no parent | OK |
| D24 | `koinNavViewModel` deprecated in both artifacts | `NavViewModel.kt:41-48` (commonMain) and `P/compose/koin-androidx-compose-navigation/src/main/java/org/koin/androidx/compose/navigation/NavViewModel.kt:41` | OK |
| D25 | `koinActivityViewModel` Android only | `P/compose/koin-compose-viewmodel/src/androidMain/kotlin/org/koin/compose/viewmodel/ViewModelExt.kt:45`. Also `koinActivityInject` in `koin-compose` androidMain (`P/compose/koin-compose/src/androidMain/kotlin/org/koin/compose/activity/KoinActivityInject.kt:44`), absent from the compose.md package table. | OK |
| D26 | `koinInject` three overloads | `P/compose/koin-compose/src/commonMain/kotlin/org/koin/compose/Inject.kt:44,67,87`; lambda form invokes `parameters.invoke()` each recomposition (`:50`) | OK |
| D27 | `navigation<T> { }` from `org.koin.dsl.navigation3`, `koinEntryProvider()` from `org.koin.compose.navigation3` | `P/compose/koin-compose-navigation3/src/commonMain/kotlin/org/koin/dsl/navigation3/ModuleExt.kt:58,99`; `.../org/koin/compose/navigation3/EntryProvider.kt:76`; Android-only `entryProvider/getEntryProvider` `.../androidMain/.../ComponentCallbacksExt.kt:26,45`; all `@KoinExperimentalAPI` | OK |
| D28 | `KoinNavigationScope`, `navigationScope` | `P/compose/koin-compose-viewmodel-navigation/src/commonMain/kotlin/org/koin/compose/scope/KoinNavigationScope.kt:33`; `.../org/koin/module/dsl/NavigationScopeDSL.kt:35` | OK |
| D29 | `KoinApplicationPreview(application = ...)` | `KoinApplication.kt:303` | OK |
| D30 | compose-testing: "`KoinTest`, `startKoin` and `declareMock` are multiplatform. `KoinTestRule` and `MockProviderRule` are JVM only." | `KoinTest` `P/core/koin-test/src/commonMain/kotlin/org/koin/test/KoinTest.kt`; `declareMock` `mock/DeclareMock.kt:30,44,58`; `MockProvider.register` `mock/MockProvider.kt:13`; rules in `P/core/koin-test-junit4/src/main/kotlin/org/koin/test/KoinTestRule.kt` and `.../mock/MockProviderRule.kt` | OK. Missing: off-JVM `declareMock` needs you to call `MockProvider.register { kClass -> ... }` (else "Missing MockProvider", `MockProvider.kt:8`); not documented anywhere. |
| D31 | kmp.md "Declaring a Swift instance": `Koin.declare(instance, qualifier, secondaryTypes, allowOverride)` | `P/core/koin-core/src/commonMain/kotlin/org/koin/core/Koin.kt:189-194` `inline fun <reified T> declare(instance: T, qualifier: Qualifier? = null, secondaryTypes: List<KClass<*>> = emptyList(), allowOverride: Boolean = true)`. The helper passes `instance: Any`, so primary type is `Any`; only `secondaryTypes` carries the real type. | Signature OK. `getOriginalKotlinClass` / `ObjCClass` / `ObjCProtocol` appear NOWHERE in `projects/` or `examples/` (only legacy `docs/reference/koin-mp/kmp.md:477-486`). The page says it "comes from `kotlin.native`"; I believe it is `kotlinx.cinterop` (UNVERIFIED, no Kotlin stdlib source on disk). Swift usage `koin.declareFromSwift(...)` never shows how Swift gets `koin`. Source is a GitHub issue comment (#1108). |
| D32 | kmp.md Threading: "current Kotlin/Native memory model (default since 1.7.20) ... no freezing or `@SharedImmutable`" | Native context uses stately `Lock` (`P/core/koin-core/src/nativeMain/kotlin/org/koin/core/context/GlobalContext.kt:30,56-70`); stately dep `P/core/koin-core/build.gradle.kts:42` | Consistent; Kotlin version fact not checkable here |
| D33 | cmp: `createdAtStart`, `withOptions` imports from `org.koin.core.module.dsl` | `P/core/koin-core/src/commonMain/kotlin/org/koin/core/module/dsl/OptionDSL.kt:40,187` | OK. `P/CLAUDE.md` notes a compiler-plugin `createdAtStart` drop (#2425/#2415) and core `createdAtStart` failing on wasmJs; the tutorial relies on `createdAtStart`. |
| D34 | cmp: `single { create(::buildClient) }` with `org.koin.plugin.module.dsl.create` | `P/core/koin-core/src/commonMain/kotlin/org/koin/plugin/module/dsl/CreateDSL.kt:11-27` (`Scope.create(function)`) | OK |
| D35 | `androidContext(this@X)` | `P/android/koin-android/src/main/java/org/koin/android/ext/koin/KoinExt.kt:54`; `androidLogger` `:42` | OK |
| D36 | `printLogger()` on desktop/web; `fileProperties()` JVM-only | `P/core/koin-core/src/commonMain/kotlin/org/koin/core/KoinApplication.kt:134`; `P/core/koin-core/src/jvmMain/kotlin/org/koin/KoinApplicationExt.kt:13` | OK |

Plugin facts used on KMP pages: `@Module expect class` / `@Module @ComponentScan actual class`. The plugin skips `expect` classes and generates the module for the `actual` (`refs/koin-compiler-plugin/koin-compiler-plugin/src/org/koin/compiler/plugin/fir/KoinModuleFirGenerator.kt:825-836`, `:1110`). Consistent with the docs.

---

## 3. Duplicated content

| Topic | Pages | Action |
|---|---|---|
| `expect fun platformModule()` + `AndroidComponent`/`IOSComponent` | kmp-setup (structure + `includes`), kmp-shared-modules (full), cmp, cmp-annotations (annotation variant), kmp.md (`ContextWrapper` variant of the same idea) | Keep one canonical on kmp-shared-modules |
| `expect class NativeComponent()` | kmp-shared-modules, cmp, cmp-annotations | Same |
| "Android Context in shared code" | kmp.md `ContextWrapper`; kmp-shared-modules `AndroidComponent(private val context: Context)` + "Choosing a pattern" row | Merge: kmp-shared-modules already shows a Context-taking implementation |
| `initKoin(config)` + `includes(config)` | kmp-setup (3 flavours), cmp, kmp.md (desktop, web), starting-koin bullet, compose.md "Start outside Compose" | One definition in kmp-setup |
| Per-platform entry points | kmp-setup "Platform entry points" AND kmp.md "Desktop (JVM)", "Web (JS and Wasm)" (near-identical, different snippets) | Merge |
| KMP Gradle dependencies | gradle.md (3 sections), kmp-setup, compiler-plugin "Kotlin Multiplatform", annotations-ksp "Kotlin Multiplatform", from-ksp migration "Kotlin Multiplatform", cmp, cmp-annotations, viewmodel "Dependencies", compose "Packages", setup.md table | Gradle page as source; others show only the delta |
| Supported targets | kmp-setup table, gradle.md paragraph + artifact table, what-is-koin lines 84-91, production-monitoring line 31 | One matrix (5a) |
| `KoinApplication` vs `initKoin` | kmp-setup "Compose Multiplatform", compose.md "Starting Koin", both quickstarts | One decision table |
| Museum data layer (MuseumObject, MuseumApi, MuseumStorage, MuseumRepository) | cmp and cmp-annotations, about 120 identical lines | Share or link; both are copies of KMP-App-Template |
| ViewModel declaration in KMP | cmp "Let's write the ViewModels", viewmodel.md, compose-viewmodel.md | Declare once |
| Deprecated Compose entry points | compose.md table; starting-koin still recommends one | Fix starting-koin |

---

## 4. Gaps for a KMP developer (empty project to Android + iOS + Compose MP + tests)

| # | Gap |
|---|---|
| G1 | No "create the project" step: no wizard/template, no `iosApp` Xcode project, no mention that the samples fork `KMP-App-Template` (only visible in `API_URL`). |
| G2 | iOS Gradle config absent from every page: `iosX64/iosArm64/iosSimulatorArm64`, `binaries.framework { baseName = "ComposeApp"; isStatic = true }` (`refs/koin-getting-started/compose/composeApp/build.gradle.kts`), how `baseName` becomes the Swift `import` (kmp-setup `import Shared`, cmp omits it, sample `import ComposeApp`). Also `androidTarget`, AGP `android { namespace; compileSdk }`, Koin Android minSdk 23 (`P/gradle.properties`). |
| G3 | No complete KMP version catalog: kmp-setup uses `libs.koin.bom`, `libs.koin.compose`, `libs.plugins.koin.compiler` without defining them; Gradle catalog lacks the plugin entry, `koin-core-viewmodel`, `koin-compose-navigation3`; quickstarts use `<KOIN_VERSION>`. No matching Compose MP / lifecycle / navigation versions (C10). |
| G4 | Source-set placement only implied (`koin-android` redundant, multi-Gradle-module `api` vs `implementation` for `koin-annotations` in cmp-annotations unexplained, plugin must be applied in every module with definitions). |
| G5 | `commonTest`: no plain KMP unit-test recipe (`startKoin/stopKoin` with `kotlin.test`, `KoinTest`, `declareMock` + `MockProvider.register`, KMP mocking libraries). `verify()` is JVM-only so KMP users must use `jvmTest` or the plugin; unsaid. `P/CLAUDE.md` known off-JVM failures and rules (no backtick names, block-body `runTest`, `test` skips native/JS/Wasm) affect user tests too and are unmentioned. |
| G6 | Swift: only the 2022 `KoinComponent` helper. No `KoinPlatform` from Swift, no ObjC class lookup (does not exist), nothing for native SwiftUI ViewModels. Koin-skills says "Better Swift integration is in progress, not available yet" and native SwiftUI screens are out of scope. Docs should say this plainly. |
| G7 | Web: no `ComposeViewport`/wasmJs `main()`, no browser vs node note, no annotation-on-WASM/JS Kotlin 2.4.0 constraint (C9). Desktop: Compose Desktop `application { }` pitfall (C12). |
| G8 | No KMP troubleshooting: `KoinApplicationAlreadyStartedException` (initKoin plus KoinApplication, desktop recomposition), plugin missing `NotImplementedError` (only in dsl/troubleshooting), KLIB "Different declarations with the same signatures" (releases 1.2.1 note line 174), Kotlin/plugin version mismatch (`NoSuchMethodError` on 2.4.20 with plugin 1.2.0), `Koin could not create a SavedStateHandle` off-Android. |
| G9 | Idempotence story absent: `startKoin` is not idempotent on any platform (D8); `KoinApplication` is tolerant. No guidance on where to call `initKoin` on iOS (App init, not a view init) or after Activity recreation. |
| G10 | iOS lifecycle/threading: one sentence only. Nothing on JS/wasm having no lock, or on ViewModel dispatchers. |
| G11 | No statement of what runs where: `koin-android` APIs (`androidContext`, `by viewModel`, `koinActivityViewModel`, `entryProvider`) vs commonMain. The DSL sample's `MainActivity` uses `koinActivityViewModel` (Android only) but the tutorial never mentions it. |
| G12 | Navigation: cmp teaches Nav Compose 2 via `koin-compose-viewmodel-navigation`. The nav3 page is Android-first in tone though the artifact is KMP; no CMP walkthrough. Koin-skills recommends nav3 for shared UI. |
| G13 | CMP previews (`KoinApplicationPreview` + `@Preview`) only on compose-testing in Android flavour. |
| G14 | No sample for: shared module + native SwiftUI, desktop, web (5j). |
| G15 | `initKoin` vs `KoinApplication` for apps needing Koin outside Compose: one sentence each, no decision table. |

---

## 5. VERIFIED FACT SHEET for writing a new KMP section

`P` = `projects`. `koinVersion=4.2.2` (`P/gradle.properties:14`). Repo toolchain: Kotlin 2.3.20, AGP 8.13.2, jb-compose 1.10.2, jb-lifecycle 2.9.6, jb-navigation 2.9.2, androidx navigation3 1.0.1 (`P/gradle/libs.versions.toml`). Android minSdk 23, compileSdk 36.

### (a) Shared vs platform; artifacts and targets

All artifacts, `koin-annotations` included, share one version (the module is in this repo: `P/core/koin-annotations`). Group `io.insert-koin`.

| Artifact | KMP? | Targets published (build.gradle.kts) | Notes |
|---|---|---|---|
| `koin-core` | yes | jvm, js (browser+node), wasmJs (node), iosX64/Arm64/SimArm64, macosX64/Arm64, watchosArm32/Arm64/DeviceArm64/SimArm64/X64, tvosArm64/SimArm64/X64, mingwX64, linuxX64, linuxArm64 (`P/core/koin-core/build.gradle.kts:10-38`) | Android uses the jvm variant. Plugin DSL stubs (`single<T>()`, `startKoin<T>()`) in `commonMain/.../plugin/module/dsl`. |
| `koin-core-annotations` | yes | same list as koin-core | Holds `@InjectedParam`, `@Provided` |
| `koin-annotations` | yes | same list | `api(koin-core-annotations)`; `@Singleton`, `@Factory`, `@Module`, `@ComponentScan`, `@Configuration`, `@KoinApplication`, `@KoinViewModel`, `@ViewModelScope`, `@Named` (`CoreAnnotations.kt`, `CoreScopeArchetypes.kt`) |
| `koin-core-coroutines` | yes | koin-core list minus wasmJs | `P/core/koin-core-coroutines/build.gradle.kts:10-33` |
| `koin-core-viewmodel` | yes | android, jvm, js, wasmJs, ios x3, macos x2, tvos x3 | `api(koin-core)`, `api(jb lifecycle-viewmodel + savedstate)` |
| `koin-android` | Android | android | `api(koin-core-viewmodel)` (`P/android/koin-android/build.gradle.kts:44-49`) |
| `koin-compose` | yes | android, jvm, js, wasmJs, ios x3, macos x2 | `api(koin-core, compose runtime+foundation)`; androidMain `api(koin-android)` |
| `koin-compose-viewmodel` | yes | same | `api(koin-compose, koin-core-viewmodel, jb compose-viewmodel)` |
| `koin-compose-viewmodel-navigation` | yes | same | `api(koin-compose-viewmodel, jb compose navigation)` |
| `koin-compose-navigation3` | yes | same | `api(koin-compose)`, `implementation(androidx navigation3 runtime)`; androidMain `api(koin-android)` |
| `koin-androidx-compose`, `koin-androidx-compose-navigation` | Android only | android | first = `api(koin-compose)` + `api(koin-compose-viewmodel)` (`P/compose/koin-androidx-compose/build.gradle.kts:43-44`) |
| `koin-test` | yes | all koin-core targets | commonMain: `KoinTest`, `declare`, `declareMock`, `MockProvider`, deprecated `checkModules`; jvmMain: `verify()`, `verifyAll()` |
| `koin-test-junit4`, `koin-test-junit5`, `koin-test-coroutines` | JVM only | jvm | `KoinTestRule`, `AutoCloseKoinTest`, `MockProviderRule` / `KoinTestExtension`, `MockProviderExtension` |
| `koin-bom` | n/a | `java-platform` | Plain constraints plus explicit `-jvm` entries for core and ktor, comment "Check later KMP Bom". UNVERIFIED in `commonMain`. |

Split to state: shared = `koin-core`, `koin-annotations`, `koin-core-viewmodel`, `koin-compose*`; platform-only = `koin-android*`, `koin-androidx-*`, JVM-only test modules. Compose targets exclude tvOS/watchOS/Linux/mingw.

### (b) Install

- Plugin id `io.insert-koin.compiler.plugin`, version 1.2.1 (`refs/koin-compiler-plugin/gradle.properties:4`), needs Koin 4.2.0+, Kotlin verified 2.3.20, 2.4.0, 2.4.10, 2.4.20 (`refs/koin-compiler-plugin/supported-kotlin-versions.txt`). Annotation-based WASM/JS needs Kotlin 2.4.0 (README). Options: `koinCompiler { userLogs; debugLogs; unsafeDslChecks; skipDefaultValues }`.
- One plugin application per Gradle module that declares definitions, no per-target config, no KSP.
- Tested catalog (`refs/koin-getting-started/gradle/libs.versions.toml`): `koin-core`, `koin-annotations`, `koin-test`, `koin-android`, `koin-compose-viewmodel`, `koin-compose-viewmodel-navigation` all `version.ref = "koin"`; plugin `koin = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }`; no BOM.
- Samples' placement: DSL sample commonMain `koin-core` + `koin-compose-viewmodel-navigation` (`refs/koin-getting-started/compose/composeApp/build.gradle.kts`); annotations sample commonMain `koin-core` + `koin-compose-viewmodel` + `koin-annotations`. androidMain/iosMain: Ktor engines only, no Koin artifact. No `commonTest` in any sample.
- Minimal CMP set: `koin-core`, `koin-compose`, `koin-compose-viewmodel`; add `koin-compose-viewmodel-navigation` (Nav Compose 2), `koin-compose-navigation3` (Nav 3), `koin-annotations` (annotations style). `koin-android` only for non-Compose Android APIs.

### (c) Starting Koin once from shared code

- `startKoin(appDeclaration: KoinAppDeclaration)`, `startKoin(KoinConfiguration)`, `startKoin(KoinApplication)` in `org.koin.core.context` (`P/core/koin-core/src/commonMain/kotlin/org/koin/core/context/DefaultContextExt.kt:35,41,44`). `typealias KoinAppDeclaration = KoinApplication.() -> Unit` (`.../dsl/KoinApplication.kt:22`).
- Typed: `startKoin<T>(appDeclaration: KoinAppDeclaration? = null): KoinApplication`, `koinConfiguration<T>(appDeclaration = null): KoinConfiguration` (`ApplicationDSLExt.kt:99`), `koinApplication<T>()`, `KoinApplication.withConfiguration<T>()` (`:133`), all in `org.koin.plugin.module.dsl`. `T` must be annotated `@KoinApplication`; modules come from `@Configuration` modules across Gradle modules. The plugin rewrites the call to `startKoinWith(modules, appDeclaration)` (`:45`) = `KoinPlatformTools.defaultContext().startKoin { modules(modules); appDeclaration?.invoke() }`. Without the plugin: `NotImplementedError` (`BaseDSLExt.kt:7`). Plain Kotlin function, so Swift name mangling is unchanged.
- `koinConfiguration(declaration)` (non-generic) is `org.koin.dsl.koinConfiguration` (`KoinConfiguration.kt:57`). Merge helper `KoinApplication.includes(vararg KoinAppDeclaration?)` / `(vararg KoinConfiguration?)` in `org.koin.dsl` (`:66,:77`). Idiom used by both samples and `refs/KMP-App-Template`: `fun initKoin(config: KoinAppDeclaration? = null) { startKoin { includes(config); modules(appModule) } }`.
- Android: `class App : Application { onCreate { initKoin { androidContext(this@App) } } }` (`KoinExt.kt:54`).
- iOS: a top-level `initKoin` in `Koin.kt` is exported as `KoinKt.doInitKoin(...)` (`init` prefix -> `doInit`; file `Foo.kt` -> `FooKt`). Evidence: `refs/koin-getting-started/compose/iosApp/iosApp/iOSApp.swift:7`, `refs/hello-kmp/iosApp/iosApp/iOSApp.swift:8` (`HelperKt.doInitKoin()` on a no-arg Kotlin `initKoin()` at `Helper.kt:14`). Callable as `doInitKoin()` when the Kotlin function has a defaulted lambda parameter: UNVERIFIED (C2). Safe form: a no-arg `iosMain` function (as kmp-setup says) or `doInitKoin(config: nil)`. Swift module name = `binaries.framework.baseName` (sample `ComposeApp`, static).
- Desktop: call `initKoin { printLogger() }` in `fun main()` BEFORE `application { }` (C12). Web: `initKoin()` in `main()` before the Compose viewport.
- Compose-hosted start: `KoinApplication(configuration: KoinConfiguration, logLevel: Level = Level.INFO, content: @Composable () -> Unit)` in `org.koin.compose` (`P/compose/koin-compose/src/commonMain/kotlin/org/koin/compose/KoinApplication.kt:182`). Android: `androidContext + androidLogger`; elsewhere `printLogger(level)`; re-attaches if Koin already runs (`RememberKoinApplication.kt:28-31`).
- Keep `initKoin` when anything outside Compose needs Koin (Service/WorkManager, Swift `KoinComponent` helper, native SwiftUI screens, tests). Whichever starter runs first wins.

### (d) expect/actual patterns (verified in samples)

1. `expect fun platformModule(): Module` (commonMain) + `actual fun platformModule() = module { single<AndroidComponent>() bind PlatformComponent::class }` (`refs/koin-getting-started/compose/composeApp/src/commonMain/kotlin/com/jetbrains/kmpapp/di/Koin.kt`, `iosMain/.../Koin.ios.kt`, `androidMain/.../Koin.android.kt`). `bind` = `org.koin.dsl.bind` (`P/core/koin-core/src/commonMain/kotlin/org/koin/dsl/DefinitionBinding.kt:36`).
2. `expect class NativeComponent()` + `actual class ... actual constructor()` + `single<NativeComponent>()` in common.
3. Annotations: `@Module expect class X` with `@Module @ComponentScan actual class X` per platform; `@Singleton` classes in each platform source set; `@Module(includes = [X::class])` in common (`refs/koin-getting-started/compose-annotations/composeApp/src/.../di/Koin.kt`, `Koin.ios.kt`, `Koin.android.kt`).
4. Platform libraries via lambda (`single<HttpClientEngine> { OkHttp.create() }`): kmp-shared-modules only, not in a sample.
5. Android `Context`: `AndroidContextWrapper(val context: Context)` bound to a common `ContextWrapper`; resolves after `androidContext(...)` (or `KoinApplication` on Android). Sample: `refs/koin-getting-started/compose-annotations/composeApp/src/androidMain/.../AndroidContextWrapper.kt`.

### (e) Swift / SwiftUI

- In `projects/` and `examples/` there is NO Swift helper, NO ObjC-class lookup, NO `getOriginalKotlinClass`, `ObjCClass`, `ObjCProtocol` (grep over `.kt`, `.swift`, `.md`: only legacy `docs/reference/koin-mp/kmp.md`). `koin.get(objCClass:...)` does not exist. `Koin.get` takes `KClass` or is reified inline (not callable from Swift).
- Supported: a `KoinComponent` subclass in `iosMain` with `by inject()`, exposed as a plain Kotlin class (`refs/hello-kmp/shared/src/iosMain/kotlin/com/example/helloworldkmp/Helper.kt:9-12`, used as `GreetingHelper().greet()`; 2022 sample). `KoinComponent.getKoin()` = `KoinPlatformTools.defaultContext().get()` (`P/core/koin-core/src/commonMain/kotlin/org/koin/core/component/KoinComponent.kt:33`). `KoinPlatform.getKoin()/getKoinOrNull()` exist (`P/core/koin-core/src/commonMain/kotlin/org/koin/mp/KoinPlatform.kt:54,59`); from Swift this would be `KoinPlatform.shared.getKoin()` (UNVERIFIED, no Swift sample).
- Declaring a Swift-implemented instance: `Koin.declare(...)` exists (D31); the ObjC-to-KClass helper is documentation-only and unverified.
- ViewModels from Swift: no API. Supported route is Compose Multiplatform inside `ComposeUIViewController { App() }` (`refs/koin-getting-started/compose/composeApp/src/iosMain/kotlin/com/jetbrains/kmpapp/MainViewController.kt`). Koin-skills `entry-points.md`: native SwiftUI screens out of scope, "Better Swift integration is in progress, not available yet".

### (f) Shared ViewModels

- `koin-core-viewmodel` (targets in 5a), pulled in by `koin-android` and `koin-compose-viewmodel`.
- DSL: `Module.viewModel(qualifier = null, definition)` (`org.koin.core.module.dsl`, `ModuleExt.kt:33`, a `factory`), `viewModelOf(::T)` (`ViewModelOf.kt`, up to 18 args in `ScopeViewModelOf.kt`). Compiler Plugin DSL: `org.koin.plugin.module.dsl.viewModel<T: ViewModel>()` on `Module` and `ScopeDSL` (`ViewModelDSLExt.kt:10,13`). Annotation: `@KoinViewModel` (`org.koin.core.annotation`, koin-annotations).
- Retrieval: `koinViewModel<T>()` (compose, common); `by viewModel()` is Android only.
- ViewModel scope: `Module.viewModelScope { }` + `startKoin { options(viewModelScopeFactory()) }`; opt-ins `@KoinViewModelScopeApi` and `@KoinExperimentalAPI`; annotation `@ViewModelScope` (`CoreScopeArchetypes.kt:36`); `ScopeViewModel` exists (experimental, `ScopeViewModel.kt`). Do not confuse with androidx `viewModelScope`.
- `SavedStateHandle`: created from `CreationExtras` for the constructor parameter (`AndroidParametersHolder.kt:25-55`, commonMain). Fails with "Koin could not create a SavedStateHandle..." if extras lack a `SavedStateRegistryOwner`. Behavior on iOS/desktop with Nav Compose: UNVERIFIED.
- `@InjectedParam` needs `koin-annotations` or `koin-core-annotations` (D19).

### (g) Compose Multiplatform

| API | Status | Location |
|---|---|---|
| `KoinApplication(configuration, logLevel, content)` | current | `KoinApplication.kt:182` |
| `KoinApplication(application = {...}, content)` | deprecated (WARNING) | `:128` |
| `KoinMultiplatformApplication(config, logLevel, content)` | deprecated (WARNING), `@KoinExperimentalAPI` | `:213` |
| `KoinContext { }` | deprecated, "not needed" | `:252`, `:265` |
| `KoinAndroidContext { }` | deprecated, Android only | `koin-androidx-compose/.../KoinAndroidContext.kt:41` |
| `KoinIsolatedContext(context: KoinApplication, content)` | current | `:283` |
| `KoinApplicationPreview(application, content)` | current, local container | `:303` |
| `getKoin()`, `currentKoinScope()` | current, Composable | `:89`, `:106` |
| `koinInject<T>(qualifier, scope, parameters / parametersHolder)` | current | `Inject.kt:44,67,87` |
| `koinViewModel<T>(qualifier, viewModelStoreOwner, key, extras, scope, parameters)` | current, common | `koin-compose-viewmodel/.../ViewModel.kt:47` |
| `koinActivityViewModel<T>()`, `koinActivityInject<T>()` | Android only | `.../androidMain/.../ViewModelExt.kt:45`, `KoinActivityInject.kt:44` |
| `NavBackStackEntry.sharedKoinViewModel<VM>(navController, navGraphRoute)` | current | `koin-compose-viewmodel-navigation/.../NavViewModel.kt:81` |
| `koinNavViewModel<T>()` | deprecated | `NavViewModel.kt:41-57` |
| `KoinScope`, `UnboundKoinScope`, `rememberKoinScope`, `rememberKoinModules` (`@KoinExperimentalAPI`), `KoinNavigationScope` (`@KoinExperimentalAPI`) | current | D28, `RememberModules.kt:38`, `RememberScopes.kt:39`, `UnboundKoinScope.kt:66` |
| Nav3: `Module.navigation<T> {}`, `ScopeDSL.navigation<T>`, `koinEntryProvider<T>()` | current, `@KoinExperimentalAPI` | D27 |

Navigation artifacts: Nav Compose 2 = `koin-compose-viewmodel-navigation` (also Android-only `koin-androidx-compose-navigation`, deprecated `koinNavViewModel` only); Nav 3 = `koin-compose-navigation3`. Both samples use nav2 (`org.jetbrains.androidx.navigation:navigation-compose:2.9.1`).

### (h) commonTest

- Works on every target (`koin-test` commonMain): `KoinTest` (`interface KoinTest : KoinComponent` with `get/inject` extensions), `declare`, `declareMock` (needs `MockProvider.register { ... }`, no default), deprecated `checkModules`. `commonTest.dependencies { implementation(libs.koin.test) }`; it exposes `kotlin.test` and `koin-core-annotations` as `api`.
- JVM only: `verify()` / `verifyAll()` (jvmMain), `KoinTestRule`, `AutoCloseKoinTest`, `MockProviderRule`, JUnit5 equivalents, `koin-test-coroutines`.
- Recipe in compose-testing (consistent with source): `@BeforeTest startKoin { modules(...) }`, `@AfterTest stopKoin()`, `runComposeUiTest { setContent { ... } }`.
- Known off-JVM failures (`P/CLAUDE.md`, "Known pre-existing off-JVM failures"): wasmJs `createdAtStart` tests (`CreateOnStart`, `OverrideAndCreateatStartTest`); native `DynamicModulesTest` (backtick names with " - " crash `ServiceMessagesParser`, abort `macosArm64Test`). Rules: no backtick names in `commonTest`, block-body `runTest { }`, `./gradlew test` skips native/JS/Wasm (use `allTests`). Note `P/core/koin-core/src/commonTest/kotlin/org/koin/dsl/KoinAppCreationTest.kt:30,40,55` itself uses backtick names, contradicting the rule.

### (i) Troubleshooting facts

- Double start: `KoinApplicationAlreadyStartedException("A Koin Application has already been started")` on jvm/native (`GlobalContext.kt:44`), js/wasmJs (`:41`). `startKoin` is NOT idempotent; test at `KoinAppCreationTest.kt:55-66`. `KoinApplication` composable IS tolerant (re-attaches). `stopKoin()` = `DefaultContextExt.kt:49`.
- Android Context: not available in commonMain; bind through a platform class (5d.5). `androidContext()` must run before resolving definitions that take `Context`; `KoinApplication` on Android does it.
- iOS lifecycle: no Koin hooks; start once at app init. `KoinApplication` does not stop Koin on removal (only on abandon).
- Freezing/threading: not needed; native global context is lock-guarded (stately); js/wasm single-threaded, unguarded. Eager creation runs on the thread calling `startKoin`.
- wasmJs `createdAtStart`: core tests currently fail (CLAUDE.md), while the tutorial uses `createdAtStart`.
- Native naming: `init*` -> `doInit*`; `Foo.kt` top-level -> `FooKt`.

### (j) Best sample for the end-to-end walkthrough

- `examples/` CONFIRMED has no KMP sample: `android-perfs`, `androidx-samples`, `coffee-maker`, `hello-ktor`, `jvm-perfs`, `ktor-di-sample`, `multimodule-ktor`, `sample-android-compose`, `sample-desktop-compose`. `sample-desktop-compose` is `kotlin("jvm")` + `org.jetbrains.compose` + `koin-compose` (not multiplatform). No `iosApp`, `*.xcodeproj` or `iosMain` anywhere in the repo.
- `refs/koin-getting-started/compose` (DSL) and `compose-annotations`: single `composeApp` module (DSL) or `data` + `composeApp` (annotations); targets androidTarget + iosX64/iosArm64/iosSimulatorArm64 only (no jvm/js/wasm); Kotlin 2.4.10, Koin 4.2.2, plugin 1.2.1, AGP 8.13.2, Compose MP 1.9.3, Ktor 3.3.3, Coil 3.3.0, nav-compose 2.9.1 (HEAD 15e8a2f, 2026-10-21); catalog shared through `versionCatalogs { create("sharedLibs") }` pointing to `../gradle/libs.versions.toml`, so the folder cannot be copied alone; no BOM; no tests; no desktop/web; no Swift-side Koin use beyond `doInitKoin`; DSL sample's `MainActivity` uses `koinActivityViewModel` and `initKoin` runs `KoinPlatform.getKoin().get<...>()` smoke prints; annotations sample has its start code commented out.
- Assessment: good for Android + iOS + CMP + DI structure; does not cover Desktop/Web, tests, nav3, Swift retrieval. A full walkthrough needs `commonTest` (and optionally a `jvm()` target) added. Build the iOS app before publishing (C2).
- Other refs: `refs/hello-kmp` (2022, only source of the Swift `KoinComponent` helper, pre-plugin), `refs/KMP-App-Template` (Nov 2024, upstream of both samples, KSP-era `AppModule().module`), `refs/koin-compiler-plugin/test-apps/sample-app` (jvm only), `playground-apps/app-kmp-klib*` (klib stress apps).

---

## 6. Recommended disposition (KMP pages)

| Page | Disposition |
|---|---|
| `/docs/reference/koin-core/kmp-setup` | Rewrite as the single KMP setup page: target matrix (5a), BOM decision (C1), full catalog, layout, shared `initKoin`, entry points (absorb desktop and web), `initKoin` vs `KoinApplication` table. Fix the desktop snippet and the iOS pattern after a macOS build. |
| `/docs/reference/koin-core/kmp-shared-modules` | Keep, trim: canonical expect/actual patterns; absorb the Android Context section from kmp.md; fix the Tests link. |
| `/docs/reference/koin-mp/kmp` | Split and retire: Context -> shared-modules; Desktop/Web -> setup; Swift -> new "Swift and iOS" page stating what is supported (start, `KoinComponent` helper, Compose host, `declare` caveat, no native ViewModel API). |
| `/docs/quickstart/cmp` | Keep as DSL walkthrough; add iOS Gradle block, framework name and import, nativeMain/iosMain explanation, shrink the copied data layer, add a test step. |
| `/docs/quickstart/compose-multiplatform-annotations` | Merge with cmp (style tabs, one shared data layer) or keep thin and link to cmp. |
| `/docs/reference/koin-core/viewmodel` | Keep; add `koin-annotations` for `@InjectedParam`, non-Android `SavedStateHandle` note after verification, disambiguate `viewModelScope`. |
| `/docs/setup/gradle` (KMP parts) | Fix artifact table (D2-D7), link the target matrix, resolve BOM-in-KMP, add `koin-compose-navigation3` to the catalog. |
| `/docs/reference/koin-compose/compose` | Keep; add `koinActivityInject` and the target matrix link. |
| `/docs/reference/koin-compose/compose-viewmodel` | Keep as is. |
| `/docs/reference/koin-compose/navigation3` | Keep; correct targets (add macOS), add a CMP example. |
| `/docs/reference/koin-compose/compose-testing` | Move "Multiplatform UI tests" into a new "Testing in KMP" page (commonTest, `MockProvider.register`, JVM-only list, known off-JVM failures); keep a link. |
| `/docs/reference/koin-test/testing` | Add a Multiplatform section or link; fix C7. |
| `/docs/reference/koin-core/starting-koin` | Remove `KoinMultiplatformApplication` (C3); link the decision table. |
| `/docs/setup/compiler-plugin` (KMP section) | Add the WASM/JS annotation Kotlin 2.4.0 caveat (C9); replace the duplicated dependency block with a link. |
| `/docs/reference/koin-android/jsr330` | Verify `koin-jsr330` before keeping the KMP paragraph (C11). |
| New "KMP troubleshooting" | Add (G8, G9): double start, version/klib errors, plugin missing, SavedStateHandle, iOS init placement. |
