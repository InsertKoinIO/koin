---
title: Context Isolation
---


For SDK Makers, you can also work with Koin in a non-global way: use Koin for the DI of your library and avoid any conflict by people using your library and Koin by isolating your context.

In a standard way, we can start Koin like that:

```kotlin
// start a KoinApplication and register it in Global context
startKoin {

    // declare used modules
    modules(...)
}
```

This uses the default Koin context to register your dependencies.

But if we want to use an isolated Koin instance, you need declare an instance and store it in a class to hold your instance.
You will have to keep your Koin Application instance available in your library and pass it to your custom KoinComponent implementation:

The `MyIsolatedKoinContext` class is holding our Koin instance here:

```kotlin
// Get a Context for your Koin instance
object MyIsolatedKoinContext {

    val koinApp = koinApplication {
        // declare used modules
        modules(coffeeAppModule)
    }

    val koin = koinApp.koin 
}
```

Let's use `MyIsolatedKoinContext` to define our `IsolatedKoinComponent` class, a KoinComponent that will use our isolated context:

```kotlin
abstract class IsolatedKoinComponent : KoinComponent {

    // Override default Koin instance
    override fun getKoin(): Koin = MyIsolatedKoinContext.koin
}
```

Everything is ready, just use `IsolatedKoinComponent` to retrieve instances from isolated context:

```kotlin
class MyKoinComponent : IsolatedKoinComponent(){
    // inject & get will target MyKoinContext
}
```
