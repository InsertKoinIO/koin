
# Upgrading from Koin 1.0.x {docsify-ignore-all}

## From Koin 1.0.x

### New way to start Koin

Koin 2.0 propose a new way of starting your Koin application. Instead of having multiple `startKoin` functions, related to each extended runtime, we now only have one `startKoin` function and several ways to extend it for your platform:

In a classical Kotlin file:

### Starting Koin

```kotlin
fun main(vararg args: String) {

    startKoin {
        // enable Printlogger with default Level.INFO
        // can have Level & implementation
        // equivalent to logger(Level.INFO, PrintLogger())
        printLogger() 

        // declare properties from given map
        properties( /* properties map */)

        // load properties from koin.properties file or given file name
        fileProperties()

        // load properties from environment
        environmentProperties()

        // list all used modules
        // as list or vararg
        modules(moduleList) 
    }
}
```

### Starting Koin for Android

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
            modules(offlineWeatherApp)
        }
    }
}
```

<div class="alert alert-primary" role="alert">
    if you can't inject android context or application, be sure to use androidContext() function in your Koin application declaration.
</div>

### Starting Koin for Ktor

Starting Koin from your `Application` extension function:

```kotlin
fun Application.main() {
    // Install Ktor features
    install(Koin) {
        // Use SLF4J Koin Logger at Level.INFO
        slf4jLogger()

        // declare used modules
        modules(helloAppModule)
    }
}
```

### Logging

At start, Koin log what definition is bound by name or type:

```
[INFO] [Koin] bind type:'org.koin.example.CoffeeMaker' ~ [type:Single,class:'org.koin.example.CoffeeMaker']
[INFO] [Koin] bind type:'org.koin.example.Pump' ~ [type:Single,class:'org.koin.example.Pump']
[INFO] [Koin] bind type:'org.koin.example.Heater' ~ [type:Single,class:'org.koin.example.Heater']
```

### Import naming changes for DSL

Koin Module DSL hasn't changed much (appart the Scope API that will need entire rework), most changes can then be fixed with imports. Take a note that there is no more inner module or visibility rules for this. Module visibility is simple. Those are just list of definitions, visibile everywhere.

```kotlin
org.koin.android.viewmodel.ext.koin.viewModel -> org.koin.android.viewmodel.dsl.viewModel
org.koin.dsl.module.module -> org.koin.dsl.module
```

<div class="alert alert-primary" role="alert">
   If any problem, clean and reimport Koin API should fix most of your problem 👍
</div>

### API Breakings

For the following API/features, you will have to rewrite your content:

- Scope API - now use the new Scope API to declare `scoped` definitions
- Inner modules - Inner modules are no longer supported 
- Namings - component naming now rely on qualifiers with `named()`


