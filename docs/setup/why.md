---
title: Why Koin?
---

Koin provides an easy and efficient way to incorporate dependency injection into any Kotlin application(Multiplatform, Android, backend ...)

The goals of Koin are:
- Simplify your Dependency Injection infrastructure with smart API
- Kotlin DSL easy to read, easy to use, to let you write any kind of application 
- Provides different kind of integration from Android ecosystem, to more backend needs like Ktor
- Allow to be used with annotations 

## Making your Kotlin development easy and productive

Koin is a smart Kotlin dependency injection library to keep you focused on your app, not on your tools.

```kotlin

class MyRepository()
class MyPresenter(val repository : MyRepository) 

// just declare it 
val myModule = module { 
  singleOf(::MyPresenter)
  singleOf(::MyRepository)
}
```

Koin gives you simple tools and API to let you build, assemble Kotlin related technologies into your application and let you scale your business with easiness.

```kotlin
fun main() { 
  
  // Just start Koin
  startKoin {
    modules(myModule)
  }
} 
```

## Ready for Android

Thanks to the Kotlin language, Koin extends the Android platform and provides new features as part of the original platform.

```kotlin
class MyApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    startKoin {
      modules(myModule)
    }
  } 
}
```

Koin provides easy and powerful API to retrieve your dependencies anywhere in Android components, with just using by inject() or by viewModel()

```kotlin
class MyActivity : Application() {

  val myPresenter : MyPresenter by inject()

} 
```

## Powering Kotlin Multiplatform

Sharing code between mobile platforms is one of the major Kotlin Multiplatform use cases. With Kotlin Multiplatform Mobile, you can build cross-platform mobile applications and share common code between Android and iOS.

Koin provides multiplatform dependency injection and help build your components across your native mobile applications, and web/backend applications.

## Performances and Productivity

Koin is a pure Kotlin framework, designed to be straight forward in terms of usage and execution. It easy to use and doesn't impact your compilation time, nor require any extra plugin configuration.

## Koin is a DI Framework

Koin is a popular dependency injection framework for Kotlin. While it may seem similar to a service locator pattern, there are some key differences that set it apart.
First, a service locator is essentially a registry of available services, where you can ask for an instance of a service when you need it. The service locator is responsible for creating and managing the instances of these services.

In contrast, Koin is a pure dependency injection (DI) framework. With Koin, you declare your dependencies and let Koin handle the creation and wiring of objects. Koin works by defining modules that specify how to create objects of a particular type. When you need an instance of an object, Koin looks up the appropriate module and creates the object for you.

Another key difference is that a service locator often uses a static, global registry of services, whereas Koin allows you to create multiple, independent modules with their own scopes. This makes it easier to manage dependencies and avoid potential conflicts.
Finally, Koin encourages the use of constructor injection, where dependencies are passed as constructor parameters, rather than being obtained through a service locator. This leads to better testability and makes it easier to reason about your code.
Overall, Koin is a modern, lightweight DI framework that can help you manage your application’s dependencies more easily and with less boilerplate code.