![logo](./doc/img/banner_2000.png)

## What is KOIN?

A pragmatic lightweight dependency injection framework for Kotlin developers.

Written in pure Kotlin, using functional resolution only: no proxy, no code generation, no reflection.

`Koin is a DSL, a light container and a pragmatic API`

### Official Documentation now on https://insert-koin.io

All documentation, sample and references has been move to our website. Check the official website to get started: [insert-koin.io](https://insert-koin.io). Koin samples project are located here: [koin-samples](https://github.com/Ekito/koin-samples)

You can check the [getting started](https://insert-koin.io/docs/1.0/getting-started/introduction/) section from our website, to discover Koin with the favorite platform. Or follow the snippets below.


### Contact & Support

Follow us on Twitter for latest news: [@insertkoin_io](https://twitter.com/insertkoin_io)

Need help? Come on slack [Koin channel](https://kotlinlang.slack.com/?redir=%2Fmessages%2Fkoin) from Kotlin slack. Or just open an issue on [Github issues](https://github.com/Ekito/issues) to share your problem.


# Setup

## Actual Version

```gradle
koin_version = '0.9.3'
```

## Gradle 

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

// Koin for JUnit tests
testCompile "org.koin:koin-test:$koin_version"
```

# Quickstart

* [Your first dependency with Android](#your-first-dependency-with-android)
* [Your first dependency with Android and ViewModel](#your-first-dependency-with-android-viewmodel)
* [Unit Testing with Koin](#unit-testing-with-koin)
* [The Koin DSL in 5 minutes](#the-koin-dsl-in-5-minutes)

# Getting Started

## Your first dependency with Android

### Gradle

```gradle
compile "org.koin:koin-android:$koin_version"
```

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
    factory { MyPresenter(get()) } // get() will resolve Repository instance
    bean { MyRepository() as Repository }
}
```

By using the *factory* definition for our presenter, we will obtain a new instance each time we ask about the `MyPresenter` class.

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

The `MyPresenter` component will be created with `Repository` instance. To get it from our Activity, let's inject it with the `by inject()` delegate injector (we can't directly create Activitiy instances from Koin): 

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


## Your first dependency with Android ViewModel

### Gradle

```gradle
compile "org.koin:koin-android-architecture:$koin_version"
```

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

A ViewModel class, for consuming this data:

```kotlin
// Use Repository - injected by constructor by Koin
class MyViewModel(val repository : Repository) : ViewModel(){
    fun sayHello() = repository.giveHello()
}
```

Use the `applicationContext` function to **declare a module**. Let's write our dependencies via **constructor injection**:

```kotlin
// Koin module
val myModule : Module = applicationContext {
    viewModel { MyViewModel(get()) } // get() will resolve Repository instance
    bean { MyRepository() as Repository }
}
```

We are also using the `viewModel` keyword to declare an Android ViewModel component.

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

The `MyViewModel` component will be created with `Repository` instance. To get it from our Activity, let's inject it with the `by viewModel()` delegate injector (we can't directly create Activitiy instances from Koin): 

```kotlin
class MyActivity : AppCompatActivity(){

    // Inject MyViewModel
    val myViewModel : MyViewModel by viewModel()

    override fun onCreate() {
        super.onCreate()
        // Let's use our ViewModel
        Log.i("MyActivity","ViewModel : ${myViewModel.sayHello()}")
    }
}
```

Or if you want to eagerly create your ViewModel in a function, just use the `getViewModel()`:

```kotlin
class MyActivity : AppCompatActivity(){

    override fun onCreate() {
        super.onCreate()
        
        val myViewModel : MyViewModel = getViewModel()

        // Let's use our ViewModel
        Log.i("MyActivity","ViewModel : ${myViewModel.sayHello()}")
    }
}
```

## Unit Testing with Koin

### Gradle Setup

First, add the Koin dependency like below:

```gradle
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    // Koin testing tools
    testcompile 'org.koin:koin-test:{{ site.current_version }}'
}
```

## Declared dependencies

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

Use the `applicationContext` function to declare a module. Let's declare our first component:

```kotlin
// Koin module
val myModule : Module = applicationContext {
    bean { MyPresenter(get()) } // get() will resolve Repository instance
    bean { MyRepository() as Repository }
}
```

## Writing our first Test

To make our first test, let's write a simple Junit test file and extend it with `KoinTest`. We will be able then, to use `by inject()` operators.

```kotlin
class FirstTest : KoinTest {

    val presenter : MyPresenter by inject()
    val repository : Repository by inject()

    @Before
    fun before(){
        startKoin(listOf(myModule))
    }

    @After
    fun after(){
        closeKoin()
    }

    @Test
    fun testSayHello() {
        assertEquals(repository.giveHello(), presenter.sayHello())
    }
}
```

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

Special keywords:

* `viewModel` - declare an Android ViewModel (koin-android-architecture only)
* `controller` - declare a SparkJava controller (koin-spark only)

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

## Using multiple modules / Module import

Every definition and module is lazy be default in Koin. This means that you can assemble several modules, by using the list of desired modules. Given some classes:

```kotlin
class ComponentA()
class ComponentB(val componentA : ComponentA)
```

And the two modules to declare it:

```kotlin
val myModule1 = applicationContext {
   bean { ComponentA() }
}
```

```kotlin
val myModule2 = applicationContext {
   bean { ComponentB(get()) }
}
```

Just start the module list together: 

```kotlin
// Android Start
startKoin(this,listOf(module1,module2))
```

```kotlin
// Kotlin/Spark Start
startKoin(listOf(module1,module2))
```


# Articles


### Koin on Medium: [Koin Developers Hub](https://medium.com/koin-developers)

### Last articles

* [Koin 0.9.2 — Maintenance fixes, new branding, roadmap for 1.0.0 & some other nice announces](https://medium.com/koin-developers/koin-0-9-2-maintenance-fixes-new-branding-roadmap-for-1-0-0-some-other-nice-announces-94f14648e4ad)
* [Koin 0.9.1 - Bug fixes & Improvments](https://medium.com/koin-developers/koin-0-9-1-bug-fixes-improvements-bug-fixes-d257cd2766fa)
* [Koin 0.9.0 - Getting close to stable](https://medium.com/koin-developers/koin-0-9-0-getting-close-to-stable-release-74df9bb9e181)
* [Unlock your Android ViewModel power with Koin](https://medium.com/@giuliani.arnaud/unlock-your-android-viewmodel-power-with-koin-23eda8f493be)
* [Koin + Spark = ❤️](https://www.ekito.fr/people/sparkjava-and-koin/)
* [koin 0.8.2 Improvements bugfixes and crash fix](https://medium.com/koin-developers/koin-0-8-2-improvements-bugfixes-and-crash-fix-6b6809fc1dd2)
* [Koin release 0.8.0](https://medium.com/koin-developers/koin-released-in-0-8-0-welcome-to-koin-spark-koin-android-architecture-f6270a7d4808)
* [Push SparkJava to the next level](https://medium.com/koin-developers/pushing-sparkjava-to-the-next-level-with-koin-ed1f0b80953e) ([Kotlin Weekly issue 73](http://mailchi.mp/kotlinweekly/kotlin-weekly-73), [DZone.com](https://dzone.com/articles/push-sparkjava-to-the-next-level-with-koin) )
* [When Koin met Ktor ...](https://medium.com/koin-developers/when-koin-met-ktor-c3b2395662bf) ([Kotlin Weekly issue 72](https://us12.campaign-archive.com/?u=f39692e245b94f7fb693b6d82&id=3135ae0cf5))
* [Moving from Dagger to Koin - Simplify your Android development](https://medium.com/@giuliani.arnaud/moving-from-dagger-to-koin-simplify-your-android-development-e8c61d80cddb) - ([Kotlin Weekly issue 66](http://mailchi.mp/kotlinweekly/kotlin-weekly-66?e=e8a57c719f) & [Android Weekly issue 282](http://androidweekly.net/issues/issue-282))
* [Kotlin Weekly #64](http://mailchi.mp/kotlinweekly/kotlin-weekly-64?e=e8a57c719f)
* [Insert Koin for dependency injection](https://www.ekito.fr/people/insert-koin-for-dependency-injection/)
* [Better dependency injection for Android](https://proandroiddev.com/better-dependency-injection-for-android-567b93353ad)







