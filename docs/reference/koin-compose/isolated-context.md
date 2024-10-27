---
title: Isolated Context with Compose Applications
---

With a Compose application, you can work the same way with an [isolated context](/docs/reference/koin-core/context-isolation.md) to deal with SDK or white label application, in order to not mix your Koin definitions with an end user's one. 

## Define isolated context

First let's declare our isolated context holder, in order to store our isolated Koin instance in memory. This can be done with a simple Object class like this. The `MyIsolatedKoinContext` class is holding our Koin instance:

```kotlin
object MyIsolatedKoinContext {

    val koinApp = koinApplication {
        // declare used modules
        modules(sdkAppModule)
    }
}
```

:::note
Adapt the `MyIsolatedKoinContext` class according your need of initialization
:::

## Setup isolated context with Compose

Now that you have defined an isolated Koin context, we can seting up it up to Compose to use it and override all the API. Just use the `KoinIsolatedContext` at the root Compose function. This will propagate your Koin context in all child composables.

```kotlin
@Composable
fun App() {
    // Set current Koin instance to Compose context
    KoinIsolatedContext(context = MyIsolatedKoinContext.koinApp) {

        MyScreen()
    }
}
```

:::info
All Koin Compose APIs will use your Koi isolated context after the use of `KoinIsolatedContext`
:::