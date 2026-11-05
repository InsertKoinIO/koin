# Koin documentation audit

Phase 1 of the rebuild, read-only. Audited 2026-11-02 at repo commit `d486a5e8`. Four detailed appendices with quotes and `path:line` evidence are in [audit/](./audit/): [kmp](./audit/kmp.md), [core](./audit/core.md), [platforms](./audit/platforms.md), [intro, setup, support](./audit/intro-setup-support.md). IDs such as `kmp C12` point to them (`setup` means the intro, setup, support appendix).

## Summary

- **The reference content is mostly accurate.** About 90 Android, Compose and Ktor APIs and all core APIs checked exist in source. The problem is structure, plus a limited set of real defects, not widespread invention.
- **`docs/` in this repo is not the published source.** This changes the migration plan (section 1).
- **KMP is the weakest area.** It has real bugs in snippets, conflicting setup and start instructions, and no recipe for `commonTest` or iOS Gradle setup (section 6).
- **Duplication is structural.** About 30 pages carry install snippets in 3 conventions. Tutorial pairs are largely identical. Topics such as overrides and `verify()` are explained on 6 or more pages.

## 1. What is actually published

The brief describes `docs/` as the Docusaurus site and source of truth. The facts differ:

| Check | Result |
|---|---|
| `docusaurus.config.*`, `sidebars.*`, `versioned_docs/`, `package.json`, custom components | **Not in this repo**, on any of its branches or in its history, and no public InsertKoinIO repo appears to hold them |
| Contents of `docs/` | 87 `.md` files and 1 PNG. No `.mdx`, no `<Tabs>`, no JSX. Last change 2026-06-12 |
| `AGENTS.md` | Calls `docs/` a "legacy copy, not maintained" |
| `projects/CLAUDE.md` | Says the docs "live in `../docs/` (published to insert-koin.io)". This contradicts `AGENTS.md` |
| Live site | Docusaurus 3.8.0 on Netlify; 83 current and 121 versioned URLs in the sitemap |
| Live pages with no file in `docs/` | 11: `intro/choosing-your-style`, `intro/core-concepts`, `support`, `support/api-stability`, `support/embedded`, `support/releases`, `tooling`, `tooling/ai-agents`, `tooling/ide-plugin`, `tooling/monitor`, `tooling/production-monitoring` |
| Repo text vs live text | Of 70 pages in both, 68 have a word-overlap upper bound below 0.60 (lowest 0.21 on `koin-core/dsl`). Live text has been rewritten since |
| Live "Edit this page" links | **119 of 204 point to files that do not exist** (7 current, 112 under `versioned_docs/`). A further 15 pages have no edit link |

**Consequence:** the published text is the only complete, current copy available to me. It can be recovered from `llms-full.txt` (81 pages; tabs flatten to bold labels followed by code, which is convertible) and the crawled HTML. Because the source is unavailable, this audit could not inspect `sidebars.*`, the Docusaurus config, custom React components or partials directly. Navigation and features below are derived from rendered HTML.

## 2. Structure confirmed from the live site

| Problem in the brief | Finding |
|---|---|
| Two navigations | Confirmed. Navbar: Get Started, Setup, Documentation, Tutorials, Reference, Tooling. Sidebar: Get Started, Setup, Tutorials, Core, Platforms, Testing, Production & Tooling, Migration, Reference, Support. 81 pages are in the sidebar; only 30 are in the navbar |
| Setup separate from usage | Confirmed. See section 4 |
| KMP scattered | Confirmed. Sidebar groups 3 pages under Platforms, with KMP content on at least 10 others (`audit/kmp.md` section 3) |
| Tabs do not persist | Not tested in a browser; taken from the brief. 39 of 83 current pages use the same tab set: Annotations, Compiler Plugin DSL, Constructor / Classic DSL |
| Three styles | **Partly.** Pages say 2 styles and 3 DSL forms, with 3 tabs; one tab merges two APIs with different imports. "Three styles" appears on 0 pages |
| Migration navbar item goes to `intro/koin-vs-hilt` | Confirmed |
| Releases under Reference | Confirmed in the navbar; the sidebar has it under Support |
| 4.1 and 4.0 go to `contact` | Confirmed: `/docs/4.1/contact`, `/docs/4.0/contact` |
| Homepage says no extra plugin needed | Confirmed: "doesn't impact your compilation time, nor require any extra plugin configuration" |
| Additional homepage defects | `class MyActivity : Application()` in the "Inject on Android" sample; no mention of Compiler Plugin, BOM or Annotations; sample uses `singleOf` only |

Docusaurus features seen in output: tabs on 39 pages, admonitions (repo files use `:::info`, `note`, `tip`, `warning`, `caution`), Maven badge images, no content diagrams. Assets used: `/img/koin_new_logo.png`, favicon, `koin-kotzilla-logo.png`, showcase and sponsors folders. Only `koin_main_logo.png` is in the repo, so the rest must be taken from the live site. Primary color `#f8b133`.

## 3. Orphans and broken links

| Item | Status |
|---|---|
| Internal links in live articles (current and versioned), including anchors | **0 broken.** Docusaurus is enforcing this well |
| `/architecture` | Orphan. In the sitemap, not in any nav. A waitlist form, not documentation |
| `/docs/intro/index` | Accidental alias, returns 200 |
| `/docs/reference/koin-android/r8-proguard` | **404 live, but linked from shipped code**: `projects/core/koin-core-viewmodel/consumer-rules.pro`, `projects/compose/koin-compose-viewmodel/consumer-rules.pro`. The page exists only in `docs/` |
| `https://doc.insert-koin.io` | Redirects to `/docs/reference/introduction`, which is 404 |
| `/docs/setup/v3` | 404, linked from the `koin-getting-started` README |
| Repo `docs/` internal links | 15 broken of 368, in 9 files (for example `/docs/tutorials/your-first-app`, `/docs/integrations/android/index`) |
| Repo `README.md` | Links `/docs/setup/koin`, a redirect stub |

## 4. Duplicated content

