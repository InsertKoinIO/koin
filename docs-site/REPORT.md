# Koin documentation rebuild: report

The new site is complete on branch `docs/astro-starlight`, in `docs-site/`. It is not deployed and nothing is pushed. `docs/`, `projects/` and `examples/` are untouched.

**One thing to do before switching over:** `legacy.insert-koin.io` does not exist yet. Until it does, the 121 redirects for 4.1 and 4.0, and the "Older versions" links, lead nowhere.

## Verification results

| Check | Result |
|---|---|
| `npm run build` | Passes with 0 warnings and 0 broken internal links or anchors (validator fails the build otherwise) |
| Old URLs (`npm run check:redirects`) | 224 of 224 resolve: 215 redirects (121 to the legacy site), 9 unchanged, no redirect chains. This checks the build output, not a live host |
| Symbols (`npm run check:symbols`) | 141 Koin imports, 56 annotations and 23 artifacts found in `projects/` or the Compiler Plugin repo. Allowlisted names are in `scripts/symbols-allowlist.txt` |
| Function calls | 201 distinct names scanned. 95 are not in Koin sources, and I reviewed them by hand: AndroidX, Compose, Retrofit, Gradle, sample code or the Kotzilla SDK |
| Mobile width | 0 of 91 pages overflow at 375px (measured) |
| Dark mode | Checked by eye: home and KMP overview on desktop, plus home, KMP overview and artifacts at 375px. Other pages share the same theme |
| Synced tabs | A choice made on `concepts/definitions` is selected on `platforms/kmp/start-koin` (tested in a browser) |
| Persona paths | All 14 tested paths through the homepage decision map reach their target in at most 3 clicks, counting each answer and the final link. Tested by clicking through them in a browser |
| `llms.txt`, Pagefind | Generated. Search itself was not exercised in a browser |

### What was tested with real builds

Test project with Koin 4.2.2, Compiler Plugin 1.2.1 and Gradle 8.11.1, on Kotlin 2.3.20 and 2.4.10, for JVM, iOS simulator and wasmJs (Node). Swift was type-checked with `swiftc` against the generated framework, not run.

| Question | Result |
|---|---|
| BOM in `commonMain` | Works, and also applies to `commonTest` |
| `commonTest` recipe, `declareMock` with `MockProvider.register` | Pass on all three targets |
| Second `startKoin` | Throws on all three targets |
| Swift `KoinKt.doInitKoin()` on a defaulted parameter | **Compiles.** Before testing I expected it to fail. The docs and samples were right |
| `KoinComponent` helper, ViewModel helper, `declareFromSwift` from Swift | Compile |
| `KoinPlatform` from Swift | Not visible unless `koin-core` is exported |
| Annotations with `expect`/`actual` module, `startKoin<T>` | Pass on JVM and iOS on 2.3.20; wasmJs needs Kotlin 2.4 (below) |
| `koin-compose*` with the BOM, `KoinApplication`, `koinInject`, `koinViewModel` | Compile on all three |

### What was not verified

Please read these as limits on the "verified" claims above.

- **Most snippets were not compiled.** The Android, Ktor and Compose pages were checked at name level, which proves a symbol exists, not that a snippet works as pasted. KMP has the strongest coverage.
- **No Android target in the test project,** so `androidContext` on KMP was not built. The walkthrough samples were not built either.
- **Claims from the published text, left as they were:** `tooling/ide-plugin` does not say whether `single<T>()` is supported, and `monitoring()` and `trace()` come from the Kotzilla SDK, which is not in these repos.
- **Written from a reference, not tested:** `@IntoMap` mapping with `associateBy` (`from-hilt`), the hand-written component accessor for plain Dagger (`from-dagger`), `@Qualifier(User::class)` on a class with the Compiler Plugin DSL, and the Constructor DSL module in `testing/first-test`.
- **Off-Android `SavedStateHandle` with Navigation Compose** was not run. The page only states what the source shows.
- **Redirects on a real host:** `_redirects` follows Netlify's format but has never been served.

## What changed in the structure, and why

| Before | Now | Why |
|---|---|---|
| Navbar and sidebar with different trees | One sidebar; the top bar has only search, GitHub, theme and older versions | Readers could not build a mental model |
| "Setup" separate from usage | Each platform starts with its own install, from one `<Install>` component and one versions file | About 30 pages carried snippets in 3 conventions |
| KMP spread across URLs named after modules | 10 pages in reading order, under `/docs/platforms/kmp/` | One path from empty project to tests |
| Tabs that did not persist | `syncKey="koin-style"` and `syncKey="gradle"` | Your choice now follows you |
| Tutorial pairs for each style | One tutorial with tabs, inside its platform | Near-identical pages |
| Migration under "Koin vs Hilt", releases under Reference | Migration & Upgrades and Support sections | Labels now match content |
| Homepage says no plugin needed | Leads with Compiler Plugin and BOM | It contradicted the setup pages |

## Pages

91 documentation pages, a homepage and a 404 page, replacing 81 documentation pages. Full lists and the URL map are in [IA.md](./IA.md).

| Change | Count |
|---|---|
| Created | 15 (plus homepage and 404) |
| Split out | 6 |
| Merged | 10 |
| Rewritten | 21 |
| Moved with light edits | 39 |
| Removed | 1: `/architecture`, which redirects to `/` |

