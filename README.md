# Insert Koin to make dependency injection

KOIN is a dependency injection framework that uses Kotlin and its functional power to get things done!  No proxy/CGLib, No code generation, No introspection. Just functional Kotlin and DSL magic ;)

![logo](./img/insert_koin_android_logo.jpg)

Check the latest changes in [What's new](https://github.com/Ekito/koin/wiki/What's-new-%3Fv) wiki page.

# Getting Started

## Gradle

Check that you have `jcenter` repository. Add the following gradle dependency to your Android app:

```gradle
compile 'org.koin:koin-android:0.4.0'
```

### Setup your Application

To start Koin and your modules, you just have to make it in your Android *Application* class like below:

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

**Implement** `KoinContextAware` interface, and **override** your `koinContext` property with the `newKoinContext()` function builder.

The `newKoinContext` function builder need a list of modules to run. A function can do it four you, like `allModules()` function.

# Dependency management

## Declaration in modules

Koin ask you to declare your components in modules.

A module class **extends** [AndroidModule](https://github.com/Ekito/koin/wiki#module-class) class and implement the `context()` function by using the `applicationContext` function builder, to declare a context like below:

```Kotlin
class WeatherModule : AndroidModule() {
    override fun context() = applicationContext {
        // WeatherActivity context
        context(name = "WeatherActivity") {
            // Provide a factory for presenter WeatherContract.Presenter
            provide { WeatherPresenter(get()) } bind WeatherContract.Presenter::class
        }
        
        // Weather data repository
        provide { WeatherRepository(get()) }
        
        // Local Weather DataSource 
        provide { LocalDataSource(AndroidReader(applicationContext) } bind WeatherDatasource::class
    }
}

//for classes
class WeatherPresenter(val weatherRepository: WeatherRepository)
class WeatherRepository(val weatherDatasource: WeatherDatasource)
class LocalDataSource(val jsonReader: JsonReader) : WeatherDatasource
```
Your module provides then an *applicationContext* (description of yoru components), which will be made of provided components and subcontexts.

You can refer to the [Koin DSL](https://github.com/Ekito/koin/wiki/Koin-DSL) for more details. 

## Making dependency injection

Once your app is configured, you have 2 ways of handling injection in your application:

* In **Android components** (Activity,Fragment...): use the `by inject()` lazy operator
* in **any Kotlin component**: injection is made by constructor

Below a sample of injection with `by inject()` in an Activity:

```Kotlin
class WeatherActivity() : AppCompatActivity() {

    // inject my Presenter 
    val presenter: WeatherContract.Presenter by inject()
    
}
```

## Checking your modules

Koin is an internal DSL: all your modules evolves directly with your code (if you change a component, it will also impact your modules). 

You can check your modules with `KoinContext.dryRun()` (launch all your modules and try to inject each component). Better is to place it in your tests folder and check it regulary - ensure everything is injected correctly.

# The Sample App

The [*koin-sample-app*](https://github.com/Ekito/koin/tree/master/koin-android/koin-sample-app) application offers a complete application sample, with MVP Android style. 

The weather app [wiki page](https://github.com/Ekito/koin/wiki/The-Koin-Sample-App) describes all about Koin features used.

# Documentation

A global [wiki](https://github.com/Ekito/koin/wiki) documentation page gather all features and references about Koin Framework.

# Articles

* [Insert Koin for dependency injection](https://www.ekito.fr/people/insert-koin-for-dependency-injection/)
* [Better dependency injection for Android](https://proandroiddev.com/better-dependency-injection-for-android-567b93353ad)

# Contact & Support

## Slack
Check the [kotlin slack community](https://kotlinlang.org/community/) and join **#koin** channel

## Github
Don't hesitate to open an issue to discuss about your needs or if you don't a feature for example.

