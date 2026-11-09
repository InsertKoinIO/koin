---
title: Compiler Plugin Setup
description: Install the Koin Compiler Plugin in a Gradle build, for single-module, multi-module and Kotlin Multiplatform projects, and fix common setup problems.
---

This page installs the Koin Compiler Plugin, the Kotlin compiler plugin behind the Compiler Plugin DSL and annotations. It covers single-module, multi-module and Kotlin Multiplatform builds, the main options, and setup problems. To learn what the plugin does, read [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin). Koin libraries themselves are listed in [Gradle Setup](/docs/setup/gradle).

## Requirements

- Kotlin 2.3.20 or later (K2 compiler).
- Koin 4.2.0 or later.
- Gradle 8 or later.

Current versions:

- Koin: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-core)
- Koin Compiler Plugin: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-compiler-plugin?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-compiler-plugin)

The plugin has its own version, separate from Koin. `koin-annotations` follows the Koin version.

## Install

### 1. Declare the plugin

In `gradle/libs.versions.toml`:

```toml
[versions]
koin = "4.2.2"
koin-plugin = "1.2.1"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

If you use the [Koin BOM](/docs/setup/gradle#koin-bom), drop `version.ref` from the libraries. The plugin is not part of the BOM and keeps its version.

### 2. Plugin repositories

The plugin is published on Maven Central and the Gradle Plugin Portal. In `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
```

### 3. Apply the plugin

In the `build.gradle.kts` of each Gradle module that declares Koin definitions:

```kotlin
plugins {
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(libs.koin.core)          // or koin-android, koin-ktor, ...
    implementation(libs.koin.annotations)   // for annotations, and for @Named or @InjectedParam in DSL style
}
```

The Compiler Plugin DSL (`single<T>()`, `factory<T>()`, `viewModel<T>()`) ships with the Koin libraries, in the package `org.koin.plugin.module.dsl`. You need `koin-annotations` only if you use annotations, including parameter annotations in DSL style.

:::tip
`@KoinViewModel` and `@KoinWorker` need their runtime library on the classpath: `koin-core-viewmodel` (included in `koin-android` and `koin-compose-viewmodel`) and `koin-androidx-workmanager`. If one is missing, the build fails with an error that names the artifact.
:::

### 4. Check that it works

Turn on the plugin logs for one build:

```kotlin
koinCompiler {
    userLogs = true
}
```

The build output lists each definition the plugin detected. Turn it off again once you have checked. Then start Koin as described in [Starting Koin](/docs/reference/koin-core/starting-koin), with `startKoin { }` or, for annotations, `startKoin<MyApp>()`.

## Multi-module projects

Apply the plugin in every Gradle module that declares definitions, not only in the app module:

```kotlin
// feature/build.gradle.kts
plugins {
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
}
```

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(project(":feature"))
    implementation(libs.koin.android)
    implementation(libs.koin.annotations)
}
```

Each module compiled with the plugin carries metadata about its definitions. The app module reads it to check the full graph at `startKoin`. A library module without the plugin is invisible to these checks. How to assemble modules across Gradle modules (`includes`, `@Configuration`) is covered in [Modules](/docs/reference/koin-core/modules) and [Multi-Module Android Apps](/docs/reference/koin-android/multi-module).

## Kotlin Multiplatform

Apply the plugin once on the shared module and add the libraries to `commonMain`. There is no per-target configuration:

```kotlin
// shared/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.koin.compiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
        }
    }
}
```

See [Kotlin Multiplatform Setup](/docs/reference/koin-core/kmp-setup) for project structure.

## Options

The `koinCompiler` block configures the plugin. The defaults fit most projects:

```kotlin
koinCompiler {
    compileSafety = true      // validate the graph at build time
    skipDefaultValues = true  // use Kotlin default values instead of injecting
    userLogs = false          // log detected definitions
}
```

Every option, including `strictSafety`, `unsafeDslChecks`, `logSeverity` and `debugLogs`, is described in [Compiler Plugin Options](/docs/reference/koin-annotations/options).

