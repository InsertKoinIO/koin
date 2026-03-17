---
title: Lazy Modules and Background Loading
---

Lazy modules enable asynchronous, parallel module loading to improve startup performance. Instead of loading all modules synchronously at startup, you can defer and parallelize module initialization.

:::info
This page uses the **Koin Compiler Plugin DSL** (`single<T>()`). See [Compiler Plugin Setup](/docs/setup/compiler-plugin) for configuration.
:::

## What Are Lazy Modules?

Lazy modules delay module registration and instance creation until explicitly loaded. They're particularly useful for:

- **Large applications** - Split initialization across multiple threads
- **Performance optimization** - Reduce startup time
- **Conditional features** - Load modules only when needed
- **Background initialization** - Load non-critical modules asynchronously

## Defining Lazy Modules

Create lazy modules with the `lazyModule` function:

```kotlin
// Lazy module - not loaded until explicitly requested
val networkModule = lazyModule {
    single<ApiClient>()
    single<NetworkMonitor>()
}

val databaseModule = lazyModule {
    single<Database>()
    single<UserDao>()
}
```

### Composing Lazy Modules

Lazy modules support `includes()` just like regular modules:

```kotlin
val dataModule = lazyModule {
    single<UserRepository>()
}

val featureModule = lazyModule {
    includes(dataModule)  // Include other lazy modules
    single<FeatureService>()
}
```

:::info
Lazy modules won't allocate any resources until loaded via the `lazyModules()` function.
:::

## Loading Lazy Modules

Load lazy modules using `lazyModules()` within your Koin configuration.

### Basic Loading

```kotlin
val analyticsModule = lazyModule {
    single<AnalyticsService>()
}

val reportingModule = lazyModule {
    single<CrashReporter>()
}

startKoin {
    // Load critical modules immediately
    modules(coreModule, networkModule)

    // Load non-critical modules in background
    lazyModules(analyticsModule, reportingModule)
}
```

### Parallel Loading (4.2.0+)

Since version 4.2.0, multiple lazy modules load **in parallel**, with each module in its own coroutine:

```kotlin
val module1 = lazyModule { single<DatabaseService>() }
val module2 = lazyModule { single<NetworkService>() }
val module3 = lazyModule { single<AnalyticsService>() }

startKoin {
    // All three modules load simultaneously!
    lazyModules(module1, module2, module3)
}
```

**Performance Impact:**

| Scenario | Before 4.2.0 (Sequential) | After 4.2.0 (Parallel) |
|----------|--------------------------|------------------------|
| 1 module @ 100ms | 100ms | 100ms |
| 3 modules @ 100ms each | 300ms | ~100ms |
| 10 modules @ 100ms each | 1000ms | ~100ms |

### Waiting for Completion

#### All Platforms: `waitAllStartJobs()`

```kotlin
startKoin {
    lazyModules(module1, module2, module3)
}

val koin = KoinPlatform.getKoin()

// Block until all lazy modules are loaded
koin.waitAllStartJobs()

// Now safe to use dependencies from lazy modules
val service = koin.get<AnalyticsService>()
```

**Platform behavior:**
- **JVM/Native**: True blocking with `runBlocking`
- **JS**: Uses `GlobalScope.promise` (not truly blocking, logs warning)

#### JVM Only: `runOnKoinStarted()`

```kotlin
startKoin {
    lazyModules(analyticsModule)
}

// JVM-only callback
KoinPlatform.getKoin().runOnKoinStarted { koin ->
    // Executes after all lazy modules finish loading
    koin.get<AnalyticsService>().trackAppStart()
}
```

#### Suspending Alternative: `awaitAllStartJobs()`

For coroutine contexts or platforms without blocking support:

```kotlin
suspend fun initializeApp() {
    startKoin {
        lazyModules(module1, module2)
    }

    // Await without blocking
    KoinPlatform.getKoin().awaitAllStartJobs()

    // Safe to proceed
    println("All modules loaded!")
}
```

