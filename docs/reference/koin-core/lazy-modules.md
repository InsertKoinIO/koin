---
title: Lazy Modules and Background Loading
---

In this section we will see how to organize your modules with lazy loading approach.

## Defining Lazy Modules [Experimental]

You can now declare lazy Koin module, to avoid trigger any pre allocation of resources and load them in background with Koin start.

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

## Background loading with Kotlin coroutines [Experimental]

Once you have declared some lazy modules, you can load them in background from your Koin configuration and further more.

- `KoinApplication.lazyModules` - load lazy modules in background with coroutines, regarding platform default Dispatchers
- `Koin.waitAllStartJobs` - wait for start jobs to complete
- `Koin.runOnKoinStarted` - run block code after start completion

A good example is always better to understand:

```kotlin
startKoin {
    // load lazy Modules in background
    lazyModules(m1)
}

val koin = KoinPlatform.getKoin()

// wait for loading jobs to finish
koin.waitAllStartJobs()

// or run code after loading is done
koin.runOnKoinStarted { koin ->
    // run after background load complete
}
```

:::note
    The `lazyModules` function allow you to specify a dispatcher: `lazyModules(modules, dispatcher = Dispatcher.IO)`
:::

:::info
    Default dispatcher for coroutines engine is `Dispatchers.Default`
:::

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