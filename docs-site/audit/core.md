<!-- Detailed audit appendix. `refs/` are shallow clones of sibling InsertKoinIO repos at audit time; `live/md` is the published text split per page, neither is committed. -->

# Audit: koin-core (minus kmp-setup, kmp-shared-modules, viewmodel), compile-safety, koin-test, koin-annotations

Scope: 19 pages under https://insert-koin.io/docs/reference/... Source = projects (KOIN) and refs/koin-compiler-plugin (KCP, v1.2.1). KOIN version 4.2.2.
Overall: the pages are accurate. Every API, exception name, option name, default, diagnostic code and message I grepped exists in source. Real defects are few (section 1 and 2). The main cost is structural: triple-tab duplication and overlapping topics (section 3).

## 1. Contradictions between pages

| # | Page A | Page B | Detail | Truth (source) |
|---|--------|--------|--------|----------------|
| C1 | koin-core/scopes, "Declaring a scope": "Tag the class with `@Scope` ... and with `@Scoped` ... **You need both**" | koin-annotations/annotations-inventory, `@Scope`: "On its own, `@Scope` declares a `scoped` definition. Add `@Scoped`, `@Factory` or `@KoinViewModel` to choose another kind" | Direct contradiction, and every scopes.md example carries both annotations | KOIN CoreAnnotations.kt:84-90 KDoc: "By default, will declare a `scoped` definition. Can also override with @Scoped, @Factory..." So inventory is right, scopes.md overstates. `@Scoped` KDoc (l.97) says "Must be associated with @Scope" |
| C2 | koin-annotations/annotations-inventory: "When `binds` is empty, the definition is bound to its class and all its supertypes" (tables show default `[]`) | koin-core/advanced-patterns, Decorator: "Declare `PushNotificationService` with `@Singleton(binds = [])`, so it is not bound to `NotificationService` as well" | Inventory implies `binds = []` equals the default. Advanced-patterns relies on explicit `[]` suppressing auto-bind | KCP KoinAnnotationProcessor.kt:332 and :584: omitted = auto-bind, explicit empty = no bindings (testData/box/bindings/delegation_no_autobind.kt). Advanced-patterns is right; inventory needs "omitted vs explicit `[]`" |
| C3 | koin-core/injection-parameters: "`@InjectedParam` comes from the `koin-annotations` artifact" (also qualifiers: "`@Named` ... from `koin-annotations`") | koin-test/verify: "Both annotations come from `koin-core-annotations`" (`@InjectedParam`, `@Provided`) | Different artifact for the same annotation | KOIN koin-core-annotations/.../KoinCoreAnnotations.kt:26-35 holds InjectedParam and Provided; koin-annotations build.gradle.kts:42 exposes it via `api`. Both true, but a reader of a non-annotation project (verify only) needs to know the lighter artifact. Say it once in annotations-inventory |
| C4 | koin-test/verify: "Do you still need verify()?" lists 4 cases where plugin users still need it | koin-core/troubleshooting: "In tests: without the Compiler Plugin, call `verify()`"; koin-test/testing "Check your configuration": "use the Compiler Plugin ... or the `verify()` test API" | Troubleshooting and testing present them as alternatives; verify page says they overlap only partly (W003, library modules, lazy modules, nullable params) | Minor inconsistency in framing |
| C5 | koin-core/starting-koin (Note): "With the Compiler Plugin, there is no `AppModule().module` extension to call" | koin-compiler/compile-safety: `KOIN-A003 Cannot generate <Module>.module()`; KCP tests call `TestModule().module()` | The generated thing is a function `M.module()` (KSP was the property `.module`). Doc wording suggests nothing is generated | KCP docs/CODE_WALKTHROUGH.md:123 "`@Module class M` -> `fun M.module(): Module`"; KCP KoinDiagnostic.kt:427-433. Reword: "do not call it; use `startKoin<T>()`" |
| C6 | koin-compiler/compile-safety "All codes" + koin-core/troubleshooting: runtime-only fallback is `verify()` | KCP W003 message itself says: "it is validated at runtime via checkModules()" (KoinDiagnostic.kt:309) | Plugin output points readers to a deprecated API; docs point to verify(). Not fixable in docs, flag to plugin repo | KOIN CheckModules.kt:43-47 `@Deprecated("Migrate to verify() API")` |
| C7 | koin-annotations/options (URL under `koin-annotations/`) says options apply "to the Compiler Plugin DSL and to annotations alike" | compile-safety lives under `koin-compiler/` | IA contradiction: plugin options page lives in the annotations section, diagnostics in the compiler section | Move options to `koin-compiler/` |
| C8 | koin-core/definitions "Overriding a definition" shows `.override()` postfix only | koin-core/dsl lists `withOptions { }` options without `override()` | `override()` also exists inside the options block | KOIN OptionDSL.kt:131. Minor gap |

