---
title: Koin Compiler Plugin
---

# Koin Compiler Plugin

The **Koin Compiler Plugin** is the recommended approach for all new Kotlin 2.x projects. It's a native Kotlin compiler plugin that powers both **DSL and Annotations** with auto-wiring, compile-time safety, and cleaner syntax.

## What is the Compiler Plugin?

The Koin Compiler Plugin is a **native Kotlin Compiler Plugin (K2)** - not KSP or annotation processing. It integrates directly with the Kotlin compiler to:

- **Auto-detect constructor parameters** - No manual `get()` calls needed
- **Transform code at compile-time** - Errors caught during build
- **Work with both DSL and Annotations** - Your choice of style
- **Generate no visible files** - Cleaner project structure

## Why Use the Compiler Plugin?

### 1. Safer Code

The plugin auto-detects constructor dependencies, reducing manual wiring errors:

```kotlin
// Without Compiler Plugin - easy to make mistakes
val appModule = module {
    single { UserService(get(), get(), get()) }  // Hope you got the order right!
}

// With Compiler Plugin - auto-wired
val appModule = module {
    single<UserService>()  // Plugin detects all constructor parameters
}
```

### 2. Cleaner Syntax

Less boilerplate, more readable:

| Classic DSL | Compiler Plugin DSL |
|-------------|---------------------|
| `singleOf(::MyService)` | `single<MyService>()` |
| `single { MyService(get(), get()) }` | `single<MyService>()` |
| `factoryOf(::MyRepo)` | `factory<MyRepo>()` |
| `viewModelOf(::MyVM)` | `viewModel<MyVM>()` |
| `scopedOf(::MyPresenter)` | `scoped<MyPresenter>()` |
| `workerOf(::MyWorker)` | `worker<MyWorker>()` |

### 3. Compile-Time Safety

The Koin Compiler Plugin provides **compile-time dependency verification** for both DSL and Annotations:

- **A2 — Per-Module:** validates definitions against visible scope (early feedback)
- **A3 — Full Graph:** validates the complete assembled graph at `startKoin<T>()`
- **A4 — Call-Site:** validates every `get<T>()`, `inject<T>()`, `koinViewModel<T>()` call

If it compiles, every dependency and every injection call site is satisfied. This replaces `verify()` and `checkModules()` — no runtime test harness needed.

See [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety) for full details.

### 4. DSL & Annotations - Both Equally Powerful

Use whichever style you prefer - the same plugin powers both with identical capabilities:

**DSL Style:**
```kotlin
val appModule = module {
    single<Database>()
    single<UserRepository>()
    viewModel<UserViewModel>()
}
```

:::info DSL + Parameter Annotations
When using DSL style, you still use **parameter annotations** on your classes to guide the plugin:

```kotlin
class UserPresenter(
    @InjectedParam val userId: String,      // Runtime parameter
    @Named("api") val client: ApiClient,    // Qualified dependency
    val repository: UserRepository          // Auto-resolved
)

val appModule = module {
    factory<UserPresenter>()  // Plugin reads annotations from the class
}
```

The DSL defines **where** dependencies are registered. Parameter annotations define **how** they are resolved.
:::

