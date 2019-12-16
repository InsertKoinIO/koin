
The `koin-android` project is dedicated to provide Koin powers to Android world.

## startKoin() from your Application

From your `Application` class you can use the `startKoin` function and inject the Android context with `androidContext` as follow:

```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Koin Android logger
            androidLogger()
            //inject Android context
            androidContext(this@MainApplication)
            // use modules
            modules(myAppModules)
        }
        
    }
}
```


## Starting Koin with Android context from elsewhere?

If you need to start Koin from another Android class, you can use the `startKoin` function and provide your Android `Context`
instance with just like:

```kotlin
startKoin {
    //inject Android context
    androidContext(/* your android context */)
    // use modules
    modules(myAppModules)
}
```

## Koin Logging

Within your `KoinApplication` instance, we have an extension `androidLogger` which use the `AndroidLogger()`#
This logger is an Android implementation of the Koin logger.

Up to you to change this logger if it doesn't suits to your needs.

.Shut off Koin Logger
```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            //inject Android context
            androidContext(/* your android context */)
            // use Android logger - Level.INFO by default
            androidLogger()
            // use modules
            modules(myAppModules)
        }
    }
}
```

## Properties

You can use Koin properties in the `assets/koin.properties` file, to store keys/values:

.Use Koin extra properties
```kotlin
// Shut off Koin Logger
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            //inject Android context
            androidContext(/* your android context */)
            // use Android logger - Level.INFO by default
            androidLogger()
            // use properties from assets/koin.properties
            androidFileProperties()
            // use modules
            modules(myAppModules)
        }
    }
}
```

