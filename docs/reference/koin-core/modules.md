---
title: Modules
---

# Modules

Koin modules are the building blocks for organizing your dependency injection configuration.

## What is a Module?

A module is a logical container for grouping related definitions:

```kotlin
val appModule = module {
    single<Database>()
    single<UserRepository>()
    viewModel<UserViewModel>()
}
```

Modules help you:
- **Organize** definitions by feature or layer
- **Encapsulate** related dependencies
- **Reuse** configurations across contexts
- **Control visibility** in modular projects

## Creating Modules

### With Compiler Plugin DSL

```kotlin
import org.koin.plugin.module.dsl.*

val networkModule = module {
    single<ApiClient>()
    single<TokenManager>()
}

val databaseModule = module {
    single<Database>()
    single<UserDao>()
}
```

### With Annotations

```kotlin
@Module
@ComponentScan("com.myapp.network")
class NetworkModule

@Module
@ComponentScan("com.myapp.database")
class DatabaseModule
```

### With Classic DSL

```kotlin
val networkModule = module {
    singleOf(::ApiClient)
    singleOf(::TokenManager)
}
```

## Using Multiple Modules

Dependencies can reference definitions from other modules:

```kotlin
// Data layer
val dataModule = module {
    single<Database>()
    single<UserRepository>()  // Can use Database from this module
}

// Presentation layer
val viewModelModule = module {
    viewModel<UserViewModel>()  // Can use UserRepository from dataModule
}

// Load both
startKoin {
    modules(dataModule, viewModelModule)
}
```

:::info
Koin resolves dependencies across all loaded modules automatically. No explicit imports needed.
:::

:::note
While listing modules directly works, consider using [`includes()`](#module-composition-with-includes) to organize your modules into a hierarchy for better structure and optimized loading.
:::

## Module Composition with `includes()`

The `includes()` function is the **recommended way** to organize your modules. It provides:

- **Module hierarchy** - Structure your modules in a clear parent-child relationship
- **Optimized loading** - Koin deduplicates included modules, preventing redundant registrations
- **Cleaner startup** - Load a single root module instead of a long list
- **Encapsulation** - Internal modules can be hidden behind a public API module

:::tip
**Best Practice:** Use `includes()` to build a module hierarchy instead of listing all modules in `startKoin`. This improves organization and ensures efficient module loading.
:::

```kotlin
val networkModule = module {
    single<ApiClient>()
}

val storageModule = module {
    single<Database>()
}

// Parent module includes child modules
val dataModule = module {
    includes(networkModule, storageModule)
    single<UserRepository>()
}

// ✅ Recommended: Load root module with includes
startKoin {
    modules(dataModule)
}

// ❌ Avoid: Flat list of modules
startKoin {
    modules(networkModule, storageModule, dataModule)
}
```

### How `includes()` Optimizes Loading

When modules are included multiple times, Koin loads them only once:

```kotlin
val commonModule = module {
    single<Logger>()
}

val featureAModule = module {
    includes(commonModule)
    single<FeatureA>()
}

val featureBModule = module {
    includes(commonModule)  // Also includes commonModule
    single<FeatureB>()
}

val appModule = module {
    includes(featureAModule, featureBModule)
}

// commonModule is loaded only ONCE, even though it's included twice
startKoin {
    modules(appModule)
}
```

### Multi-Module Projects

Use visibility modifiers to control what's exposed:

```kotlin
// :feature:user module

// Private - hidden from other modules
private val userDataModule = module {
    single<UserDao>()
    single<UserCache>()
}

// Public API
val userFeatureModule = module {
    includes(userDataModule)
    viewModel<UserViewModel>()
}
```

```kotlin
// :app module
startKoin {
    modules(userFeatureModule)  // Only this is accessible
}
```

## Module Override

### Default Behavior

By default, the **last loaded definition wins**:

```kotlin
val productionModule = module {
    single<ApiService> { ProductionApi() }
}

val debugModule = module {
    single<ApiService> { DebugApi() }
}

startKoin {
    modules(productionModule, debugModule)  // DebugApi wins
}
```

### Strict Mode

Disable overrides in production:

```kotlin
startKoin {
    allowOverride(false)  // Throws exception on override attempt
    modules(productionModule)
}
```

### Explicit Override

Allow specific overrides in strict mode:

```kotlin
val testModule = module {
    single<ApiService> { MockApi() }.override()  // Allowed
}

startKoin {
    allowOverride(false)
    modules(productionModule, testModule)
}
```

## Eager Module Creation

Create singletons immediately at startup:

```kotlin
val coreModule = module(createdAtStart = true) {
    single<ConfigManager>()
    single<LoggingSystem>()
}
```

## Parameterized Modules

Create modules dynamically:

```kotlin
fun featureModule(debug: Boolean) = module {
    single<Logger> {
        if (debug) DebugLogger() else ProductionLogger()
    }
}

startKoin {
    modules(featureModule(debug = BuildConfig.DEBUG))
}
```

## Strategy Pattern

Use modules to swap implementations:

```kotlin
val repositoryModule = module {
    single<UserRepository>()  // Depends on Datasource
}

// Strategy options
val localDatasourceModule = module {
    single<Datasource> { LocalDatasource() }
}

val remoteDatasourceModule = module {
    single<Datasource> { RemoteDatasource() }
}

// Production
startKoin {
    modules(repositoryModule, remoteDatasourceModule)
}

// Offline mode
startKoin {
    modules(repositoryModule, localDatasourceModule)
}
```

## Annotated Modules

Koin supports annotation-based module configuration as an alternative to the DSL.

```kotlin
@Module
@ComponentScan("com.myapp.data")
class DataModule

@Module
@ComponentScan("com.myapp.network")
class NetworkModule

// Include other modules
@Module(includes = [DataModule::class, NetworkModule::class])
class AppModule
```

Key features:
- `@Module` marks a class as a Koin module
- `@ComponentScan` auto-discovers annotated classes in packages
- `@Configuration` enables auto-discovery at startup
- Module functions provide external library instances

:::info
For complete annotated module documentation, see [Annotations Reference - Modules](/docs/reference/koin-annotations/modules).
:::

## Best Practices

### Organization

1. **Group by feature/layer**
   ```kotlin
   val authModule = module { /* auth feature */ }
   val networkModule = module { /* network layer */ }
   ```

2. **Use `includes()` to build module hierarchy** (Recommended)
   ```kotlin
   // Create a root module that includes all features
   val appModule = module {
       includes(
           coreModule,
           networkModule,
           featureAModule,
           featureBModule
       )
   }

   // Clean startup with single module
   startKoin {
       modules(appModule)
   }
   ```

3. **Keep modules focused** - Single responsibility per module

### Naming

- Use descriptive names: `networkModule`, `userFeatureModule`
- Group related: `authDataModule`, `authDomainModule`

### Multi-Module Projects

1. **One public module per feature**
2. **Use `private`/`internal` for implementation modules**
3. **Place shared modules in `:core`**

## Next Steps

- **[Definitions](/docs/reference/koin-core/definitions)** - Create definitions
- **[Qualifiers](/docs/reference/koin-core/qualifiers)** - Named and typed qualifiers
- **[Scopes](/docs/reference/koin-core/scopes)** - Manage lifecycle with scopes
- **[Troubleshooting](/docs/reference/koin-core/troubleshooting)** - Debug and fix common issues