## Custom Dispatchers

Control which dispatcher runs lazy module loading:

```kotlin
import kotlinx.coroutines.Dispatchers

startKoin {
    // Load on IO dispatcher instead of Default
    lazyModules(
        databaseModule,
        networkModule,
        dispatcher = Dispatchers.IO
    )
}
```

**Common dispatcher choices:**
- `Dispatchers.Default` - CPU-intensive work (default)
- `Dispatchers.IO` - I/O operations, file access, network
- `Dispatchers.Main` - UI updates (Android/Desktop)

:::info
Default dispatcher is `Dispatchers.Default` if not specified.
:::

## Real-World Example

```kotlin
// Core modules - load immediately
val coreModule = module {
    single<AppConfig>()
    single<UserSession>()
}

// Feature modules - load in background
val analyticsModule = lazyModule {
    single<AnalyticsEngine>()
    single<EventTracker>()
}

val networkingModule = lazyModule {
    single<ApiClient>()
    single<WebSocketManager>()
}

val databaseModule = lazyModule {
    single<Database>()
    single<UserDao>()
}

// Android Application
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApp)

            // Critical modules load immediately
            modules(coreModule)

            // Non-critical modules load in parallel in background
            lazyModules(
                analyticsModule,
                networkingModule,
                databaseModule,
                dispatcher = Dispatchers.IO
            )
        }

        // Optional: Wait for background loading to complete
        lifecycleScope.launch {
            KoinPlatform.getKoin().awaitAllStartJobs()
            Log.d("Koin", "All modules loaded!")
        }
    }
}
```

## Important Limitations

### Avoid Cross-Dependencies

Lazy modules and regular modules should be independent. Don't create dependencies from regular modules to lazy modules:

```kotlin
// ❌ BAD - mainModule depends on lazy module
val lazyAnalytics = lazyModule {
    single { AnalyticsService() }
}

val mainModule = module {
    single { AppController(get<AnalyticsService>()) }  // May fail!
}

startKoin {
    modules(mainModule)
    lazyModules(lazyAnalytics)
}
```

```kotlin
// ✅ GOOD - Keep dependencies separate
val lazyAnalytics = lazyModule {
    single { AnalyticsService() }
}

val mainModule = module {
    single { AppController() }
}

startKoin {
    modules(mainModule)
    lazyModules(lazyAnalytics)
}
```

:::warning
Koin doesn't currently validate dependencies between regular and lazy modules. Ensure regular modules don't depend on lazy module definitions.
:::

### Best Practice: Load Order

1. **Immediate modules** - Critical services needed at startup
2. **Lazy modules** - Non-critical, deferrable services
3. **Wait if needed** - Use `waitAllStartJobs()` before accessing lazy definitions

## When to Use Lazy Modules

### Good Use Cases

- **Analytics/Tracking** - Not needed for core functionality
- **Crash Reporting** - Can initialize in background
- **Feature Modules** - Modular features loaded on-demand
- **Database/Network** - Heavy initialization that can be deferred
- **Large Apps** - Split startup load across threads

### Not Recommended

- **Core Services** - Critical dependencies needed immediately
- **Small Apps** - Overhead may exceed benefits
- **Tightly Coupled Modules** - When modules have many cross-dependencies

## API Reference

| Function | Platform | Description |
|----------|----------|-------------|
| `lazyModules()` | All | Load lazy modules in background |
| `waitAllStartJobs()` | All | Block until all lazy modules load |
| `awaitAllStartJobs()` | All | Suspend until all lazy modules load |
| `runOnKoinStarted()` | JVM only | Callback after loading completes |

## See Also

- **[Modules](/docs/reference/koin-core/modules)** - Module composition with `includes()`
- **[Definitions](/docs/reference/koin-core/definitions)** - Eager vs lazy singletons
- **[Starting Koin](/docs/reference/koin-core/starting-koin)** - Koin startup configuration