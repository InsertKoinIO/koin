# Koin documentation: information architecture proposal

Status: **implemented.** Kept as the record of the decisions behind the site. `src/sidebar.ts` is the source of truth for the current pages. Evidence is in [AUDIT.md](./AUDIT.md).

90 pages replace 81 doc pages. 20 are new or split out, 10 merge several sources, 21 are rewrites, 39 move with light edits.

## Decisions I need from you

These change what gets built, so please answer them with your approval. Each has my recommendation.

1. **Content source.** Your brief treats `docs/` as the Docusaurus source of truth, but it is not what is published (see AUDIT section 1). **Recommendation: migrate from the live text** (`llms-full.txt` plus the crawled HTML) and take only `r8-proguard` from `docs/`. If you can give me the private site repo, that is better still: it would also give me the original tabs and components.
2. **Older versions.** **Recommendation: keep only 4.2 and move the current deployment to `legacy.insert-koin.io`** (details below). This needs a DNS change on your side.
3. **Swift page scope.** Your outline asks for "retrieving shared ViewModels" from Swift. No such API exists, and koin-skills says better Swift integration is "in progress, not available yet". **Recommendation: say so plainly** and document only what works: starting Koin, `KoinComponent` helpers and hosting Compose. Is there anything from the Koin team I should know?
4. **`koin-jsr330`.** It is not in this repo, and Maven Central has it only in the 2.x stream (latest `2.3.2-Beta1`), while the docs say all Koin libraries share one version. **Recommendation: keep the page with a clear warning** until a maintainer confirms its status for 4.2.
5. **`verify()` stance.** `AGENTS.md` says no `verify()` tests; the docs keep four legitimate cases. **Recommendation: "with the Compiler Plugin, not by default; here are the four exceptions"**, stated once on `testing/verify`.

## Where this departs from your starting hypothesis

| Hypothesis | Proposal | Why |
|---|---|---|
| Separate "Jetpack Compose (Android)" and "Compose Multiplatform" sections | One **Compose** section; Compose Multiplatform specifics in a KMP page | Same artifacts and packages. Only `koinActivityViewModel` and `koinActivityInject` are Android only. Two sections would duplicate nearly every page, which is the problem you are fixing. |
| "Three declaration styles" | **Two styles** (Koin DSL, Koin Annotations), **three DSL forms** | Matches `llms.txt`, `AGENTS.md` and the site; "three styles" appears on 0 pages. The `koin-style` tabs still have three labels. |
| No home for tutorials | Each tutorial is inside its platform | Removes a top-level section while keeping "learn by building" one click away. Tutorial pairs merge into style tabs. |
| "Each guide starts with its own Install step" and "no duplicated Gradle snippets" | Both, through one small `<Install>` component reading a single versions file | These two rules conflict if snippets are written by hand. About 30 pages carry install snippets today, in 3 conventions. |
| "Compiler Plugin" under Tooling, "options" under Reference | Kept, and made explicit: install and explanation in `tooling/compiler-plugin`, options and diagnostic codes in Reference | Today this is spread over five URLs, with the options page under `koin-annotations/`. |
| Concepts list | Adds Starting Koin, ViewModel definitions, Patterns | These are platform-agnostic and had no other home. |
| API docs link in Reference | Omitted for now | Dokka is configured, but no published site was found: `doc.insert-koin.io` redirects to a 404. |

## Navigation model

There is one navigation: the sidebar below. The top bar holds only logo, search, GitHub, theme and an "Older versions" link, which removes the competing navbar. All URLs stay under `/docs/`, which leaves `/` for the homepage.

## Sidebar

Status shows how each page is produced. Paths are relative to `/docs/`.


### Get Started

| Page | Path | Purpose | Status |
|---|---|---|---|
| Welcome | `get-started` | Map of the docs and routes for each kind of reader. | merge |
| What is dependency injection? | `get-started/what-is-di` | Explains DI and service location to someone new to the idea. | keep |
| Why Koin | `get-started/why-koin` | What Koin is, what it costs, and when it fits. | rewrite |
| Core concepts in 5 minutes | `get-started/core-concepts` | Container, definition, module, scope, on one running example. | rewrite |
| Choose your style | `get-started/choose-your-style` | Decide between Koin DSL (three forms) and Koin Annotations. | rewrite |
| Pick your platform | `get-started/pick-your-platform` | Routes to each platform guide, with a target support matrix. | merge |

### Platforms


**Kotlin / JVM**

| Page | Path | Purpose | Status |
|---|---|---|---|
| Kotlin: install and start | `platforms/kotlin` | Add Koin to a JVM app and start it from main(). | new |
| Tutorial: your first Kotlin app | `platforms/kotlin/tutorial` | Build a small app with style tabs instead of two pages. | merge |

**Android**

| Page | Path | Purpose | Status |
|---|---|---|---|
| Android: install and start | `platforms/android` | Install, start in Application, androidContext, App Startup. | rewrite |
| Tutorial: your first Android app | `platforms/android/tutorial` | Compose and ViewModel tutorial with style tabs. | merge |
| Inject into Android components | `platforms/android/components` | Activity, Fragment, Service, Receiver, Provider entry points. | keep |
| ViewModel on Android | `platforms/android/viewmodel` | Retrieve ViewModels from Views and Fragments; Java compat. | keep |
| Android scopes | `platforms/android/scopes` | Activity, retained, Fragment and Service scopes. | keep |
| Navigation component | `platforms/android/navigation` | koinNavGraphViewModel and koin-androidx-navigation. | new |
| WorkManager | `platforms/android/workmanager` | Inject into Workers. | keep |
| Fragment factory | `platforms/android/fragments` | Constructor injection for Fragments. | keep |
| Library integration and recipes | `platforms/android/libraries` | Retrofit, Room and other Android recipes. | merge |
| Multi-module apps | `platforms/android/multi-module` | Structure Koin across Gradle modules. | keep |
| Lifecycle and leak pitfalls | `platforms/android/best-practices` | What to avoid, and the Android error table. | rewrite |
| R8 and ProGuard | `platforms/android/r8-proguard` | Publishes the page shipped consumer rules link to (404 today). | rewrite |

**Compose**

