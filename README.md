![logo](./img/banner_2000.png)

## What is KOIN?

A pragmatic lightweight dependency injection framework for Kotlin developers.

Written in pure Kotlin, using functional resolution only: no proxy, no code generation, no reflection.

`Koin is a DSL, a light container and a pragmatic API`

### Beta Website - [insert-koin.io](https://beta.insert-koin.io)

Official website to get started: [insert-koin.io](https://beta.insert-koin.io). Check the [getting started](https://beta.insert-koin.io/docs/1.0/getting-started/introduction/) section from our website, to discover Koin with the favorite platform. Or follow the snippets below.

### Contact & Support

Follow us on Twitter for latest news: [@insertkoin_io](https://twitter.com/insertkoin_io)

Need help? Come on slack [Koin channel](https://kotlinlang.slack.com/?redir=%2Fmessages%2Fkoin) from Kotlin slack. Or just open an issue on [Github issues](https://github.com/Ekito/issues) to share your problem.


# Setup

## Actual Version

```gradle
koin_version = '1.0.0-beta-3'
```

## Gradle 

### Jcenter 

Check that you have the `jcenter` repository. 

```gradle
// Add Jcenter to your repositories if needed
repositories {
	jcenter()    
}
```

### Dependencies

Choose your Koin dependency:

#### Core features

```gradle
// Koin for Kotlin
compile "org.koin:koin-core:$koin_version"
// Koin for Unit tests
testCompile "org.koin:koin-test:$koin_version"
// Koin for Java developers
compile "org.koin:koin-java:$koin_version"
// Advanced features
compile "org.koin:koin-reflect:$koin_version"
```

#### Android

```gradle
// Koin for Android
compile "org.koin:koin-android:$koin_version"
// Koin Android Scope feature
compile "org.koin:koin-android-scope:$koin_version"
// Koin Android ViewModel feature
compile "org.koin:koin-android-viewmodel:$koin_version"
```

#### AndroidX

```gradle

// AndroidX (based on koin-android)
// Koin AndroidX Scope feature
compile "org.koin:koin-androidx-scope:$koin_version"
// Koin AndroidX ViewModel feature
compile "org.koin:koin-androidx-viewmodel:$koin_version"
```

#### SparkJava

```gradle

// Koin for Spark Kotlin
compile "org.koin:koin-spark:$koin_version"
```

#### Ktor

```gradle
// Koin for Ktor Kotlin
compile "org.koin:koin-ktor:$koin_version"
```

# Quickstart


## Declare a Koin module

Write with the Koin DSL what you need to assemble:

```kotlin
// Given some classes 
class Controller(val service : BusinessService) 
class BusinessService() 

// just declare it 
val myModule = module { 
  single { Controller(get()) } 
  single { BusinessService() } 
} 
```

* [Learn Koin DSL in 5 min](https://beta.insert-koin.io/docs/1.0/quick-references/koin-dsl/)

## Start Koin

Use the startKoin() function to start Koin in your application.

In a Kotlin app:

```kotlin
fun main(vararg args : String) { 
  // start Koin!
  startKoin(listOf(myModule))
} 
```

In an Android app:

```kotlin
class MyApplication : Application() {
  override fun onCreate(){
    super.onCreate()
    // start Koin!
    startKoin(this, listOf(myModule))
  } 
} 
```

* [Write your app with Koin](https://beta.insert-koin.io/docs/1.0/getting-started/introduction/)
* [Quick References](https://beta.insert-koin.io/docs/1.0/quick-references/koin-core/)

## Inject in Android

Easy to inject into your Android classes:

```kotlin
// Just inject in a simple Activity 
class MyActivity() : AppCompatActivity() {

    // lazy inject BusinessService into property
    val service : BusinessService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // or directly get any instance
        val service : BusinessService = get()
    }
}
```

* [Android Quick References](https://beta.insert-koin.io/docs/1.0/quick-references/koin-android/)
* [Getting started with Android](https://beta.insert-koin.io/docs/1.0/getting-started/android/)

## Inject by constructor

You're now ready! The Koin DSL help you make injection by constructors. Now just use it:

```kotlin
// Controller & BusinessService are declared in a module
class Controller(val service : BusinessService){ 
  
  fun hello() {
     // service is ready to use
     service.sayHello()
  }
} 
```

Koin can be easily embedded with your favorite Java/Kotlin SDK, and already provide some dedicated support module.

* [Explore the documentation](https://beta.insert-koin.io/docs/1.0/documentation/reference/index.html)

## Ready for ViewModel

Want to use Android Architecture ViewModel? No problem, it's already available and easy to use:

```kotlin
// declare ViewModel using the viewModel keyword
val myModule : Module = module {
  viewModel { MyViewModel(get()) }
  single { MyRepository() }
}
```
```kotlin
// Injected by constructor
class MyViewModel(val repo : MyRepository) : ViewModel(){
}
```
```kotlin
// Just get it
class MyActivity() : AppCompatActivity() {

  // lazy inject MyViewModel
  val vm : MyViewModel by viewModel()
}
```

* [Android ViewModel Documentation](https://beta.insert-koin.io/docs/1.0/documentation/reference/index.html#_architecture_components_with_koin_viewmodel)
* [Getting started with Android ViewModel](https://beta.insert-koin.io/docs/1.0/getting-started/android-viewmodel/)

## Easy testing

Use koin from a simple JUnit class:

```kotlin
// Just tag your class with KoinTest to unlick your testing power
class SimpleTest : KoinTest { 
  
  // lazy inject BusinessService into property
  val service : BusinessService by inject()

  @Test
  fun myTest() {
      // You can start your Koin configuration
      startKoin(myModules)

      // or directly get any instance
      val service : BusinessService = get()

      // Don't forget to close it at the end
      closeKoin()
  }
} 
```

And more: check your configuration with a simple unit test, easily create mocks...

* [Test with Koin - Docuentation](https://beta.insert-koin.io/docs/1.0/documentation/reference/index.html#_testing_with_koin)



# Articles

### Koin on Medium: [Koin Developers Hub](https://medium.com/koin-developers)

### Last articles

* [Opening Koin 1.0.0 Beta]()
* [On the road to Koin 1.0](https://medium.com/koin-developers/on-the-road-to-koin-1-0-0-a624af55d07)
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







