---
title: Gradle Setup
description: Add Koin to a Gradle build with the BOM or a version catalog, and find the artifacts for Kotlin, Android, Compose, Multiplatform, Ktor and tests.
---

This page lists the Gradle dependencies for each platform and every published Koin artifact. Samples use the Kotlin DSL (`build.gradle.kts`) and a version catalog. To add compile-time checks and the Compiler Plugin DSL, also follow [Compiler Plugin Setup](/docs/setup/compiler-plugin).

## Versions

All Koin libraries, including `koin-annotations`, share one version. The current stable version is 4.2.2.

- Koin: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-core)

All artifacts are on [Maven Central](https://central.sonatype.com/search?q=io.insert-koin) under the group `io.insert-koin`. Version history and upgrade notes are in [Releases](/docs/support/releases).

## Koin BOM

The Koin BOM (bill of materials) pins every Koin library to the same version. You declare the BOM version once and add Koin libraries without a version. When you update the BOM, all Koin libraries move together.

In `gradle/libs.versions.toml`:

```toml
[versions]
koin = "4.2.2"

[libraries]
koin-bom = { module = "io.insert-koin:koin-bom", version.ref = "koin" }
koin-core = { module = "io.insert-koin:koin-core" }
koin-android = { module = "io.insert-koin:koin-android" }
koin-compose = { module = "io.insert-koin:koin-compose" }
koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel" }
koin-compose-viewmodel-navigation = { module = "io.insert-koin:koin-compose-viewmodel-navigation" }
koin-ktor = { module = "io.insert-koin:koin-ktor" }
koin-logger-slf4j = { module = "io.insert-koin:koin-logger-slf4j" }
koin-annotations = { module = "io.insert-koin:koin-annotations" }
koin-test = { module = "io.insert-koin:koin-test" }
koin-test-junit4 = { module = "io.insert-koin:koin-test-junit4" }
```

In your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
}
```

Without a version catalog:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:4.2.2"))
    implementation("io.insert-koin:koin-android")
    testImplementation("io.insert-koin:koin-test-junit4")
}
```

A version written on one dependency overrides the BOM for that dependency only. Avoid it unless you need a fix from a newer release of one library.

### Without the BOM

You can also put the version on each library. This is what the [Koin samples](https://github.com/InsertKoinIO/koin-getting-started) do, and it works the same in Kotlin Multiplatform source sets:

```toml
[versions]
koin = "4.2.2"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
```

Keep a single `koin` version for all Koin libraries: mixing versions is not supported.

## Kotlin/JVM {#kotlin}

```kotlin
dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
}
```

Then start Koin in `main`. See [Starting Koin](/docs/reference/koin-core/starting-koin) and the [Kotlin tutorial](/docs/quickstart/kotlin).

## Android {#android}

```kotlin
dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
}
```

`koin-android` includes `koin-core` and the ViewModel support. Start Koin in your `Application` class: see [Start Koin on Android](/docs/reference/koin-android/start).

Optional Android libraries:

| Artifact | Adds |
|----------|------|
| `koin-androidx-workmanager` | WorkManager workers: see [WorkManager](/docs/reference/koin-android/workmanager) |
| `koin-androidx-navigation` | ViewModels scoped to a Navigation graph (Fragments) |
| `koin-androidx-startup` | Start Koin with AndroidX App Startup |
| `koin-android-compat` | Java helpers to inject from Java classes |
| `koin-dagger-bridge` | Use Dagger components from Koin during a migration |

## Android with Jetpack Compose {#compose-android}

```kotlin
dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
}
```

`koin-androidx-compose` is an Android-only artifact that brings `koin-compose` and `koin-compose-viewmodel` in one dependency. Either setup works.

For navigation, add one of:

| Artifact | Navigation library |
|----------|--------------------|
| `koin-androidx-compose-navigation` | Navigation Compose 2, Android only |
| `koin-compose-viewmodel-navigation` | Navigation Compose 2, multiplatform |
| `koin-compose-navigation3` | Navigation 3: see [Navigation 3](/docs/reference/koin-compose/navigation3) |

See [Koin for Compose](/docs/reference/koin-compose/compose) for the APIs.

## Compose Multiplatform {#compose}

In the shared module, add the Compose libraries to `commonMain`. In multiplatform source sets, use catalog entries that carry the version (see [Without the BOM](#without-the-bom)):

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation) // if you use Navigation Compose
        }
    }
}
```

On Android, `koin-compose` brings `koin-android`, so you don't need to add it. See the [Compose Multiplatform tutorial](/docs/quickstart/cmp).

## Kotlin Multiplatform {#kotlin-multiplatform}

In the shared module's `build.gradle.kts`, with catalog entries that carry the version (see [Without the BOM](#without-the-bom)):

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.koin.test)
        }
    }
}
```

