---
title: Context Isolation
---


For SDK Makers, you can also work with Koin in a non global way: use Koin for the DI of your library and avoid any conflict by people using your library and Koin by isolating your context.

In a standard way, we can start Koin like that:

```kotlin
// start a KoinApplication and register it in Global context
startKoin {
    // declare used modules
    modules(coffeeAppModule)
}
```

From this, we can use the `KoinComponent` as it: it will use the `GlobalContext` Koin instance.

But if we want to use an isolated Koin instance, you can just declare it like follow:

```kotlin
// create a KoinApplication
val myApp = koinApplication {
    // declare used modules
    modules(coffeeAppModule)
}
```

You will have to keep your `myApp` instance avilable in your library and pass it to your custom KoinComponent implementation:

```kotlin
// Get a Context for your Koin instance
object MyKoinContext {
    var koinApp : KoinApplication? = null
}

// Register the Koin context
MyKoinContext.koinApp = KoinApp
```

```kotlin
abstract class CustomKoinComponent : KoinComponent {
    // Override default Koin instance, intially target on GlobalContext to yours
    override fun getKoin(): Koin = MyKoinContext?.koinApp.koin
}
```

And now, you register your context and run your own isolated Koin components:

```kotlin
// Register the Koin context
MyKoinContext.koinApp = myApp

class ACustomKoinComponent : CustomKoinComponent(){
    // inject & get will target MyKoinContext
}
```
