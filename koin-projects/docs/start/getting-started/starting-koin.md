
> Koin is a DSL, a container & a pragamtic API to leverage your dependencies. 

The Koin DSL consists in:

* KoinApplication DSL: describe how to configure your Koin application
* Module DSL: describe your definitions

Starting Koin consists in using the `startKoin` fuction as below:

## StartKoin

In a classical Kotlin file:

```kotlin

fun main(vararg args: String) {

    startKoin {
        // enable Printlogger with default Level.INFO
        // can have Level & implementation
        // equivalent to logger(Level.INFO, PrintLogger())
        printlogger() 

        // declare properties from given map
        properties( /* properties map */)

        // load properties from koin.properties file or given file name
        fileProperties()

        // load properties from environment
        environmentProperties()

        // list all used modules
        // as list or vararg
        modules(myModules) 
    }
}
```

## Starting for Android

In any Android class:

```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(this@MainApplication)

            // load properties from assets/koin.properties file
            androidFileProperties()

            // module list
            modules(myModules)
        }
    }
}
```

<div class="alert alert-primary" role="alert">
    if you can't inject android context or appliation, be sure to use androidContext() function in your Koin application declaration.
</div>

## Starting for Ktor

Starting Koin from your `Application` extension function:

```kotlin
fun Application.main() {
    // Install Ktor features
    install(Koin) {
        // Use SLF4J Koin Logger at Level.INFO
        slf4jLogger()

        // declare used modules
        modules(myModules)
    }
}
```

## Custom Koin instance 

Here below are the KoinApplication builders:

* `startKoin { }` - Create and register following KoinApplication instance
* `koinApplication { }` - create KoinApplication instance

```kotlin
// Create and register following KoinApplication instance
startKoin {
    logger()
    modules(coffeeAppModule)
}

// create KoinApplication instance
koinApplication {
    logger()
    modules(coffeeAppModule)
}
```

## Logging

At start, Koin log what definition is bound by name or type (if log is activated):

```
[INFO] [Koin] bind type:'org.koin.example.CoffeeMaker' ~ [type:Single,class:'org.koin.example.CoffeeMaker']
[INFO] [Koin] bind type:'org.koin.example.Pump' ~ [type:Single,class:'org.koin.example.Pump']
[INFO] [Koin] bind type:'org.koin.example.Heater' ~ [type:Single,class:'org.koin.example.Heater']
```

## DSL

A quick recap of the Koin DSL keywords:

* `startKoin { }` - Create and register following KoinApplication instance
* `koinApplication { }` - create KoinApplication instance
* `modules(...)` - declare used modules
* `logger()` - declare PrintLogger
* `properties(...)` - declare map properties
* `fileProperties()` - use properties from external file
* `environmentProperties()` - use properties from environment
* `androidLogger()` - declare Android Koin logger
* `androidContext(...)` - use given Android context
* `androidFileProperties()` - use properties file from Android assets
* `slf4jLogger(...)` - use SLF4J Logger
