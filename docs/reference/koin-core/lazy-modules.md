---
title: Lazy Modules
description: Load Koin modules in background coroutines with lazyModule and lazyModules to reduce startup time, and wait for them when needed.
---

A lazy module is a module that Koin registers in a background coroutine instead of during `startKoin`. This page covers declaring lazy modules, loading them in parallel, waiting for them, and the limits of the feature. It assumes you know [modules](/docs/reference/koin-core/modules) and [starting Koin](/docs/reference/koin-core/starting-koin).

## Dependency

Lazy modules are in the `koin-core-coroutines` artifact:

```kotlin
// build.gradle.kts
implementation("io.insert-koin:koin-core-coroutines")
```

## Declaring a lazy module

`lazyModule` takes the same content as `module`. Nothing is created or allocated until Koin loads it.

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.dsl.lazyModule
import org.koin.plugin.module.dsl.*

val analyticsModule = lazyModule {
    single<AnalyticsService>()
    single<EventTracker>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

Annotation modules (`@Module`) are loaded at start and cannot be declared lazy. To load some definitions in the background, declare them in a `lazyModule` with any DSL form (Compiler Plugin, Constructor or Classic DSL).

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.dsl.lazyModule
import org.koin.core.module.dsl.*

val analyticsModule = lazyModule {
    singleOf(::AnalyticsService)
    singleOf(::EventTracker)
}
```

</TabItem>
</Tabs>

A lazy module can include other lazy modules:

```kotlin
val dataModule = lazyModule {
    single<UserRepository>()
}

val featureModule = lazyModule {
    includes(dataModule)
    single<FeatureService>()
}
```

## Loading lazy modules

Pass lazy modules to `lazyModules()` in `startKoin`. Modules in `modules()` load on the calling thread, before `startKoin` returns. Lazy modules load in background coroutines:

```kotlin
startKoin {
    modules(coreModule)
    lazyModules(analyticsModule, reportingModule)
}
```

Since Koin 4.2, each lazy module loads in its own coroutine, so several lazy modules load in parallel. Before 4.2 they loaded one after the other.

`lazyModules()` sets up the Koin coroutines engine if needed. It runs on the platform default dispatcher (`Dispatchers.Default` on JVM and Native). To choose another one, pass `dispatcher`:

```kotlin
startKoin {
    lazyModules(databaseModule, networkModule, dispatcher = Dispatchers.IO)
}
```

The dispatcher is set when the coroutines engine is created, by the first `lazyModules()` call. A `dispatcher` passed in a later call has no effect.

## Waiting for lazy modules

A definition from a lazy module is not available until its module is loaded. Wait for loading to finish before you resolve it:

| Function | Platform | Behavior |
|----------|----------|----------|
| `Koin.awaitAllStartJobs()` | All | Suspends until every lazy module is loaded |
| `Koin.onKoinStarted { koin -> }` | All | Suspends until loaded, then runs the block |
| `Koin.waitAllStartJobs()` | All | Blocks the thread until loaded. On JS, it does not block and logs a warning |
| `Koin.runOnKoinStarted { koin -> }` | JVM | Blocks until loaded, then runs the block |
| `Koin.isAllStartedJobsDone()` | All | Returns `true` when every lazy module is loaded |

From a coroutine:

```kotlin
suspend fun trackAppStart() {
    val koin = KoinPlatform.getKoin()
    koin.awaitAllStartJobs()
    koin.get<AnalyticsService>().trackAppStart()
}
```

From blocking code:

```kotlin
startKoin {
    lazyModules(analyticsModule)
}

KoinPlatform.getKoin().waitAllStartJobs()
val analytics = KoinPlatform.getKoin().get<AnalyticsService>()
```

:::warning
Do not call `waitAllStartJobs()` on the Android main thread during startup: it blocks the UI until every lazy module is loaded, which cancels the benefit. Wait from a coroutine instead.
:::

## Android example

Load what the first screen needs with `modules()`, and the rest in the background:

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(coreModule)
            lazyModules(analyticsModule, databaseModule, dispatcher = Dispatchers.IO)
        }
    }
}
```

Good candidates for lazy modules: analytics, crash reporting, features behind a navigation step, and definitions with a costly module body. Keep in `modules()` anything the first screen resolves.

## Limits

- **No dependency from regular to lazy modules**: if a definition in a regular module depends on a definition in a lazy module, resolving it before the lazy module is loaded fails with `NoDefinitionFoundException`. Koin does not check this for you.
- **Lazy modules load at start**: `lazyModules()` starts loading right away in the background. It does not load modules on first use.
- **Module registration only**: lazy loading moves module registration off the main thread. A `single` is still created on first request, on the thread that asks for it. Avoid `createdAtStart` in a lazy module: Koin creates eager instances at the end of `startKoin`, so a lazy module that is not loaded yet misses that step.
- **No annotations**: `@Module` classes cannot be lazy.

:::tip
To measure how long startup and module loading take on real devices, see [Production Monitoring](/docs/tooling/production-monitoring).
:::

## Related

- [Modules](/docs/reference/koin-core/modules): module declaration and `includes()`.
- [Starting Koin](/docs/reference/koin-core/starting-koin): `startKoin` and the application DSL.
- [Definitions](/docs/reference/koin-core/definitions#eager-creation): `createdAtStart` and eager creation.
- [Android Setup](/docs/reference/koin-android/start): starting Koin in an Android `Application`.
- [DSL Reference](/docs/reference/koin-core/dsl): every DSL keyword.