## 2. Docs vs source discrepancies (copy-risk first)

| # | Page | Doc quote | Source evidence | Verdict |
|---|------|-----------|-----------------|---------|
| D1 | scopes | "You need both" (@Scope + @Scoped) | CoreAnnotations.kt:84-90 | Wrong (see C1). Examples still compile, but teach redundant code |
| D2 | annotations-inventory | "`binds` ... Empty binds all supertypes" | KCP KoinAnnotationProcessor.kt:332,584 | Incomplete (see C2) |
| D3 | starting-koin | "no `AppModule().module` extension" | KCP CODE_WALKTHROUGH.md:123 | Misleading (see C5) |
| D4 | scopes | `options(viewModelScopeFactory())` shown without opt-in | KOIN KoinOption.kt:39 `@KoinViewModelScopeApi`; ViewModelScopeArchetypeDSL.kt:19,41 | Doc states the annotations are required, but gives no `@OptIn(KoinViewModelScopeApi::class)` in the snippet. Copy-paste will fail to compile without opt-in (or warn, depending on RequiresOptIn level) |
| D5 | testing / starting-koin / koin-component | `KoinApplicationAlreadyStartedException` on second `startKoin` | GlobalContext.kt present in jvm, js, wasmJs, native | OK on all targets |
| D6 | verify | verify accepts "Declared / Optional (default) / Wrapped / Whitelisted / Injected". Compile-safety table: "Nullable parameter (`T?`), no definition: OK" | KOIN Verification.kt:131-185: only `isOptional` (default value) is tolerated, nullable without default is not | verify() will fail on `T?` params that compile-safety accepts. Not stated anywhere. Add a row "Nullable: not accepted" |
| D7 | dsl | `includes(otherModule, ...)` and `moduleA + moduleB` listed with package `org.koin.dsl` | KOIN Module.kt:67,75,170: members of `Module` (no import) | Package column wrong for those 2 rows (cosmetic) |
| D8 | dsl | `options(...)` row | KOIN KoinApplication.kt:117 takes `Pair<KoinOption,Any>`; `viewModelScopeFactory()` is `@KoinViewModelScopeApi` | Fine, but same opt-in gap as D4 |
| D9 | dsl | Missing: `onOptions`, `override()` in options block | OptionDSL.kt:64,131 | Gaps in reference completeness |
| D10 | project note | createdAtStart documented as working everywhere (definitions, modules, lazy-modules) | projects/CLAUDE.md:84 "createdAtStart on wasmJs ... fail" | Known wasmJs limitation not in docs |

Checked and correct (no action): startKoin/koinApplication/stopKoin/loadKoinModules; strictOverride/allowOverride (default true); fileProperties/environmentProperties (jvmMain only); androidFileProperties; printLogger default INFO; Level values; EmptyLogger NONE; `new(::T)`; `create(::f)` (Scope extension, up to 15 params); `bind/binds/withOptions/named/createdAtStart/onClose`; fragmentOf; lazyModule/lazyModules (+ per-module launch, `dispatcher` param); awaitAllStartJobs/onKoinStarted/waitAllStartJobs/isAllStartedJobsDone; KoinExtension/ResolutionExtension/ExtensionManager (@KoinInternalApi) and addResolutionExtension (@KoinExperimentalAPI); getOrNull/injectOrNull; KoinPlatform.getKoinOrNull; ParametersHolder destructuring up to 5, parameterArrayOf/parameterSetOf; qualified lookups ignore parameters (CoreResolverV2.kt:146); named(enum)/qualifier()/named<T>(); createScope/getOrCreateScope/getScopeOrNull/deleteScope/linkTo/unlink/registerCallback/closed; KoinScopeComponent.createScope/newScope; every annotation, target and parameter default in the inventory (CoreAnnotations.kt, AndroidAnnotations.kt, AndroidScopeArchetypes.kt, KoinCoreAnnotations.kt); all Gradle options and defaults (KoinGradleExtension.kt:24-130); strictSafetyForceOff; KOIN-D001..D008, W001, W003..W006, A001..A003, S001, P001, M001 (W002 and W007 were removed from KCP, correctly absent from docs); exception names and message texts in troubleshooting; verify/verifyAll/androidVerify/injectedParameters/definition; MissingKoinDefinitionException, CircularInjectionException; KoinTestRule (closes existing instance), KoinTestExtension, MockProviderRule/Extension, AutoCloseKoinTest, ClosingKoinTest, declareMock (qualifier, secondaryTypes, Scope overload), declare (global context).