| Topic | Spread | Canonical in the new IA |
|---|---|---|
| Install snippets | About 30 pages, 3 conventions: BOM, catalog with `version.ref`, inline `$koin_version` | `reference/artifacts`, plus one component |
| `4.2.2` hard-coded | 6 pages | Single versions file |
| Requirements for the plugin | 6 pages | `tooling/compiler-plugin` |
| Style comparison | 4 pages | `get-started/choose-your-style` |
| KSP deprecation notice | 8 pages | `migration/from-ksp` |
| `verify()` vs plugin | 7 pages | `testing/verify` |
| Overriding definitions | 7 pages | `concepts/definitions` |
| Activity scope vs `Context` | 4 pages | `platforms/android/scopes` |
| `expect fun platformModule` | 5 pages | `platforms/kmp/platform-dependencies` |
| `initKoin` with `includes(config)` | 5 pages | `platforms/kmp/start-koin` |
| Tutorial pairs | `ktor`, `kotlin`, `cmp` pairs are near copies; the Museum data layer repeats about 120 lines | One tabbed page each |
| Near-identical tabs | `testing`, `starting-koin`, `koin-component` show identical code in two tabs | Tabs only where code differs |

Identical code blocks over 60 characters: 25 blocks repeat across pages. The largest:

| Pages | Snippet starts with | Where |
|---|---|---|
| 8 | `data class User(val name: String, val email: String) val DEF...` | quickstart/android, quickstart/android-annotations, quickstart/android-compose, quickstart/android-viewmodel ... |
| 5 | `interface UserRepository { fun findUserOrNull(name: String):...` | quickstart/android, quickstart/android-compose, quickstart/android-viewmodel, quickstart/kotlin ... |
| 3 | `interface UserService { fun getUserOrNull(name: String = DEF...` | quickstart/android, quickstart/android-compose, quickstart/android-viewmodel |
| 3 | `import org.koin.dsl.bind import org.koin.dsl.module import o...` | quickstart/android, quickstart/android-viewmodel, quickstart/ktor |
| 3 | `import org.koin.core.annotation.Singleton interface UserRepo...` | quickstart/android-annotations, quickstart/kotlin-annotations, quickstart/ktor-annotations |
| 3 | `import org.koin.core.annotation.ComponentScan import org.koi...` | quickstart/android-annotations, quickstart/kotlin-annotations, quickstart/ktor-annotations |
| 2 | `[Koin][KOIN-D001] Missing dependency: ApiService required by...` | intro/core-concepts, intro/koin-compiler-plugin |
| 2 | `@Singleton class UserRepositoryImpl(private val api: ApiServ...` | intro/koin-vs-hilt, reference/koin-android/hilt-migration |
| 2 | `# gradle/libs.versions.toml [versions] # Kotlin, Android Gra...` | quickstart/android, quickstart/android-viewmodel |
| 2 | `// app/build.gradle.kts plugins { alias(libs.plugins.android...` | quickstart/android, quickstart/android-viewmodel |
| 2 | `import org.koin.android.ext.koin.androidContext import org.k...` | quickstart/android, quickstart/android-viewmodel |
| 2 | `import org.koin.androidx.viewmodel.ext.android.viewModel cla...` | quickstart/android-annotations, quickstart/android-viewmodel |
| 2 | `class UserViewModel( private val userService: UserService ) ...` | quickstart/android-compose, quickstart/android-viewmodel |
| 2 | `MuseumApi -> MuseumStorage -> MuseumRepository -> ListViewMo...` | quickstart/cmp, quickstart/compose-multiplatform-annotations |

Note the `android` and `android-annotations` tutorials are **not** twins despite saying so (`platforms C5`), so merging them needs real rewriting.

## 5. Contradictions and errors

Ordered by reader impact. "Source" means verified in `projects/` or the Compiler Plugin repo.

### Will break a reader's build or app

| ID | Finding | Evidence |
|---|---|---|
| `kmp C12` | Desktop snippet calls `initKoin` inside `application { }`, while the text says "in `main()`, before the first window". Recomposition can re-run it, and a second `startKoin` throws `KoinApplicationAlreadyStartedException` | `GlobalContext.kt:44` |
| `setup 2b` | `koin-android-workmanager` does not exist; real name is `koin-androidx-workmanager`. This is on 2 pages, including an error code explanation | `settings.gradle.kts:54` |
| `setup C6` | Welcome and What is Koin samples use `single<T>()` without the plugin import, and call the plugin "optional". Without the plugin those functions throw `NotImplementedError` | `BaseDSLExt.kt:7` |
| `kmp C1` | `platform(libs.koin.bom)` in `commonMain` on `kmp-setup` vs "use catalog entries that carry the version" on `setup/gradle`; `/docs/setup` recommends the BOM. Official samples use none. The BOM is a plain `java-platform` | `koin-bom/build.gradle.kts:14` |
| `kmp C2` | Two iOS start patterns: `doInitKoinIos()` through a no-arg helper vs `doInitKoin()` on a defaulted parameter. Second form unverified; needs a build | Sample `iOSApp.swift:7` |
| `kmp C9` | Annotation-based WASM/JS needs Kotlin 2.4.0 (KT-82395); docs imply 2.3.20 | Plugin README |
| `platforms S7` | Ktor snippets omit `import org.koin.ktor.plugin.scope`, so `call.scope` fails as pasted | `KoinPlugin.kt:115` |
| `core D4` | `viewModelScopeFactory()` snippet has no opt-in | `KoinOption.kt:39` |
| `platforms C9` | Hilt migration Step 1 omits `koin-annotations` for annotation users | Quickstarts |

### Wrong or misleading statements

