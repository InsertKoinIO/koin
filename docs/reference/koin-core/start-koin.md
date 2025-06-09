---
title: Start Koin
---


Koin is a DSL, a lightweight container and a pragmatic API. Once you have declared your definitions within Koin modules, you are ready to start the Koin container.

### The startKoin function

The `startKoin` function is the main entry point to launch Koin container. It needs a *list of Koin modules* to run.
Modules are loaded and definitions are ready to be resolved by the Koin container.

.Starting Koin
```kotlin
// start a KoinApplication in Global context
startKoin {
    // declare used modules
    modules(coffeeAppModule)
}
```

Once `startKoin` has been called, Koin will read all your modules & definitions. Koin is then ready for any `get()` or `by inject()` call to retrieve the needed instance.

Your Koin container can have several options:

* `logger` - to enable logging - see [Logging](#logging) section
* `properties()`, `fileProperties( )` or `environmentProperties( )` to load properties from environment, koin.properties file, extra properties ... - see [Loading properties](#loading-properties) section


:::info
 The `startKoin` can't be called more than once. If you need several point to load modules, use the `loadKoinModules` function.
:::

### Extending your Koin start (help reuse for KMP and other ...)

Koin now supports reusable and extensible configuration objects for KoinConfiguration. You can extract shared configuration for use across platforms (Android, iOS, JVM, etc.) or tailor it to different environments. This can be done with the includes() function. Below, we can reuse easily a common configuration, and extend it to add some Android environment settings:

```kotlin
fun initKoin(config : KoinAppDeclaration? = null){
   startKoin {
        includes(config) //can include external configuration extension
        modules(appModule)
   }
}

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@MainApplication)
            androidLogger()
        }
    }
}
```


### Behind the start - Koin instance under the hood

When we start Koin, we create a `KoinApplication` instance that represents the Koin container configuration instance. Once launched, it will produce a `Koin` instance resulting of your modules and options.
This `Koin` instance is then hold by the `GlobalContext`, to be used by any `KoinComponent` class.

The `GlobalContext` is a default JVM context strategy for Koin. It's called by `startKoin` and register to `GlobalContext`. This will allow us to register a different kind of context, in the perspective of Koin Multiplatform.

### Loading modules after startKoin

You can't call the `startKoin` function more than once. But you can use directly the `loadKoinModules()` functions.

This function is interesting for SDK makers who want to use Koin, because they don't need to use the `startKoin()` function and just use the `loadKoinModules` at the start of their library.

```kotlin
loadKoinModules(module1,module2 ...)
```

### Unloading modules

it's possible also to unload a bunch of definition, and then release theirs instance with the given function:

```kotlin
unloadKoinModules(module1,module2 ...)
```


### Stop Koin - closing all resources

You can close all the Koin resources and drop instances & definitions. For this you can use the `stopKoin()` function from anywhere, to stop the Koin `GlobalContext`.
Else on a `KoinApplication` instance, just call `close()`


## Logging

Koin has a simple logging API to log any Koin activity (allocation, lookup ...). The logging API is represented by the class below:

Koin Logger

```kotlin
abstract class Logger(var level: Level = Level.INFO) {

    abstract fun display(level: Level, msg: MESSAGE)

    fun debug(msg: MESSAGE) {
        log(Level.DEBUG, msg)
    }

    fun info(msg: MESSAGE) {
        log(Level.INFO, msg)
    }

    fun warn(msg: MESSAGE) {
        log(Level.WARNING, msg)
    }

    fun error(msg: MESSAGE) {
        log(Level.ERROR, msg)
    }
}
```

Koin proposes some implementation of logging, in function of the target platform:

* `PrintLogger` - directly log into console (included in `koin-core`)
* `EmptyLogger` - log nothing (included in `koin-core`)
* `SLF4JLogger` - Log with SLF4J. Used by ktor and spark (`koin-logger-slf4j` project)
* `AndroidLogger` - log into Android Logger (included in `koin-android`)

### Set logging at start

By default, Koin use the `EmptyLogger`. You can use directly the `PrintLogger` as following:

```kotlin
startKoin {
    logger(LEVEL.INFO)
}
```


## Loading properties

You can load several type of properties at start:

* environment properties - load *system* properties
* koin.properties file - load properties from `/src/main/resources/koin.properties` file
* "extra" start properties - map of values passed at `startKoin` function

### Read property from a module

Be sure to load properties at Koin start:

```kotlin
startKoin {
    // Load properties from the default location
    // (i.e. `/src/main/resources/koin.properties`)
    fileProperties()
}
```

In a Koin module, you can get a property by its key:

in /src/main/resources/koin.properties file
```java
// Key - value
server_url=http://service_url
```

Just load it with `getProperty` function:

```kotlin
val myModule = module {

    // use the "server_url" key to retrieve its value
    single { MyService(getProperty("server_url")) }
}
```

## Koin Options - Feature Flagging (4.1.0)

Your Koin application can now activate some experimental features through a dedicated `options` section, like:

```kotlin
startKoin {
    options(
        // activate ViewModel Scope factory feature 
        viewModelScopeFactory()
    )
}
```