### verify()/checkModules deprecation (asked)
- `checkModules()`, `checkKoinModules()` (all overloads), and the `CheckModulesDSL` helpers are `@Deprecated` in source (KOIN core/koin-test/.../check/CheckModules.kt:43,56,73,89,102,113; CheckModulesDSL.kt:29,32). Replacement named: `verify()`.
- `verify()` is NOT deprecated (VerifyModule.kt:30, AndroidVerify.kt:24,39). `Module.verify` in koin-android-test is a duplicate of `androidVerify`, undocumented.
- The verify page is correct on both points. Repo AGENTS.md says "No checkModules() / verify() tests" for Compiler Plugin users: stricter than the verify page, which keeps 4 legitimate cases. Docs should state the policy once (see section 3).

## 3. Duplicated content

| Topic | Where it is repeated | Canonical page | Action |
|-------|----------------------|----------------|--------|
| 3-tab snippets (Compiler Plugin DSL / Annotations / Constructor+Classic) | every core page; testing, starting-koin, koin-component have two identical tabs (CP DSL = Classic) | n/a | Collapse identical tabs; keep tabs only where code differs. Biggest bulk reduction |
| starting-koin vs modules | `@Configuration` rules + labels in starting-koin ("Configuration labels") and modules ("Auto-discovery", "Load order"); load order also in inventory `@KoinApplication`; `module<T>()`/`modules(KClass)` in starting-koin and dsl | modules for declaring/composing/discovery/order; starting-koin for run-time start options, logging, properties, stop | Move "Configuration labels" next to "Auto-discovery with @Configuration" in modules; starting-koin links. Keep typed startup in starting-koin. Cross-links already good; overlap is moderate, not harmful |
| koin-core/dsl vs definitions | definition types, bind/binds, onClose, createdAtStart, `.override()`, scope DSL, archetypes (dsl and scopes) | definitions = explanation; dsl = pure lookup table | Keep both, but remove prose from dsl, and drop the archetype table from either dsl or scopes (keep in dsl, link from scopes) |
| advanced-patterns | "Collections" duplicates injection "All instances of a type" and definitions constructor-param table; "Generic types" duplicates compile-safety "Generic types"; "Provider pattern" overlaps injection-parameters | injection for `getAll()` API; advanced-patterns for the patterns | Split advanced-patterns: collections goes to injection, generics goes to qualifiers, keep decorator + provider as a short "patterns" how-to or fold under definitions |
| verify vs compile-safety | "Do you still need verify()" (verify) vs "Replacing verify() and checkModules()" (compile-safety); also troubleshooting "Find problems early", testing "Check your configuration", quickstart/junit-test, tooling, core-concepts | verify page (decision + API) | Reduce compile-safety, testing and troubleshooting to one sentence + link. Put the decision table only in verify. State deprecation of checkModules once |
| Override semantics | definitions "Overriding", modules "Load order", testing "Override definitions", starting-koin table, advanced-patterns "Strict override", troubleshooting "Already existing definition", dsl | definitions | Others: one line + link |
| Logging | starting-koin "Logging" (logger table) and troubleshooting "Logging" (level table) | starting-koin | Merge the two tables into starting-koin; troubleshooting links |
| Properties | starting-koin, definitions param table, inventory `@Property`/`@PropertyValue`, compile-safety "@Property" | starting-koin (how-to) + inventory (reference) | OK, trim compile-safety |
| Context isolation snippet | context-isolation (`SdkKoinComponent`) and koin-component (`SdkComponent`, different names) | context-isolation | koin-component: link only |
| Eager creation | definitions, modules "Eager modules", starting-koin `createEagerInstances`, lazy-modules limit | definitions | Others link |
| Testing a component | koin-component "Testing a KoinComponent", context-isolation "Testing", testing | testing | Link |
| `@Provided` / `@InjectedParam` | compile-safety, inventory, verify, injection-parameters | inventory (reference) + compile-safety (semantics) | Keep verify's two sentences |
| Plugin options split | options under koin-annotations, diagnostics under koin-compiler | single koin-compiler section | Move options.md, add redirect |