| ID | Finding | Truth |
|---|---|---|
| `kmp C3` | `starting-koin` recommends `KoinMultiplatformApplication` | Deprecated, `KoinApplication.kt:213` |
| `kmp C6` | `koin-test` table implies `verify()` on all targets | jvmMain only, `VerifyModule.kt:30` |
| `kmp D2`, `D3` | `koin-core-viewmodel` and `koin-core-coroutines` listed as "All" | No watchOS, Linux or Windows; no wasmJs |
| `core C1` | `scopes` says `@Scope` and `@Scoped` are both required | `@Scope` alone works, `CoreAnnotations.kt:84-90` |
| `core C2` | Inventory says empty `binds` binds all supertypes | Omitted auto-binds; explicit `[]` binds nothing |
| `core D6` | `verify()` rejects `T?` parameters that the plugin accepts; undocumented | `Verification.kt:131-185` |
| `core C5` | "No `AppModule().module` extension" | Plugin generates `M.module()` |
| `platforms C2` | `releases` says the Dagger bridge injects Koin into Dagger | Koin reads Dagger, `DaggerBridge.kt:53` |
| `platforms C1` | `koin-vs-hilt` promises "how to run both frameworks side by side"; `hilt-migration` has no such section | Mechanism is `Scope.dagger<T>()`, undocumented |
| `platforms C3` | Two non-deprecated `koinViewModel` imports, with no guidance; both on the classpath can be ambiguous | `koin-androidx-compose` vs `koin-compose-viewmodel` |
| `platforms C8` | Two different mappings for Hilt `@InstallIn` | Use one |
| `setup C1` | "Keeps DI out of your classes" vs `@Named` and `@InjectedParam` on the class in the same DSL form. Also in `llms.txt` | Definitions stay out; qualifiers do not |
| `setup C2` | The Versions table's Kotlin column mixes "built with", minimum and baseline. No page gives the minimum Kotlin for consumers of 4.2.x | Decide and state |
| `setup C11` | `releases` lists annotations under the wrong module | `koin-annotations`, not `koin-core-annotations` |
| `kmp C11` | `koin-jsr330` "published with Koin Annotations" while all libraries "share one version" | Maven Central: only 2.x, latest `2.3.2-Beta1`; not in repo or BOM |

### Source-side issues to report (not fixable in docs)

- The plugin's W003 message points to deprecated `checkModules()`, while docs point to `verify()` (`core C6`).
- `KoinApplication` KDoc still says `@throws KoinApplicationAlreadyStartedException`, though it re-attaches (`kmp D9`).
- `koin-gradle-plugin` (id `koin`) is an old `checkModules` task plugin, undocumented and easily mistaken for the Compiler Plugin (`setup 2c`).
- `projects/core/koin-core/src/commonTest/.../KoinAppCreationTest.kt` uses backtick names against the repo's own rule (`kmp 5h`).
- `AGENTS.md` and `projects/CLAUDE.md` disagree about `docs/` (section 1).

## 6. Gaps per audience

**Kotlin Multiplatform developer** (full list: `audit/kmp.md` section 4)

- No "create the project" step, iOS Gradle block (`binaries.framework`), or explanation of how `baseName` becomes the Swift `import`.
- No complete version catalog; `libs.koin.*` names are used without being defined.
- No `commonTest` recipe. The page linked for it is JVM only. `MockProvider.register` is undocumented, and `declareMock` off JVM fails without it.
- No KMP troubleshooting, and nothing saying `startKoin` is not idempotent on any target.
- No decision between `initKoin` and `KoinApplication`. The two flagship tutorials and `koin-skills` give different defaults.
- Swift documentation relies on a 2022 helper and an ObjC lookup taken from a GitHub issue. `getOriginalKotlinClass` appears nowhere in `projects/`. No API retrieves ViewModels from Swift, and the docs do not say so.
- Nothing on Web entry (`ComposeViewport`), Compose previews in KMP, or which APIs are Android only.
- No KMP sample in `examples/`. `koin-getting-started/compose` covers Android and iOS only, with no tests.

**Android developer new to DI**

- The first tutorial teaches a presenter, not a ViewModel.
- Style tabs ask for a choice before the reader knows what a definition is.
- Scopes page has no "do I need a scope?" start.
- No published R8 guidance; no Navigation component guide; no first-aid page for common errors.

**Migrating from Hilt or Dagger**

- Hilt guide is strong, but has no side-by-side path.
- `koin-dagger-bridge` is the only module with no usage page. Dagger without Hilt has no content at all.
- Missing mappings: `@IntoSet`, `@IntoMap`, `@BindsInstance`, `@AssistedFactory` code, `HiltWorkerFactory` before and after.

**Ktor developer**

- Reference is sound. Missing: configuration and properties, `EngineMain` deployment shape, shutdown, links to the 3 samples in `examples/`.

**Upgrading (4.0 or 4.1 to 4.2, KSP to Compiler Plugin)**

- No upgrade guide: breaking notes are scattered through a 3,260-word changelog.
- No stated Kotlin floor, JVM target or Android `minSdk` (23) on install pages.
- No path for "stay on 4.1 and KSP" vs "go to 4.2 and plugin", nor a KSP option to `koinCompiler { }` table.
- No guidance for `id("koin")` users. Few version tags on APIs. No FAQ page.

## 7. Terminology

There are 2 styles, 3 DSL forms and 3 tabs. Recommended vocabulary for the glossary (`audit/intro-setup-support.md` section 5):

- **Style:** Koin DSL or Koin Annotations.
- **DSL form:** Compiler Plugin DSL, Constructor DSL, Classic DSL.
- **Koin Compiler Plugin**, then **Compiler Plugin**. Never "Koin Gradle plugin", which collides with `koin-gradle-plugin`.
- **KSP processor** for `koin-ksp-compiler`.
- **Compile-time safety** in prose; `compileSafety` for the option.

## Appendix A: Current pages (83)

"Last repo commit" is for the matching file in `docs/` and is the only history visible; the live source's history is unavailable. "Overlap" is a word-level upper bound between repo and live text (1.00 means same words). Blank means no matching file. Word counts are from rendered article text.

