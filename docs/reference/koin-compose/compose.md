---
title: Injecting in Jetpack Compose and Android
---

This page describe how you can inject your dependencies for your Jetpack Compose app - https://developer.android.com/jetpack/compose

## Starting Koin with Android Jetpack Compose - KoinApplication or KoinAndroidContext

Most of the time, `startKoin` function is used to start Koin in your application. This is done before running any Composable function. You need to setup Compose with your current Koin instance. Use `KoinAndroidContext()` to do so:

```kotlin
@Composable
fun App() {
    // Set current Koin instance to Compose context
    KoinAndroidContext() {

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

:::info
Difference between `KoinAndroidContext` and `KoinContext`:
- `KoinAndroidContext` is looking into current Android app context for Koin instance
- `KoinContext` is looking into current GlobalContext for Koin instances
:::

### Compose Preview with Koin

If the composables you want to preview depend on definitions provided by Koin, you can use the `KoinPreviewApplication'.  
This function allows you to launch a dedicated Koin context for each of your composable previews:

```kotlin
@Composable
@Preview
fun MyComposablePreview() {
    KoinPreviewApplication(modules = {
        // Your definitions which the composable you want to preview here depend on here
        listOf(previewModule)
    }) {
        // Composable to preview with Koin here
        MyComposable()
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
    // or constructor DSL
    singleOf(::MyService)
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

## ViewModel for @Composable

The same way you have access to classical single/factory instances, you gain access to the following Koin ViewModel API:

* `koinViewModel()` - fetch instance

For a module that declares a 'MyViewModel' component:

```kotlin
module {
    viewModel { MyViewModel() }
    // or constructor DSL
    viewModelOf(::MyViewModel)
}
```

We can get your instance like that:

```kotlin
@Composable
fun App() {
    val vm = koinViewModel<MyViewModel>()
}
```

We can get your instance in function parameters:

```kotlin
@Composable
fun App(vm : MyViewModel = koinViewModel()) {

}
```

:::warning
Lazy API is not supported with updates of jetpack Compose 1.1+
:::

