# Insert Koin to make dependency injection

KOIN is a dependency injection framework that uses Kotlin and its functional power to get things done!  No proxy/CGLib, No code generation, No introspection. Just functional Kotlin and DSL magic ;)

![logo](./img/insert_koin_android_logo.jpg)

## Setup

Check that you have `jcenter` repository. Add the following gradle dependency to your Android app:

```gradle
compile 'org.koin:koin-android:0.3.1'
```

## Getting Started

First of all, you need to write a module. A module **gathers your components definitions** and allows it to be loaded by Koin and injected in your application. Keep in mind, that **injection by constructor** is the default strategy targeted by Koin. In Android components (Activity, Fragment ...) you can use `by inject()` to inject your dependencies.  

### Declare your dependencies

First of all, write a module class (extends [AndroidModule](https://github.com/Ekito/koin/wiki#module-class)), overrides the `context()` function by using the `declareContext` function, to declare a context like below:

```Kotlin
class MyModule : AndroidModule() {
    override fun context() =
            declareContext {
                provide { ServiceA(get()) }
                provide { ServiceB() }
                provide { ServiceC(get(), get()) }
            }
}

//for classes
class ServiceA(val serviceB: ServiceB) 
class ServiceB()
class ServiceC(val serviceA: ServiceA, val serviceB: ServiceB)
```

### Koin DSL

To describe your module, you can use the following **Koin DSL** keywords:

* `provide { /* component definition */ }` declares a component for your [Module](https://github.com/Ekito/koin/wiki#module-class) - You provide a function to instanciate your component
* `bind {/* compatible type */}` [bind](https://github.com/Ekito/koin/wiki#type-binding) a compatible type for *provided definition* (use it behind provide{} expression)
* `get()` resolve a component dependency
* `scope {/* scope class */}` use the given [scope](https://github.com/Ekito/koin/wiki#scopes) for current module's definitions

_NB_: Koin is simple: All your components are singletons. You have to use **scopes** to handle your components lifecycle and release them when needed.

### Setup your Application

To start Koin and your modules, you just have to build it in your *application* class like below:

```Kotlin
class MainApplication : Application(), KoinContextAware {

     // Your Koin Context here
    override val koinContext = newKoinContext(this, allModules())

    override fun onCreate() {
        super.onCreate()
        // Your Koin context is ready ! :)
    }
}
```

Implement `KoinContextAware` interface, and build your *koinContext* with the `newKoinContext` function huilder. This will able you to use the **Koin Android extensions** in your Android Application.

The `newKoinContext` function build a KoinContext for your `ApplicationContext` class and list of `AndroidModule` (`allmMdules()` is just a function returning a list of `AndroidModule`).

### Inject your components

Once your app is configured, you have to ways of handling injection in your application:

* In Android components (Activity,Fragment...): use the `by inject()` lazy operator
* in any Kotlin component: **injection is made by constructor**


Below a quick sample of injection with `by inject()` in an Activity:

```Kotlin
class MainActivity : AppCompatActivity() {

    // inject my WeatherService 
    val weatherService by inject<WeatherService>()
    // ...
}
```


## The Sample App

The [*koin-sample-app*](https://github.com/Ekito/koin/tree/master/koin-android/koin-sample-app) application offers a complete application sample, with MVP Android style. 

The weather app [wiki page](https://github.com/Ekito/koin/wiki/The-Koin-Sample-App) describes all about Koin features used.

## Documentation

A global wiki[wiki](https://github.com/Ekito/koin/wiki) page gather all features and references about Koin Framework.

## Articles

* [Insert Koin for dependency injection](https://www.ekito.fr/people/insert-koin-for-dependency-injection/)
* [Better dependency injection for Android](https://proandroiddev.com/better-dependency-injection-for-android-567b93353ad)

## Contact & Support

### Slack
Check the [kotlin slack community](https://kotlinlang.org/community/) and join **#koin** channel

### Github
Don't hesitate to open an issue to discuss about your needs or if you don't a feature for example.

