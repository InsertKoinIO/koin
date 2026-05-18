---
title: KSP Processor Setup (Deprecated)
---

# KSP Processor Setup for Koin Annotations

:::warning
**`koin-ksp-compiler` is deprecated.** The KSP-based processor for Koin Annotations is replaced by the [Koin Compiler Plugin](/docs/setup/compiler-plugin). **Koin Annotations itself is not deprecated** — the `koin-annotations` library is now part of the main Koin project and remains fully supported. Only the processor changes.
:::

:::info
**Your annotations stay the same** — only the build setup changes. See [Migration Guide](#migration-to-koin-compiler-plugin) below.
:::

## Why Migrate?

| Aspect | KSP Processor (`koin-ksp-compiler`) | Koin Compiler Plugin |
|--------|-------------------------------------|----------------------|
| **Generated files** | Visible in build/ | None |
| **Build speed** | ⚠️ Slower | Faster |
| **KMP setup** | ⚠️ Complex | Simple |
| **Future support** | ⚠️ Deprecated | ✅ Active development |
| **Your code** | ⚠️ Use generated extensions | Use Kotlin Compiler plugin dedicated API |

## When to Use the KSP Processor (Temporary)

Only use `koin-ksp-compiler` if you're:
- Stuck on Kotlin 1.x (upgrade recommended)
- Mid-migration and can't switch yet
- Have specific KSP requirements

## Current KSP Processor Setup (Reference)

If you must use the KSP processor, here's the setup:

### Gradle Setup

```kotlin
// build.gradle.kts
plugins {
    id("com.google.devtools.ksp") version "$ksp_version"
}

dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-annotations:$koin_ksp_version")
    ksp("io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
}
```

### Version Compatibility

| Koin Annotations | KSP Version | Kotlin Version |
|------------------|-------------|----------------|
| 1.4 | 1.9 | 1.9 |
| 2.0 | 2.0 | 2.0 |
| 2.1/2.2 | 2.1/2.2 | 2.1/2.2 |
| 2.3 | 2.3 | Independant |

### Basic Usage

```kotlin
@Single
class MyComponent

@Module
class MyModule

// Import generated extensions
import org.koin.ksp.generated.*

fun main() {
    startKoin {
        modules(MyModule().module)
    }
}
```

### KSP Options

```kotlin
// build.gradle.kts
ksp {
    arg("KOIN_CONFIG_CHECK", "true")  // Enable compile-time validation
}
```

:::tip
This KSP-based compile-time check has been replaced by native compile-time safety in the **Koin Compiler Plugin**. See [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety) and [Compiler Plugin Setup](/docs/setup/compiler-plugin).
:::

### KMP Setup (Complex)

```kotlin
// shared/build.gradle.kts
plugins {
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.insert-koin:koin-core:$koin_version")
            implementation("io.insert-koin:koin-annotations:$koin_ksp_version")
        }
    }
}

dependencies {
    // Per-platform KSP configuration required
    add("kspAndroid", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosX64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosArm64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosSimulatorArm64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
}
```

## Migration to Koin Compiler Plugin

### Step 1: Update Kotlin

Ensure you're using Kotlin 2.3.20+:

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.3.20-Beta1" // or earlier
}
```

### Step 2: Remove KSP

Remove KSP plugin and dependencies:

```kotlin
// Remove these:
plugins {
    // id("com.google.devtools.ksp")  // Remove
}

dependencies {
    // ksp("io.insert-koin:koin-ksp-compiler:...")  // Remove
}
```

### Step 3: Add Compiler Plugin

See the **[Compiler Plugin Setup Guide](/docs/setup/compiler-plugin)** for detailed instructions.

### Step 4: Keep Your Code

**Your annotations stay exactly the same 👍**

```kotlin
// This code doesn't change!
@Singleton
class MyService(val repository: MyRepository)

@Factory
class MyRepository

@KoinViewModel
class MyViewModel(val service: MyService)

@Module
@ComponentScan("com.myapp")
class AppModule
```

### Step 5: Update Koin Startup

With the Compiler Plugin, **no generated code is used**. Replace generated extensions with typed API:

**Before (KSP):**
```kotlin
import org.koin.ksp.generated.*

startKoin {
    modules(AppModule().module)  // Uses generated extension
}
```

**After (Compiler Plugin):**
```kotlin
@KoinApplication
@ComponentScan("com.myapp")
class MyApp

// Use typed API - no generated code!
startKoin<MyApp>()

// Or with configuration
startKoin<MyApp> {
    androidContext(this@MyApplication)
}
```

Available typed APIs:
- `startKoin<T>()` - Start Koin globally with application T
- `koinApplication<T>()` - Create isolated KoinApplication with T
- `koinConfiguration<T>()` - Create KoinConfiguration from T (for Compose KoinApplication, Ktor, etc.)

Where `T` is a class annotated with `@KoinApplication`.

### Step 6: Clean Up

Delete generated files:

```bash
rm -rf build/generated/ksp
```

Rebuild your project.

### What Stays the Same

| Annotation | Status |
|------------|--------|
| `@Singleton` / `@Single` | ✅ Same |
| `@Factory` | ✅ Same |
| `@Scoped` | ✅ Same |
| `@KoinViewModel` | ✅ Same |
| `@KoinWorker` | ✅ Same |
| `@Named` | ✅ Same |
| `@InjectedParam` | ✅ Same |
| `@Property` | ✅ Same |
| `@Module` | ✅ Same |
| `@ComponentScan` | ✅ Same |
| `@Configuration` | ✅ Same |

### What Changes

| Aspect | KSP Processor | Koin Compiler Plugin |
|--------|---------------|----------------------|
| Build plugin | `com.google.devtools.ksp` | `io.insert-koin.compiler.plugin` |
| Dependencies | `ksp("io.insert-koin:koin-ksp-compiler")` | None required (plugin only) |
| `koin-annotations` version | Separate (`koin-ksp` version) | Same as main Koin version |
| Generated files | Visible in `build/` | None |
| Koin startup | `modules(AppModule().module)` | `startKoin<MyApp>()` |
| KMP setup | Per-platform KSP | Just plugin |

## Timeline

:::warning
The `koin-ksp-compiler` processor will be removed in a future Koin version. We recommend migrating to the Koin Compiler Plugin as soon as possible. The `koin-annotations` library and your `@Singleton` / `@Factory` / `@Module` annotations are not going away — they're now processed by the Koin Compiler Plugin.
:::

## Help

If you encounter issues during migration:
- Check [Troubleshooting](/docs/reference/troubleshooting)
- Ask on [Slack](https://kotlinlang.slack.com/messages/koin/)
- Open an issue on [GitHub](https://github.com/InsertKoinIO/koin)

## Next Steps

- **[Migration Guide](/docs/migration/from-ksp-to-compiler-plugin)** - Step-by-step migration to Compiler Plugin
- **[Compiler Plugin Setup](/docs/setup/compiler-plugin)** - Complete setup guide
