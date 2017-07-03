# Insert Koin to make dependency injection

KOIN is a dependency injection framework that uses Kotlin and its functional power to get things done!  No proxy/CGLib, No code generation, No introspection. Just functional Kotlin and DSL magic ;)

![logo](./img/insert_koin_android_logo.jpg)

# Table of Contents

1. [What's new?](#what-s-new)
2. [Setup](#setup)
2. [Getting Started](#getting-Started)
	1. [Write a Module](#write-a-module)
	2. [Setup your Android Application](#setup-your-android-application)
	3. [Start injecting](#start-injecting)
4. [Sample App](#sample-app)
5. [Koin for Android](#Koin-for-android)
	1. [Module Class](#module-class)	
	2. [Resolve a dependency](#resolve-a-dependency)
	2. [Type Binding](#type-binding)
	2. [Android Application Context](#android-application-context)
	3. [Starting Koin Context](#starting-koin-context)
	4. [Koin Everywhere](#koin-everywhere)
	5. [Easy Dependency Injection](#easy-dependency-injection)
	6. [Loading mulitple modules](#loading-mulitple-modules)
	7. [Scopes](#scopes)
6. [More about Koin](#more-about-koin)
	1. 	[Safely resolving a dependency](#safely-resolving-a-dependency)
	2. [Compile time & Lazy linking](#compile-time--lazy-linking)
	3. [Using properties](#using-properties)
7. [Roadmap](#roadmap) 

## What's new?

### Changes in 0.2.x

**Koin & Koin-Android project has meen merged**

_DSL_

* Module class must now override `context()` function and return a Context object (instead of `onLoad()`). You have to use the following syntax:

```Kotlin
class NetworkModule : Module() {
    override fun context() = declareContext { ... }
}
```
* You can [bind types](#type-binding) for provided definitions

_Koin_

* Koin builder now takes module instances (instead of module classes):

```Kotlin
// fill applicationContext for Koin context
val ctx = Koin().init(applicationContext).build(MyModule(),OtherModule()...)
```

* Context has been move to KoinContext. Context class is kept for DSL use
* factory, stack operators have been removed, for the scope fatures
* delete/remove replcaed with `release()` Scope operation
* import is replaced with module instances load
* All reflection & kotlin-reflect code have been removed

_Scope_

* You can declare scopes in your modules, with `scope {}` operator. See [scopes](#scopes) section
* Release scope instances with `release()`

_Android_

* `KoinAwareContext` interface used to setup Android application (KoinApplication & KoinMultidexApplication removed)
* `by inject<>()` operator to inject any Activity or Fragment


## Setup

Check that you have `jcenter` repository and add the following gradle dependency for Android:

```gradle
compile 'org.koin:koin-android:0.2.0'
```

For pure Kotlin usage, you can just use the core module:

```gradle
compile 'org.koin:koin-core:0.2.0'
```

## Getting Started

First of all, you need to write a module. A module **gather your components definitions** and allow it to be loaded by Koin and injected in your application. Keep in mind, that **injection by constructor** is the default strategy targeted by Koin.

### Writing a module

Write a class that extends [AndroidModule](), and override the `context()` function and use to `declareContext` function to define a context like below:

```Kotlin
class MyModule : AndroidModule() {
    override fun context() =
        declareContext {
			// declare dependency here ...
			provide { createOkHttpClient() }
	       }
    }
}

fun createOkHttpClient() : OkHttpClient { //create OkHttpClient ...}
```
Your context is provided by the `context()` function, and described via the `declareContext` function. This unlock the **Koin DSL**:

* `provide { /* component definition */ }` declare a component for your [Module](#module)
* `bind {/* compatible type */}` [bind](#type-binding) a compatible type for *provided definition*
* `get()` inject a component dependency
* `scope {/* scope class */}` defineor reuse a [scope](#scopes) current module's definitions

**AndroidModule** also gives you the possibility to retrieve Android specific resources directly in your module context (*ApplicationContext*, *Resources* & *Assets*). e.g: Get an Android string in your module:

```Kotlin
resources.getString(R.string.server_url)
```

### Setup your Android Application

To start your module, you must build it: 

```Kotlin
val myContext = Koin().init(applicationInstance).build(MyModule())
```

This will return a [KoinContext]() object, your Koin context. **Don't forget to use the** `init()` function to init *Android context* injection, else you won't be able to load your modules & extensions.

Koin also proposes a [KoinContextAware]() interface, to help define your Koin context and bring you the context definition all over your app. Configure your app like below:

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


### Start injecting

Once you have your Koin context, you can get **inject your components from anywhere** (Application, Activity, Fragment) by using `by inject<>()` function:

```Kotlin
class MainActivity : AppCompatActivity() {

    // inject my WeatherService 
    val weatherService by inject<WeatherService>()
}
```

That's it !


## Sample App

You can find a complete sample app here: [github sources]()

This sample demo basics concepts:

* A [Module]() -- Module example to create okhttpClient, retrofit and web service component
* An [Application]() -- Setup for loading `MyModule`
* An [Activity]() -- Inject the `WeatherService` component into Activity

## Koin for Android

### Module class

Write a class that extends the [AndroidModule]() class (an Android specialized Koin [Module]()). Open a declaration section wihtin the `declareContext` function, for the `context()` method. The `declareContext` function brings you the Koin context and its DSL to work with your dependencies. All the features below can be used here, or on a built context from **Koin builder**.

The `AndroidModule` brings Androdi resouces within modules:

* `applicationContext` - registered Android application context
* `resources` - Android resources
* `assets` - Android assets

YHere is a complete [example]() below:

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

### Providing a bean definition 

Whitin `declareContext` function or on a Koin context object, you can use the `provide` function for functional declaration (lambda containing your component instantiation). This will provide a **singleton** instance for your component.

```kotlin
declareContext {
    // provide Singleton definition for ServiceB
    provide { ServiceB()}
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

Instance resolution will be made lazily: instance will be created/resolve when you will ask for it.


### Type Binding

Once you have declare a component, you can specify additional type that can be used to instantiate it with `bind {}`. For example:

```kotlin
// interface
interface Processor {
    fun processing()
}
// component with interface
class ServiceB() : Processor {//...}

//in a module context
class MyModule : AndroidModule() {
	 override fun context() = declareContext {
	    // definie ServiceB and allow binding with Processor
	    provide { ServiceB()} bind { Processor::class }
	}
}
```
This way, we provide a component that can be bound to ServiceB::class &  DoSomething::class.

```kotlin
val context = Koin().build(MyModule())
val processor = context.get<Processor>()
processor.processing()
```

### Android Application Context

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


### Starting Koin Context

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

When you have setup your project with `KoinContextAware`, you can use the **Android Koin extensions** to retrieve your **KoinContext** from anywhere. Extensions are available on:

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
class ModuleB : AndroidModule() {
    override fun context() =
        declareContext {
            provide { ServiceB() }
        }
    }
}

class ModuleA : AndroidModule() {
    override fun context() =
        declareContext {
			  // Resolve ServiceB
            provide { ServiceA(get()) }
        }
    }
}

// load them all
val context = Koin().build(ModuleB(), ModuleA())
```

Resulting context will contain components from ModuleB and ModuleA.


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

## More about Koin

### Safely resolving a dependency

If you are not sure about a dependency, you can resolve it safely with `getOrNull()`:

```kotlin
val ctx = Koin().build(MyModule())

// will throw NoBeanDefFoundException if not found
val serviceA = ctx.get<ServiceA>()

// will return null if not found
val serviceA = ctx.getOrNull<ServiceA>() 
```

### Compile time & Lazy linking

All your definitions are checked at compile time (your code is compiling), and the linking with `get()` is resolved lazily.

The `get()` function ask the container for given definition. But all of this done lazily, when you ask the dependency. That means that your can provide later definition:

```kotlin
// used classes
class ServiceB() {//...}
class ServiceA(val serviceB: ServiceB) {//...}

class MyModule : Module() {
    override fun context() {
        declareContext {
            // will resolve for ServiceB
            provide { ServiceA(get()) }
        }
    }
}
```

From here you can:

1. load another module with it, at start. Check [Loading mulitple modules](#loading-mulitple-modules) section
2. provide later, a definition on context


In case 2, provide a late definition:

```kotlin
val ctx = Koin().build(SampleModule())
// Provide a definition for ServiceB
ctx.provide { ServiceB() }
```

### Using properties

You can **inject properties into your context**, directly at start with a map of values via `properties()` function:

```kotlin
val ctx = Koin()
        .properties(mapOf("myVal" to "VALUE!"))
        .build(SampleModule())
```

And retrieve your value from context, with `getProperty()`:

```kotlin
val myVal = ctx.getProperty<String>("myVal")
```

In module definition, this will be done lazily:

```kotlin
class ServiceD(val myVal :String){//...}

class SampleModule : Module() {
    override fun context() =
        declareContext {
            provide { ServiceD(getProperty<String>("myVal"))}
        }
    }
}
```

You can **resolve safely a property** with `getPropertyOrNull()`, which lets you retrieve a nullable property.

To set a property on a context, use the `setProperty(key,value)` function.


## Roadmap

* Instrumented Tests & RoboElectric tests
* Android lifecycle Architecture
* Javadoc/Dokka Documentation
* get/set component with qualifiers