---
title: Compose Multiplatform Features
---

This page describe how you can inject your dependencies for your Jetpack & Jetbrains Compose app - https://www.jetbrains.com/lp/compose-mpp/

## Starting Koin with Compose - KoinApplication or KoinContext

Most of the time, `startKoin` function is used to start Koin in your application. This is done before running any Composable function. You need to seting up Compose with your current Koin instance. Use `KoinContext()` to do so:

```kotlin
@Composable
fun App() {
    // Set current Koin instance to Compose context
    KoinContext() {

        MyScreen()
    }
}
```

Else if you want to start a new Koin instance from your Compose app, The function `KoinApplication` helps to create Koin application instance, as a Composable. This is a replacement of the classic `startKoin` application function.

```kotlin
@Composable
fun App() {
    KoinApplication(application = {
        // Koin configuration here
    }) {
        
    }
}
```

### Compose Preview with Koin

The `KoinApplication` function is also interesting to start dedicated context for preview. This can be also used to help with Compose preview:

```kotlin
@Composable
@Preview
fun App() {
    KoinApplication(application = {
        // your preview config here
        modules(previewModule)
    }) {
        // Compose to preview with Koin
    }
}
```

## Injecting into a @Composable

While writing your composable function, you gain access to the following Koin API:

* `koinInject()` - fetch instance from Koin container
* `getKoin()` - get current Koin instance

For a module that declares a 'MyService' component:

```kotlin
val androidModule = module {

    single { MyService() }
}
```

We can get your instance like that:

```kotlin
@Composable
fun App() {
    val myService = koinInject<MyService>()
}
```

:::note 
To keep aligned on the functional aspect of Jetpack Compose, the best writing approach is to inject instances directly into functions properties. This way allow to have default implementation with Koin, but keep open to inject instances how you want.
:::

```kotlin
@Composable
fun App(myService: MyService = koinInject()) {
}
```


## Module loading & unloading tied to Composable

Koin offers you a way to load specific modules for a given Composable function. The `rememberKoinModules` function load Koin modules and remember on current Composable:

```kotlin
@Composable
@Preview
fun MyComponentComposable() {
    // load module at first call of this component
    rememberKoinModules(myModule)
}
```

You can use one of the abandon function, to unload module on 2 aspects:
- onForgotten - after a composition is dropped out
- onAbandoned - composition has failed

For this use `unloadOnForgotten` or `unloadOnAbandoned` argument for `rememberKoinModules`.

## Creating Koin Scope with Composable

The composable function `rememberKoinScope` and `KoinScope` allow to handle Koin Scope in a Composable, follow-up current to close scope once Composable is ended.

:::info
this API is still unstable for now
:::