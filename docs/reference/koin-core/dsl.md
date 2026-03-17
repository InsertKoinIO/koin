---
title: Koin DSL
---

Quick reference for Koin DSL. For detailed guides see **[Core - Definitions](/docs/reference/koin-core/definitions)** and **[Core - Modules](/docs/reference/koin-core/modules)**.

## DSL Approaches

| Approach | Syntax | Package |
|----------|--------|---------|
| **Classic DSL** | `single { Class(get()) }` | `org.koin.dsl` |
| **Classic Autowire** | `singleOf(::Class)` | `org.koin.dsl` |
| **Compiler Plugin** | `single<Class>()` | `org.koin.plugin.module.dsl` |

:::tip
The **Compiler Plugin DSL** provides auto-wiring and compile-time safety. See [Compiler Plugin Setup](/docs/setup/compiler-plugin).
:::

## Application DSL

A `KoinApplication` instance represents your configured Koin container. This lets you set up logging, load properties, and register modules.

### Creating a KoinApplication

Choose between two approaches:

* `koinApplication { }` - Creates a standalone `KoinApplication` instance
* `startKoin { }` - Creates a `KoinApplication` and registers it in the `GlobalContext`

```kotlin
// Standalone instance (useful for testing or custom contexts)
val koinApp = koinApplication {
    modules(myModule)
}

// Global instance (standard approach for applications)
startKoin {
    logger()
    modules(myModule)
}
```

### Configuration Functions

Within `koinApplication` or `startKoin`, you can use:

* `logger()` - Set the logging level and Logger implementation (default: EmptyLogger)
* `modules()` - Load modules into the container (accepts list or vararg)
* `properties()` - Load a HashMap of properties
* `fileProperties()` - Load properties from a file
* `environmentProperties()` - Load properties from OS environment variables
* `createEagerInstances()` - Instantiate all definitions marked with `createdAtStart`
* `allowOverride(Boolean)` - Enable/disable definition overriding (default: true since 3.1.0)

### Global vs Local Context

The key difference between `koinApplication` and `startKoin`:

- **`startKoin`** - Registers the container in `GlobalContext`, making it accessible via `KoinComponent`, `by inject()`, and other global APIs
- **`koinApplication`** - Creates an isolated instance you control directly

```kotlin
// Global context - standard usage
startKoin {
    logger()
    modules(appModule)
}

// Later, anywhere in your app:
class MyClass : KoinComponent {
    val service: Service by inject() // Uses GlobalContext
}
```

```kotlin
// Local context - advanced usage (testing, multi-context apps)
val customKoin = koinApplication {
    modules(testModule)
}.koin

val service = customKoin.get<Service>() // Use specific instance
```

### Starting Koin

A complete Koin setup example:

```kotlin
startKoin {
    // Configure logging
    logger(Level.INFO)

    // Load properties
    environmentProperties()

    // Declare modules
    modules(
        networkModule,
        databaseModule,
        repositoryModule,
        viewModelModule
    )

    // Create eager singletons
    createEagerInstances()
}
```

## Module DSL

For comprehensive module and definition documentation, see:
- **[Definitions](/docs/reference/koin-core/definitions)** - All definition types with DSL and Annotations
- **[Modules](/docs/reference/koin-core/modules)** - Module organization and composition
- **[Definitions Reference](/docs/reference/koin-core/definitions)** - Quick lookup table

### Quick Reference

| Definition | Classic Lambda | Classic Autowire | Compiler Plugin |
|------------|----------------|------------------|-----------------|
| Singleton | `single { Class(get()) }` | `singleOf(::Class)` | `single<Class>()` |
| Factory | `factory { Class(get()) }` | `factoryOf(::Class)` | `factory<Class>()` |
| Scoped | `scoped { Class(get()) }` | `scopedOf(::Class)` | `scoped<Class>()` |
| ViewModel | `viewModel { VM(get()) }` | `viewModelOf(::VM)` | `viewModel<VM>()` |

### Basic Module

```kotlin
val myModule = module {
    single<Database>()
    single<UserRepository>()
    factory<UserPresenter>()
}
```

### Module Composition

```kotlin
val appModule = module {
    includes(networkModule, databaseModule)
    single<AppConfig>()
}

startKoin {
    modules(appModule)
}
```

See **[Modules - includes()](/docs/reference/koin-core/modules#module-composition-with-includes)** for details.
