![logo](./img/insert_koin_android_logo.jpg)

### What's KOIN?

KOIN is a simple (but powerful) dependency injection framework for Android & any Kotlin runtime. It uses [Kotlin](https://kotlinlang.org/) and its functional power to get things done!  No proxy/CGLib, no code generation, no introspection. Just functional Kotlin and DSL magic ;)

KOIN is a very small library, that aims to be as simple as possible and let's you write dependency injection in a breath.

*Just describe your stuff and inject it!*

#### New web site is coming :)

### What's up?

Check the latest changes in [What's New](https://github.com/Ekito/koin/wiki/What's-new-%3F). For users using a version prior to Koin 0.6.x, please refer the [migrating to 0.6.0](https://github.com/Ekito/koin/wiki/Migrating#migrating-to-06x) page to understand the latest changes. 

# Getting Started

## Gradle

Check that you have the `jcenter` repository. Choose the needed depedency:

```gradle

// Add Jcenter to your repositories if needed
repositories {
        jcenter()    
}

// Koin for Android
compile 'org.koin:koin-android:0.6.1'

// Koin Testing tools
testCompile 'org.koin:koin-test:0.6.1'
```

### Setup your Application

To start KOIN and your modules, you just have to use the `startKoin()` function in your Android *Application* class like below:

```Kotlin
class MainApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin(this, /* your list of modules */)
    }
}
```

The `startKoin` function requires an `Application` instance, and a a list of modules to run. 

## Declaring your dependencies

To declare your dependencies, you have to declare it in **modules**.

To create a module, make a class and **extends** your `AndroidModule`. Implements the `context()` function by using the `applicationContext` function builder like below:

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
* In **any Kotlin component**: injection is made **by constructor**

```Kotlin
// In Android class, use the by inject() operator
class WeatherActivity() : AppCompatActivity() {

    // inject my Presenter 
    val presenter by inject<WeatherPresenter>()

    // you can use your injected dependencies anywhere
}
```

```Kotlin
// In pure Kotlin class, All is injected in constructor
class WeatherPresenter(val weatherRepository: WeatherRepository) {

    // you can use your dependencies here
}
```

For ViewModel or others classes, use the `KoinComponent` interface.

## Koin Components

`KoinComponent` is a Kotlin **interface** to help you **bring the Koin features on any class**. By adding this interface, you will be able to use following functions:

* Injection by `inject()` & `property()`
* Write any property with `setProperty()`
* release a context with `releaseContext()`
* release some properties with `releaseProperties()`

You need to start a Koin context (usally `startKoin()`), to be able to use any module and dependencies.

In Android, the following classes have already *KoinComponent* features: `Application`,`Context`, `Activity`, `Fragment`, `Service`



## Working with properties

Declare any property from any `KoinComponent` :

```Kotlin
// Set property key with its value
setProperty("key",value)
```

You can also use any property in your Koin module with `getProperty("key")` or inject in an Android class with `by property("key")`

You can also easily bind any Android property:


```Kotlin
// bind R.string.server_url to Koin WeatherModule.SERVER_URL
bindString(R.string.server_url, WeatherModule.SERVER_URL)
```

Android/Koin property binding is available for int/string/boolean. 


## Named dependencies

You can provide a name to a provided component:

```Kotlin
class WeatherModule : AndroidModule() {
    override fun context() = applicationContext {
           provide("MyPresenter") { WeatherPresenter() }
    }
}
```

To get a component with its name, in an Android class:
```Kotlin
class WeatherActivity : AppcompatActivity(){
    val presenter by inject<WeatherPresenter>("MyPresenter")
}
```

or in constructor:
```Kotlin
class WeatherModule : AndroidModule() {
    override fun context() = applicationContext {
           provide("MyPresenter") { WeatherPresenter() }
           // inject name dependency
           provide { WeatherView(get("MyPresenter")) }
    }
}
```


## Managing context life cycle

One of the biggest value of Koin, is the ability to drop any instances from a given context, to suits your components life cycle. At any moment, you can use the `releaseContext()` function to release all instances from a context.

You can use the `ContextAwareActivity` or `ContextAwareFragent` to automatically drop an associated context:

```Kotlin
// A module with a context
class WeatherModule : AndroidModule() {
    override fun context() = applicationContext {
        context(name = "WeatherActivity") {
            provide { WeatherPresenter(get(), get()) }
        }
    }
}

class WeatherActivity : ContextAwareActivity(), WeatherContract.View {
    
    // associated context name
    override val contextName = "WeatherActivity"
    
    override val presenter by inject<WeatherPresenter>()

    //will call releaseContext("WeatherActivity") on onPause() - drop WeatherPresenter instance
}
```

Up to you to adapt it to your project if need, you are not forced to use those ContextAware components. You can make like follow:

```kotlin

abstract class MyCustomActivity : AppCompatActivity() {

    abstract val contextName: String

    override fun onPause() {
        releaseContext(contextName)
        super.onPause()
    }
}

```


## Checking your modules

KOIN is an internal DSL: all your modules evolves directly with your code (if you change a component, it will also impact your modules). 

You can check your modules with `KoinTest.dryRun()` (launch all your modules and try to inject each component). Better is to place it in your tests folder and check it regulary - ensure everything is injected correctly.

in a JUnit test file:

```kotlin
class MyTest : KoinTest {
	@Test
	fun dryRun(){
	     // start Koin
	     startKoin(/* list of modules */)
	     // dry run of given module list
	     dryRun()
	}
}
```


## Testing

You can also use Koin for your tests. You can extend the `KoinTest` interface to inject any component from Koin context: 

```kotlin
class LocalWeatherPresenterTest : KoinTest {
    
    // Directly injected
    val presenter by inject<WeatherContract.Presenter>()

    @Mock lateinit var view: WeatherContract.View

    @Before
    fun before() {
        // Mocks
        MockitoAnnotations.initMocks(this)
        // Koin context
        startKoin(testLocalDatasource())

        presenter.view = view
    }

    @Test
    fun testDisplayWeather() {
        Assert.assertNotNull(presenter)

        val locationString = "Paris, france"
        presenter.getWeather(locationString)

        Mockito.verify(view).displayWeather(any(), any())
    }
}

```

# The Sample App

The [*koin-sample-app*](https://github.com/Ekito/koin/tree/master/samples/android-weatherapp) application offers a complete application sample, with MVP Android style. 


# Documentation

#### Documentation rewriting is in progress ... check incoming new web site for more info.

# Articles

* [Moving from Dagger to Koin - Simplify your Android development](https://medium.com/@giuliani.arnaud/moving-from-dagger-to-koin-simplify-your-android-development-e8c61d80cddb) - ([Kotlin Weekly issue 66](http://mailchi.mp/kotlinweekly/kotlin-weekly-66?e=e8a57c719f) & [Android Weekly issue 282](http://androidweekly.net/issues/issue-282))
* [Kotlin Weekly #64](http://mailchi.mp/kotlinweekly/kotlin-weekly-64?e=e8a57c719f)
* [Insert Koin for dependency injection](https://www.ekito.fr/people/insert-koin-for-dependency-injection/)
* [Better dependency injection for Android](https://proandroiddev.com/better-dependency-injection-for-android-567b93353ad)

# Contact & Support

## Slack
Check the [kotlin slack community](https://kotlinlang.org/community/) and join **#koin** channel

## Stackoverflow

Use the `Koin` & `Kotlin` tags to mark your questions. [Koin @ Stackoverflow](https://stackoverflow.com/search?q=koin)

## Github
Don't hesitate to open an issue to discuss about your needs or if you don't a feature for example.