| Page | Path | Purpose | Status |
|---|---|---|---|
| Koin in Compose | `platforms/compose` | Shared API for Jetpack Compose and Compose Multiplatform: koinInject, KoinApplication. | rewrite |
| ViewModel in Compose | `platforms/compose/viewmodel` | koinViewModel, shared ViewModels; resolves the two-imports issue. | keep |
| Scopes in Compose | `platforms/compose/scopes` | KoinScope, rememberKoinScope, navigation scopes. | keep |
| Lifecycle and dynamic modules | `platforms/compose/lifecycle` | How composition lifetime relates to Koin; rememberKoinModules. | merge |
| Isolated context in Compose | `platforms/compose/isolated-context` | KoinIsolatedContext for SDKs and embedded UIs. | keep |
| Navigation 3 | `platforms/compose/navigation3` | navigation<T>, koinEntryProvider, on every Compose target. | rewrite |

**Kotlin Multiplatform**

| Page | Path | Purpose | Status |
|---|---|---|---|
| Overview | `platforms/kmp` | How Koin fits shared and platform code, with a diagram and target matrix. | new |
| Install and project structure | `platforms/kmp/install` | Catalog, Compiler Plugin, iOS framework, which artifact in which source set. | rewrite |
| Start Koin once | `platforms/kmp/start-koin` | One shared initKoin, called from Android, iOS, Desktop and Web. | new |
| Platform-specific dependencies | `platforms/kmp/platform-dependencies` | expect/actual modules, platform classes, Android Context in shared code. | rewrite |
| Koin from Swift and SwiftUI | `platforms/kmp/swift` | Native naming, KoinComponent helpers, and a plain statement of what is not supported. | split out |
| Shared ViewModels | `platforms/kmp/viewmodels` | Declare in common code; retrieval per platform; SavedStateHandle off Android. | new |
| Compose Multiplatform | `platforms/kmp/compose-multiplatform` | KoinApplication vs initKoin decision table; koinInject, koinViewModel, navigation, previews. | new |
| Testing in commonTest | `platforms/kmp/testing` | Recipe for common tests; what is JVM only; off-JVM rules. | new |
| Troubleshooting and recipes | `platforms/kmp/troubleshooting` | Double start, plugin missing, klib errors, version mismatch, iOS start placement. | new |
| Walkthrough: Android and iOS app | `platforms/kmp/walkthrough` | End to end on koin-getting-started/compose, with style tabs. | merge |

**Ktor**

| Page | Path | Purpose | Status |
|---|---|---|---|
| Ktor: install and start | `platforms/ktor` | Install the plugin, inject in routes, lifecycle events. | rewrite |
| Tutorial: your first Ktor service | `platforms/ktor/tutorial` | Build a service with style tabs instead of two pages. | merge |
| Request scopes | `platforms/ktor/request-scopes` | requestScope and call.scope, with the missing imports. | keep |
| Isolated context in Ktor | `platforms/ktor/isolated-context` | KoinIsolated for embedded servers and tests. | keep |
| Ktor DI bridge | `platforms/ktor/di-bridge` | Interoperate with Ktor's own DI. | keep |
| Configuration and properties | `platforms/ktor/configuration` | application.conf, getProperty, and links to the samples. | new |

### Concepts

| Page | Path | Purpose | Status |
|---|---|---|---|
| Definitions | `concepts/definitions` | single, factory, bindings, overriding, eager creation (canonical). | keep |
| Modules | `concepts/modules` | Compose modules: includes, @Configuration, labels, load order. | keep |
| Starting Koin | `concepts/starting-koin` | startKoin, typed startup, stop, and why start only once. | rewrite |
| Injection | `concepts/injection` | Constructor injection, get, inject, KoinComponent, collections. | merge |
| Qualifiers | `concepts/qualifiers` | Tell two definitions of one type apart; generic types. | keep |
| Injected parameters | `concepts/parameters` | Pass values at resolution time. | keep |
| Scopes | `concepts/scopes` | Do you need a scope, then scope API; corrects the @Scope claim. | rewrite |
| ViewModel definitions | `concepts/viewmodels` | Declare ViewModels on any platform; retrieval table per platform. | keep |
| Properties | `concepts/properties` | Declare and read properties, on each platform. | split out |
| Lazy modules | `concepts/lazy-modules` | Background loading, with target caveats. | keep |
| Context isolation | `concepts/context-isolation` | A private container for SDKs (canonical). | keep |
| Logging | `concepts/logging` | Loggers and levels, merged from two tables. | split out |
| Patterns | `concepts/patterns` | Decorator and provider patterns. | split out |

### Testing

| Page | Path | Purpose | Status |
|---|---|---|---|
| Tutorial: your first test | `testing/first-test` | Test a module with JUnit, on the DSL and Annotations paths. | rewrite |
| Unit tests | `testing` | KoinTest, overrides, mocks, JUnit 4 and 5. | keep |
| Verifying your configuration | `testing/verify` | Do you need verify()? Single decision table; checkModules deprecation. | rewrite |
| Testing composables | `testing/compose` | Previews and UI tests. | keep |
| Android instrumented tests | `testing/android-instrumented` | Rules and test lifecycle. | keep |
| Ktor tests | `testing/ktor` | testApplication with Koin. | keep |

The Testing sidebar also links to `platforms/kmp/testing`, so that topic has one canonical page reachable from both places.

### Tooling & Production

| Page | Path | Purpose | Status |
|---|---|---|---|
| Overview | `tooling` | Which tool for which need. | keep |
| Koin Compiler Plugin | `tooling/compiler-plugin` | What it does, requirements, install, and its limits (canonical). | merge |
| Compile-time safety | `tooling/compile-time-safety` | What the plugin checks and how to handle findings. | split out |
| IDE plugin | `tooling/ide-plugin` | Navigation and inspections in IntelliJ and Android Studio. | keep |
| AI agents and Koin skills | `tooling/ai-agents` | Install official skills for coding agents. | keep |
| Built-in monitoring | `tooling/monitor` | @Monitor. | keep |
| Production monitoring | `tooling/production-monitoring` | Kotzilla platform. | keep |

### Migration & Upgrades