## Kotlin version compatibility

Each plugin release is verified on a list of Kotlin versions:

| Kotlin | Koin Compiler Plugin |
|--------|----------------------|
| 2.3.20, 2.4.0, 2.4.10 | 1.2.0+ |
| 2.4.20 | 1.2.1+ |

On a Kotlin version that is not in the list, the plugin prints a warning and the build continues.

:::warning
On Kotlin 2.4.20, plugin 1.2.0 fails with `NoSuchMethodError` during IR generation. Use 1.2.1 or later.
:::

## Troubleshooting

### Plugin not found

Gradle can't resolve `io.insert-koin.compiler.plugin`. Check that `mavenCentral()` and `gradlePluginPortal()` are in `pluginManagement.repositories` in `settings.gradle.kts`.

### NotImplementedError at runtime

`single<T>()`, `factory<T>()` and `create(::T)` are placeholders that the plugin replaces. If the plugin is not applied to the Gradle module that contains them, the code compiles and throws `NotImplementedError` when the module is created. Apply the plugin to that module.

### Wrong imports

The Compiler Plugin DSL and the Constructor DSL have functions with the same names in different packages:

```kotlin
// Compiler Plugin DSL
import org.koin.plugin.module.dsl.*

// Constructor DSL: singleOf, factoryOf, viewModelOf
import org.koin.core.module.dsl.*

// both
import org.koin.dsl.module
```

### Stale results after a refactor

Like other Kotlin IR plugins (the Compose compiler, for example), the Koin Compiler Plugin can produce stale results with incremental compilation. Symptoms:

- A compile safety error that should not appear, or none after you removed a definition.
- `NoSuchMethodError` or `ClassNotFoundException` at runtime after a refactor.

It usually follows these changes:

- Changing an annotation on a class (`@Singleton` to `@Factory`, adding or removing `@Named`).
- Moving classes between packages scanned by `@ComponentScan`.
- Changing module `includes` or `@Configuration` labels.
- Adding or removing definitions in a library module another module depends on.

Run a clean build: `./gradlew clean build`, or in Android Studio, Build > Clean Project then Build > Rebuild Project.

For changes inside `module { }` lambdas, the plugin turns on `strictSafety` for modules that contain an entry point (`startKoin`, `koinApplication`, `@KoinApplication`). It re-runs the full graph check on each build of those modules. See [`strictSafety`](/docs/reference/koin-annotations/options).

### Missing dependency reported across modules

If the plugin reports a missing dependency that a library module does declare:

1. Apply the plugin to the library module too. Without it, the library carries no metadata.
2. Make sure the library is built before the module that uses it. `implementation(project(":lib"))` normally does this.
3. Run a clean build after adding the plugin to a library for the first time.
4. Use plugin 1.2.1 or later. Earlier versions missed definitions two or more `implementation` levels away and merged `@Named` providers across modules. If you turned `compileSafety` off because of false `KOIN-D001` errors, turn it back on.

## Moving to the Compiler Plugin

- **From the Constructor or Classic DSL**: apply the plugin. Your existing `singleOf(::T)` definitions and `get()` calls are checked at build time without changes. You can then replace them with `single<T>()` module by module. See [Choosing your style](/docs/intro/choosing-your-style).
- **From the KSP processor**: remove KSP and `koin-ksp-compiler`, apply the plugin, and replace the generated `.module` extensions with `startKoin<MyApp>()`. Your annotations stay the same. See [Migrating from KSP](/docs/migration/from-ksp-to-compiler-plugin).

## Related

- [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin): what the plugin does and how it works.
- [Compiler Plugin Options](/docs/reference/koin-annotations/options): every Gradle option.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): every check and error code.
- [Gradle Setup](/docs/setup/gradle): Koin libraries and the BOM.
- [Starting Koin](/docs/reference/koin-core/starting-koin): typed start APIs such as `startKoin<MyApp>()`.
