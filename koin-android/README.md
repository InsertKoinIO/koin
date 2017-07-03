# Insert Koin to make dependency injection - For Android

KOIN is a dependency injection framework that uses Kotlin and its functional power to get things done!  No proxy/CGLib, No code generation, No introspection. Just functional Kotlin and DSL magic ;)

![logo](./img/insert_koin_android_logo.jpg)

**Koin Android is a [Koin](https://github.com/Ekito/koin) library extension to help you make easily dependency injection on your Android project**

# Table of Contents

1. [What's new?](#what-s-new)
2. [Setup](#setup)
2. [Getting Started](#getting-Started)
	1. [Write your Module](#write-your-module)
	2. [Setup your Android Application](#setup-your-android-application)
	3. [Start injecting](#start-injecting)
4. [Sample App](#sample-app)
5. [Koin for Android](#Koin-for-android)
	1. [Module](#module)	
	2. [Resolve a dependency](#resolve-a-dependency)
	2. [Type Binding](#type-binding)
	2. [Android Application](#android-application)
	3. [Setup Your App](#setup-your-app)
	4. [Koin Everywhere](#koin-everywhere)
	5. [Easy Dependency Injection](#easy-dependency-injection)
	6. [Loading mulitple modules](#loading-mulitple-modules)
	7. [Scopes](#scopes)
6. [Koin Core Features](#koin-core-features)
7. [Roadmap](#roadmap) 

## What's new?

### 0.2.x

_DSL_

* Module class must now override `context()` function and return a Context object (instead of `onLoad()`). You have to use the following syntax construction:

```Kotlin
class NetworkModule : Module() {
    override fun context() = declareContext { ... }
}
```
* You can define [scope](#scopes) for a module, and [bind types](#type-binding) for provided components

_Koin_

* Koin builder now takes modules instances (instead of module classes):

```Kotlin
// fill applicationContext for Koin context
val ctx = Koin().init(applicationContext).build(MyModule())
```

* factory, stack operators have been removed
* import is replaced with module instances load
* delete/remove replcaed with `release()` Scope operation
* All reflection & kotlin-reflect code have been removed

_Scope_

* You can declare scopes in your modules, with `scope {}` operator. See [scopes](#scopes) section
* Release scope instances with `release()`

_Android_

* `KoinAwareContext` interface used to setup Android application (KoinApplication & KoinMultidexApplication removed)
* `by inject<>()` operator to inject any Activity or Fragment


## Setup

Check that you have `jcenter` repository and add the following gradle dependency:

```gradle
compile 'org.koin:koin-android:0.2.0'
```

## Getting Started

First of all, **you need to write a Module to gather your components definitions**. Then you will be ready to load your module and inject yoru components. Keep in mind, that **injection by constructor** is the default strategy targeted by Koin.

### Writing your Android Module

Write a class that extends [AndroidModule](https://github.com/Ekito/koin-android/blob/master/koin-android/src/main/kotlin/org/koin/android/AndroidModule.kt) like below:

```Kotlin
class MyModule : AndroidModule() {
    override fun context() =
        declareContext {
		// declare dependency here ...		
		provide { createOkHttpClient() }
        }
    }
}

fun createOkHttpClient() : OkHttpClient {//create OkHttpClient ...}
```
Your context is provided by the `context()` function, and described via the `declareContext` function. This unlock the Koin DSL:

* `provide { /* component definition */ }` declare your component for your [Module](#module)
* `bind {/* compatible type */}` allow [type binding](#type-binding) `Class` type for *provided definition*
* `get()` resolve the component dependency
* `scope {/* scope class */}` creates a [scope](#scopes) for all definitions in current module


**AndroidModule** also gives you the possibility to retrieve your `ApplicationContext`, `Resources` & `Assets` directly in your module context. e.g: Get an Android string with:

```Kotlin
resources.getString(R.string.server_url)
```

### Setup your Android Application

To start your module, you must build it this way: 

```Kotlin
val myContext : KoinContext = Koin().init(/* applicationInstance */).build(MyModule())
```

This will return a [KoinContext]() on which you will get your components instances. **Don't forget to use the** `init()` function to init *Android context*, else you won't be able to load your AndroidModule.

Koin proposes a [KoinContextAware]() interface, to help define your Koin context and bring you the context definition all over your app:

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
    }
}
```

By using `KoinContextAware` interface, you will be able to use the **Koin Android extensions** in your Android Application.


### Start injecting

Once you have your Koin context, you can get **inject your components from anywhere** (Application, Activity, Fragment) by using `by inject<>()`. Below a way of lazy injection dependency in an Activity:

```Kotlin
class MainActivity : AppCompatActivity() {

    // inject my WeatherService 
    val weatherService by inject<WeatherService>()
}
```

Here you're ready to use it !


## Sample App

You can find a complete sample app here: [github sources](https://github.com/Ekito/koin-android/tree/master/app)

This sample demo basics concepts:

* A [Module]() -- Module example to create okhttpClient, retrofit and web service component
* An [Application](https://github.com/Ekito/koin-android/blob/master/app/src/main/kotlin/koin/sampleapp/MainApplication.kt) -- Setup for loading `MyModule`
* An [Activity](https://github.com/Ekito/koin-android/blob/master/app/src/main/kotlin/koin/sampleapp/MainActivity.kt#L26) -- Inject the `WeatherService` component into Activity

## Koin for Android

### Module

The `AndroidModule` is a [Koin module](https://github.com/Ekito/koin#creating-a-module)  with Android aware capcity:

* applicationContext - registered Android application context
* resources - Android resources
* assets - Android assets

**Note :** You must build your context with `init()` to inject your Android Application before using it in your module.

You can follow the [example below]():

```Kotlin
class MyModule : AndroidModule() {
	override fun context() =
	    declareContext {
		// Scope MainActivity
		scope { MainActivity::class }

		// provided WeatherService
		provide { WeatherService(get()) }
		// create OkHttpClient
		provide { createClient() }
		// Create retrofit services with Android URL String
		provide { retrofitWS(get(), resources.getString(R.string.server_url)) }
	    }

    private fun createClient(): OkHttpClient {}
    private fun retrofitWS(okHttpClient: OkHttpClient, url: String): WeatherWS {}
    
}
```

### Resolve a dependency

In a Koin module, you can resolve an component instance with the `get()` function:

```kotlin
// used classes
class ServiceB() {//...}
class ServiceA(val serviceB: ServiceB) {//...}

//in a module context
declareContext {
    // Resolving needed dependency with get()
    provide { ServiceA(get())}
}
```

### Type Binding

When providing a component, you can also specify what type it can also bind:

```Kotlin
interface Proecssor {
    fun process()
}
class ServiceB() : Proecssor {//...}

provide { ServiceB() } bind { Proecssor::class }
```

When retrieving your component, you will be able to get it:

```Kotlin
// Retrieve with Proecssor
val processor = context.get<Proecssor>()
```


### Android Application

Koin-Android provides a way to inject Android `ApplicationContext` for loading your modules. This way you will gain access to it, in your module context:

```Kotlin
// fill applicationContext for Koin context
val ctx = Koin().init(applicationContext).build(modules...)
```

This also allows to bring resouces and assets in your [AndroidModule]() class.

```Kotlin
// use an Android String for URL
provide { retrofitWS(get(), resources.getString(R.string.server_url)) }
```


### Setup your app

To setup your App, you need to use [KoinContextAware]() interface on your Application class:

```Kotlin
class MainApplication : Application(), KoinContextAware {

    /**
     * Koin context
     */
    lateinit var context: KoinContext

    /**
     * KoinContextAware getter
     */
    override fun getKoin(): KoinContext = context

    override fun onCreate() {
        super.onCreate()

        // init Koin with NetworkModule module
        context = Koin().init(this).build(MyModule())
    }
}
```


### Koin everywhere

When you have setup your project with `KoinContextAware `, you can use the **Android Koin extensions** to retrieve your **KoinContext** from anywhere. Extensions are available on:

* Application
* Activity
* Fragment

Just use the `getKoin()` function to get your Koin context.


### Easy dependency injection

When Koin has been configured for your application, you can use the `by inject<>()` function to inject your Activity or Fragment with your desired component:

```Kotlin
class MainActivity : AppCompatActivity() {

    // inject my WeatherService 
    val weatherService by inject<WeatherService>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
    	super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_main)
       // can use injections from here
       weatherService.getWeather()
    }
}
```

You can also get manually any component from the Koin context, with the `getKoin().get<>()` function:

```Kotlin
val weatherService = getKoin().get<WeatherService>()
```


### Loading mulitple modules

You can load several modules, into in your context like below:

```kotlin
class ModuleB : Module() {}
class ModuleA : Module() {}

// load them all
val context = Koin().build(ModuleB(), ModuleA())
```


### Scopes

**A scope is an isolated space**, where you gather components instances. **By default, all components are setup at root scope**. Each component will be resolved against its scope. Let's take an example:

```kotlin
class MainActivityModule : Module() {
    override fun context() =
            declareContext {
                // Scope MainActivity
                scope { MainActivity::class }
                
                // provided WeatherService
                provide { WeatherService(get()) }
            }
}

class NetworkModule : Module() {
    override fun context() =
            declareContext {                
		// create OkHttpClient
                provide { createClient() }
                // Create retrofit services with Android URL String
                provide { retrofitWS(get(), resources.getString(R.string.server_url)) }
            }
}

// run context
val context = Koin().build(MainActivityModule(), NetworkModule())
```

The context will contains 2 scopes:

* Root scope : NetworkModule instances
* MainActivity scope : MainActivityModule instances

When you retrieve `OkHttpClient ` component, you will resolve it from **root scope**. Each time you retrieve `WeatherService `, you will resolve it from **MainActivity scope**.

The scope idea is to allow handle/differentiate module lifecycles & instances. You can release an entire scope, example for an Activity module:

```Kotlin
// release all instances from ServiceA scope
context.release(MainActivity::class)
```

## Koin Core Features

Check the [Koin core project](https://github.com/Ekito/koin/) for the all core features.


## Roadmap

* Instrumented Tests & RoboElectric tests
* Android lifecycle Architecture