`koin-core` supports JVM, Android, Apple targets (iOS, macOS, tvOS, watchOS), Linux, Windows, JS and Wasm (`wasmJs`). Project structure and platform-specific definitions are covered in [Kotlin Multiplatform Setup](/docs/reference/koin-core/kmp-setup).

## Ktor {#ktor}

```kotlin
dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
}
```

Install the Koin plugin in your Ktor application: see [Koin for Ktor](/docs/reference/koin-ktor/ktor).

## Testing {#testing}

```kotlin
dependencies {
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4) // or koin-test-junit5
}
```

For Android instrumented tests, add `koin-android-test` to `androidTestImplementation`. See [Injecting in Tests](/docs/reference/koin-test/testing) and [Android Instrumented Testing](/docs/reference/koin-android/instrumented-testing).

## All artifacts

| Artifact | Platforms | Description |
|----------|-----------|-------------|
| `koin-bom` | All | Bill of materials for every artifact below |
| `koin-core` | All | Container, DSL, scopes, Compiler Plugin DSL |
| `koin-core-coroutines` | All | Lazy modules and background loading |
| `koin-core-viewmodel` | All | ViewModel definitions for Android and Compose Multiplatform |
| `koin-annotations` | All | Annotations for the Compiler Plugin (`@Singleton`, `@Module`, `@Named`, ...) |
| `koin-android` | Android | Android context, Activity and Fragment APIs, ViewModel |
| `koin-android-compat` | Android | Java compatibility helpers |
| `koin-androidx-navigation` | Android | Navigation graph scoped ViewModels |
| `koin-androidx-workmanager` | Android | WorkManager integration |
| `koin-androidx-startup` | Android | AndroidX App Startup integration |
| `koin-dagger-bridge` | Android | Access Dagger components from Koin |
| `koin-compose` | All Compose targets | `koinInject()`, `KoinApplication` and Compose context |
| `koin-compose-viewmodel` | All Compose targets | `koinViewModel()` |
| `koin-compose-viewmodel-navigation` | All Compose targets | ViewModels scoped to a Navigation Compose back stack entry |
| `koin-compose-navigation3` | All Compose targets | Navigation 3 integration |
| `koin-androidx-compose` | Android | Bundle of `koin-compose` and `koin-compose-viewmodel` for Android |
| `koin-androidx-compose-navigation` | Android | Navigation Compose 2 for Android |
| `koin-ktor` | JVM, JS, Wasm, iOS, macOS (Ktor server targets) | Ktor server plugin |
| `koin-logger-slf4j` | JVM | SLF4J logger |
| `koin-test` | All | Test API: `KoinTest`, `verify()` |
| `koin-test-coroutines` | JVM | Verification for lazy modules |
| `koin-test-junit4` | JVM | JUnit 4 rules |
| `koin-test-junit5` | JVM | JUnit 5 extensions |
| `koin-android-test` | Android | Instrumented test helpers |

The Koin Compiler Plugin is a Gradle plugin, not a library: it has its own version. See [Compiler Plugin Setup](/docs/setup/compiler-plugin).

## Related

- [Compiler Plugin Setup](/docs/setup/compiler-plugin): add the Compiler Plugin and compile-time checks.
- [Setup](/docs/setup): what to install for each platform.
- [Starting Koin](/docs/reference/koin-core/starting-koin): start the container once dependencies are in place.
- [Tutorials](/docs/quickstart/kotlin): a first app on each platform.
- [Releases](/docs/support/releases): versions and upgrade notes.