**Annotation Style:**
```kotlin
@Singleton
class Database

@Singleton
class UserRepository(private val database: Database)

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

## Getting Started

### Setup

Add the Compiler Plugin to your project. 

:::info
    See the **[Compiler Plugin Setup Guide](/docs/setup/compiler-plugin)** for detailed instructions.
:::

### Using the Compiler Plugin DSL

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

:::note
The Compiler Plugin DSL is in `org.koin.plugin.module.dsl`. Classic DSL remains in `org.koin.dsl`.
:::

### Using Annotations

Annotations work the same as before:

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

## How It Works

The Compiler Plugin operates in two phases:

### 1. FIR Phase (Analysis)

During the Frontend Intermediate Representation phase, the plugin:
- Analyzes your module definitions
- Detects constructor parameters
- Validates dependency declarations

### 2. IR Phase (Transformation)

During the Intermediate Representation phase, the plugin:
- Generates proper `get()` calls for each parameter
- Handles qualifiers (`@Named`)
- Handles injected parameters (`@InjectedParam`)
- Handles nullable and Lazy types

### What Gets Generated

When you write:

```kotlin
single<UserRepository>()
```

The plugin transforms it to:

```kotlin
single { UserRepository(get(), get()) }  // Parameters auto-detected
```

For more complex cases:

```kotlin
// Your code
@Singleton
class MyService(
    val required: RequiredDep,
    val optional: OptionalDep?,
    @Named("special") val named: NamedDep,
    val lazy: Lazy<LazyDep>,
    @InjectedParam val param: String
)
```

The plugin generates proper handling for each parameter type:
- Required: `get()`
- Optional: `getOrNull()`
- Named: `get(named("special"))`
- Lazy: `inject()`
- InjectedParam: `params.get()`

## Compiler Plugin DSL Reference

### Definition Types

```kotlin
import org.koin.plugin.module.dsl.*

val appModule = module {
    // Singleton - one instance
    single<MyService>()

    // Factory - new instance each time
    factory<MyPresenter>()

    // Scoped - instance per scope
    scope<MyActivity> {
        scoped<ActivityPresenter>()
    }

    // ViewModel
    viewModel<MyViewModel>()

    // Worker (Android WorkManager)
    worker<MyWorker>()
}
```

### Safe Instance Creation with `create()`

Use `create(::T)` inside a definition lambda to safely build an instance with auto-resolved constructor dependencies:

```kotlin
val appModule = module {
    single { create(::MyService) }
}
```

The compiler plugin transforms `create(::MyService)` into `MyService(get(), get(), ...)`, auto-wiring all constructor parameters.

### With Qualifiers

Use `@Named` on your classes to define qualifiers, and on parameters to specify which dependency to inject:

```kotlin
// Define implementations with @Named qualifier
@Named("local")
class LocalDatabase : Database

@Named("remote")
class RemoteDatabase : Database

// Use @Named on parameters to specify which one to inject
class SyncService(
    @Named("local") val localDb: Database,
    @Named("remote") val remoteDb: Database
)

// DSL - plugin reads @Named from classes and parameters
val appModule = module {
    single<LocalDatabase>()
    single<RemoteDatabase>()
    single<SyncService>()
}
```

You can also create custom qualifiers with `@Qualifier`:

```kotlin
@Qualifier
annotation class LocalDb

@Qualifier
annotation class RemoteDb

@LocalDb
class LocalDatabase : Database

@RemoteDb
class RemoteDatabase : Database

class SyncService(
    @LocalDb val localDb: Database,
    @RemoteDb val remoteDb: Database
)
```

### With Parameters

Use `@InjectedParam` on your class to mark parameters passed at injection time:

```kotlin
// Annotation on the class - tells the plugin how to handle this parameter
class UserPresenter(
    @InjectedParam val userId: String,    // Passed via parametersOf()
    val repository: UserRepository        // Auto-resolved by Koin
)

// DSL in module - tells Koin where to register
val appModule = module {
    factory<UserPresenter>()
}

// Usage - pass the runtime parameter
val presenter: UserPresenter = get { parametersOf("user123") }
```

### Interface Binding

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class

    // Or multiple bindings
    single<MyServiceImpl>() binds arrayOf(
        ServiceA::class,
        ServiceB::class
    )
}
```

## Annotations Reference

### Definition Annotations

| Annotation | Description |
|------------|-------------|
| `@Singleton` / `@Single` | Single instance |
| `@Factory` | New instance each time |
| `@Scoped` | Instance per scope |
| `@KoinViewModel` | Android ViewModel |
| `@KoinWorker` | Android WorkManager Worker |

### Parameter Annotations

| Annotation | Description |
|------------|-------------|
| `@Named("qualifier")` | Named qualifier |
| `@InjectedParam` | Runtime parameter (via `parametersOf()`) |
| `@Property("key")` | Koin property value |
| `@Provided` | External dependency (skip validation) |