| Page | Path | Purpose | Status |
|---|---|---|---|
| Which guide do I need? | `migration` | Routes Hilt, Dagger, KSP and version upgrades. | new |
| Koin vs Hilt | `migration/koin-vs-hilt` | Comparison for deciding; fixes its broken promise and Hilt mappings. | rewrite |
| Migrate from Hilt | `migration/from-hilt` | Step by step, now including running both side by side. | rewrite |
| Migrate from Dagger | `migration/from-dagger` | koin-dagger-bridge: the only module without a usage page today. | new |
| JSR-330 annotations | `migration/jsr330` | @Inject and friends; wording depends on the koin-jsr330 decision. | rewrite |
| Migrate from KSP to the Compiler Plugin | `migration/from-ksp` | Canonical guide, plus a KSP arg to option table. | keep |
| KSP processor setup (deprecated) | `migration/ksp-setup` | Kept for projects that cannot move yet. | keep |
| Upgrade from Koin 3.x | `migration/upgrade-from-3` | From 3.5.6 to 4.x: toolchain baselines, Gradle, replacements for removed APIs. | new |
| Upgrade to Koin 4.2 | `migration/upgrade-4-2` | Checklist from 4.0 or 4.1: breaking changes, floors, deprecations. | new |

### Reference

| Page | Path | Purpose | Status |
|---|---|---|---|
| Gradle artifacts and BOM | `reference/artifacts` | Single install table: artifacts, targets, BOM, versions. | rewrite |
| DSL reference | `reference/dsl` | Lookup table only; prose moves to Concepts. | keep |
| Annotations reference | `reference/annotations` | Every annotation; fixes binds semantics and artifact notes. | keep |
| Compiler Plugin options | `reference/compiler-options` | koinCompiler { } block. | keep |
| Compiler Plugin diagnostics | `reference/diagnostics` | KOIN-D, W, A codes. | split out |
| Extension manager | `reference/extensions` | Advanced API for library authors. | keep |
| Glossary | `reference/glossary` | Canonical vocabulary. | new |

### Support

| Page | Path | Purpose | Status |
|---|---|---|---|
| Versions, roadmap and support | `support` | LTS lines, cadence, community. | keep |
| Releases | `support/releases` | Changelog; upgrade steps move out. | keep |
| API stability | `support/api-stability` | API contract, semver and release types. | keep |
| Koin for SDKs and libraries | `support/embedded` | Embedded distribution. | keep |
| Troubleshooting | `support/troubleshooting` | Error catalog. | rewrite |
| FAQ | `support/faq` | Short answers that link to the canonical page. | new |

## Persona check

Every path starts from the homepage decision map (`src/data/decision-map.ts`), which asks whether the reader knows Koin, then routes by platform or need. Clicks count the answers and the final link.

| Persona | Clicks | Path | Reaches |
|---|---|---|---|
| Android developer new to DI | 3 | Home, "I'm new to Koin", "Android", then Tutorial (or `what-is-di` first) | Working app |
| Migrating from Hilt or Dagger | 2 | Home, "I'm coming from Hilt or Dagger", then `from-hilt` or `from-dagger` | Step-by-step guide with side-by-side |
| KMP developer | 3 | Home, "I'm new to Koin", "Kotlin Multiplatform", then Install; or "I already use Koin", "Add Koin to a platform", then Install | Working setup; the section then runs in order through to tests |
| Ktor developer | 3 | Home, "I'm new to Koin", "Ktor", then Tutorial | Working service |
| Upgrading from 4.0/4.1 or KSP | 3 | Home, "I already use Koin", "Upgrade Koin", then `upgrade-4-2` or `from-ksp` | Checklist |

## Kotlin Multiplatform outline

This follows your 10 items. Facts are verified in `audit/kmp.md` section 5. Anything marked "test" gets an actual build in Phase 4, since Xcode and JDK 17 are available here.

1. **Overview.** Shared vs platform code, diagram, target matrix. Compose targets exclude tvOS, watchOS, Linux and Windows; `koin-core-viewmodel` lacks watchOS, Linux and Windows; `koin-core-coroutines` lacks wasmJs.
2. **Install.** Catalog, plugin, `binaries.framework`, placement per source set. *Test:* `platform(koin-bom)` in `commonMain`. The docs disagree, the samples avoid it, and the BOM is a plain `java-platform`. Until proven, the page shows explicit versions.
3. **Start Koin once.** Shared `initKoin`. Fixes the Desktop snippet, which calls it inside `application { }`. *Test:* whether Swift can call `doInitKoin()` with no argument on a defaulted parameter, or needs the no-arg helper. The docs and samples disagree.
4. **Platform dependencies.** Four verified patterns, including `@Module expect class`, plus `Context` in shared code.
5. **Swift and SwiftUI.** See decision 3.
6. **Shared ViewModels.** Links to `concepts/viewmodels` for declarations; covers what differs by platform. *Test:* `SavedStateHandle` off Android.
7. **Compose Multiplatform.** Decision table: `KoinApplication` vs `initKoin`. The two current tutorials teach opposite answers. Replaces deprecated `KoinMultiplatformApplication`.
8. **Testing.** `kotlin.test` recipe, `MockProvider.register`, JVM-only list. `verify()` is JVM only despite the current table. *Test:* the recipe on JVM, iOS simulator and wasmJs.
9. **Troubleshooting.** Includes the undocumented Kotlin 2.4.0 floor for annotation-based WASM/JS (KT-82395). Also wasmJs `createdAtStart`: the repo notes failing core tests there, and the tutorial relies on it.
10. **Walkthrough.** `examples/` has no KMP sample, so this uses `koin-getting-started/compose` (Android and iOS). It has no tests, Desktop or Web, and needs a catalog outside its folder. I will state those limits and not edit samples.

## Versioning decision

| Option | Pros | Cons |
|---|---|---|
| **A. 4.2 only; current deployment moves to `legacy.insert-koin.io`; `/docs/4.1/*` and `/docs/4.0/*` redirect there (recommended)** | No rework or risk; old content stays exactly as published; new IA stays simple; search covers current docs only | Needs a DNS change and keeping the old Netlify site; old versions look different |
| B. Reproduce versions with `starlight-versions` 0.10.2 (active; compatible with Starlight 0.42) | Single site; one version switcher | 121 unmaintained pages to convert with no available source; they use the old IA, so each URL needs its own mapping; slower builds; search noise |
| C. Copy the crawled 4.1 and 4.0 HTML into `public/` | No DNS change; URLs unchanged | Ships a frozen Docusaurus bundle inside the new build; awkward to maintain |