| URL | Title | Words | Sidebar | Navbar | Repo `docs/` file | Last repo commit | Overlap with live |
|---|---|---|---|---|---|---|---|
| `/` | Koin | 0 | (none) | (none) | **none** |  |  |
| `/architecture` | (no h1) | 0 | (none) | (none) | **none** |  |  |
| `/docs/intro` | Welcome | 738 | Get Started > Welcome | Get Started > Welcome; 4.2 > 4.2 | docs/intro/index.md | 2026-05-18 | 0.27 |
| `/docs/intro/choosing-your-style` | Choosing your style | 1397 | Get Started > Choosing your style | Get Started > Choosing your Style | **none** |  |  |
| `/docs/intro/core-concepts` | Core Concepts | 1416 | Get Started > Core Concepts | Get Started > Core Concepts | **none** |  |  |
| `/docs/intro/koin-compiler-plugin` | Koin Compiler Plugin | 846 | Get Started > Koin Compiler Plugin | Get Started > Koin Compiler Plugin | docs/intro/koin-compiler-plugin.md | 2026-05-18 | 0.33 |
| `/docs/intro/koin-vs-hilt` | Koin vs Hilt | 1136 | Migration > Koin vs Hilt | Documentation > Migration | docs/intro/koin-vs-hilt.md | 2026-05-18 | 0.43 |
| `/docs/intro/what-is-dependency-injection` | What is Dependency Injection? | 1552 | Get Started > What is Dependency Injection? | Get Started > What is Dependency Injection? | docs/intro/what-is-dependency-injection.md | 2026-05-18 | 0.55 |
| `/docs/intro/what-is-koin` | What is Koin? | 1033 | Get Started > What is Koin? | Get Started > What is Koin? | docs/intro/what-is-koin.md | 2026-05-18 | 0.38 |
| `/docs/migration/from-ksp-to-compiler-plugin` | Migrating from KSP to the Compiler Plugin | 1284 | Migration > Migrating from KSP to the Compiler Plugin | (none) | docs/migration/from-ksp-to-compiler-plugin.md | 2026-05-19 | 0.60 |
| `/docs/quickstart/android` | Android | 1267 | Tutorials > Android | Tutorials > Android | docs/quickstart/android.md | 2026-01-30 | 0.55 |
| `/docs/quickstart/android-annotations` | Android & Annotations | 1471 | Tutorials > Android & Annotations | Tutorials > Android with Annotations | docs/quickstart/android-annotations.md | 2026-05-18 | 0.52 |
| `/docs/quickstart/android-compose` | Android Jetpack Compose | 1415 | Tutorials > Android Jetpack Compose | Tutorials > Android Jetpack Compose | docs/quickstart/android-compose.md | 2026-01-30 | 0.59 |
| `/docs/quickstart/android-viewmodel` | Android ViewModel | 1264 | Tutorials > Android ViewModel | Tutorials > Android ViewModel | docs/quickstart/android-viewmodel.md | 2026-01-30 | 0.55 |
| `/docs/quickstart/cmp` | Compose Multiplatform | 2035 | Tutorials > Compose Multiplatform | Tutorials > Compose Multiplatform | docs/quickstart/cmp.md | 2026-01-30 | 0.60 |
| `/docs/quickstart/compose-multiplatform-annotations` | Compose Multiplatform & Annotations | 2091 | Tutorials > Compose Multiplatform & Annotations | (none) | docs/quickstart/compose-multiplatform-annotations.md | 2026-01-30 | 0.54 |
| `/docs/quickstart/junit-test` | JUnit Tests | 734 | Tutorials > JUnit Tests | Tutorials > JUnit Tests | docs/quickstart/junit-test.md | 2026-05-18 | 0.44 |
| `/docs/quickstart/kotlin` | Kotlin | 1024 | Tutorials > Kotlin | Tutorials > Kotlin App | docs/quickstart/kotlin.md | 2026-01-30 | 0.56 |
| `/docs/quickstart/kotlin-annotations` | Kotlin & Annotations | 1018 | Tutorials > Kotlin & Annotations | (none) | docs/quickstart/kotlin-annotations.md | 2026-01-30 | 0.52 |
| `/docs/quickstart/ktor` | Ktor | 1051 | Tutorials > Ktor | Tutorials > Ktor Backend | docs/quickstart/ktor.md | 2026-01-30 | 0.54 |
| `/docs/quickstart/ktor-annotations` | Ktor & Annotations | 1127 | Tutorials > Ktor & Annotations | (none) | docs/quickstart/ktor-annotations.md | 2026-01-30 | 0.50 |
| `/docs/reference/koin-android/advanced-patterns` | Advanced Android Patterns | 696 | Platforms > Android > Advanced Android Patterns | (none) | docs/reference/koin-android/advanced-patterns.md | 2026-01-30 | 0.35 |
| `/docs/reference/koin-android/best-practices` | Android Best Practices | 992 | Platforms > Android > Android Best Practices | (none) | docs/reference/koin-android/best-practices.md | 2026-05-18 | 0.25 |
| `/docs/reference/koin-android/binding-patterns` | Android Library Integration | 1345 | Platforms > Android > Android Library Integration | (none) | docs/reference/koin-android/binding-patterns.md | 2026-01-30 | 0.46 |
| `/docs/reference/koin-android/entry-points` | Android Entry Points | 1584 | Platforms > Android > Android Entry Points | (none) | docs/reference/koin-android/entry-points.md | 2026-01-30 | 0.43 |
| `/docs/reference/koin-android/fragment-factory` | Fragment Factory | 675 | Platforms > Android > Fragment Factory | (none) | docs/reference/koin-android/fragment-factory.md | 2026-01-30 | 0.42 |
| `/docs/reference/koin-android/hilt-migration` | Migrating from Hilt | 2415 | Migration > Migrating from Hilt | (none) | docs/reference/koin-android/hilt-migration.md | 2026-01-30 | 0.50 |
| `/docs/reference/koin-android/instrumented-testing` | Android Instrumented Testing | 1936 | Testing > Android Instrumented Testing | (none) | docs/reference/koin-android/instrumented-testing.md | 2026-05-18 | 0.35 |
| `/docs/reference/koin-android/jsr330` | JSR-330 Annotations | 907 | Migration > JSR-330 Annotations | (none) | docs/reference/koin-android/jsr330.md | 2026-01-30 | 0.48 |
| `/docs/reference/koin-android/multi-module` | Multi-Module Apps | 975 | Platforms > Android > Multi-Module Apps | (none) | docs/reference/koin-android/multi-module.md | 2026-05-18 | 0.37 |
| `/docs/reference/koin-android/scope` | Android Scopes | 1411 | Platforms > Android > Android Scopes | (none) | docs/reference/koin-android/scope.md | 2026-01-30 | 0.39 |
| `/docs/reference/koin-android/start` | Starting Koin on Android | 1811 | Platforms > Android > Starting Koin on Android | Documentation > Android | docs/reference/koin-android/start.md | 2026-01-30 | 0.38 |
| `/docs/reference/koin-android/viewmodel` | Android ViewModel | 1117 | Platforms > Android > Android ViewModel | (none) | docs/reference/koin-android/viewmodel.md | 2026-01-30 | 0.42 |
| `/docs/reference/koin-android/workmanager` | WorkManager | 741 | Platforms > Android > WorkManager | (none) | docs/reference/koin-android/workmanager.md | 2026-01-30 | 0.44 |
| `/docs/reference/koin-annotations/annotations-inventory` | Annotations Reference | 1961 | Reference > Annotations Reference | Reference > Annotations Reference | docs/reference/koin-annotations/annotations-inventory.md | 2026-05-18 | 0.39 |
| `/docs/reference/koin-annotations/options` | Compiler Plugin Options | 1151 | Reference > Compiler Plugin Options | Reference > Compiler Plugin Options | docs/reference/koin-annotations/options.md | 2026-05-19 | 0.37 |
| `/docs/reference/koin-compiler/compile-safety` | Compile-Time Safety | 2705 | Core > Compile-Time Safety | (none) | docs/reference/koin-compiler/compile-safety.md | 2026-05-19 | 0.47 |
| `/docs/reference/koin-compose/compose` | Koin for Compose | 1081 | Platforms > Compose > Koin for Compose | Documentation > Compose | docs/reference/koin-compose/compose.md | 2026-01-30 | 0.42 |
| `/docs/reference/koin-compose/compose-lifecycle` | Lifecycle and State | 776 | Platforms > Compose > Lifecycle and State | (none) | docs/reference/koin-compose/compose-lifecycle.md | 2026-01-30 | 0.34 |
| `/docs/reference/koin-compose/compose-modules` | Dynamic Modules | 527 | Platforms > Compose > Dynamic Modules | (none) | docs/reference/koin-compose/compose-modules.md | 2026-01-30 | 0.36 |
| `/docs/reference/koin-compose/compose-scopes` | Scopes in Compose | 1102 | Platforms > Compose > Scopes in Compose | (none) | docs/reference/koin-compose/compose-scopes.md | 2026-01-30 | 0.44 |
| `/docs/reference/koin-compose/compose-testing` | Testing Composables | 1001 | Testing > Testing Composables | (none) | docs/reference/koin-compose/compose-testing.md | 2026-01-30 | 0.33 |
| `/docs/reference/koin-compose/compose-viewmodel` | ViewModel in Compose | 852 | Platforms > Compose > ViewModel in Compose | (none) | docs/reference/koin-compose/compose-viewmodel.md | 2026-06-12 | 0.37 |
| `/docs/reference/koin-compose/isolated-context` | Isolated Context in Compose | 683 | Platforms > Compose > Isolated Context in Compose | (none) | docs/reference/koin-compose/isolated-context.md | 2026-01-30 | 0.39 |
| `/docs/reference/koin-compose/navigation3` | Navigation 3 | 1161 | Platforms > Compose > Navigation 3 | (none) | docs/reference/koin-compose/navigation3.md | 2026-06-12 | 0.45 |
| `/docs/reference/koin-core/advanced-patterns` | Advanced Patterns | 1376 | Core > Advanced Patterns | (none) | docs/reference/koin-core/advanced-patterns.md | 2026-01-30 | 0.41 |
| `/docs/reference/koin-core/context-isolation` | Context Isolation | 809 | Core > Context Isolation | (none) | docs/reference/koin-core/context-isolation.md | 2026-01-30 | 0.36 |
| `/docs/reference/koin-core/definitions` | Definitions | 1636 | Core > Definitions | (none) | docs/reference/koin-core/definitions.md | 2026-05-18 | 0.47 |
| `/docs/reference/koin-core/dsl` | DSL Reference | 1376 | Reference > DSL Reference | Reference > DSL Reference | docs/reference/koin-core/dsl.md | 2026-05-18 | 0.21 |
| `/docs/reference/koin-core/extension-manager` | Extension Manager | 707 | Core > Extension Manager | (none) | docs/reference/koin-core/extension-manager.md | 2026-01-30 | 0.45 |
| `/docs/reference/koin-core/injection` | Injection | 871 | Core > Injection | (none) | docs/reference/koin-core/injection.md | 2026-01-30 | 0.27 |
| `/docs/reference/koin-core/injection-parameters` | Injected Parameters | 1005 | Core > Injected Parameters | (none) | docs/reference/koin-core/injection-parameters.md | 2026-04-08 | 0.36 |
| `/docs/reference/koin-core/kmp-setup` | Kotlin Multiplatform Setup | 1189 | Platforms > Kotlin Multiplatform > Kotlin Multiplatform Setup | Documentation > Kotlin Multiplatform | docs/reference/koin-core/kmp-setup.md | 2026-01-30 | 0.40 |
| `/docs/reference/koin-core/kmp-shared-modules` | KMP Sharing Patterns | 1253 | Platforms > Kotlin Multiplatform > KMP Sharing Patterns | (none) | docs/reference/koin-core/kmp-shared-modules.md | 2026-01-30 | 0.33 |
| `/docs/reference/koin-core/koin-component` | KoinComponent | 1151 | Core > KoinComponent | (none) | docs/reference/koin-core/koin-component.md | 2026-01-30 | 0.35 |
| `/docs/reference/koin-core/lazy-modules` | Lazy Modules | 856 | Core > Lazy Modules | (none) | docs/reference/koin-core/lazy-modules.md | 2026-01-30 | 0.37 |
| `/docs/reference/koin-core/modules` | Modules | 1424 | Core > Modules | (none) | docs/reference/koin-core/modules.md | 2026-01-30 | 0.46 |
| `/docs/reference/koin-core/qualifiers` | Qualifiers | 1375 | Core > Qualifiers | (none) | docs/reference/koin-core/qualifiers.md | 2026-01-30 | 0.29 |
| `/docs/reference/koin-core/scopes` | Scopes | 2087 | Core > Scopes | (none) | docs/reference/koin-core/scopes.md | 2026-01-30 | 0.41 |
| `/docs/reference/koin-core/starting-koin` | Starting Koin | 1977 | Core > Starting Koin | Documentation > Core | docs/reference/koin-core/starting-koin.md | 2026-01-30 | 0.38 |
| `/docs/reference/koin-core/troubleshooting` | Troubleshooting | 1651 | Core > Troubleshooting | (none) | docs/reference/koin-core/troubleshooting.md | 2026-05-19 | 0.25 |
| `/docs/reference/koin-core/viewmodel` | ViewModel | 954 | Core > ViewModel | (none) | docs/reference/koin-core/viewmodel.md | 2026-06-12 | 0.51 |
| `/docs/reference/koin-ktor/ktor` | Koin for Ktor | 1009 | Platforms > Ktor > Koin for Ktor | Documentation > Ktor | docs/reference/koin-ktor/ktor.md | 2026-01-30 | 0.37 |
| `/docs/reference/koin-ktor/ktor-bridge` | Ktor DI Bridge | 615 | Platforms > Ktor > Ktor DI Bridge | (none) | docs/reference/koin-ktor/ktor-bridge.md | 2026-01-30 | 0.52 |
| `/docs/reference/koin-ktor/ktor-isolated` | Isolated Context in Ktor | 664 | Platforms > Ktor > Isolated Context in Ktor | (none) | docs/reference/koin-ktor/ktor-isolated.md | 2026-01-30 | 0.38 |
| `/docs/reference/koin-ktor/ktor-scopes` | Request Scopes | 670 | Platforms > Ktor > Request Scopes | (none) | docs/reference/koin-ktor/ktor-scopes.md | 2026-05-19 | 0.37 |
| `/docs/reference/koin-ktor/ktor-testing` | Ktor Testing | 1105 | Testing > Ktor Testing | (none) | docs/reference/koin-ktor/ktor-testing.md | 2026-01-30 | 0.41 |
| `/docs/reference/koin-mp/kmp` | KMP Platform Integration | 1039 | Platforms > Kotlin Multiplatform > KMP Platform Integration | (none) | docs/reference/koin-mp/kmp.md | 2026-05-18 | 0.42 |
| `/docs/reference/koin-test/testing` | Unit Testing | 1575 | Testing > Unit Testing | Documentation > Testing | docs/reference/koin-test/testing.md | 2026-05-18 | 0.45 |
| `/docs/reference/koin-test/verify` | Verifying your Configuration | 1002 | Testing > Verifying your Configuration | (none) | docs/reference/koin-test/verify.md | 2026-05-18 | 0.38 |
| `/docs/setup` | Setup | 332 | Setup | Setup | docs/setup/index.md | 2026-05-18 | 0.44 |
| `/docs/setup/annotations-ksp` | KSP Processor Setup (Deprecated) | 561 | Setup > KSP Processor Setup (Deprecated) | (none) | docs/setup/annotations-ksp.md | 2026-05-18 | 0.44 |
| `/docs/setup/compiler-plugin` | Compiler Plugin Setup | 1265 | Setup > Compiler Plugin Setup | (none) | docs/setup/compiler-plugin.md | 2026-05-19 | 0.47 |
| `/docs/setup/gradle` | Gradle Setup | 1125 | Setup > Gradle Setup | (none) | docs/setup/gradle.md | 2026-01-30 | 0.46 |
| `/docs/support` | Versions, Roadmap & Support | 1283 | Support > Versions, Roadmap & Support | (none) | **none** |  |  |
| `/docs/support/api-stability` | API Stability & Release Types | 540 | Support > API Stability & Release Types | (none) | **none** |  |  |
| `/docs/support/embedded` | Koin for SDK & Libraries | 2360 | Support > Koin for SDK & Libraries | (none) | **none** |  |  |
| `/docs/support/releases` | Releases & Upgrade Guides | 3295 | Support > Releases & Upgrade Guides | Reference > Releases & Upgrades | **none** |  |  |
| `/docs/tooling` | Production & Tooling | 557 | Production & Tooling | Tooling > Production & Tooling | **none** |  |  |
| `/docs/tooling/ai-agents` | AI Agents and Koin Skills | 586 | Production & Tooling > AI Agents and Koin Skills | (none) | **none** |  |  |
| `/docs/tooling/ide-plugin` | IDE Plugin | 584 | Production & Tooling > IDE Plugin | Tooling > IDE Plugin | **none** |  |  |
| `/docs/tooling/monitor` | @Monitor | 859 | Production & Tooling > @Monitor | Tooling > Built-in Monitoring | **none** |  |  |
| `/docs/tooling/production-monitoring` | Production Monitoring | 809 | Production & Tooling > Production Monitoring | Tooling > Production Monitoring | **none** |  |  |

