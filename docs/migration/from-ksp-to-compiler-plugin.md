---
title: Migrating Koin Annotations from KSP to Compiler Plugin
---

# Migrating Koin Annotations: KSP to Compiler Plugin

This guide helps you migrate your Koin Annotations project from KSP-based processing to the new Koin Compiler Plugin.

:::info Good News!
**Your annotations stay exactly the same.** Only the build configuration and Koin startup code change.
:::

## What's Different?

| Aspect | KSP Processing | Compiler Plugin |
|--------|----------------|-----------------|
| **Processing** | KSP (separate step) | K2 Compiler (integrated) |
| **Generated files** | Visible in `build/generated/ksp` | None - inline transformations |
| **Build speed** | Slower | Faster |
| **KMP setup** | Per-platform KSP configuration | Single plugin application |
| **Koin startup** | `modules(AppModule().module)` | `startKoin<MyApp>()` |
| **Future support** | Deprecated | Active development |

## Requirements

- **Kotlin 2.3+** (K2 compiler required)
- **Gradle 8.x+**

## Migration Steps

### Step 1: Update Kotlin Version

The Compiler Plugin requires Kotlin 2.3+:

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.3.20-Beta1" //2.3.20-Beta1 minimum
}
```

### Step 2: Update Version Catalog

**Before (KSP):**
```toml
[versions]
koin = "4.0.0"
koin-ksp = "2.0.0"  # Separate versioning for KSP annotations
ksp = "2.0.0-1.0.22"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin-ksp" }
koin-ksp-compiler = { module = "io.insert-koin:koin-ksp-compiler", version.ref = "koin-ksp" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

**After (Compiler Plugin):**
```toml
[versions]
koin = "4.2.0-Beta4" // or later
koin-plugin = "0.2.9" // or later

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

:::note
`koin-annotations` is now part of the main Koin project and uses the same version as `koin-core`.
:::

### Step 3: Update Build Configuration

**Before (KSP):**
```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)  // KSP version (separate versioning)
    ksp(libs.koin.ksp.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}
```

**After (Compiler Plugin):**
```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)  // Same version as koin-core
}

// Optional configuration
koinCompiler {
    userLogs = true  // Log component detection
}
```

### Step 4: Update Koin Startup

This is the main code change. The KSP approach uses generated extensions, while the Compiler Plugin uses typed APIs.

**Before (KSP):**
```kotlin
import org.koin.ksp.generated.*  // Generated extensions

@Module
@ComponentScan("com.myapp")
class AppModule

fun main() {
    startKoin {
        modules(AppModule().module)  // Generated .module extension
    }
}
```

**After (Compiler Plugin):**
```kotlin
// No generated imports needed

@Module
@ComponentScan("com.myapp")
class AppModule

@KoinApplication(modules = [AppModule::class])
class MyApp

fun main() {
    startKoin<MyApp>()  // Typed API
}
```

#### Android Example

**Before (KSP):**
```kotlin
import org.koin.ksp.generated.*

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(AppModule().module)
        }
    }
}
```

**After (Compiler Plugin):**
```kotlin
@KoinApplication(modules = [AppModule::class])
class MyApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<MyApp> {
            androidContext(this@MyApplication)
        }
    }
}
```

### Step 5: Clean Up

Remove KSP-generated files and rebuild:

```bash
rm -rf build/generated/ksp
./gradlew clean build
```

## Annotations Stay the Same

All your annotated classes remain unchanged:

```kotlin
// No changes needed!
@Singleton
class UserRepository(private val database: Database)

@Factory
class GetUserUseCase(private val repository: UserRepository)

@KoinViewModel
class UserViewModel(private val useCase: GetUserUseCase) : ViewModel()

@Module
@ComponentScan("com.myapp")
class AppModule
```

All annotations work identically. See **[Annotations Reference](/docs/reference/koin-annotations/definitions)** for the complete list.

## KMP Migration

The Compiler Plugin greatly simplifies KMP setup.

**Before (KSP) - Per-platform configuration:**
```kotlin
// shared/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.insert-koin:koin-core:$koin_version")
            implementation("io.insert-koin:koin-annotations:$koin_ksp_version")  // Separate version
        }
    }
}

dependencies {
    // KSP compiler required for each platform
    add("kspAndroid", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosX64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosArm64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosSimulatorArm64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
}
```

**After (Compiler Plugin) - Single plugin:**
```kotlin
// shared/build.gradle.kts
plugins {
    kotlin("multiplatform")
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

## Typed Startup APIs

The Compiler Plugin provides typed APIs: `startKoin<T>()`, `koinApplication<T>()`, `koinConfiguration<T>()`.

See **[Starting with Annotations](/docs/reference/koin-annotations/start)** for details.

## Configuration Labels (New)

The Compiler Plugin adds configuration labels for conditional module loading.

See **[Modules - Configuration](/docs/reference/koin-annotations/modules)** for details.

## Compiler Plugin Options

See **[Compiler Plugin Options](/docs/reference/koin-annotations/options)** for all configuration options.

## Troubleshooting

### Build fails after removing KSP

1. `./gradlew clean`
2. `rm -rf build/generated/ksp`
3. Invalidate IDE caches
4. Rebuild

### Annotations not detected

Enable logging:
```kotlin
koinCompiler {
    userLogs = true
}
```

### Missing dependencies at runtime

1. Check `@ComponentScan` packages
2. Verify modules in `@KoinApplication(modules = [...])`
3. Use `@Provided` for external dependencies

## Migration Checklist

- [ ] Update Kotlin to 2.3+
- [ ] Remove KSP plugin
- [ ] Remove `koin-ksp-compiler` dependency
- [ ] Update `koin-annotations` to main Koin version (`io.insert-koin:koin-annotations:$koin_version`)
- [ ] Add Koin Compiler Plugin
- [ ] Create `@KoinApplication` class
- [ ] Replace `modules(X().module)` with `startKoin<MyApp>()`
- [ ] Remove `import org.koin.ksp.generated.*`
- [ ] Clean and rebuild

## See Also

- **[Compiler Plugin Setup](/docs/setup/compiler-plugin)** - Complete setup guide
- **[Annotations Reference](/docs/reference/koin-annotations/start)** - All annotations
- **[KSP Setup (Deprecated)](/docs/setup/annotations-ksp)** - Legacy reference
