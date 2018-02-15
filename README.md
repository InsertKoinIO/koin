![logo](./img/insert_koin.png)

## What's KOIN?

A small library for writing dependency injection in a concise and pragmatic way. No proxy, no code generation, no introspection. Just DSL and functional Kotlin magic!

`Declare, Start, Inject`

## insert-koin.io

* All documentation, sample and references has been move to our website. Check the official website to get started: [insert-koin.io](https://insert-koin.io)
* Koin samples have been moved here: [koin-samples @ github](https://github.com/Ekito/koin-samples)

### NEW : [Koin Android Developer Quick Reference](https://insert-koin.io/docs/1.0/quick-reference/android/)
The essentials of Koin API for Android developer


## Getting Started

You can check the [getting started section](https://insert-koin.io/docs/1.0/getting-started/introduction/) from our website, to discover Koin with the favorite platform. Or follow the snippets below.

### Actual Version

```gradle
koin_version = '0.8.2'
```

### Gradle
Check that you have the `jcenter` repository. 

```gradle
// Add Jcenter to your repositories if needed
repositories {
	jcenter()    
}
```

Choose your the Koin module for your runtime:

```gradle
// Koin for Kotlin
compile "org.koin:koin-core:$koin_version"

// Koin for Android
compile "org.koin:koin-android:$koin_version"

// Koin for Android Architecture Components
compile "org.koin:koin-android-architecture:$koin_version"

// Koin for Spark Kotlin
compile "org.koin:koin-spark:$koin_version"
```

Check others modules (Ktor, JUnit ...) on [getting started](https://insert-koin.io/docs/1.0/getting-started/introduction/) web page

## Declare

Write a **module** with what you want to declare and assemble:

```kotlin
// Given some classes 
class Controller(val service : BusinessService) 
class BusinessServiceImpl() : BusinessService
interface BusinessService

// just declare it 
val myModule = applicationContext { 
  provide { Controller(get()) } 
  provide { BusinessServiceImpl() as BusinessService } 
} 
```

## Start

Use the `startKoin()` function to start Koin with your modules in your application. Below some start examples.

Kotlin:

```kotlin
fun main(vararg args : String) { 
  // start Koin!
  startKoin(listOf(myModule))
} 
```

Android:

```kotlin
class MyApplication : Application() {
  override fun onCreate(){
    super.onCreate()
    // start Koin!
    startKoin(this, listOf(myModule))
  } 
} 
```

SparkKotlin:

```kotlin
fun main(vararg args : String) { 
  // start Spark & Koin
  start( modules = listOf(myModule)){
  	runControllers()
  }
} 
```


## Inject

You're ready to go! Components declared in modules are injected **by constructors**.

```kotlin
class Controller(val service : BusinessService){ 
  // service has been injected 
} 
```

**Inject** into Android Activity:

```kotlin
// Just a simple Activity - No need of interface nor annotation 
class MyActivity() : AppCompatActivity() {

    // lazy inject BusinessService
    val service : BusinessService by inject()
}
```

Inject your Android **ViewModel**:

```kotlin
// MyViewModel must be previously declared with 'viewModel'
val module = applicationContext{
  viewModel { MyViewModel(get()) }
  //...
}

// Your ViewModel
class MyViewModel(val service : BusinessService) : ViewModel() {
  // do antyhing with service
}

// Bind it to your Activity
class MyActivity() : AppCompatActivity() {

  val viewModel : MyViewModel by viewModel()

  override fun onCreate(){
    super.onCreate()
  }
}
```

Start **Spark** HTTP Controller:

```kotlin
// Declare your controller
val module = applicationContext {
  controller { HelloController(get())}
  //...
}

// Your Spark HTTP Controller
class HelloController(val service: HelloService) {
  init {
      get("/hello") {
          service.sayHello()
      }
  }
}

fun main(vararg args: String) {
  // Spark
  start(modules = listOf(helloAppModule)) {
      // Run all Controllers
      runControllers()
  }
}
```

Go to the [getting started](https://insert-koin.io) sections for more details.

## Follow us & Contact

### Website - [https://insert-koin.io](https://insert-koin.io)

### Twitter - [@insertkoin_io](https://twitter.com/insertkoin_io)

### Medium - [Koin Developers Hub](https://medium.com/koin-developers)

### Slack - [Kotlin Slack](https://kotlinlang.org/community/) on **#koin** channel