Also fixes the dropdown problem: the "Older versions" link goes to a short section on `support` that links to each version's real start page rather than `contact`. If DNS is not possible, C is the fallback.

## Page changes

**Create from scratch** (14)

- `platforms/kotlin`
- `platforms/android/navigation`
- `platforms/kmp`
- `platforms/kmp/start-koin`
- `platforms/kmp/viewmodels`
- `platforms/kmp/compose-multiplatform`
- `platforms/kmp/testing`
- `platforms/kmp/troubleshooting`
- `platforms/ktor/configuration`
- `migration`
- `migration/from-dagger`
- `migration/upgrade-4-2`
- `reference/glossary`
- `support/faq`

**Split out of an existing page** (6)

- `platforms/kmp/swift` from `/docs/reference/koin-mp/kmp`
- `concepts/properties` from `/docs/reference/koin-core/starting-koin`
- `concepts/logging` from `/docs/reference/koin-core/starting-koin`
- `concepts/patterns` from `/docs/reference/koin-core/advanced-patterns`
- `tooling/compile-time-safety` from `/docs/reference/koin-compiler/compile-safety`
- `reference/diagnostics` from `/docs/reference/koin-compiler/compile-safety`

**Merge several pages** (10)

- `get-started` from `/docs/intro`
- `get-started/pick-your-platform` from `/docs/setup`
- `platforms/kotlin/tutorial` from `/docs/quickstart/kotlin`, `/docs/quickstart/kotlin-annotations`
- `platforms/android/tutorial` from `/docs/quickstart/android-viewmodel`, `/docs/quickstart/android`, `/docs/quickstart/android-annotations`
- `platforms/android/libraries` from `/docs/reference/koin-android/binding-patterns`, `/docs/reference/koin-android/advanced-patterns`
- `platforms/compose/lifecycle` from `/docs/reference/koin-compose/compose-lifecycle`, `/docs/reference/koin-compose/compose-modules`
- `platforms/kmp/walkthrough` from `/docs/quickstart/cmp`, `/docs/quickstart/compose-multiplatform-annotations`
- `platforms/ktor/tutorial` from `/docs/quickstart/ktor`, `/docs/quickstart/ktor-annotations`
- `concepts/injection` from `/docs/reference/koin-core/injection`, `/docs/reference/koin-core/koin-component`
- `tooling/compiler-plugin` from `/docs/setup/compiler-plugin`, `/docs/intro/koin-compiler-plugin`

**Rewrite** (21)

- `get-started/why-koin` from `/docs/intro/what-is-koin`
- `get-started/core-concepts` from `/docs/intro/core-concepts`
- `get-started/choose-your-style` from `/docs/intro/choosing-your-style`
- `platforms/android` from `/docs/reference/koin-android/start`
- `platforms/android/tutorial` from `/docs/quickstart/android-compose`
- `platforms/android/best-practices` from `/docs/reference/koin-android/best-practices`
- `platforms/android/r8-proguard` from `/docs/reference/koin-android/r8-proguard`
- `platforms/compose` from `/docs/reference/koin-compose/compose`
- `platforms/compose/navigation3` from `/docs/reference/koin-compose/navigation3`
- `platforms/kmp/install` from `/docs/reference/koin-core/kmp-setup`
- `platforms/kmp/platform-dependencies` from `/docs/reference/koin-core/kmp-shared-modules`
- `platforms/ktor` from `/docs/reference/koin-ktor/ktor`
- `concepts/starting-koin` from `/docs/reference/koin-core/starting-koin`
- `concepts/scopes` from `/docs/reference/koin-core/scopes`
- `testing/first-test` from `/docs/quickstart/junit-test`
- `testing/verify` from `/docs/reference/koin-test/verify`
- `migration/koin-vs-hilt` from `/docs/intro/koin-vs-hilt`
- `migration/from-hilt` from `/docs/reference/koin-android/hilt-migration`
- `migration/jsr330` from `/docs/reference/koin-android/jsr330`
- `reference/artifacts` from `/docs/setup/gradle`
- `support/troubleshooting` from `/docs/reference/koin-core/troubleshooting`

**Removed** (1): `/architecture`, a waitlist form that is not documentation. Please tell me if it should stay.

**Not ported as is:** no content is deleted without a home; duplicated explanations collapse to one canonical page per AUDIT section 4.

## Migration map

Every URL in the live sitemap (204), every file in repo `docs/` (87), and URLs referenced from outside the sitemap. Generation fails if one is missing. Implementation in Phase 3: Astro `redirects` plus a Netlify `_redirects` file for real 301s, because HTML meta-refresh pages drop `#fragments`. Fragments survive a 301 but may not exist on split pages, in which case the reader lands at the top of the new page.

### Current version, published or referenced

