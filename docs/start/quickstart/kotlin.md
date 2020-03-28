
# Getting Started with Kotlin app {docsify-ignore-all}

> This tutorial lets you write a Kotlin application and use Koin inject and retrieve your components.

## Get the code

> Checkout the project directly on Github or download the zip file <br>
> 🚀 Go to [Github](https://github.com/InsertKoinIO/getting-started-koin-core) or [download Zip](https://github.com/InsertKoinIO/getting-started-koin-core/archive/master.zip)

## Setup

First, check that the `koin-core` dependency is added like below:

```groovy
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    // Koin for Kotlin apps
    compile 'org.koin:koin-core:$koin_version'
    // Testing
    testCompile 'org.koin:koin-test:$koin_version'
}
```

## The application

In our small app we need to have 2 components:

* HelloMessageData - hold data
* HelloService - use and display data from HelloMessageData
* HelloApplication - retrieve and use HelloService

### Data holder

Let's create a `HelloMessageData` data class to hold our data:

```kotlin
/**
 * A class to hold our message data
 */
data class HelloMessageData(val message : String = "Hello Koin!")
```

### Service

Let's create a service to display our data from `HelloMessageData`. Let's write `HelloServiceImpl` class and its interface `HelloService`:

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

To run our `HelloService` component, we need to create a runtime component. Let's write a `HelloApplication` class and tag it with `KoinComponent` interface. This will later allows us to use the `by inject()` functions to retrieve our component:

```kotlin
/**
 * HelloApplication - Application Class
 * use HelloService
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

We declare each component as `single`, as singleton instances.

* `single { HelloMessageData() }` : declare a singleton of `HelloMessageData` instance
* `single { HelloServiceImpl(get()) as HelloService }` : Build `HelloServiceImpl` with injected instance of `HelloMessageData`,  declared a singleton of `HelloService`.

## That's it!

Just start our app from a `main` function:

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