## Appendix B: Repo files with no page of their own on the live site

| Repo file | Words | Last commit | Live behaviour |
|---|---|---|---|
| `docs/intro/what-is-koin-annotations.md` | 575 | 2026-05-19 | redirect /docs/intro/choosing-your-style |
| `docs/reference/koin-android/get-instances.md` | 598 | 2026-01-30 | redirect /docs/reference/koin-android/entry-points |
| `docs/reference/koin-android/modules-android.md` | 347 | 2026-01-30 | redirect /docs/reference/koin-android/start |
| `docs/reference/koin-android/qualifiers.md` | 314 | 2026-01-30 | redirect /docs/reference/koin-android/start |
| `docs/reference/koin-android/r8-proguard.md` | 483 | 2026-06-11 | 404 |
| `docs/reference/koin-annotations/definitions.md` | 1464 | 2026-05-18 | redirect /docs/reference/koin-core/definitions |
| `docs/reference/koin-annotations/kmp.md` | 1009 | 2026-01-30 | redirect /docs/reference/koin-core/kmp-setup |
| `docs/reference/koin-annotations/modules.md` | 994 | 2026-05-18 | redirect /docs/reference/koin-core/modules |
| `docs/reference/koin-annotations/monitor.md` | 422 | 2026-01-30 | redirect /docs/tooling/monitor |
| `docs/reference/koin-annotations/scope.md` | 860 | 2026-05-18 | redirect /docs/reference/koin-core/scopes |
| `docs/reference/koin-annotations/start.md` | 512 | 2026-05-18 | redirect /docs/reference/koin-core/starting-koin |
| `docs/reference/koin-core/dsl-update.md` | 442 | 2026-01-30 | redirect /docs/reference/koin-core/dsl |
| `docs/reference/koin-core/start-koin.md` | 378 | 2026-01-30 | redirect /docs/reference/koin-core/starting-koin |
| `docs/setup/annotations.md` | 412 | 2026-01-30 | redirect /docs/setup/compiler-plugin |
| `docs/setup/koin.md` | 740 | 2026-01-30 | redirect /docs/setup/gradle |
| `docs/setup/migrate.md` | 15 | 2025-03-25 | redirect /docs/support/releases |
| `docs/setup/why.md` | 1374 | 2026-01-30 | redirect /docs/intro/what-is-koin |