| Old URL | Change | New URL | Note |
|---|---|---|---|
| `/` | same URL | `/` | Same URL; messaging fixed. |
| `/architecture` | removed | `(none)` | Waitlist form, not documentation; not linked from nav. Redirect to / unless you want it kept. |
| `/docs` | moved to | `/docs/get-started` | Linked from AGENTS.md and sibling repos. |
| `/docs/intro` | moved to | `/docs/get-started` |  |
| `/docs/intro/choosing-your-style` | moved to | `/docs/get-started/choose-your-style` |  |
| `/docs/intro/core-concepts` | moved to | `/docs/get-started/core-concepts` |  |
| `/docs/intro/index` | moved to | `/docs/get-started` | Accidental alias currently served with status 200. |
| `/docs/intro/koin-compiler-plugin` | merged into | `/docs/tooling/compiler-plugin` |  |
| `/docs/intro/koin-vs-hilt` | moved to | `/docs/migration/koin-vs-hilt` |  |
| `/docs/intro/what-is-dependency-injection` | moved to | `/docs/get-started/what-is-di` |  |
| `/docs/intro/what-is-koin` | moved to | `/docs/get-started/why-koin` |  |
| `/docs/intro/what-is-koin-annotations` | moved to | `/docs/get-started/choose-your-style` | Existing redirect, retargeted. |
| `/docs/migration/from-ksp-to-compiler-plugin` | moved to | `/docs/migration/from-ksp` |  |
| `/docs/quickstart/android` | merged into | `/docs/platforms/android/tutorial` |  |
| `/docs/quickstart/android-annotations` | merged into | `/docs/platforms/android/tutorial` |  |
| `/docs/quickstart/android-compose` | moved to | `/docs/platforms/android/tutorial` |  |
| `/docs/quickstart/android-viewmodel` | moved to | `/docs/platforms/android/tutorial` |  |
| `/docs/quickstart/cmp` | moved to | `/docs/platforms/kmp/walkthrough` |  |
| `/docs/quickstart/compose-multiplatform-annotations` | merged into | `/docs/platforms/kmp/walkthrough` |  |
| `/docs/quickstart/junit-test` | moved to | `/docs/testing/first-test` |  |
| `/docs/quickstart/kotlin` | moved to | `/docs/platforms/kotlin/tutorial` |  |
| `/docs/quickstart/kotlin-annotations` | merged into | `/docs/platforms/kotlin/tutorial` |  |
| `/docs/quickstart/ktor` | moved to | `/docs/platforms/ktor/tutorial` |  |
| `/docs/quickstart/ktor-annotations` | merged into | `/docs/platforms/ktor/tutorial` |  |
| `/docs/reference/introduction` | moved to | `/docs/get-started` | 404 today; doc.insert-koin.io redirects here. |
| `/docs/reference/koin-android/advanced-patterns` | merged into | `/docs/platforms/android/libraries` |  |
| `/docs/reference/koin-android/best-practices` | moved to | `/docs/platforms/android/best-practices` |  |
| `/docs/reference/koin-android/binding-patterns` | moved to | `/docs/platforms/android/libraries` |  |
| `/docs/reference/koin-android/entry-points` | moved to | `/docs/platforms/android/components` |  |
| `/docs/reference/koin-android/fragment-factory` | moved to | `/docs/platforms/android/fragments` |  |
| `/docs/reference/koin-android/get-instances` | moved to | `/docs/platforms/android/components` | Existing redirect, retargeted. |
| `/docs/reference/koin-android/hilt-migration` | moved to | `/docs/migration/from-hilt` |  |
| `/docs/reference/koin-android/instrumented-testing` | moved to | `/docs/testing/android-instrumented` |  |
| `/docs/reference/koin-android/jsr330` | moved to | `/docs/migration/jsr330` |  |
| `/docs/reference/koin-android/modules-android` | moved to | `/docs/platforms/android` | Existing redirect, retargeted. |
| `/docs/reference/koin-android/multi-module` | moved to | `/docs/platforms/android/multi-module` |  |
| `/docs/reference/koin-android/qualifiers` | moved to | `/docs/concepts/qualifiers` | Existing redirect, better target than today. |
| `/docs/reference/koin-android/r8-proguard` | moved to | `/docs/platforms/android/r8-proguard` |  |
| `/docs/reference/koin-android/scope` | moved to | `/docs/platforms/android/scopes` |  |
| `/docs/reference/koin-android/start` | moved to | `/docs/platforms/android` |  |
| `/docs/reference/koin-android/viewmodel` | moved to | `/docs/platforms/android/viewmodel` |  |
| `/docs/reference/koin-android/workmanager` | moved to | `/docs/platforms/android/workmanager` |  |
| `/docs/reference/koin-annotations/annotations-inventory` | moved to | `/docs/reference/annotations` |  |
| `/docs/reference/koin-annotations/definitions` | moved to | `/docs/concepts/definitions` | Existing redirect, retargeted. |
| `/docs/reference/koin-annotations/kmp` | moved to | `/docs/platforms/kmp/install` | Existing redirect, retargeted. |
| `/docs/reference/koin-annotations/modules` | moved to | `/docs/concepts/modules` | Existing redirect, retargeted. |
| `/docs/reference/koin-annotations/monitor` | moved to | `/docs/tooling/monitor` | Existing redirect, retargeted. |
| `/docs/reference/koin-annotations/options` | moved to | `/docs/reference/compiler-options` |  |
| `/docs/reference/koin-annotations/scope` | moved to | `/docs/concepts/scopes` | Existing redirect, retargeted. |
| `/docs/reference/koin-annotations/start` | moved to | `/docs/concepts/starting-koin` | Existing redirect, retargeted. |
| `/docs/reference/koin-compiler/compile-safety` | split, main part at | `/docs/tooling/compile-time-safety` | Split: code table to reference/diagnostics. |
| `/docs/reference/koin-compose/compose` | moved to | `/docs/platforms/compose` |  |
| `/docs/reference/koin-compose/compose-lifecycle` | moved to | `/docs/platforms/compose/lifecycle` |  |
| `/docs/reference/koin-compose/compose-modules` | merged into | `/docs/platforms/compose/lifecycle` |  |
| `/docs/reference/koin-compose/compose-scopes` | moved to | `/docs/platforms/compose/scopes` |  |
| `/docs/reference/koin-compose/compose-testing` | moved to | `/docs/testing/compose` |  |
| `/docs/reference/koin-compose/compose-viewmodel` | moved to | `/docs/platforms/compose/viewmodel` |  |
| `/docs/reference/koin-compose/isolated-context` | moved to | `/docs/platforms/compose/isolated-context` |  |
| `/docs/reference/koin-compose/navigation3` | moved to | `/docs/platforms/compose/navigation3` |  |
| `/docs/reference/koin-core/advanced-patterns` | split, main part at | `/docs/concepts/patterns` | Split: collections to injection; generics to qualifiers. |
| `/docs/reference/koin-core/context-isolation` | moved to | `/docs/concepts/context-isolation` |  |
| `/docs/reference/koin-core/definitions` | moved to | `/docs/concepts/definitions` |  |
| `/docs/reference/koin-core/dsl` | moved to | `/docs/reference/dsl` |  |
| `/docs/reference/koin-core/dsl-update` | moved to | `/docs/reference/dsl` | Existing redirect, retargeted. |
| `/docs/reference/koin-core/extension-manager` | moved to | `/docs/reference/extensions` |  |
| `/docs/reference/koin-core/injection` | moved to | `/docs/concepts/injection` |  |
| `/docs/reference/koin-core/injection-parameters` | moved to | `/docs/concepts/parameters` |  |
| `/docs/reference/koin-core/kmp-setup` | split, main part at | `/docs/platforms/kmp/install` | Split: start code to start-koin; Compose choice to compose-multiplatform. |
| `/docs/reference/koin-core/kmp-shared-modules` | moved to | `/docs/platforms/kmp/platform-dependencies` |  |
| `/docs/reference/koin-core/koin-component` | merged into | `/docs/concepts/injection` |  |
| `/docs/reference/koin-core/lazy-modules` | moved to | `/docs/concepts/lazy-modules` |  |
| `/docs/reference/koin-core/modules` | moved to | `/docs/concepts/modules` |  |
| `/docs/reference/koin-core/qualifiers` | moved to | `/docs/concepts/qualifiers` |  |
| `/docs/reference/koin-core/scopes` | moved to | `/docs/concepts/scopes` |  |
| `/docs/reference/koin-core/start-koin` | moved to | `/docs/concepts/starting-koin` | Existing redirect, retargeted. |
| `/docs/reference/koin-core/starting-koin` | split, main part at | `/docs/concepts/starting-koin` | Split: properties and logging become their own pages. |
| `/docs/reference/koin-core/troubleshooting` | moved to | `/docs/support/troubleshooting` |  |
| `/docs/reference/koin-core/viewmodel` | moved to | `/docs/concepts/viewmodels` |  |
| `/docs/reference/koin-ktor/ktor` | moved to | `/docs/platforms/ktor` |  |
| `/docs/reference/koin-ktor/ktor-bridge` | moved to | `/docs/platforms/ktor/di-bridge` |  |
| `/docs/reference/koin-ktor/ktor-isolated` | moved to | `/docs/platforms/ktor/isolated-context` |  |
| `/docs/reference/koin-ktor/ktor-scopes` | moved to | `/docs/platforms/ktor/request-scopes` |  |
| `/docs/reference/koin-ktor/ktor-testing` | moved to | `/docs/testing/ktor` |  |
| `/docs/reference/koin-mp/kmp` | split, main part at | `/docs/platforms/kmp/swift` | Split: Swift to swift; Context to platform-dependencies; Desktop and Web to start-koin. |
| `/docs/reference/koin-test/testing` | moved to | `/docs/testing` |  |
| `/docs/reference/koin-test/verify` | moved to | `/docs/testing/verify` |  |
| `/docs/setup` | moved to | `/docs/get-started/pick-your-platform` |  |
| `/docs/setup/annotations` | moved to | `/docs/tooling/compiler-plugin` | Existing redirect, retargeted. |
| `/docs/setup/annotations-ksp` | moved to | `/docs/migration/ksp-setup` |  |
| `/docs/setup/compiler-plugin` | moved to | `/docs/tooling/compiler-plugin` |  |
| `/docs/setup/gradle` | moved to | `/docs/reference/artifacts` |  |
| `/docs/setup/koin` | moved to | `/docs/reference/artifacts` | Existing redirect; README.md links here. |
| `/docs/setup/migrate` | moved to | `/docs/migration/upgrade-4-2` | Existing redirect, better target than today. |
| `/docs/setup/v3` | moved to | `/docs/reference/artifacts` | 404 today; linked by the koin-getting-started README. |
| `/docs/setup/why` | moved to | `/docs/get-started/why-koin` | Existing redirect, retargeted. |
| `/docs/support` | same URL | `/docs/support` |  |
| `/docs/support/api-stability` | same URL | `/docs/support/api-stability` |  |
| `/docs/support/embedded` | same URL | `/docs/support/embedded` |  |
| `/docs/support/releases` | same URL | `/docs/support/releases` |  |
| `/docs/tooling` | same URL | `/docs/tooling` |  |
| `/docs/tooling/ai-agents` | same URL | `/docs/tooling/ai-agents` |  |
| `/docs/tooling/ide-plugin` | same URL | `/docs/tooling/ide-plugin` |  |
| `/docs/tooling/monitor` | same URL | `/docs/tooling/monitor` |  |
| `/docs/tooling/production-monitoring` | same URL | `/docs/tooling/production-monitoring` |  |

