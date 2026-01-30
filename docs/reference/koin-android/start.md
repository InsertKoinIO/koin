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

## Start Koin with Annotations

When using Koin Annotations, you can use `startKoin<T>()` to start Koin with your annotated module class:

```kotlin
@KoinApplication
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin<MainApplication> {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}
```

The `startKoin<T>()` function automatically loads the generated module from your `@Module` annotated class.

For multiple modules:

```kotlin
@Module
@Configuration
@ComponentScan("com.myapp.data")
class DataModule

@Module
@Configuration
@ComponentScan("com.myapp.domain")
class DomainModule

@KoinApplication
class MainApplication

// Start with multiple modules
startKoin<MainApplication> {
    androidLogger()
    androidContext(this@MainApplication)
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

## Start Koin with AndroidX Startup (4.0.1)

[AndroidX Startup](https://developer.android.com/topic/libraries/app-startup) is a library that provides a straightforward way to initialize components at app startup. It uses a single ContentProvider to initialize all dependencies, avoiding the overhead of separate ContentProviders for each component that needs early initialization.

By using Gradle package `koin-androidx-startup`, we can use `KoinStartup` interface to declare your Koin configuration in your Application class:

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
`KoinStartup` integrates with AndroidX App Startup to initialize Koin via ContentProvider before `Application.onCreate()`. This is useful when you need to manage initialization order with other Initializers that depend on Koin being available.
:::

:::warning
`KoinStartup` runs on the main thread during app startup. If you're not using AndroidX App Startup library to manage other Initializers, there is **no benefit** to using `KoinStartup` - use the standard `startKoin` approach instead. For dispatching module loading to background threads, see [Lazy Modules](/docs/reference/koin-core/lazy-modules).
:::

If you have other Initializers that need Koin, make them depend on `KoinInitializer`:

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

## Next Steps

- **[JSR-330 Compatibility](/docs/reference/koin-android/jsr330)** - Use standard `@Inject`, `@Singleton` annotations
- **[Injecting in Android](/docs/reference/koin-android/get-instances)** - Get instances in Activity, Fragment, Service
- **[Android ViewModel](/docs/reference/koin-android/viewmodel)** - ViewModel injection and scoping
- **[Hilt Migration](/docs/reference/koin-android/hilt-migration)** - Migrate from Hilt to Koin