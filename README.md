![logo](./img/banner_2000.png)

## What is KOIN?

[![Kotlin](https://img.shields.io/badge/koin-1.0-orange.svg)]()
[![Kotlin](https://img.shields.io/badge/kotlin-powered-blue.svg)]()
[![Backers on Open Collective](https://opencollective.com/koin/backers/badge.svg)](#backers)
 [![Sponsors on Open Collective](https://opencollective.com/koin/sponsors/badge.svg)](#sponsors) 
 
A pragmatic lightweight dependency injection framework for Kotlin developers.

Written in pure Kotlin, using functional resolution only: no proxy, no code generation, no reflection.

`Koin is a DSL, a light container and a pragmatic API`

## Official Website 👉 [https://insert-koin.io](https://insert-koin.io)

Check the [getting started](https://insert-koin.io/docs/1.0/getting-started/introduction/) section from our website, to discover Koin with the favorite platform.

### Contact & Latest News 🌐

- Follow us on Twitter for latest news: [@insertkoin_io](https://twitter.com/insertkoin_io)
- Koin developers on Medium: [koin developers hub](https://medium.com/koin-developers)

### Getting Help 🚒

Any question about Koin usage? 
- Come talk on slack [#koin channel](https://kotlinlang.slack.com/?redir=%2Fmessages%2Fkoin)
- Post your question on [Stackoverflow - #koin tag](https://stackoverflow.com/questions/tagged/koin)

### Reporting issues 🛠

Found a bug or a problem on a specific feature? Open an issue on [Github issues](https://github.com/Ekito/issues)

# Setup

## Current Version

```gradle
koin_version = '1.0.2'
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
implementation "org.koin:koin-core:$koin_version"
// Koin extended & experimental features
implementation "org.koin:koin-core-ext:$koin_version"
// Koin for Unit tests
testImplementation "org.koin:koin-test:$koin_version"
// Koin for Java developers
implementation "org.koin:koin-java:$koin_version"
```

#### Android

```gradle
// Koin for Android
implementation "org.koin:koin-android:$koin_version"
// Koin Android Scope feature
implementation "org.koin:koin-android-scope:$koin_version"
// Koin Android ViewModel feature
implementation "org.koin:koin-android-viewmodel:$koin_version"
```

#### AndroidX

```gradle

// AndroidX (based on koin-android)
// Koin AndroidX Scope feature
implementation "org.koin:koin-androidx-scope:$koin_version"
// Koin AndroidX ViewModel feature
implementation "org.koin:koin-androidx-viewmodel:$koin_version"
```

#### Ktor

```gradle
// Koin for Ktor Kotlin
implementation "org.koin:koin-ktor:$koin_version"
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

* [Learn Koin DSL in 5 min](https://insert-koin.io/docs/1.0/quick-references/koin-dsl/)

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

* [Write your app with Koin](https://insert-koin.io/docs/1.0/getting-started/introduction/)
* [Quick References](https://insert-koin.io/docs/1.0/quick-references/koin-core/)

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

* [Android Quick References](https://insert-koin.io/docs/1.0/quick-references/koin-android/)
* [Getting started with Android](https://insert-koin.io/docs/1.0/getting-started/android/)

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

* [Explore the documentation](https://insert-koin.io/docs/1.0/documentation/reference/index.html)

## Ready for ViewModel

Want to use Android Architecture ViewModel? No problem, it's already available and easy to use:

```kotlin
// Injected by constructor
class MyViewModel(val repo : MyRepository) : ViewModel(){
}
```

```kotlin
// declare ViewModel using the viewModel keyword
val myModule : Module = module {
  viewModel { MyViewModel(get()) }
  single { MyRepository() }
}
```

```kotlin
// Just get it
class MyActivity() : AppCompatActivity() {

  // lazy inject MyViewModel
  val vm : MyViewModel by viewModel()
}
```

* [Android ViewModel Documentation](https://insert-koin.io/docs/1.0/documentation/reference/index.html#_architecture_components_with_koin_viewmodel)
* [Getting started with Android ViewModel](https://insert-koin.io/docs/1.0/getting-started/android-viewmodel/)

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

* [Test with Koin - Documentation](https://insert-koin.io/docs/1.0/documentation/reference/index.html#_testing_with_koin)



# Articles

### Articles & resouces about Koin
* [The thermosiphon app — from Dagger to Koin step by step](https://medium.com/@giuliani.arnaud/the-thermosiphon-app-from-dagger-to-koin-step-by-step-a09af7f5b5b1)
* [Koin in Feature Modules Project](https://proandroiddev.com/koin-in-feature-modules-project-6329f069f943)
* [A brief look at Koin on Android](https://overflow.buffer.com/2018/09/13/a-brief-look-at-koin-on-android/)
* [Bye bye Dagger](https://medium.com/@charbgr/bye-bye-dagger-1494118dcd41)
* [Testing with Koin](https://proandroiddev.com/testing-with-koin-ade8a46eb4d)
* [Painless Android testing with Room & Koin](https://android.jlelse.eu/painless-android-testing-with-room-koin-bb949eefcbee)
* [Unlock your Android ViewModel power with Koin](https://medium.com/@giuliani.arnaud/unlock-your-android-viewmodel-power-with-koin-23eda8f493be)
* [Using dependency injection with Koin](https://medium.com/mindorks/using-dependency-injection-with-koin-bee0b461714a)
* [Koin + Spark = ❤️](https://www.ekito.fr/people/sparkjava-and-koin/)
* [Push SparkJava to the next level](https://medium.com/koin-developers/pushing-sparkjava-to-the-next-level-with-koin-ed1f0b80953e) ([Kotlin Weekly issue 73](http://mailchi.mp/kotlinweekly/kotlin-weekly-73), [DZone.com](https://dzone.com/articles/push-sparkjava-to-the-next-level-with-koin) )
* [When Koin met Ktor ...](https://medium.com/koin-developers/when-koin-met-ktor-c3b2395662bf) ([Kotlin Weekly issue 72](https://us12.campaign-archive.com/?u=f39692e245b94f7fb693b6d82&id=3135ae0cf5))
* [Android Dependency Injection – Why we moved from Dagger 2 to Koin?](https://www.nan-labs.com/blog/android-dependency-injection-moved-dagger-2-koin/)
* [Moving from Dagger to Koin - Simplify your Android development](https://medium.com/@giuliani.arnaud/moving-from-dagger-to-koin-simplify-your-android-development-e8c61d80cddb) - ([Kotlin Weekly issue 66](http://mailchi.mp/kotlinweekly/kotlin-weekly-66?e=e8a57c719f) & [Android Weekly issue 282](http://androidweekly.net/issues/issue-282))
* [Kotlin Weekly #64](http://mailchi.mp/kotlinweekly/kotlin-weekly-64?e=e8a57c719f)
* [Insert Koin for dependency injection](https://www.ekito.fr/people/insert-koin-for-dependency-injection/)
* [Better dependency injection for Android](https://proandroiddev.com/better-dependency-injection-for-android-567b93353ad)

### Talks & podcasts

* [L'injection de poireaux avec Koin - AndroidLeaks ep42 (French)](https://androidleakspodcast.com/2018/08/05/episode-42-linjection-de-poireaux-avec-koin/)

### Koin developers hub

* [Koin 1.0.0 Unleashed](https://medium.com/koin-developers/koin-1-0-0-unleashed-dcc15b293a3a)
* [Opening Koin 1.0.0 Beta](https://medium.com/koin-developers/opening-the-koin-1-0-0-beta-version-99cb8be1c308)
* [On the road to Koin 1.0](https://medium.com/koin-developers/on-the-road-to-koin-1-0-0-a624af55d07)
* [Koin 0.9.2 — Maintenance fixes, new branding, roadmap for 1.0.0 & some other nice announces](https://medium.com/koin-developers/koin-0-9-2-maintenance-fixes-new-branding-roadmap-for-1-0-0-some-other-nice-announces-94f14648e4ad)
* [Koin 0.9.1 - Bug fixes & Improvments](https://medium.com/koin-developers/koin-0-9-1-bug-fixes-improvements-bug-fixes-d257cd2766fa)
* [Koin 0.9.0 - Getting close to stable](https://medium.com/koin-developers/koin-0-9-0-getting-close-to-stable-release-74df9bb9e181)
* [Unlock your Android ViewModel power with Koin](https://medium.com/@giuliani.arnaud/unlock-your-android-viewmodel-power-with-koin-23eda8f493be)
* [koin 0.8.2 Improvements bugfixes and crash fix](https://medium.com/koin-developers/koin-0-8-2-improvements-bugfixes-and-crash-fix-6b6809fc1dd2)
* [Koin release 0.8.0](https://medium.com/koin-developers/koin-released-in-0-8-0-welcome-to-koin-spark-koin-android-architecture-f6270a7d4808)




## Contributors

This project exists thanks to all the people who contribute. [[Contribute](CONTRIBUTING.adoc)].
<a href="graphs/contributors"><img src="https://opencollective.com/koin/contributors.svg?width=890&button=false" /></a>


## Backers

Thank you to all our backers! 🙏 [[Become a backer](https://opencollective.com/koin#backer)]

<a href="https://opencollective.com/koin#backers" target="_blank"><img src="https://opencollective.com/koin/backers.svg?width=890"></a>


## Sponsors

Support this project by becoming a sponsor. Your logo will show up here with a link to your website. [[Become a sponsor](https://opencollective.com/koin#sponsor)]

<a href="https://opencollective.com/koin/sponsor/0/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/0/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/1/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/1/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/2/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/2/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/3/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/3/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/4/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/4/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/5/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/5/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/6/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/6/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/7/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/7/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/8/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/8/avatar.svg"></a>
<a href="https://opencollective.com/koin/sponsor/9/website" target="_blank"><img src="https://opencollective.com/koin/sponsor/9/avatar.svg"></a>