### Older versions (121 URLs)

All follow one rule under option A: same path on `https://legacy.insert-koin.io`. Listed in full so each URL is accounted for.

| Old URL | New URL |
|---|---|
| `/docs/4.0/contact` | `https://legacy.insert-koin.io/docs/4.0/contact` |
| `/docs/4.0/quickstart/android` | `https://legacy.insert-koin.io/docs/4.0/quickstart/android` |
| `/docs/4.0/quickstart/android-annotations` | `https://legacy.insert-koin.io/docs/4.0/quickstart/android-annotations` |
| `/docs/4.0/quickstart/android-compose` | `https://legacy.insert-koin.io/docs/4.0/quickstart/android-compose` |
| `/docs/4.0/quickstart/android-viewmodel` | `https://legacy.insert-koin.io/docs/4.0/quickstart/android-viewmodel` |
| `/docs/4.0/quickstart/cmp` | `https://legacy.insert-koin.io/docs/4.0/quickstart/cmp` |
| `/docs/4.0/quickstart/junit-test` | `https://legacy.insert-koin.io/docs/4.0/quickstart/junit-test` |
| `/docs/4.0/quickstart/kmp` | `https://legacy.insert-koin.io/docs/4.0/quickstart/kmp` |
| `/docs/4.0/quickstart/kotlin` | `https://legacy.insert-koin.io/docs/4.0/quickstart/kotlin` |
| `/docs/4.0/quickstart/ktor` | `https://legacy.insert-koin.io/docs/4.0/quickstart/ktor` |
| `/docs/4.0/quickstart/ktor-annotations` | `https://legacy.insert-koin.io/docs/4.0/quickstart/ktor-annotations` |
| `/docs/4.0/reference/introduction` | `https://legacy.insert-koin.io/docs/4.0/reference/introduction` |
| `/docs/4.0/reference/koin-android/dsl-update` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-android/dsl-update` |
| `/docs/4.0/reference/koin-android/fragment-factory` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-android/fragment-factory` |
| `/docs/4.0/reference/koin-android/get-instances` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-android/get-instances` |
| `/docs/4.0/reference/koin-android/instrumented-testing` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-android/instrumented-testing` |
| `/docs/4.0/reference/koin-android/modules-android` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-android/modules-android` |
| `/docs/4.0/reference/koin-android/scope` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-android/scope` |
| `/docs/4.0/reference/koin-android/start` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-android/start` |
| `/docs/4.0/reference/koin-android/viewmodel` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-android/viewmodel` |
| `/docs/4.0/reference/koin-android/workmanager` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-android/workmanager` |
| `/docs/4.0/reference/koin-annotations/definitions` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-annotations/definitions` |
| `/docs/4.0/reference/koin-annotations/kmp` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-annotations/kmp` |
| `/docs/4.0/reference/koin-annotations/modules` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-annotations/modules` |
| `/docs/4.0/reference/koin-annotations/scope` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-annotations/scope` |
| `/docs/4.0/reference/koin-annotations/start` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-annotations/start` |
| `/docs/4.0/reference/koin-compose/compose` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-compose/compose` |
| `/docs/4.0/reference/koin-compose/isolated-context` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-compose/isolated-context` |
| `/docs/4.0/reference/koin-core/context-isolation` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/context-isolation` |
| `/docs/4.0/reference/koin-core/definitions` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/definitions` |
| `/docs/4.0/reference/koin-core/dsl` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/dsl` |
| `/docs/4.0/reference/koin-core/dsl-update` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/dsl-update` |
| `/docs/4.0/reference/koin-core/extension-manager` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/extension-manager` |
| `/docs/4.0/reference/koin-core/injection-parameters` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/injection-parameters` |
| `/docs/4.0/reference/koin-core/koin-component` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/koin-component` |
| `/docs/4.0/reference/koin-core/lazy-modules` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/lazy-modules` |
| `/docs/4.0/reference/koin-core/modules` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/modules` |
| `/docs/4.0/reference/koin-core/scopes` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/scopes` |
| `/docs/4.0/reference/koin-core/start-koin` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-core/start-koin` |
| `/docs/4.0/reference/koin-ktor/ktor` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-ktor/ktor` |
| `/docs/4.0/reference/koin-ktor/ktor-isolated` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-ktor/ktor-isolated` |
| `/docs/4.0/reference/koin-mp/kmp` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-mp/kmp` |
| `/docs/4.0/reference/koin-test/checkmodules` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-test/checkmodules` |
| `/docs/4.0/reference/koin-test/testing` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-test/testing` |
| `/docs/4.0/reference/koin-test/verify` | `https://legacy.insert-koin.io/docs/4.0/reference/koin-test/verify` |
| `/docs/4.0/resources` | `https://legacy.insert-koin.io/docs/4.0/resources` |
| `/docs/4.0/resources/articles` | `https://legacy.insert-koin.io/docs/4.0/resources/articles` |
| `/docs/4.0/resources/videos` | `https://legacy.insert-koin.io/docs/4.0/resources/videos` |
| `/docs/4.0/setup/annotations` | `https://legacy.insert-koin.io/docs/4.0/setup/annotations` |
| `/docs/4.0/setup/koin` | `https://legacy.insert-koin.io/docs/4.0/setup/koin` |
| `/docs/4.0/setup/migrate` | `https://legacy.insert-koin.io/docs/4.0/setup/migrate` |
| `/docs/4.0/setup/why` | `https://legacy.insert-koin.io/docs/4.0/setup/why` |
| `/docs/4.0/support` | `https://legacy.insert-koin.io/docs/4.0/support` |
| `/docs/4.0/support/api-stability` | `https://legacy.insert-koin.io/docs/4.0/support/api-stability` |
| `/docs/4.0/support/embedded` | `https://legacy.insert-koin.io/docs/4.0/support/embedded` |
| `/docs/4.0/support/releases` | `https://legacy.insert-koin.io/docs/4.0/support/releases` |
| `/docs/4.0/upgrade/migrate` | `https://legacy.insert-koin.io/docs/4.0/upgrade/migrate` |
| `/docs/4.0/upgrade/whats-new` | `https://legacy.insert-koin.io/docs/4.0/upgrade/whats-new` |
| `/docs/4.1/contact` | `https://legacy.insert-koin.io/docs/4.1/contact` |
| `/docs/4.1/quickstart/android` | `https://legacy.insert-koin.io/docs/4.1/quickstart/android` |
| `/docs/4.1/quickstart/android-annotations` | `https://legacy.insert-koin.io/docs/4.1/quickstart/android-annotations` |
| `/docs/4.1/quickstart/android-compose` | `https://legacy.insert-koin.io/docs/4.1/quickstart/android-compose` |
| `/docs/4.1/quickstart/android-viewmodel` | `https://legacy.insert-koin.io/docs/4.1/quickstart/android-viewmodel` |
| `/docs/4.1/quickstart/cmp` | `https://legacy.insert-koin.io/docs/4.1/quickstart/cmp` |
| `/docs/4.1/quickstart/junit-test` | `https://legacy.insert-koin.io/docs/4.1/quickstart/junit-test` |
| `/docs/4.1/quickstart/kmp` | `https://legacy.insert-koin.io/docs/4.1/quickstart/kmp` |
| `/docs/4.1/quickstart/kotlin` | `https://legacy.insert-koin.io/docs/4.1/quickstart/kotlin` |
| `/docs/4.1/quickstart/ktor` | `https://legacy.insert-koin.io/docs/4.1/quickstart/ktor` |
| `/docs/4.1/quickstart/ktor-annotations` | `https://legacy.insert-koin.io/docs/4.1/quickstart/ktor-annotations` |
| `/docs/4.1/reference/introduction` | `https://legacy.insert-koin.io/docs/4.1/reference/introduction` |
| `/docs/4.1/reference/koin-android/dsl-update` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-android/dsl-update` |
| `/docs/4.1/reference/koin-android/fragment-factory` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-android/fragment-factory` |
| `/docs/4.1/reference/koin-android/get-instances` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-android/get-instances` |
| `/docs/4.1/reference/koin-android/instrumented-testing` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-android/instrumented-testing` |
| `/docs/4.1/reference/koin-android/modules-android` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-android/modules-android` |
| `/docs/4.1/reference/koin-android/scope` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-android/scope` |
| `/docs/4.1/reference/koin-android/start` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-android/start` |
| `/docs/4.1/reference/koin-android/viewmodel` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-android/viewmodel` |
| `/docs/4.1/reference/koin-android/workmanager` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-android/workmanager` |
| `/docs/4.1/reference/koin-annotations/annotations-inventory` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-annotations/annotations-inventory` |
| `/docs/4.1/reference/koin-annotations/definitions` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-annotations/definitions` |
| `/docs/4.1/reference/koin-annotations/kmp` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-annotations/kmp` |
| `/docs/4.1/reference/koin-annotations/modules` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-annotations/modules` |
| `/docs/4.1/reference/koin-annotations/monitor` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-annotations/monitor` |
| `/docs/4.1/reference/koin-annotations/options` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-annotations/options` |
| `/docs/4.1/reference/koin-annotations/scope` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-annotations/scope` |
| `/docs/4.1/reference/koin-annotations/start` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-annotations/start` |
| `/docs/4.1/reference/koin-compose/compose` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-compose/compose` |
| `/docs/4.1/reference/koin-compose/isolated-context` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-compose/isolated-context` |
| `/docs/4.1/reference/koin-compose/navigation3` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-compose/navigation3` |
| `/docs/4.1/reference/koin-core/context-isolation` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/context-isolation` |
| `/docs/4.1/reference/koin-core/definitions` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/definitions` |
| `/docs/4.1/reference/koin-core/dsl` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/dsl` |
| `/docs/4.1/reference/koin-core/dsl-update` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/dsl-update` |
| `/docs/4.1/reference/koin-core/extension-manager` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/extension-manager` |
| `/docs/4.1/reference/koin-core/injection-parameters` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/injection-parameters` |
| `/docs/4.1/reference/koin-core/koin-component` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/koin-component` |
| `/docs/4.1/reference/koin-core/lazy-modules` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/lazy-modules` |
| `/docs/4.1/reference/koin-core/modules` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/modules` |
| `/docs/4.1/reference/koin-core/scopes` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/scopes` |
| `/docs/4.1/reference/koin-core/start-koin` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-core/start-koin` |
| `/docs/4.1/reference/koin-ktor/ktor` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-ktor/ktor` |
| `/docs/4.1/reference/koin-ktor/ktor-isolated` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-ktor/ktor-isolated` |
| `/docs/4.1/reference/koin-mp/kmp` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-mp/kmp` |
| `/docs/4.1/reference/koin-test/checkmodules` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-test/checkmodules` |
| `/docs/4.1/reference/koin-test/testing` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-test/testing` |
| `/docs/4.1/reference/koin-test/verify` | `https://legacy.insert-koin.io/docs/4.1/reference/koin-test/verify` |
| `/docs/4.1/resources` | `https://legacy.insert-koin.io/docs/4.1/resources` |
| `/docs/4.1/resources/articles` | `https://legacy.insert-koin.io/docs/4.1/resources/articles` |
| `/docs/4.1/resources/videos` | `https://legacy.insert-koin.io/docs/4.1/resources/videos` |
| `/docs/4.1/setup/annotations` | `https://legacy.insert-koin.io/docs/4.1/setup/annotations` |
| `/docs/4.1/setup/koin` | `https://legacy.insert-koin.io/docs/4.1/setup/koin` |
| `/docs/4.1/setup/migrate` | `https://legacy.insert-koin.io/docs/4.1/setup/migrate` |
| `/docs/4.1/setup/why` | `https://legacy.insert-koin.io/docs/4.1/setup/why` |
| `/docs/4.1/support` | `https://legacy.insert-koin.io/docs/4.1/support` |
| `/docs/4.1/support/api-stability` | `https://legacy.insert-koin.io/docs/4.1/support/api-stability` |
| `/docs/4.1/support/embedded` | `https://legacy.insert-koin.io/docs/4.1/support/embedded` |
| `/docs/4.1/support/releases` | `https://legacy.insert-koin.io/docs/4.1/support/releases` |
| `/docs/4.1/support/tooling` | `https://legacy.insert-koin.io/docs/4.1/support/tooling` |
| `/docs/4.1/upgrade/migrate` | `https://legacy.insert-koin.io/docs/4.1/upgrade/migrate` |
| `/docs/4.1/upgrade/whats-new` | `https://legacy.insert-koin.io/docs/4.1/upgrade/whats-new` |

