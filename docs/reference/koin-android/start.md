---
title: Start Koin on Android
---

The `koin-android` project is dedicated to provide Koin powers to Android world. See the [Android setup](/docs/setup/koin#android) section for more details.

## From your Application class

From your `Application` class you can use the `startKoin` function and inject the Android context with `androidContext` as follows:

```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@MainApplication)
            // Load modules
            modules(myAppModules)
        }
    }
}
```

:::info
You can also start Koin from anywhere if you don't want to start it from your Application class.
:::

If you need to start Koin from another Android class, you can use the `startKoin` function and provide your Android `Context`
instance with just like:

```kotlin
startKoin {
    //inject Android context
    androidContext(/* your android context */)
    // ...
}
```

## Extra Configurations

From your Koin configuration (in `startKoin { }` block code), you can also configure several parts of Koin.

### Koin Logging for Android

Within your `KoinApplication` instance, we have an extension `androidLogger` which uses the `AndroidLogger()` class.
This logger is an Android implementation of the Koin logger.

Up to you to change this logger if it doesn't suit to your needs.

```kotlin
startKoin {
    // use Android logger - Level.INFO by default
    androidLogger()
    // ...
}
```

### Loading Properties

You can use Koin properties in the `assets/koin.properties` file, to store keys/values:

```kotlin
startKoin {
    // ...
    // use properties from assets/koin.properties
    androidFileProperties()   
}
```

## Start Koin with Androidx Startup (4.0.1) [Experimental]

By using Gradle packge `koin-androidx-startup`, we can use `KoinStartup` interface to declare your Koin configuration your Application class:

```kotlin
class MainApplication : Application(), KoinStartup {

     override fun onKoinStartup() = koinConfiguration {
        androidContext(this@MainApplication)
        modules(appModule)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
```

This replaces the `startKoin` function that is usally used in `onCreate`. The `koinConfiguration` function is returning a `KoinConfiguration` instance.

:::info
`KoinStartup` avoid blocking main thread at for startup time, and offers better performances.
:::

## Startup Dependency with Koin

You can make your `Initializer` depend on `KoinInitializer` if you need Koin to be setup, and allow to inject dependencies:

```kotlin
class CrashTrackerInitializer : Initializer<Unit>, KoinComponent {

    private val crashTrackerService: CrashTrackerService by inject()

    override fun create(context: Context) {
        crashTrackerService.configure(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(KoinInitializer::class.java)
    }

}
```