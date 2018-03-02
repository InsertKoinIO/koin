![logo](./img/insert_koin_android_logo.jpg)

## What's KOIN?

A small library for writing dependency injection in a concise and pragmatic way. No proxy, no code generation, no introspection. Just DSL and functional Kotlin magic!

`Declare, Start, Inject`

## Official website - https://insert-koin.io

* All documentation, sample and references has been move to our website. Check the official website to get started: [insert-koin.io](https://insert-koin.io)
* Koin samples project are located here: [koin-samples @ github](https://github.com/Ekito/koin-samples)
* The essentials of Koin API for Android developer: [Android Developer Quick Reference](https://insert-koin.io/docs/1.0/quick-reference/android/) 

You can check the **[getting started section](https://insert-koin.io/docs/1.0/getting-started/introduction/)** from our website, to discover Koin with the favorite platform. Or follow the snippets below.

## Setup

### Actual Version

```gradle
koin_version = '0.9.0'
```

### Gradle
Check that you have the `jcenter` repository. 

```gradle
// Add Jcenter to your repositories if needed
repositories {
	jcenter()    
}
```

Choose your the Koin module:

```gradle
// Koin for Kotlin
compile "org.koin:koin-core:$koin_version"

// Koin for Android
compile "org.koin:koin-android:$koin_version"

// Koin for Android Architecture Components
compile "org.koin:koin-android-architecture:$koin_version"

// Koin for Spark Kotlin
compile "org.koin:koin-spark:$koin_version"

// Koin for Ktor Kotlin
compile "org.koin:koin-ktor:$koin_version"
```


# Getting Started

## Android

### Declaring our first dependencies

Let's create a Repository to provide some data (`giveHello()`):

```kotlin
interface Repository {
    fun giveHello()
}

class MyRepository() : Repository {
    override fun giveHello() = "Hello Koin"
}
```

A Presenter class, for consuming this data:

```kotlin
// Use Repository - injected by constructor by Koin
class MyPresenter(val repository : Repository){
    fun sayHello() = repository.giveHello()
}
```

Use the `applicationContext` function to **declare a module**. Let's write our dependencies via **constructor injection**:

```kotlin
// Koin module
val myModule : Module = applicationContext {
    provide { MyPresenter(get()) } // get() will resolve Repository instance
    provide { MyRepository() as Repository }
}
```

### Start Koin

Now that we have a module, let's start it with Koin. Open your application class, or make one (don't forget to declare it in your manifest.xml). Just call the `startKoin()` function:

```kotlin
class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin(this, listOf(myModule))
    }
}
```

### Injecting dependencies

The `MyPresenter` component will be created with `Repository` instance. To get it from our Activity, let's inject it with the `by inject()` delegate injector (we can't create Activitiy instances): 

```kotlin
class MyActivity : AppCompatActivity(){

    // Inject MyPresenter
    val presenter : MyPresenter by inject()

    override fun onCreate() {
        super.onCreate()
        // Let's use our presenter
        Log.i("MyActivity","presenter : ${presenter.sayHello()}")
    }
}
```

### Testing



# Quick reference

## Android




# The KOIN DSL in 5 minutes

## Keywords

A quick recap of the Koin DSL keywords:

* `applicationContext` - create a Koin Module
* `factory` - provide a *factory* bean definition
* `bean` - provide a bean definition
* `bind` - additional Kotlin type binding for given bean definition
* `get` - resolve a component dependency
* `getProperty` - resolve a property
* `context` - declare a logical context

**Deprecated**: `provide` has been deprecated in favor to aliases. `bean ~ provide` and `factory ~ provide(isSingleton=false)`


## Writing a module

Here below the Koin DSL keywords you need to know, to write your module. To declare a module, use the `applicationContext` function:

```kotlin
val myModule = applicationContext {
   // your dependencies here
}
```

The `applicationContext` lambda function is where you will write your definitions. `myModule` is the *Koin module*

To define your components, use the following keywords:

* `bean` - define a singleton (create only one instance)
*  `factory` - define a factory (create a new instance each time)

*_Deprecated_*: `provide` keyword is now deprecated. Please use `bean` or `factory`

Below a simple definition of a `MyRepository` singleton:

```kotlin
class MyRepository()

val myModule = applicationContext {
   bean { MyRepository() }
}
```

## Binding interfaces or several types

To bind a component with its interface, we have 2 solutions. Given an interface and its implmentation:

```kotlin
class MyRepositoryImpl()
interface MyRepository
```

We can write it:

* `bean { MyRepositoryImpl() as MyRepository }` - will create an instance of type `MyRepository`
* `bean { MyRepositoryImpl() } bind MyRepository::class` - will create an instance of type `MyRepositoryImpl` and will accept to bind on type `MyRepository`

*You can use the `bind` keyword with a class several times: `bind Class1::class bind Class2::class`

## Multiple definitions of the same type

If you have mulitple definitions of the same type, Koin can't guess which instance to use. Then, you have to name each instance to clearly specify which instance to use. `bean` and `factory` have the `name` parameter (default parameter).

```kotlin
class MyLocalRepositoryImpl()
class MyRemoteRepositoryImpl()
interface MyRepository
```

we will write our module like:

```kotlin
val myModule = applicationContext {
   bean("local") { MyLocalRepositoryImpl() as  MyRepository }
   bean("remote") { MyRemoteRepositoryImpl() as  MyRepository }
}
```


## Resolving a dependency

Koin push you to use **constructor injection** to bind your component. Given classes:

```kotlin
class ComponentA()
class ComponentB(val componentA : ComponentA)
```

We wil use the `get()` function to resolve a dependency:

```kotlin
val myModule = applicationContext {
   bean { ComponentA() }
   bean { ComponentB(get()) }
}
```



## Using multiple modules




## Articles


## Follow us & Contact

### Website - [https://insert-koin.io](https://insert-koin.io)

### Twitter - [@insertkoin_io](https://twitter.com/insertkoin_io)

### Medium - [Koin Developers Hub](https://medium.com/koin-developers)

### Slack - [Kotlin Slack](https://kotlinlang.org/community/) on **#koin** channel







