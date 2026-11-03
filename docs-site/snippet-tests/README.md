# Snippet tests

Compile and run the Kotlin code of the docs against the published Koin artifacts, at the versions in `src/data/versions.ts`. A snippet that only exists here would drift from the page, so the code is extracted from the pages.

```bash
npm run check:snippets
```

## Test a snippet

1. Put a marker just before the code block. It does not render. A block can belong to several scenarios, separated by commas.

   ````mdx
   {/* check: scopes-declare-annotations */}

   ```kotlin
   import org.koin.core.annotation.Scope
   ...
   ```
   ````

2. Create `fixtures/<project>/<scenario>.kt`. Use `with-plugin` when the code needs the Koin Compiler Plugin, and `without-plugin` otherwise. Each folder is its own Gradle project, and a folder whose name starts with `with-plugin` gets the plugin. Add a folder when `@Configuration` modules or a shared package would otherwise affect other scenarios. The fixture holds what the page leaves out, plus a `check()` function that runs the code and throws if the page's claim is false.

All the snippets and the fixture of a scenario share one package, and each snippet is its own file, so a snippet must bring its own imports. Use `__PACKAGE__` where the fixture needs the package name. To set the package, start the fixture with `// package: com.example.sdk`.

## Test a claim made in prose

A fixture with no snippet is allowed. It records a behavior the docs state in words, such as "`@Scope` alone registers nothing". If Koin changes the behavior, the test fails and the page needs an update.

## Multiplatform pages

```bash
npm run check:snippets:kmp   # needs ANDROID_HOME, and macOS for the iOS target
```

Scenarios live in `fixtures-kmp/<scenario>/<sourceSet>/`, each with its own Gradle project under `kmp/`. They compile the Android and iOS targets. The Koin graph runs on a JVM target, from a `jvmTest` in the fixtures, because the other targets need a device. A scenario whose name ends with `constructor` does not get the Compiler Plugin, and only a name ending with `annotations` gets `koin-annotations`.

A multiplatform block without a `package` line goes to `com.example.app`. A first line such as `// androidMain, MuseumApp.kt` or `// commonTest` sets its source set and file name, and the default is `commonMain`. A scenario whose name contains `testing` also gets `koin-test` and the Compose test library.

## Android pages

```bash
npm run check:snippets:android   # needs ANDROID_HOME
```

Scenarios live in `fixtures-android/<scenario>/`, each with its own Gradle project under `android/`. The app starts with Robolectric: the real `Application` and manifest, and a Compose test that uses the screen as the tutorial says. The manifest of every scenario is in `fixtures-android/_shared`.

Kotlin blocks go to the package `org.koin.sample`, the one the Annotations tab scans, unless they have their own `package` line. A scenario name ending with `annotations` gets `koin-annotations`, and one ending with `constructor` does not get the Compiler Plugin.

## Multi-module apps

```bash
npm run check:snippets:multimodule   # needs ANDROID_HOME
```

`multimodule/` is one real Android app per style, split into the Gradle modules of `android/multi-module.mdx`. A scenario `multimodule-dsl` or `multimodule-annotations` sends each block to the module named on its first line, such as `// Gradle module ":feature:login"`. The rest comes from `fixtures-multimodule/<style>/<module>`. Keep the dependencies in `multimodule/build.gradle.kts` in sync with the table on the page, which it can't read.

## Failures the build must report

```bash
npm run check:compile-failures
```

A scenario must compile, so `scripts/check-compile-failures.mjs` removes a definition from a generated copy of a tutorial or of the multi-module app, then requires the compilation to fail with `KOIN-D001`.

## Limits

- Nothing runs on a real device or simulator. Android code runs on Robolectric, multiplatform UI code is only compiled, and Swift code and Wasm are not covered.
- Coverage is partial. `npm run coverage:snippets` lists the checked blocks of each platform page. The Compose Multiplatform dependencies of Navigation 3 are not covered.
- `android-multimodule-*` runs in one Gradle module. Only `multimodule/` proves visibility and discovery across Gradle modules, for the DSL and Annotations styles.
- Swift code is typechecked by hand, not in CI, and never run, so `deinit` calling `clear()` is untested.
