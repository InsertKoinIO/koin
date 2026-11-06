---
title: Migrating from KSP to the Compiler Plugin
description: Move a Koin Annotations project from the deprecated KSP processor to the Koin Compiler Plugin, step by step, with a checklist.
---

This guide moves a Koin Annotations project from the KSP processor (`koin-ksp-compiler`) to the Koin Compiler Plugin. Your annotated classes stay the same: only the Gradle configuration, the way you start Koin, and a few imports change. For what the Compiler Plugin is and how it works, see [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin).

## What changes

| Aspect | KSP processor | Compiler Plugin |
|--------|---------------|-----------------|
| Processing | KSP, a separate step before compilation | Inside the Kotlin K2 compiler |
| Generated files | Visible in `build/generated/ksp` | None, code is generated in the compiler |
| KMP setup | One KSP configuration per target | One plugin for all targets |
| `koin-annotations` version | Separate version (`koin-ksp`) | Same version as `koin-core` |
| Starting Koin | `modules(AppModule().module)` | `startKoin<MyApp>()` |
| Compile-time checks | `KOIN_CONFIG_CHECK` option | On by default, full graph and call sites |
| Status | Deprecated | Active development |

## Requirements

- Kotlin 2.3.20 or later (K2 compiler).
- Gradle 8 or later.
- Koin 4.2 or later.

## Step 1: Update Kotlin

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.3.20"
}
```

## Step 2: Update the version catalog

Before (KSP):

```toml
[versions]
koin = "4.0.0"
koin-ksp = "2.0.0"
ksp = "2.0.0-1.0.22"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin-ksp" }
koin-ksp-compiler = { module = "io.insert-koin:koin-ksp-compiler", version.ref = "koin-ksp" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

After (Compiler Plugin):

```toml
[versions]
koin = "4.2.0"
koin-plugin = "1.2.1"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

`koin-annotations` is now part of the main Koin project and uses the `koin-core` version. Check the latest plugin version in [Compiler Plugin Setup](/docs/setup/compiler-plugin).

## Step 3: Update the build configuration

Before (KSP):

```kotlin
plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}
```

After (Compiler Plugin):

```kotlin
plugins {
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
}

