---
title: Compiler Plugin Setup
---

# Koin Compiler Plugin Setup

The **Koin Compiler Plugin** is the recommended approach for all new Kotlin 2.x projects. It provides auto-wiring, compile-time safety, and a cleaner DSL syntax.

## What is the Compiler Plugin?

The Koin Compiler Plugin is a **native Kotlin Compiler Plugin (K2)** that:

- Auto-detects constructor dependencies
- Provides compile-time analysis
- Works with both DSL and Annotations
- Generates no visible files

See [Introduction to Koin Compiler Plugin](/docs/intro/koin-compiler-plugin) for details on features and benefits.

:::tip IDE Plugin
Install the **[Koin IDE Plugin](https://plugins.jetbrains.com/plugin/26131-koin-dependency-injection-official-)** for Android Studio & IntelliJ IDEA — code navigation between definitions and injection points, live safety checks, and dependency graph visualization.
:::

## Requirements

- **Kotlin 2.3+** (K2 compiler)
- **Gradle 8.x+**

## Setup

### Step 1: Add Koin to Version Catalog

First, check latest versions:
- Koin: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-core?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-core)
- Koin Compiler Plugin: [![Maven Central](https://img.shields.io/maven-central/v/io.insert-koin/koin-compiler-plugin?label=latest)](https://mvnrepository.com/artifact/io.insert-koin/koin-compiler-plugin)

Then, in your `gradle/libs.versions.toml`:

```toml
[versions]
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

### Step 2: Configure Settings

In your `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
```

### Step 3: Apply the Plugin

In your module's `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)  // For annotation support
}
```

:::tip
**Using `@KoinViewModel` or `@KoinWorker`?** Those annotations need their runtime DSL to be on the classpath:

- `@KoinViewModel` → `implementation("io.insert-koin:koin-core-viewmodel")`
- `@KoinWorker` → `implementation("io.insert-koin:koin-android-workmanager")`

The compiler will fail with a clear error naming the missing artifact if you add the annotation without its runtime — no more silent `NoDefinitionFoundException` at startup.
:::

## Complete Example

### gradle/libs.versions.toml

```toml
[versions]
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

### settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
```

### build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
}
```

## Using the Compiler Plugin

### DSL Style

Import from the compiler plugin package:

```kotlin
import org.koin.plugin.module.dsl.*
import org.koin.dsl.module

val appModule = module {
    single<Database>()
    single<ApiClient>()
    single<UserRepository>()
    viewModel<UserViewModel>()
}
```

:::info
The Compiler Plugin DSL is in package **`org.koin.plugin.module.dsl`**. Classic DSL remains in `org.koin.dsl`.
:::

### Annotation Style

Use annotations on your classes:

```kotlin
@Singleton
class Database

@Singleton
class ApiClient

@Singleton
class UserRepository(
    private val database: Database,
    private val apiClient: ApiClient
)

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()

@Module
@ComponentScan("com.myapp")
class AppModule
```

### Starting Koin with Annotations

With the Compiler Plugin, use typed APIs to start Koin - **no generated code needed**:

```kotlin
@KoinApplication
@ComponentScan("com.myapp")
class MyApp

// Start Koin with typed API
startKoin<MyApp>()

// Or with additional configuration
startKoin<MyApp> {
    androidContext(this@MyApplication)
    printLogger()
}
```

**Available typed APIs:**

| API | Description |
|-----|-------------|
| `startKoin<T>()` | Start Koin globally with application T |
| `startKoin<T> { }` | Start Koin with application T and configuration block |
| `koinApplication<T>()` | Create isolated KoinApplication with T |
| `koinConfiguration<T>()` | Create KoinConfiguration from T (for Compose KoinApplication, Ktor, etc.) |

Where `T` is a class annotated with `@KoinApplication`.

**Loading individual modules:**

You can also load `@Module` classes directly without `@KoinApplication`, using `module<T>()` or `modules()`:

```kotlin
startKoin {
    module<NetworkModule>()                              // Load a single module
    modules(DataModule::class, CacheModule::class)       // Load multiple modules
}
```

| API | Description |
|-----|-------------|
| `module<T>()` | Load a single `@Module` class into the KoinApplication |
| `modules(vararg KClass)` | Load multiple `@Module` classes into the KoinApplication |

Where `T` / each `KClass` is a class annotated with `@Module`. This is useful for tests or when mixing annotation and DSL modules:

```kotlin
// In tests
@get:Rule
val koinTestRule = KoinTestRule.create {
    module<NetworkModule>()
}
```

## Configuration Options

Configure the compiler plugin in your `build.gradle.kts`:

```kotlin
koinCompiler {
    userLogs = true
    debugLogs = false
    unsafeDslChecks = true
}
```

### Available Options

