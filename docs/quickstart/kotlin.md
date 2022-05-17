---
title: Kotlin
---

> This tutorial lets you write a Kotlin application and use Koin to inject and retrieve your components.

## Get the code

:::info
[The source code is available on Github](https://github.com/InsertKoinIO/koin/tree/master/quickstart/getting-started-koin-core)
:::

## Setup

Starting with a blank project, add the `koin-core` dependency to the project's `build.gradle` file as shown:

```groovy
// Add Maven Central to your repositories if needed
repositories {
	mavenCentral()    
}
dependencies {
    // Koin for Kotlin apps
    compile "io.insert-koin:koin-core:$koin_version"
    // Testing
    testCompile "io.insert-koin:koin-test:$koin_version"
}
```

## The sample application

The sample application has four Kotlin files:

* HelloMessageData - Data container
* HelloService - Extracts and manipulates data from HelloMessageData for display
* HelloApplication - Utilize the injected HelloService instance and print data to the console
* HelloModule - Configures injectables

### Data container

Let's create a `HelloMessageData` data class to hold our data:

```kotlin
/**
 * A class to hold our message data
 */
data class HelloMessageData(val message : String = "Hello Koin!")
```

### Service

Let's create a service interface and implementation, `HelloService` and `HelloServiceImpl`, to display our data from `HelloMessageData`.:

```kotlin
/**
 * Hello Service - interface
 */
interface HelloService {
    fun hello(): String
}


/**
 * Hello Service Impl
 * Will use HelloMessageData data
 */
class HelloServiceImpl(private val helloMessageData: HelloMessageData) : HelloService {

    override fun hello() = "Hey, ${helloMessageData.message}"
}
```


## The application class

To run our application, we need to create a runtime component. Let's write a `HelloApplication` class and tag it with the `KoinComponent` interface. This will enable the `by inject()` functions to retrieve our `HelloService` component:

```kotlin
/**
 * HelloApplication - Application Class
 * that uses HelloService
 */
class HelloApplication : KoinComponent {

    // Inject HelloService
    val helloService by inject<HelloService>()

    // display our data
    fun sayHello() = println(helloService.hello())
}
```

## Declaring dependencies

Now, let's assemble `HelloMessageData` with `HelloService`, with a Koin module:

```kotlin
val helloModule = module {

    single { HelloMessageData() }

    single { HelloServiceImpl(get()) as HelloService }
}
```

We declare each component as `single` which creates a singleton instance of each.

* `single { HelloMessageData() }` : declare a singleton instance of `HelloMessageData`
* `single { HelloServiceImpl(get()) as HelloService }` : Build `HelloServiceImpl` with injected instance of `HelloMessageData`,  declared a singleton of `HelloService`.

## That's it!

Now pull it all together in our `main` function:

```kotlin
fun main(vararg args: String) {

    startKoin {
        // use Koin logger
        printLogger()
        // declare modules
        modules(helloModule)
    }

    HelloApplication().sayHello()
}

```