// optional
koinCompiler {
    userLogs = true // log detected components
}
```

Remove the `ksp { }` block: compile-time checks are on by default. All plugin options are in [Compiler Plugin Options](/docs/reference/koin-annotations/options).

### Kotlin Multiplatform

With KSP, each target needs its own processor configuration:

```kotlin
dependencies {
    add("kspAndroid", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosX64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosArm64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    add("kspIosSimulatorArm64", "io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
}
```

With the Compiler Plugin, apply the plugin once in the shared module and remove all `ksp...` entries:

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

## Step 4: Update how Koin starts

KSP generates a `.module` extension for each `@Module` class. The Compiler Plugin has no such extension: you declare a `@KoinApplication` class and start it with a typed API.

Before (KSP):

```kotlin
import org.koin.ksp.generated.*

@Module
@ComponentScan("com.myapp")
class AppModule

fun main() {
    startKoin {
        modules(AppModule().module)
    }
}
```

After (Compiler Plugin):

```kotlin
import org.koin.plugin.module.dsl.startKoin

@Module
@ComponentScan("com.myapp")
class AppModule

@KoinApplication(modules = [AppModule::class])
class MyApp

fun main() {
    startKoin<MyApp>()
}
```

On Android, pass the usual options in the lambda:

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

The other typed APIs are `koinApplication<T>()`, `koinConfiguration<T>()` and `withConfiguration<T>()` inside an existing `startKoin { }` or test rule. To load a single module class, use `module<AppModule>()` inside a plain `startKoin { }`. See [Starting Koin](/docs/reference/koin-core/starting-koin).

## Step 5: Update imports

`@KoinViewModel` moved to the core annotation package:

```kotlin
// before (KSP)
import org.koin.android.annotation.KoinViewModel

// after (Compiler Plugin)
import org.koin.core.annotation.KoinViewModel
```

Remove every `import org.koin.ksp.generated.*`.

## Step 6: Clean and rebuild

Delete the files generated by KSP, then rebuild:

```bash
rm -rf build/generated/ksp
./gradlew clean build
```

The first build runs the full compile-time check. A missing definition that KSP did not report now fails the build: see [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety) for the error messages and for `@Provided`.

## What stays the same

Your annotated classes don't change:

```kotlin
@Singleton
class UserRepository(private val database: Database)

@Factory
class GetUserUseCase(private val repository: UserRepository)

@KoinViewModel
class UserViewModel(private val useCase: GetUserUseCase) : ViewModel()
```

Every annotation is listed in the [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory).

## What you can use after migrating

These features need the Compiler Plugin:

- Top-level functions: `@Singleton fun database(): Database` is found by `@ComponentScan` like a class (see [Definitions](/docs/reference/koin-core/definitions#classes-you-dont-own)).
- Module discovery: modules tagged `@Configuration` in any Gradle module are loaded by `@KoinApplication` without listing them.
- Configuration labels: `@Configuration("test")` with `@KoinApplication(configurations = ["test"])` loads only the modules with that label (see [Modules](/docs/reference/koin-core/modules)).
- Compiler Plugin DSL: if you also have DSL modules, you can switch them to the Compiler Plugin DSL:

| Constructor / Classic DSL | Compiler Plugin DSL |
|---------------------------|---------------------|
| `singleOf(::MyService)` | `single<MyService>()` |
| `factoryOf(::MyRepository)` | `factory<MyRepository>()` |
| `viewModelOf(::MyViewModel)` | `viewModel<MyViewModel>()` |
| `scopedOf(::MyScoped)` | `scoped<MyScoped>()` |
| `workerOf(::MyWorker)` | `worker<MyWorker>()` |
| `single { build(get()) }` | `single { create(::build) }` |

The Compiler Plugin DSL is in `org.koin.plugin.module.dsl`. The Constructor DSL stays in `org.koin.core.module.dsl` and the Classic DSL in `org.koin.dsl`. Both keep working and are checked by the plugin: you don't have to migrate them.

## Troubleshooting

### The build fails after removing KSP

Stale generated code is still on the classpath. Run `./gradlew clean`, delete `build/generated/ksp`, invalidate the IDE caches, then rebuild.

### Annotated classes are not detected

Turn on plugin logs to see which classes are found:

```kotlin
koinCompiler {
    userLogs = true
}
```

Check that the packages in `@ComponentScan` match the packages of your classes.

### A dependency is missing at runtime

- Check that the module is listed in `@KoinApplication(modules = [...])`, or tagged `@Configuration`.
- Check the `@ComponentScan` packages.
- For a type created outside Koin, mark it `@Provided` (see [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety)).

## Checklist

- [ ] Update Kotlin to 2.3.20 or later
- [ ] Update Koin to 4.2.0 or later
- [ ] Remove the KSP plugin
- [ ] Remove the `koin-ksp-compiler` dependency and the `ksp { }` block
- [ ] Use the main Koin version for `koin-annotations` (`io.insert-koin:koin-annotations:$koin_version`)
- [ ] Apply the Koin Compiler Plugin (`io.insert-koin.compiler.plugin`)
- [ ] Change the `@KoinViewModel` import to `org.koin.core.annotation`
- [ ] Create a `@KoinApplication` class and replace `modules(X().module)` with `startKoin<MyApp>()`
- [ ] Remove `import org.koin.ksp.generated.*`
- [ ] Optional: switch DSL modules to `org.koin.plugin.module.dsl.*` and `single<X>()`
- [ ] Clean and rebuild (`rm -rf build/generated/ksp && ./gradlew clean build`)

## Related

- [Compiler Plugin Setup](/docs/setup/compiler-plugin): install the plugin and pick its version.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): what the plugin checks and its error codes.
- [Starting Koin](/docs/reference/koin-core/starting-koin): `startKoin<T>()` and the other typed APIs.
- [Compiler Plugin Options](/docs/reference/koin-annotations/options): every Gradle option.
- [KSP Processor Setup](/docs/setup/annotations-ksp): the deprecated KSP setup, for reference.
