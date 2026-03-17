---
title: Koin
---

All you need to setting up Koin in your project

## Current Versions

You can find all Koin packages on [Maven Central](https://central.sonatype.com/search?q=io.insert-koin+koin-core&sort=name).

Here are the currently available Koin versions:

- Koin Stable [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core?label=stable)](https://mvnrepository.com/artifact/io.insert-koin/koin-core)
- Koin Latest [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core)](https://mvnrepository.com/artifact/io.insert-koin/koin-core)

## Koin BOM (Recommended)

:::info
**Best Practice**: Use the Koin Bill of Materials (BOM) to manage all Koin library versions consistently. This is the recommended approach for all projects.
:::

The Koin Bill of Materials (BOM) lets you manage all of your Koin library versions by specifying only the BOM's version. The BOM itself has links to the stable versions of the different Koin libraries, in such a way that they work well together. When using the BOM in your app, you don't need to add any version to the Koin library dependencies themselves. When you update the BOM version, all the libraries that you're using are automatically updated to their new versions.

### Using BOM with Version Catalogs (Recommended)

In your `gradle/libs.versions.toml`:

```toml
[versions]
koin-bom = "4.1.1"  # Stable version

[libraries]
koin-bom = { module = "io.insert-koin:koin-bom", version.ref = "koin-bom" }
koin-core = { module = "io.insert-koin:koin-core" }
koin-android = { module = "io.insert-koin:koin-android" }
koin-androidx-compose = { module = "io.insert-koin:koin-androidx-compose" }
koin-compose = { module = "io.insert-koin:koin-compose" }
koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel" }
koin-ktor = { module = "io.insert-koin:koin-ktor" }
koin-test = { module = "io.insert-koin:koin-test" }
```

In your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    // Add other Koin dependencies without versions
}
```

### Using BOM Without Version Catalogs

```kotlin
dependencies {
    // Declare koin-bom version
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))

    // Declare the koin dependencies without versions
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-core-coroutines")
    implementation("io.insert-koin:koin-androidx-workmanager")

    // If you need to specify a different version for a specific dependency
    implementation("io.insert-koin:koin-androidx-navigation:1.2.3-alpha03")

    // Works with test libraries too!
    testImplementation("io.insert-koin:koin-test-junit4")
    testImplementation("io.insert-koin:koin-android-test")
}
```

## Platform-Specific Setup

### Kotlin

Add the Koin BOM and `koin-core` dependency to your application:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-core")
}
```

Or specify the exact dependency version (not recommended):

```kotlin
dependencies {
    implementation("io.insert-koin:koin-core:$koin_version")
}
```

You are now ready to start Koin:

```kotlin
fun main() {
    startKoin {
        modules(...)
    }
}
```

If you need testing capacity:

```kotlin
dependencies {
    // Koin Test features
    testImplementation("io.insert-koin:koin-test:$koin_version")
    // Koin for JUnit 4
    testImplementation("io.insert-koin:koin-test-junit4:$koin_version")
    // Koin for JUnit 5
    testImplementation("io.insert-koin:koin-test-junit5:$koin_version")
}
```

:::info
**Next Steps**: Continue with [Kotlin App Tutorial](/docs/quickstart/kotlin) or explore [Core Features](/docs/reference/koin-core/dsl).
:::

### Android

Add `koin-android` dependency to your Android application:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-android")
}
```

You are now ready to start Koin in your `Application` class:

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
```

If you need extra features, add the following packages:

```kotlin
dependencies {
    // Java Compatibility
    implementation("io.insert-koin:koin-android-compat")
    // Jetpack WorkManager
    implementation("io.insert-koin:koin-androidx-workmanager")
    // Navigation Graph
    implementation("io.insert-koin:koin-androidx-navigation")
    // App Startup - Start Koin with AndroidX Startup
    implementation("io.insert-koin:koin-androidx-startup")
}
```

:::info
**Next Steps**: Continue with [Android App Tutorial](/docs/quickstart/android-viewmodel) or see [Starting Koin on Android](/docs/reference/koin-android/start) for detailed integration.
:::

### Jetpack Compose or Compose Multiplatform

For **Compose Multiplatform** (Android, iOS, Desktop, Web), add these dependencies:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-compose")
    implementation("io.insert-koin:koin-compose-viewmodel")
    implementation("io.insert-koin:koin-compose-viewmodel-navigation")
}
```

For **pure Android Jetpack Compose**, you can use:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-androidx-compose-navigation")
}
```

For **Navigation 3 integration** (experimental):

```kotlin
dependencies {
    // Navigation 3 support (alpha)
    implementation("io.insert-koin:koin-compose-navigation3")
}
```

:::warning
Navigation 3 is in alpha. See [Navigation 3 Integration](/docs/reference/koin-compose/navigation3) for details.
:::

:::info
**Next Steps**: Continue with [Compose Tutorial](/docs/quickstart/android-compose) or see [Koin Compose](/docs/reference/koin-compose/compose) for detailed integration.
:::

### Kotlin Multiplatform

In your `shared/build.gradle.kts`, add `koin-core` dependency to commonMain:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(platform("io.insert-koin:koin-bom:$koin_version"))
            implementation("io.insert-koin:koin-core")
        }

        commonTest.dependencies {
            implementation("io.insert-koin:koin-test")
        }
    }
}
```

:::info
**Next Steps**: See [Kotlin Multiplatform with Koin](/docs/reference/koin-mp/kmp) for platform-specific setup, expect/actual patterns, and architecture guidance.
:::

### Ktor

Add `koin-ktor` dependency to your Ktor application:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    // Koin for Ktor
    implementation("io.insert-koin:koin-ktor")
    // SLF4J Logger
    implementation("io.insert-koin:koin-logger-slf4j")
}
```

You are now ready to install Koin feature into your Ktor application:

```kotlin
fun Application.main() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
```

:::info
**Next Steps**: Continue with [Ktor App Tutorial](/docs/quickstart/ktor) or see [Ktor Integration](/docs/reference/koin-ktor/ktor) for detailed setup.
:::

## Alternative: Direct Version Specification

If you prefer not to use the BOM, you can specify versions directly for each dependency:

```kotlin
dependencies {
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-android:$koin_version")
    implementation("io.insert-koin:koin-compose:$koin_version")
}
```

:::note
This approach requires manually keeping all Koin dependencies synchronized to compatible versions. **Using the BOM is strongly recommended** to avoid version conflicts.
:::
