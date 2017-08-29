# Insert Koin to make dependency injection

KOIN is a dependency injection framework that uses Kotlin and its functional power to get things done!  No proxy/CGLib, No code generation, No introspection. Just functional Kotlin and DSL magic ;)

![logo](./img/insert_koin_android_logo.jpg)

## Setup

Check that you have `jcenter` repository. Add the following gradle dependency for your Android app:

```gradle
compile 'org.koin:koin-android:0.2.2'
```
or if you need android-support classes:

```gradle
compile 'org.koin:koin-android-support:0.2.2'
```

## Getting Started

First of all, you need to write a module. A module **gathers your components definitions** and allows it to be loaded by Koin and injected in your application. Keep in mind, that **injection by constructor** is the default strategy targeted by Koin.

### Writing a module

Write a class that extends [AndroidModule](https://github.com/Ekito/koin/blob/master/koin-android/koin-android/src/main/kotlin/org/koin/android/AndroidModule.kt), overrides the `context()` function and uses the `declareContext` function to define a context like below:

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

Your context is provided by the `context()` function and described via the `declareContext` function. This unlocks the **Koin DSL**:

* `provide { /* component definition */ }` declares a component for your [Module](https://github.com/Ekito/koin/wiki#module-class)
* `bind {/* compatible type */}` [bind](https://github.com/Ekito/koin/wiki#type-binding) a compatible type for *provided definition*
* `get()` inject a component dependency
* `scope {/* scope class */}` define or reuse a [scope](https://github.com/Ekito/koin/wiki#scopes) current module's definitions

Below a more complete [module example](https://github.com/Ekito/koin/blob/master/koin-android/app/src/main/kotlin/koin/sampleapp/koin/MyModule.kt), with sample weather app:

```Kotlin
class MyModule : AndroidModule() {

    val TAG = MyModule::class.java.simpleName

    override fun context() =
            declareContext {
                // Scope MainActivity
                scope { MainActivity::class }
		
                // provided components
                provide { WeatherService(get()) }
                provide { createClient() }
		// build retrofit web service with android String resource url
                provide { retrofitWS(get(), resources.getString(R.string.server_url)) }
            }

    private fun createClient(): OkHttpClient {//return OkHttpClient}

    private fun retrofitWS(okHttpClient: OkHttpClient, url: String): WeatherWS { // create retrofit WeatherWS class}
}
```

**AndroidModule** also gives you the possibility to retrieve Android specific resources directly in your module context (*ApplicationContext*, *Resources* & *Assets*). e.g: Get an Android string in your module:

```Kotlin
resources.getString(R.string.server_url)
```

### Setup your Application

To start your module, you must build it: 

```Kotlin
val myContext = Koin().init(applicationInstance).build(MyModule())
```

This will return a [KoinContext](https://github.com/Ekito/koin/blob/master/koin-core/src/main/kotlin/org/koin/KoinContext.kt) object. Koin proposes the [KoinContextAware](https://github.com/Ekito/koin/blob/master/koin-android/koin-android/src/main/kotlin/org/koin/android/KoinContextAware.kt) interface, to help define and bring your Koin context all over your app. Configure it like below:

```Kotlin
class MainApplication : Application(), KoinContextAware {

     /**
     * Koin context
     */
    lateinit var context: KoinContext

    /**
     * KoinContextAware - Retrieve Koin Context
     */
    override fun getKoin(): KoinContext = context

    override fun onCreate() {
        super.onCreate()
        // insert Koin !
        context = Koin().init(this).build(MyModule()) 
        // ...
    }
}
```

By using `KoinContextAware` interface, you will be able to use the **Koin Android extensions** in your Android Application.

**Don't forget to use the** `init()` function to init *Android context* injection, else you won't be able to load your modules & extensions.


### Start injecting

Once you have your Koin context, you can **inject your components from anywhere** (Application, Activity, Fragment) by using `by inject<>()` function:

```Kotlin
class MainActivity : AppCompatActivity() {

    // inject my WeatherService 
    val weatherService by inject<WeatherService>()
    
    ...
}
```

That's it !


## Sample App

You can find a complete sample app here: [github sources](https://github.com/Ekito/koin/tree/master/koin-android/app)

This sample shows the basic concepts of:

* A [Module](https://github.com/Ekito/koin/blob/master/koin-android/app/src/main/kotlin/koin/sampleapp/koin/MyModule.kt) -- Module example to create okhttpClient, retrofit and web service component
* An [Application](https://github.com/Ekito/koin/blob/master/koin-android/app/src/main/kotlin/koin/sampleapp/MainApplication.kt) -- Setup for loading module with Android application
* An [Activity](https://github.com/Ekito/koin/blob/master/koin-android/app/src/main/kotlin/koin/sampleapp/MainActivity.kt#L26) -- Inject `WeatherService` into MainActivity

## Documentation

Check the [wiki](https://github.com/Ekito/koin/wiki) for complete documentation.

## Articles

* [Insert Koin for dependency injection](https://www.ekito.fr/people/insert-koin-for-dependency-injection/)
* [Better dependency injection for Android](https://proandroiddev.com/better-dependency-injection-for-android-567b93353ad)

## Contact & Support

Check the [kotlin slack](https://kotlinlang.org/community/) - #koin channel


