---
title: Gradle Setup
---

# Gradle Setup

This guide covers how to add Koin dependencies to your Gradle project.

## Koin BOM (Recommended)

The **Bill of Materials (BOM)** is the recommended way to manage Koin dependencies. It ensures all Koin libraries use compatible versions.

:::info
**Best Practice**: Always use the Koin BOM to avoid version conflicts between Koin libraries.
:::

### Using Version Catalogs (Recommended)

In your `gradle/libs.versions.toml`:

```toml
[versions]
koin-bom = "4.2.0"

[libraries]
koin-bom = { module = "io.insert-koin:koin-bom", version.ref = "koin-bom" }
koin-core = { module = "io.insert-koin:koin-core" }
koin-android = { module = "io.insert-koin:koin-android" }
koin-compose = { module = "io.insert-koin:koin-compose" }
koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel" }
koin-ktor = { module = "io.insert-koin:koin-ktor" }
koin-test = { module = "io.insert-koin:koin-test" }
```

In your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)  // No version needed
}
```

### Using BOM Directly

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))

    // Add dependencies without versions
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-compose")  // Works for Android & Multiplatform
}
```

## Platform-Specific Setup

### Kotlin/JVM {#kotlin}

For pure Kotlin applications:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-core")
}
```

Start Koin in your application:

```kotlin
fun main() {
    startKoin {
        modules(appModule)
    }
}
```

**Testing dependencies:**

```kotlin
dependencies {
    testImplementation("io.insert-koin:koin-test")
    testImplementation("io.insert-koin:koin-test-junit5")  // or junit4
}
```

### Android {#android}

For Android applications:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-android")
}
```

Start Koin in your Application class:

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

**Optional Android packages:**

```kotlin
dependencies {
    // Jetpack WorkManager
    implementation("io.insert-koin:koin-androidx-workmanager")

    // Navigation Graph
    implementation("io.insert-koin:koin-androidx-navigation")

    // AndroidX Startup
    implementation("io.insert-koin:koin-androidx-startup")

    // Java Compatibility
    implementation("io.insert-koin:koin-android-compat")
}
```

### Android with Jetpack Compose {#compose-android}

For Android apps using Jetpack Compose:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-compose")
    implementation("io.insert-koin:koin-compose-viewmodel")
}
```

**With Navigation:**

```kotlin
dependencies {
    // Navigation 2 (Android only)
    implementation("io.insert-koin:koin-androidx-compose-navigation")

    // Or Navigation 3
    implementation("io.insert-koin:koin-compose-navigation3")
}
```

:::info
The `koin-androidx-compose` is now covered by `koin-compose`
:::

### Compose Multiplatform {#compose}

For Compose Multiplatform projects (Android, iOS, Desktop, Web):

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-compose")
    implementation("io.insert-koin:koin-compose-viewmodel")
    implementation("io.insert-koin:koin-compose-viewmodel-navigation")
}
```

:::info
`koin-compose` includes Android support automatically. No need for separate `koin-android` in Compose Multiplatform projects.
:::

### Kotlin Multiplatform {#kotlin-multiplatform}

In your shared module's `build.gradle.kts`:

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

        androidMain.dependencies {
            implementation("io.insert-koin:koin-android")
        }
    }
}
```

### Ktor {#ktor}

For Ktor server applications:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-ktor")
    implementation("io.insert-koin:koin-logger-slf4j")
}
```

Install Koin in your Ktor application:

```kotlin
fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
```

## All Available Packages

Last version is currently: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-core)

| Package | Description |
|---------|-------------|
| `koin-core` | Core Koin library |
| `koin-core-coroutines` | Coroutines support |
| `koin-android` | Android support |
| `koin-android-compat` | Java compatibility for Android |
| `koin-androidx-navigation` | Navigation Component support |
| `koin-androidx-workmanager` | WorkManager support |
| `koin-androidx-startup` | AndroidX Startup support |
| `koin-compose` | Compose (Android & Multiplatform) |
| `koin-compose-viewmodel` | ViewModel for Compose |
| `koin-compose-viewmodel-navigation` | Navigation + ViewModel for Compose MP |
| `koin-androidx-compose` | ⚠️ Superseedeed - use `koin-compose` instead |
| `koin-androidx-compose-navigation` | Navigation 2 for Android (not KMP compatible) |
| `koin-compose-navigation3` | Navigation 3 |
| `koin-ktor` | Ktor server support |
| `koin-logger-slf4j` | SLF4J logging |
| `koin-test` | Testing utilities |
| `koin-test-junit4` | JUnit 4 support |
| `koin-test-junit5` | JUnit 5 support |
| `koin-android-test` | Android instrumented testing |

## Direct Version Specification

If you prefer not to use the BOM:

```kotlin
dependencies {
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-android:$koin_version")
}
```

:::note
This approach requires manually keeping all dependencies synchronized. **Using the BOM is strongly recommended.**
:::

## Next Steps

- **[Compiler Plugin Setup](/docs/setup/compiler-plugin)** - Add compile-time safety
- **[Starting Koin](/docs/reference/koin-core/starting-koin)** - Configure your application
- **[Tutorials](/docs/quickstart/kotlin)** - Build your first app
