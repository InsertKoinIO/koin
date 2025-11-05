---
title: Lazy Modules and Background Loading
---

In this section we will see how to organize your modules with lazy loading approach and parallel initialization.

## Defining Lazy Modules

You can declare lazy Koin modules to avoid triggering any pre-allocation of resources and load them asynchronously in the background during Koin startup.

- `lazyModule` - declare a Lazy Kotlin version of Koin Module
- `Module.includes` - allow to include lazy Modules

A good example is always better to understand:

```kotlin
// Some lazy modules
val m2 = lazyModule {
    singleOf(::ClassB)
}

// include m2 lazy module
val m1 = lazyModule {
    includes(m2)
    singleOf(::ClassA) { bind<IClassA>() }
}
```

:::info
LazyModule won't trigger any resources until it has been loaded by the following API
:::

## Parallel Loading with Kotlin Coroutines

Once you have declared some lazy modules, you can load them in parallel in the background from your Koin configuration.

### Key Functions

- `KoinApplication.lazyModules` - load lazy modules in parallel background coroutines, using platform default Dispatchers
- `Koin.waitAllStartJobs` - wait for all start jobs to complete (available on all platforms)
- `Koin.runOnKoinStarted` - run block code after start completion (JVM only)

### Parallel Loading Performance (4.2.0+)

Starting from version 4.2.0, lazy modules are loaded **in parallel**, with each module getting its own coroutine job. This significantly improves startup time when you have multiple modules:

```kotlin
val module1 = lazyModule {
    singleOf(::DatabaseService)
}

val module2 = lazyModule {
    singleOf(::NetworkService)
}

val module3 = lazyModule {
    singleOf(::AnalyticsService)
}

startKoin {
    // All three modules load in parallel!
    lazyModules(module1, module2, module3)
}
```

**Performance Impact:**
- Single module: Same performance as before
- Multiple modules: Loads N modules in parallel instead of sequentially
- With 10 modules that each take 100ms to load:
  - Before 4.2.0: ~1000ms (sequential)
  - After 4.2.0: ~100ms (parallel)

### Basic Usage

```kotlin
startKoin {
    // load lazy Modules in background (parallel)
    lazyModules(m1, m2, m3)
}

val koin = KoinPlatform.getKoin()

// wait for loading jobs to finish
koin.waitAllStartJobs()

// or run code after loading is done (JVM only)
koin.runOnKoinStarted { koin ->
    // run after background load complete
}
```

### Platform-Specific Behavior

**Multiplatform Support (4.2.0+):**
- `waitAllStartJobs()` is now available on **all platforms** (JVM, Native, JS)
- JVM/Native: Uses true blocking with `runBlocking`
- JS: Uses `GlobalScope.promise` (not truly blocking, logs a warning)

**JVM-Only Functions:**
- `runOnKoinStarted { }` - Only available on JVM platform

### Custom Dispatcher

You can specify a custom dispatcher for lazy module loading:

```kotlin
startKoin {
    // Load modules on IO dispatcher
    lazyModules(m1, m2, dispatcher = Dispatchers.IO)
}
```

:::info
Default dispatcher for coroutines engine is `Dispatchers.Default`
:::

### Suspending Alternative

For platforms that don't support blocking or if you're already in a coroutine context, use the suspend functions:

```kotlin
// Suspend version
suspend fun initKoin() {
    startKoin {
        lazyModules(m1, m2)
    }

    // Await without blocking
    KoinPlatform.getKoin().awaitAllStartJobs()
}
```

### Limitation - Mixing Modules/Lazy Modules

For now we advise to avoid mixing modules & lazy modules, in the startup. Avoid having `mainModule` requiring dependency in `lazyReporter`.

```kotlin
startKoin {
    androidLogger()
    androidContext(this@TestApp)
    modules(mainModule)
    lazyModules(lazyReporter)
}
```

:::warning
For now Koin doesn't check if your module depends on a lazy modules
:::