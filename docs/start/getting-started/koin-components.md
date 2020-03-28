
Sometimes you can't declare only components via Koin. Dependening on your runtime technology, you might need to retrieve instances from Koin in a class that was not created with Koin (e.g. Android)

## The KoinComponent interface

Tag your class with the `KoinComponent` interface to unlock Koin injection features:

* `by inject()` - lazy inject an instance
* `get()` - retrieve an instance
* `getProperty()` - get a Koin property

We can inject the module above into class properties:

```kotlin
// Tag class with KoinComponent
class HelloApp : KoinComponent {

    // lazy inject dependency
    val helloService: HelloServiceImpl by inject()
    
    fun sayHello(){
        helloService.sayHello()
    }
}
```

And we just need to start Koin and run our class:

```kotlin
// a module with our declared Koin dependencies 
val helloModule = module {
    single { HelloServiceImpl() }
}

fun main(vararg args: String) {

    // Start Koin
    startKoin {
        modules(helloModule)
    }
    
    // Run our Koin component
    HelloApp().sayHello()
}
```

#### Bootstrapping

> `KoinComponent` interface is also used to help you bootstrap an application from outside of Koin. Also, you can bring  `KoinComponent` feature by extension functions directly on some target classes (i.e: Activity, Fragment have KoinComponent feature in Android). 


## Bridge with Koin instance

The `KoinComponent` interface brings the following:

```kotlin
interface KoinComponent {

    /**
     * Get the associated Koin instance
     */
    fun getKoin(): Koin = KoinContextHandler.get().koin
}
```

It opens the following possibilties:

> You can then redefine then `getKoin()` function to redirect to a local custom Koin instance