### Stable links that must keep working

These are hard-coded outside the docs, and all are covered above: `consumer-rules.pro` in `koin-core-viewmodel` and `koin-compose-viewmodel` (`r8-proguard`), the sibling `koin-skills` references (for example `koin-core/scopes`, 10 occurrences), `README.md`, `AGENTS.md`, `llms.txt`. `llms.txt` and `llms-full.txt` must stay at the site root. I will not edit `README.md` or `llms.txt` without your say; the redirects cover them.

## Page templates

Applies to `src/content/docs/docs/platforms/`. `npm run check:templates` enforces the headings.

| Type | Opening | Body | End |
|---|---|---|---|
| Tutorial (`tutorial`, `walkthrough`) | `## What you will do`, then `## Prerequisites` | Steps in order, a checkpoint when the app runs, `## See the build catch a mistake` | `## Next steps` |
| Guide or reference | One paragraph starting with what the page covers, then `## Prerequisites` if the page needs them | Sections by task or by API | `## Next steps` |

A page that grows two unrelated subjects is split, and any old URL goes in `src/data/redirects.json`.

## Style tabs

`npm run check:tabs` enforces these rules on `koin-style` tabs:

- A tab only holds what differs between styles. When the Constructor / Classic DSL code is the same as the Compiler Plugin DSL code, leave out its tab and end the first tab with "Same code with the Constructor / Classic DSL."
- Labels never change. Starlight syncs tabs by exact label, so a new label such as "Koin DSL" would stop the reader's choice from following them.