## Appendix C: Versioned pages (121)

Sidebar and navbar are the same as the version's own tree and are not listed. No git history is available for these.

| URL | Title | Words |
|---|---|---|
| `/docs/4.0/contact` | Contact | 38 |
| `/docs/4.0/quickstart/android` | Android | 807 |
| `/docs/4.0/quickstart/android-annotations` | Android & Annotations | 1000 |
| `/docs/4.0/quickstart/android-compose` | Android - Jetpack Compose | 1080 |
| `/docs/4.0/quickstart/android-viewmodel` | Android - ViewModel | 808 |
| `/docs/4.0/quickstart/cmp` | Compose Multiplatform - Shared UI | 652 |
| `/docs/4.0/quickstart/junit-test` | JUnit Tests | 349 |
| `/docs/4.0/quickstart/kmp` | Kotlin Multiplatform - No shared UI | 879 |
| `/docs/4.0/quickstart/kotlin` | Kotlin | 650 |
| `/docs/4.0/quickstart/ktor` | Ktor | 687 |
| `/docs/4.0/quickstart/ktor-annotations` | Ktor & Annotations | 683 |
| `/docs/4.0/reference/introduction` | What is Koin? | 31 |
| `/docs/4.0/reference/koin-android/dsl-update` | Constructor DSL for Android | 311 |
| `/docs/4.0/reference/koin-android/fragment-factory` | Fragment Factory | 375 |
| `/docs/4.0/reference/koin-android/get-instances` | Injecting in Android | 471 |
| `/docs/4.0/reference/koin-android/instrumented-testing` | Android Instrumented Testing | 387 |
| `/docs/4.0/reference/koin-android/modules-android` | Multiple Koin Modules in Android | 754 |
| `/docs/4.0/reference/koin-android/scope` | Managing Android Scopes | 1640 |
| `/docs/4.0/reference/koin-android/start` | Start Koin on Android | 462 |
| `/docs/4.0/reference/koin-android/viewmodel` | Android ViewModel & Navigation | 1016 |
| `/docs/4.0/reference/koin-android/workmanager` | WorkManager | 434 |
| `/docs/4.0/reference/koin-annotations/definitions` | Definitions with Annotations | 924 |
| `/docs/4.0/reference/koin-annotations/kmp` | Annotations for Definitions and Modules in Kotlin Multiplatform App | 635 |
| `/docs/4.0/reference/koin-annotations/modules` | Modules with @Module | 403 |
| `/docs/4.0/reference/koin-annotations/scope` | Scopes in Koin Annotations | 467 |
| `/docs/4.0/reference/koin-annotations/start` | Starting with Koin Annotations | 479 |
| `/docs/4.0/reference/koin-compose/compose` | Koin for Jetpack Compose and Compose Multiplatform | 1206 |
| `/docs/4.0/reference/koin-compose/isolated-context` | Isolated Context with Compose Applications | 210 |
| `/docs/4.0/reference/koin-core/context-isolation` | Context Isolation | 319 |
| `/docs/4.0/reference/koin-core/definitions` | Definitions | 1395 |
| `/docs/4.0/reference/koin-core/dsl` | Koin DSL | 690 |
| `/docs/4.0/reference/koin-core/dsl-update` | Constructor DSL | 458 |
| `/docs/4.0/reference/koin-core/extension-manager` | Extension Manager | 168 |
| `/docs/4.0/reference/koin-core/injection-parameters` | Passing Parameters - Injected Parameters | 657 |
| `/docs/4.0/reference/koin-core/koin-component` | Koin Component | 469 |
| `/docs/4.0/reference/koin-core/lazy-modules` | Lazy Modules and Background Loading | 340 |
| `/docs/4.0/reference/koin-core/modules` | Modules | 1165 |
| `/docs/4.0/reference/koin-core/scopes` | Scopes | 1145 |
| `/docs/4.0/reference/koin-core/start-koin` | Start Koin | 716 |
| `/docs/4.0/reference/koin-ktor/ktor` | Dependency Injection in Ktor | 353 |
| `/docs/4.0/reference/koin-ktor/ktor-isolated` | Ktor & Koin Isolated Context | 93 |
| `/docs/4.0/reference/koin-mp/kmp` | Kotlin Multiplatform Dependency Injection | 468 |
| `/docs/4.0/reference/koin-test/checkmodules` | CheckModules - Check Koin configuration (Deprecated) | 935 |
| `/docs/4.0/reference/koin-test/testing` | Injecting in Tests | 1059 |
| `/docs/4.0/reference/koin-test/verify` | Verifying your Koin configuration | 411 |
| `/docs/4.0/resources` | Koin Developer Hub | 91 |
| `/docs/4.0/resources/articles` | Articles | 139 |
| `/docs/4.0/resources/videos` | Talk and Conferences | 96 |
| `/docs/4.0/setup/annotations` | Koin Annotations | 362 |
| `/docs/4.0/setup/koin` | Koin | 764 |
| `/docs/4.0/setup/migrate` | Migration Guides (Deprecated) | 17 |
| `/docs/4.0/setup/why` | Why Koin? | 1038 |
| `/docs/4.0/support` | Versions, Roadmap & Support | 311 |
| `/docs/4.0/support/api-stability` | API Stability & Release Types | 552 |
| `/docs/4.0/support/embedded` | Koin Embedded | 176 |
| `/docs/4.0/support/releases` | Releases & API Upgrade Guides | 999 |
| `/docs/4.0/upgrade/migrate` | Migration Guide | 346 |
| `/docs/4.0/upgrade/whats-new` | What's new? | 22 |
| `/docs/4.1/contact` | Contact | 38 |
| `/docs/4.1/quickstart/android` | Android | 807 |
| `/docs/4.1/quickstart/android-annotations` | Android & Annotations | 1000 |
| `/docs/4.1/quickstart/android-compose` | Android - Jetpack Compose | 1080 |
| `/docs/4.1/quickstart/android-viewmodel` | Android - ViewModel | 808 |
| `/docs/4.1/quickstart/cmp` | Compose Multiplatform - Shared UI | 652 |
| `/docs/4.1/quickstart/junit-test` | JUnit Tests | 349 |
| `/docs/4.1/quickstart/kmp` | Kotlin Multiplatform - No shared UI | 879 |
| `/docs/4.1/quickstart/kotlin` | Kotlin | 650 |
| `/docs/4.1/quickstart/ktor` | Ktor | 687 |
| `/docs/4.1/quickstart/ktor-annotations` | Ktor & Annotations | 683 |
| `/docs/4.1/reference/introduction` | What is Koin? | 31 |
| `/docs/4.1/reference/koin-android/dsl-update` | Constructor DSL for Android | 311 |
| `/docs/4.1/reference/koin-android/fragment-factory` | Fragment Factory | 375 |
| `/docs/4.1/reference/koin-android/get-instances` | Injecting in Android | 471 |
| `/docs/4.1/reference/koin-android/instrumented-testing` | Android Instrumented Testing | 387 |
| `/docs/4.1/reference/koin-android/modules-android` | Multiple Koin Modules in Android | 754 |
| `/docs/4.1/reference/koin-android/scope` | Android Scopes | 1691 |
| `/docs/4.1/reference/koin-android/start` | Start Koin on Android | 462 |
| `/docs/4.1/reference/koin-android/viewmodel` | Android ViewModel & Navigation | 1015 |
| `/docs/4.1/reference/koin-android/workmanager` | WorkManager | 434 |
| `/docs/4.1/reference/koin-annotations/annotations-inventory` | Koin Annotations Inventory | 2521 |
| `/docs/4.1/reference/koin-annotations/definitions` | Definitions with Annotations | 1339 |
| `/docs/4.1/reference/koin-annotations/kmp` | Kotlin Multiplatform - Definitions and Modules Annotations | 1257 |
| `/docs/4.1/reference/koin-annotations/modules` | Application, Configuration and Modules | 1024 |
| `/docs/4.1/reference/koin-annotations/monitor` | Koin Built-in Performance Monitoring with @Monitor | 528 |
| `/docs/4.1/reference/koin-annotations/options` | KSP Compiler Options | 596 |
| `/docs/4.1/reference/koin-annotations/scope` | Scopes in Koin Annotations | 913 |
| `/docs/4.1/reference/koin-annotations/start` | Starting with Koin Annotations | 705 |
| `/docs/4.1/reference/koin-compose/compose` | Koin for Jetpack Compose and Compose Multiplatform | 1233 |
| `/docs/4.1/reference/koin-compose/isolated-context` | Isolated Context with Compose Applications | 210 |
| `/docs/4.1/reference/koin-compose/navigation3` | Navigation 3 Integration | 1236 |
| `/docs/4.1/reference/koin-core/context-isolation` | Context Isolation | 321 |
| `/docs/4.1/reference/koin-core/definitions` | Definitions | 1395 |
| `/docs/4.1/reference/koin-core/dsl` | Koin DSL | 690 |
| `/docs/4.1/reference/koin-core/dsl-update` | Constructor DSL | 457 |
| `/docs/4.1/reference/koin-core/extension-manager` | Extension Manager | 396 |
| `/docs/4.1/reference/koin-core/injection-parameters` | Passing Parameters - Injected Parameters | 657 |
| `/docs/4.1/reference/koin-core/koin-component` | Koin Component | 469 |
| `/docs/4.1/reference/koin-core/lazy-modules` | Lazy Modules and Background Loading | 340 |
| `/docs/4.1/reference/koin-core/modules` | Modules | 1166 |
| `/docs/4.1/reference/koin-core/scopes` | Scopes | 1288 |
| `/docs/4.1/reference/koin-core/start-koin` | Start Koin | 902 |
| `/docs/4.1/reference/koin-ktor/ktor` | Dependency Injection in Ktor | 518 |
| `/docs/4.1/reference/koin-ktor/ktor-isolated` | Ktor & Koin Isolated Context | 93 |
| `/docs/4.1/reference/koin-mp/kmp` | Kotlin Multiplatform Dependency Injection | 468 |
| `/docs/4.1/reference/koin-test/checkmodules` | CheckModules - Check Koin configuration (Deprecated) | 935 |
| `/docs/4.1/reference/koin-test/testing` | Injecting in Tests | 1059 |
| `/docs/4.1/reference/koin-test/verify` | Verifying your Koin configuration | 524 |
| `/docs/4.1/resources` | Koin Developer Hub | 91 |
| `/docs/4.1/resources/articles` | Articles | 139 |
| `/docs/4.1/resources/videos` | Talk and Conferences | 96 |
| `/docs/4.1/setup/annotations` | Koin Annotations | 392 |
| `/docs/4.1/setup/koin` | Koin | 764 |
| `/docs/4.1/setup/migrate` | Migration Guides (Deprecated) | 17 |
| `/docs/4.1/setup/why` | Why Koin? | 1038 |
| `/docs/4.1/support` | Versions, Roadmap & Support | 311 |
| `/docs/4.1/support/api-stability` | API Stability & Release Types | 552 |
| `/docs/4.1/support/embedded` | Koin Embedded | 176 |
| `/docs/4.1/support/releases` | Releases & API Upgrade Guides | 2096 |
| `/docs/4.1/support/tooling` | Debugging and Performance Monitoring | 106 |
| `/docs/4.1/upgrade/migrate` | Migration Guide | 346 |
| `/docs/4.1/upgrade/whats-new` | What's new? | 22 |
