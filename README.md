![logo](./img/insert_koin_android_logo.jpg)

### What's KOIN?

KOIN is a simple (but powerful) dependency injection framework for Android. It uses [Kotlin](https://kotlinlang.org/) and its functional power to get things done!  No proxy/CGLib, no code generation, no introspection. Just functional Kotlin and DSL magic ;)

KOIN is a very small library, that aims to be as simple as possible and let's you write dependency injection in a breath.

*Just describe your stuff and inject it!*

### What's up?

Check the latest changes in [What's New](https://github.com/Ekito/koin/wiki/What's-new-%3F) and the [Roadmap](https://github.com/Ekito/koin/wiki/Roadmap) for next releases.

For users using a version prior to Koin 0.4.x, please refer the [migrating to 0.4.0](https://github.com/Ekito/koin/wiki/Migrating#migrating-to-04x) page to understand the latest changes. 

# Getting Started

## Gradle

Check that you have the `jcenter` repository. Add the following gradle dependency to your Android app:

```gradle
compile 'org.koin:koin-android:0.4.0'
```

### Setup your Application

To start KOIN and your modules, you just have to implement it in your Android *Application* class like below:

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

The `newKoinContext` function builder requires a list of modules to run. A function can manage this for you, check out the `allModules()` function.

## Describing your dependencies

KOIN requires you to declare your components in modules.

A module class **extends** your [AndroidModule](https://github.com/Ekito/koin/wiki#module-class) class and implements the `context()` function by using the `applicationContext` function builder, to declare a context like below:

```Kotlin
class WeatherModule : AndroidModule() {
    override fun context() = applicationContext {
        // WeatherActivity context
        context(name = "WeatherActivity") {
            // Provide a factory for presenter WeatherContract.Presenter
            provide { WeatherPresenter(get()) }
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
Your module then provides an *applicationContext* (description of your components), which will be made of provided components and subcontexts.

You can refer to the [KOIN DSL](https://github.com/Ekito/koin/wiki/Koin-DSL) for more details. 

## Making dependency injection

Once your app is configured, you have 2 ways of handling injection in your application:

* In **Android components** (Activity, Fragment etc.): use the `by inject()` lazy operator
* In **any Kotlin component**: injection is made by constructor


Below is a sample of injection with `by inject()` in an Android Activity:

```Kotlin
class WeatherActivity() : AppCompatActivity() {

    // inject my Presenter 
    val presenter by inject<WeatherPresenter>()
    
}
```

## Checking your modules

KOIN is an internal DSL: all your modules evolves directly with your code (if you change a component, it will also impact your modules). 

You can check your modules with `KoinContext.dryRun()` (launch all your modules and try to inject each component). Better is to place it in your tests folder and check it regulary - ensure everything is injected correctly.

in a JUnit test file:

```kotlin
@Test
fun dryRun(){
     val koinContext = Koin().build(allModules()).dryRun()
     // or if you need Application context in your injection
     val koinContext = Koin().init(mock(Application::class.java)).build(allModules()).dryRun()
}
```

# The Sample App

The [*koin-sample-app*](https://github.com/Ekito/koin/tree/master/koin-android/koin-sample-app) application offers a complete application sample, with MVP Android style. 

The weather app [wiki page](https://github.com/Ekito/koin/wiki/The-Koin-Sample-App) describes all the KOIN features used.

# Documentation

A global [wiki](https://github.com/Ekito/koin/wiki) documentation page gather all features and references about the KOIN Framework.

# Articles

* [Insert Koin for dependency injection](https://www.ekito.fr/people/insert-koin-for-dependency-injection/)
* [Better dependency injection for Android](https://proandroiddev.com/better-dependency-injection-for-android-567b93353ad)

# Contact & Support

## Slack
Check the [kotlin slack community](https://kotlinlang.org/community/) and join **#koin** channel

## Github
Don't hesitate to open an issue to discuss about your needs or if you don't a feature for example.

