---
title: Multiple Koin Modules in Android
---

By using Koin, you describe definitions in modules. In this section we will see how to declare, organize & link your modules.

## Using several modules

Components don't have to be necessarily in the same module. A module is a logical space to help you organize your definitions, and can depend on definitions from another
module. Definitions are lazy, and they are resolved only when a component requests them.

Let's take an example, with linked components in separate modules:

```kotlin
// ComponentB <- ComponentA
class ComponentA()
class ComponentB(val componentA : ComponentA)

val moduleA = module {
    // Singleton ComponentA
    single { ComponentA() }
}

val moduleB = module {
    // Singleton ComponentB with linked instance ComponentA
    single { ComponentB(get()) }
}
```

We just have to declare list of used modules when we start our Koin container:

```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // ...

            // Load modules
            modules(moduleA, moduleB)
        }
        
    }
}
```
Up to you to organise your self per Gradle module, and gather several Koin modules.

> Check [Koin Modules Section](../koin-core/modules) for more details

## Module Includes (since 3.2)

A new function `includes()` is available in the `Module` class, which lets you compose a module by including other modules in an organized and structured way.

The two prominent use cases of the new feature are:
- Split large modules into smaller and more focused ones.
- In modularized projects, it allows you more fine control over a module visibility (see examples below).

How does it work? Let's take some modules, and we include modules in `parentModule`:

```kotlin
// `:feature` module
val childModule1 = module {
    /* Other definitions here. */
}
val childModule2 = module {
    /* Other definitions here. */
}
val parentModule = module {
    includes(childModule1, childModule2)
}

// `:app` module
startKoin { modules(parentModule) }
```

Notice we do not need to set up all modules explicitly: by including `parentModule`, all the modules declared in the `includes` will be automatically loaded (`childModule1` and `childModule2`).  In other words, Koin is effectively loading: `parentModule`, `childModule1` and `childModule2`.

An important detail to observe is that you can use `includes` to add `internal` and `private` modules too - that gives you flexibility over what to expose in a modularized project.

:::info
Module loading is now optimized to flatten all your module graphs and avoid duplicated definitions of modules.
:::

Finally, you can include multiple nested or duplicates modules, and Koin will flatten all the included modules removing duplicates:

```kotlin
// :feature module
val dataModule = module {
    /* Other definitions here. */
}
val domainModule = module {
    /* Other definitions here. */
}
val featureModule1 = module {
    includes(domainModule, dataModule)
}
val featureModule2 = module {
    includes(domainModule, dataModule)
}
```

```kotlin
// `:app` module
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // ...

            // Load modules
             modules(featureModule1, featureModule2)
        }
        
    }
}
```

Notice that all modules will be included only once: `dataModule`, `domainModule`, `featureModule1`, `featureModule2`.


## Reducing Startup time with background module loading

You can now declare "lazy" Koin module, to avoid trigger any pre allocation of resources and load them in background with Koin start. This can help avoid to block Android starting process, by passing lazy modules to be loaded in background.

- `lazyModule` - declare a Lazy Kotlin version of Koin Module
- `Module.includes` - allow to include lazy Modules
- `KoinApplication.lazyModules` - load lazy modules in background with coroutines, regarding platform default Dispatchers
- `Koin.waitAllStartJobs` - wait for start jobs to complete
- `Koin.runOnKoinStarted` - run block code after start completion

A good example is always better to understand:

```kotlin

// Lazy loaded module
val m2 = lazyModule {
    singleOf(::ClassB)
}

val m1 = module {
    singleOf(::ClassA) { bind<IClassA>() }
}

startKoin {
    // sync module loading
    modules(m1)
    // load lazy Modules in background
    lazyModules(m2)
}

val koin = KoinPlatform.getKoin()

// wait for start completion
koin.waitAllStartJobs()

// or run code after start
koin.runOnKoinStarted { koin ->
    // run after background load complete
}
```