### Module Annotations

| Annotation | Description |
|------------|-------------|
| `@Module` | Declares a Koin module |
| `@ComponentScan("package")` | Scan package for annotated classes |
| `@Configuration` | Auto-discovered module |

## Comparison: Approaches

| Approach | Status | Package | Syntax |
|----------|--------|---------|--------|
| **Compiler Plugin DSL** | Recommended | Already located in Koin **`org.koin.plugin.module.dsl`** | `single<MyService>()`, `factory<MyRepo>()`, `viewModel<MyVM>()` |
| **Compiler Plugin Annotations** | Recommended | Annotations available in **`koin-annotations`** | `@Singleton`, `@Factory`, `@KoinViewModel ` |
| **Classic DSL** | Fully Supported | `org.koin.dsl` | `singleOf(::MyService)`, `single { MyService(get()) }`, `viewModelOf(::MyVM)` |
| **KSP Processor** | Deprecated | `koin-ksp-compiler` | Legacy processor for Koin Annotations — same annotations, **Migrate to Compiler Plugin ⚠️** |

### Compiler Plugin DSL (Recommended)

- Auto-detects dependencies
- Compile-time analysis
- Cleanest syntax

### Compiler Plugin Annotations (Recommended)

- Auto-detects dependencies
- Compile-time analysis
- Familiar annotation style

### Classic DSL (Fully Supported)

- Works with any Kotlin version
- Full control over wiring
- Can migrate to Plugin DSL when ready

### KSP Processor `koin-ksp-compiler` (Deprecated)

- The `koin-annotations` library is **not deprecated** — it's now part of the Koin project
- Only the legacy KSP-based processor (`koin-ksp-compiler`) is deprecated
- Migrate to the Koin Compiler Plugin — your annotations stay the same
- `koin-ksp-compiler` will be removed in a future Koin version

## Migration

### From Classic DSL

If you're using classic DSL, migration is optional but recommended:

1. Add the Compiler Plugin to Gradle
2. Update imports to `org.koin.plugin.module.dsl.*`
3. Replace `singleOf(::Class)` with `single<Class>()`
4. Remove manual `get()` calls

See the [Compiler Plugin DSL reference](/docs/setup/compiler-plugin#dsl-style) for the compile-time safe syntax.

### From the KSP Processor (`koin-ksp-compiler`)

If you're using Koin Annotations with the legacy KSP processor, migration is recommended now:

1. Update Kotlin to 2.x
2. Replace `koin-ksp-compiler` with the Koin Compiler Plugin
3. **Your annotations stay the same** — no code changes!
4. Delete generated files

See [Migrating from KSP to Compiler Plugin](/docs/migration/from-ksp-to-compiler-plugin).

## Requirements

- **Kotlin 2.x** (K2 compiler)
- Gradle 8.x+

## Configuration Options

```kotlin
// build.gradle.kts
koinCompiler {
    // Options will be documented here
}
```

## Classic DSL: Still Fully Supported

The Compiler Plugin doesn't replace Classic DSL - it adds analysis and generation on top. Classic DSL remains fully supported:

```kotlin
// Still works perfectly
val appModule = module {
    singleOf(::Database)
    singleOf(::ApiClient)
    single { CustomService(get(), get(), configValue) }  // Custom logic
    viewModelOf(::UserViewModel)
}
```

Use Classic DSL when you need:
- Custom factory logic
- `getOrNull()` for optional dependencies
- Conditional instantiation
- Backward compatibility with Kotlin 1.x

## Next Steps

- **[Setup Guide](/docs/setup/compiler-plugin)** - Detailed setup instructions
- **[DSL Reference](/docs/reference/dsl-reference)** - Complete DSL documentation
- **[Annotations Reference](/docs/reference/annotations-reference)** - Complete annotations documentation
- **[Migration Guides](/docs/migration/from-ksp-to-compiler-plugin)** - Upgrade your project
