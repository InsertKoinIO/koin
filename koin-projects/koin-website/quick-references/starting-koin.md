---
layout: docs
title: Starting Koin
description: How to start Koin from your Kotlin app
group: quick-references
toc: true
---

Koin is a DSL, a container & a pragamtic API to leverage your dependencies. The Koin DSL consists in: 

* KoinApplication DSL: describe how to configure your Koin application
* Module DSL: describe your definitions

## How to start Koin?

Starting Koin consists in using the `startKoin` fuction as below:

### Starting Koin

In a classical Kotlin file:

{% highlight kotlin %}

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
{% endhighlight %}

### Starting Koin for Android

In any Android class:

{% highlight kotlin %}
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
{% endhighlight %}

<div class="alert alert-primary" role="alert">
    if you can't inject android context or appliation, be sure to use androidContext() function in your Koin application declaration.
</div>

### Starting Koin for Ktor

Starting Koin from your `Application` extension function:

{% highlight kotlin %}
fun Application.main() {
    // Install Ktor features
    install(Koin) {
        // Use SLF4J Koin Logger at Level.INFO
        slf4jLogger()

        // declare used modules
        modules(myModules)
    }
}
{% endhighlight %}

### KoinApplication builders

* `startKoin { }` - Create and register following KoinApplication instance
* `koinApplication { }` - create KoinApplication instance

{% highlight kotlin %}
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
{% endhighlight %}

### Logging

At start, Koin log what definition is bound by name or type (if log is activated):

```
[INFO] [Koin] bind type:'org.koin.example.CoffeeMaker' ~ [type:Single,class:'org.koin.example.CoffeeMaker']
[INFO] [Koin] bind type:'org.koin.example.Pump' ~ [type:Single,class:'org.koin.example.Pump']
[INFO] [Koin] bind type:'org.koin.example.Heater' ~ [type:Single,class:'org.koin.example.Heater']
```

### KoinApplication DSL recap

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

## Documentation Topics

Below are some further readings:

* [Complete documentation of Koin DSL]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_koin_dsl)
* [Overriding definitions]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_overriding_a_definition_or_a_module)
* [Create instances at start]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_create_instances_at_start)
* [More about modules]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_koin_modules)
* [Modules visibility rules]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_hierarchy_visibility)
* [Checking your modules configuration with check() and dryRun()]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/koin-core/index.html#_checking_your_koin_configuration)