## 4. Gaps by persona

### Android dev new to DI
- No tutorial or mental model inside these pages: "container", "definition", "graph", "scope" used before defined (definitions links to core-concepts only once).
- Style tabs force a choice (3 styles) before the reader knows what a definition is. The "Choosing your style" link exists but the tab pressure is on every page.
- starting-koin has no Android snippet (`androidContext`, `Application` class, manifest) in the tabs, only a link to koin-android/start.
- Nowhere in core pages: "constructor injection for ViewModel/Activity" end-to-end flow; `by inject()` in Activity appears in injection but without `startKoin` context.
- Error recovery is good (troubleshooting) but there is no "my first error" bridge from the quickstart.
- Scopes page is dense for beginners: no motivating Android example (Activity-scoped) before `createScope` API; archetypes come late. Needs "do I need a scope?" decision up front.
- Reference-style testing page opens with dependencies, not "why/what to test".

### Existing Koin user upgrading
- No consolidated "what changed" table in these pages: `AppModule().module` -> `startKoin<T>()`, `@KoinViewModel` import moved to `org.koin.core.annotation`, glob `@ComponentScan` removed, `checkModules` deprecated, `singleOf` now checked in plugin 1.2.0 (only a warning box in compile-safety), `@Single` vs `@Singleton` preference, labels in `@Configuration`. Migration page covers KSP only partially in these pages.
- Almost no version tags ("since 4.2" appears once in lazy-modules; "since 1.1.0" in options). Add Since/Changed badges.
- `viewModelScopeFactory`, `KoinExperimentalAPI`/`KoinInternalApi` opt-ins not shown in snippets (D4).
- Deprecation list missing: which core APIs are deprecated (only checkModules is mentioned).
- Behaviour change of `get()` in compile-safety (call sites now checked) is covered, but "how to silence/handle new build failures after upgrade" is only in a warning box.
- Missing wasmJs/native caveats (D10) and `KoinApplicationAlreadyStartedException` handling on KMP.

## 5. Page disposition (Diataxis)

| Page | Real category | Disposition | Note |
|------|---------------|-------------|------|
| core/starting-koin | how-to + reference mix | split | Start/stop + typed startup = how-to; options and logger tables = reference. Move "Configuration labels" to modules |
| core/modules | how-to + explanation (load order) | keep | Absorb configuration labels; explanation of load order stays |
| core/definitions | explanation + how-to (many tabs) | keep, trim | Canonical for overrides, eager, onClose |
| core/dsl | reference | keep | Remove prose, add onOptions/override, fix package column (D7, D9) |
| core/advanced-patterns | how-to (4 patterns, unrelated) | split | collections -> injection, generics -> qualifiers, decorator/provider -> how-to "patterns" |
| core/injection | reference + how-to | keep | Add collections section from advanced-patterns |
| core/injection-parameters | how-to + reference ("How Koin matches values") | keep | Fix artifact wording (C3) |
| core/koin-component | explanation + how-to | merge into injection (or trim 50%) | Duplicates SDK snippet; "why constructor injection" is explanation, move to core-concepts |
| core/qualifiers | how-to + reference | keep | Absorb generics from advanced-patterns |
| core/scopes | explanation + reference + how-to (mixed) | split | "Scopes concept" (explanation) vs "Scope API" (reference); fix C1 and D4 |
| core/lazy-modules | how-to + reference | keep | Add wasm/native caveat |
| core/context-isolation | how-to | keep | Canonical for isolated container; koin-component links |
| core/extension-manager | reference (advanced, internal API) | keep | Mark clearly "library authors" |
| core/troubleshooting | reference (error catalog) + how-to (cycles) + explanation (anti-patterns) | split | Error catalog = reference; "Patterns that cause errors" = explanation, move to core-concepts or definitions; merge logging into starting-koin |
| compiler/compile-safety | reference (diagnostics) + explanation (what is checked) | keep | Trim "Replacing verify()" to a link |
| koin-test/testing | how-to (reference for test APIs) | keep | Drop identical tabs |
| koin-test/verify | how-to + decision | keep | Add nullable row (D6); canonical for deprecation |
| koin-annotations/annotations-inventory | reference | keep | Fix C2 (binds), add artifact note (C3) |
| koin-annotations/options | reference | move to koin-compiler/ | Fix IA (C7) |

Mixed-category pages to flag: starting-koin, scopes, troubleshooting, advanced-patterns, koin-component.
No em dash is used in this report.