| Option | Description | Default |
|--------|-------------|---------|
| `compileSafety` | Compile-time dependency validation (A2/A3/A4) | `true` |
| `skipDefaultValues` | Skip injection for parameters with Kotlin default values | `true` |
| `userLogs` | Enable logs for component detection and DSL/annotation processing | `false` |
| `debugLogs` | Enable verbose debug logs for internal plugin processing | `false` |
| `unsafeDslChecks` | Validate that `create()` calls inside lambdas are the only instruction | `true` |

:::tip
Set `userLogs = true` during development to see which components are detected and processed by the plugin.
:::

## Compile-Time Safety

The Koin Compiler Plugin provides **compile-time dependency verification** — validating that all your dependencies can be resolved at build time rather than failing at runtime. This is enabled by default.

```kotlin
koinCompiler {
    compileSafety = true       // Enabled by default
    skipDefaultValues = true   // Enabled by default
}
```

The plugin validates your graph at three levels: per-module (A2), full graph at `startKoin<T>()` (A3), and every call site (A4). See [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety) for full details.

## Multi-Module Projects

For projects with multiple Gradle modules:

### Library Module

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
// feature/src/main/kotlin/FeatureModule.kt
@Module
@ComponentScan("com.myapp.feature")
class FeatureModule
```

### App Module

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(project(":feature"))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
}
```

```kotlin
// app/src/main/kotlin/MyModule.kt
@Module
@Configuration
class MyModule

// app/src/main/kotlin/MyApp.kt
@KoinApplication
class MyApp

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<MyApp>()
    }
}
```

Use `@KoinApplication` for the main application class with typed startup APIs.

## Kotlin Multiplatform

The Compiler Plugin works with KMP projects:

```kotlin
// shared/build.gradle.kts
plugins {
    id("org.jetbrains.kotlin.multiplatform")
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

## Troubleshooting

### Plugin Not Found

Ensure the plugin is in your plugin repositories:

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
```

### Kotlin Version Mismatch

The Compiler Plugin requires Kotlin 2.3.20+. Check your Kotlin version:

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.3.20"  // Requires 2.3.20+
}
```

### Import Errors

Make sure you're importing from the correct package:

```kotlin
// Compiler Plugin DSL
import org.koin.plugin.module.dsl.*

// Classic DSL
import org.koin.dsl.*
```

### Incremental Compilation & Cache Issues

Like other Kotlin compiler plugins (e.g., Compose Compiler, Metro), the Koin Compiler Plugin operates at the IR level. Kotlin's incremental compilation may sometimes produce **stale or inconsistent results** after certain changes:

**Symptoms:**
- Compile safety errors that shouldn't appear (false positives)
- Missing compile safety errors after removing a definition (false negatives)
- `NoSuchMethodError` or `ClassNotFoundException` at runtime after refactoring

**When this typically happens:**
- Changing annotations on a class (`@Single` → `@Factory`, adding/removing `@Named`)
- Moving classes between packages (affects `@ComponentScan` discovery)
- Changing module `includes` or `@Configuration` labels
- Adding/removing definitions in a library module that another module depends on

**Fix:** Run a clean build:

```bash
./gradlew clean build
```

Or in Android Studio: **Build → Clean Project**, then **Build → Rebuild Project**.

:::tip
If you encounter unexpected compile safety errors after a refactor, try a clean build first. This is a known limitation of incremental compilation with compiler plugins — not specific to Koin.
:::

### Compile Safety False Positives in Multi-Module Projects

If the plugin reports a missing dependency that exists in a library module, ensure:

1. **The library module also applies the Koin Compiler Plugin** — it generates hint functions that downstream modules read
2. **The library builds before the consuming module** — Gradle usually handles this via `implementation(project(":lib"))`, but check your task dependencies
3. **Run a clean build** after adding the plugin to a library module for the first time

## Migration

### From Classic DSL

1. Add the Compiler Plugin
2. Update imports to `org.koin.plugin.module.dsl.*`
3. Replace `single { Class(get() ...) }` or `singleOf(::Class)` with `single<Class>()`

See the [DSL Style](#dsl-style) reference above for the compile-time safe syntax.

### From the KSP Processor (`koin-ksp-compiler`)

1. Remove KSP plugin and `koin-ksp-compiler` dependency
2. Add the Koin Compiler Plugin
3. Update `startKoin { modules(...) }` to `startKoin<MyApp>()`
4. **Your annotations stay the same!** The `koin-annotations` library remains — only the processor changes.

See **[Migrating from KSP to Compiler Plugin](/docs/migration/from-ksp-to-compiler-plugin)** for the complete guide.

## Next Steps

- **[DSL Reference](/docs/reference/dsl-reference)** - Complete DSL documentation
- **[Annotations Reference](/docs/reference/annotations-reference)** - Complete annotations documentation
- **[Starting Koin](/docs/reference/koin-core/starting-koin)** - Configure your application
