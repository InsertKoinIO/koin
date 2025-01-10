---
title: Why Koin?
---

Koin provides an easy and efficient way to incorporate dependency injection into any Kotlin application(Multiplatform, Android, backend ...)

The goals of Koin are:
- Simplify your Dependency Injection infrastructure with smart API
- Kotlin DSL easy to read, easy to use, to let you write any kind of application 
- Provides different kind of integration from Android ecosystem, to more backend needs like Ktor
- Allow to be used with annotations 

## Koin in a nutshell

### Making your Kotlin development easy and productive

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

### Ready for Android

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
class MyActivity : AppCompatActivity() {

  val myPresenter : MyPresenter by inject()

} 
```

### Powering Kotlin Multiplatform

Sharing code between mobile platforms is one of the major Kotlin Multiplatform use cases. With Kotlin Multiplatform Mobile, you can build cross-platform mobile applications and share common code between Android and iOS.

Koin provides multiplatform dependency injection and help build your components across your native mobile applications, and web/backend applications.

### Performances and Productivity

Koin is a pure Kotlin framework, designed to be straight forward in terms of usage and execution. It easy to use and doesn't impact your compilation time, nor require any extra plugin configuration.

## Koin: A Dependency Injection Framework

Koin is a popular dependency injection (DI) framework for Kotlin, offering a modern and lightweight solution for managing your application’s dependencies with minimal boilerplate code.

### Dependency Injection vs. Service Locator

While Koin may appear similar to a service locator pattern, there are key differences that set it apart:

- Service Locator: A service locator is essentially a registry of available services where you can request an instance of a service as needed. It is responsible for creating and managing these instances, often using a static, global registry.

- Dependency Injection: In contrast, Koin is a pure dependency injection framework. With Koin, you declare your dependencies in modules, and Koin handles the creation and wiring of objects. It allows for the creation of multiple, independent modules with their own scopes, making dependency management more modular and avoiding potential conflicts.

### Koin’s Approach: A Blend of Flexibility and Best Practices

Koin supports both DI and the Service Locator pattern, offering flexibility to developers. However, it strongly encourages the use of DI, particularly constructor injection, where dependencies are passed as constructor parameters. This approach promotes better testability and makes your code easier to reason about.

Koin’s design philosophy is centered around simplicity and ease of setup while allowing for complex configurations when necessary. By using Koin, developers can manage dependencies effectively, with DI being the recommended and preferred approach for most scenarios.


### Transparency and Design Overview

Koin is designed to be a versatile Inversion of Control (IoC) container that supports both Dependency Injection (DI) and Service Locator (SL) patterns. To provide a clear understanding of how Koin operates and to guide you in using it effectively, let’s explore the following aspects:

#### How Koin Balances DI and SL

Koin combines elements of both DI and SL, which may influence how you use the framework:

1. **Global Context Usage:** By default, Koin provides a globally accessible component that acts like a service locator. This allows you to retrieve dependencies from a central registry using `KoinComponent` or `inject` functions.

2. **Isolated Components:** Although Koin encourages the use of Dependency Injection, particularly constructor injection, it also allows for isolated components. This flexibility means you can configure your application to use DI where it makes the most sense while still taking advantage of SL for specific cases.

3. **SL in Android Components:** In Android development, Koin often uses SL internally within components such as `Application` and `Activity` for ease of setup. From this point, Koin recommends DI, especially constructor injection, to manage dependencies in a more structured way. However, this is not enforced, and developers have the flexibility to use SL if needed.

#### Why This Matters to You

Understanding the distinction between DI and SL helps in managing your application’s dependencies effectively:

- **Dependency Injection:** Encouraged by Koin for its benefits in testability and maintainability. Constructor injection is preferred as it makes dependencies explicit and enhances code clarity.

- **Service Locator:** While Koin supports SL for convenience, especially in Android components, relying solely on SL can lead to tighter coupling and reduced testability. Koin’s design provides a balanced approach, allowing you to use SL where it’s practical but promoting DI as the best practice.

#### Making the Most of Koin

To use Koin effectively:

- **Follow Best Practices:** Use constructor injection where possible to align with best practices for dependency management. This approach improves testability and maintainability.

- **Leverage Koin’s Flexibility:** Utilize Koin’s support for SL in scenarios where it simplifies setup, but aim to rely on DI for managing core application dependencies.

- **Refer to Documentation and Examples:** Review Koin’s documentation and examples to understand how to configure and use DI and SL appropriately based on your project needs.

- **Visualize Dependency Management:** Diagrams and examples can help illustrate how Koin resolves dependencies and manages them within different contexts. These visual aids can provide a clearer understanding of Koin’s internal workings.

> By providing this guidance, we aim to help you navigate Koin’s features and design choices effectively, ensuring you can leverage its full potential while adhering to best practices in dependency management.