IA.md's outline predates the tests: the KMP Install page now recommends the BOM, and `<Install>` supports it in source sets.

## Discrepancies between docs and source

The full list with evidence is in [AUDIT.md](./AUDIT.md) and `audit/`. All are corrected in the new pages. The most significant:

- `docs/` is not the source of the published site. The real source was not available, so content came from the live text.
- `koin-android-workmanager` does not exist. It is `koin-androidx-workmanager`.
- The Desktop snippet started Koin inside `application { }`.
- `starting-koin` recommended deprecated `KoinMultiplatformApplication`.
- `verify()` was listed for all targets, but it is JVM only. It also rejects `T?` parameters that the plugin accepts.
- `koin-core-coroutines` has no wasmJs, and `koin-core-viewmodel` has no watchOS, Linux or Windows.
- `binds = []` differs from omitting `binds`.
- The Dagger bridge direction was backwards in the release notes. `koin-dagger-bridge` also depends on Hilt, so it does not serve plain Dagger.
- The promised Hilt side-by-side guide did not exist.
- The shipped `consumer-rules.pro` links to an R8 page that was a 404.
- `koin-jsr330` is not in this repo or the BOM, and Maven Central has only 2.x.
- The 4.2.0 notes name `Module.getAll`, which does not exist. The real functions are `Scope.getAll` and `Koin.getAll`.

### Issues in Koin itself (not reported anywhere yet)

Found while testing. They are documented as pitfalls, but belong upstream. I have not opened any issues.

1. **Compiler Plugin 1.2.1:** `single<T>()` and `single<T> { }` for one type in one compilation fail on iOS and wasmJs with `Different declarations with the same signatures`. JVM passes.
2. **`createdAtStart` does nothing on wasmJs,** silently. Reproduced in a user project, and `projects/CLAUDE.md` lists it as failing.
3. **Annotation-based wasmJs on Kotlin 2.3.20** fails with a raw compiler crash (`No file found for source null`, KT-82395). Works on 2.4.10.
4. **Plugin message W003** recommends deprecated `checkModules()`.
5. **`KoinApplication` KDoc** still claims it throws when Koin is running.
6. **`examples/multimodule-ktor`** calls a `koin { }` function that is deprecated at error level.

## Open questions and content to review

**Need your decision**

1. Does a private site repo exist? If so, compare against it: `docs/` and the live text differ, and I only had the second.
2. Is `koin-jsr330` still supported for 4.2? The page carries a caution until a maintainer answers.
3. Is Kotlin 2.2 the minimum you want to state for the runtime artifacts without the Compiler Plugin? The docs now say so, from a test with `koin-core` on the JVM only: 2.2.21 compiles, 2.1.21 and 2.0.21 crash the compiler.
4. Should Swift support wording change? It says "in progress" based on `koin-skills`.

**Please review**

- **Homepage:** it has app logos, sponsors, talks and a new hero title. "Koin is used in thousands of apps" and the listed apps were not verified as part of this rebuild. Marketing content is yours to decide.
- **`migration/from-dagger` and `from-hilt` side by side:** new, and the least tested.
- **`platforms/kmp/swift`:** it publicly states limits of the product.
- **`get-started/why-koin`:** it says Koin is the most widely used DI framework for KMP. The figures are dated November 2, 2026, so they need refreshing, and no independent survey confirms the ranking. The page now states that the KotlinConf client moved from Koin to Metro in 2026 (PR #581) and that its backend still uses Koin.
- **Prose around tabs:** the converter guessed where a tab's content ends, so a sentence may sit just outside the tab it belongs to.
- **"Last updated" dates:** they come from git, so every page shows this rebuild's date.

**Small known gaps:** diagram text is small at phone width. No API docs link, because no published site was found (`doc.insert-koin.io` redirects to a 404).

**Outside `docs-site/`, not changed:** `llms.txt` and `AGENTS.md` say the DSL means "no annotations on classes", which is not true for qualifiers. `README.md` links a redirect stub. `AGENTS.md` and `projects/CLAUDE.md` disagree about `docs/`. Once the new site is live, the "documentation lives in" lines need updating.

## Suggested next steps

**Deployment:** stay on Netlify, as today. Base directory `docs-site`, build `npm run build`, publish `docs-site/dist`. `_redirects` is generated by the build.

**CI:** a GitHub Actions workflow on changes to `docs-site/**` running `npm ci`, `npm run build`, `npm run check:redirects`, and `npm run check:symbols`. The last one needs a checkout of `koin-compiler-plugin` passed with `--ref`, and also protects docs when APIs change in `projects/`.

**Switchover**

1. Review this branch and resolve the questions above.
2. Deploy `docs-site` to a preview URL and walk it, especially redirects on the real host.
3. Point `legacy.insert-koin.io` at the current Netlify site and confirm `/docs/4.1/` loads.
4. Point `insert-koin.io` at the new site. Keep the old deployment available for rollback.
5. Check search and analytics. The current site loads Plausible, and the new one does not yet.
6. Resubmit the sitemap, and watch 404s for a few weeks.
7. Retire or redirect `docs/`, and update `AGENTS.md`, `projects/CLAUDE.md`, `README.md` and `llms.txt`. Fix `doc.insert-koin.io`.
8. File the upstream issues above.
