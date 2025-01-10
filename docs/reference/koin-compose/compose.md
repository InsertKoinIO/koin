---
title: Koin for Jetpack Compose and Compose Multiplatform
---

This page describe how you can inject your dependencies for your [Android Jetpack Compose](https://developer.android.com/jetpack/compose) or your [Multiplaform Compose](https://www.jetbrains.com/lp/compose-mpp/) apps.


## Koin Compose Multiplatform vs Koin Android Jetpack Compose

Since mid 2024, Compose applications can be done with Koin Multiplatform API. All APIs are identifcal between Koin Jetpack Compose (koin-androidx-compose) and Koin Compose Multiplatform (koin-compose).

### What Koin package for Compose?

for a pure Android app that uses only Android Jetpack Compose API, use the following packages:
- `koin-androidx-compose` - to unlock Compose base API + Compose ViewModel API
- `koin-androidx-compose-navigation` - Compose ViewModel API with Navigation API integration

for an Android/Multiplatform app, use the following packages:
- `koin-compose` - Compose base API
- `koin-compose-viewmodel` - Compose ViewModel API
- `koin-compose-viewmodel-navigation` - Compose ViewModel API with Navigation API integration

## Starting over an existing Koin context (Koin already started)

Some time the `startKoin` function is already used in the application, to start Koin in your application (like in Android main app class, the Application class). In that case you need to inform your Compose application about the current Koin context with `KoinContext` or `KoinAndroidContext`. Those functions reuse current Koin context and bind it to the Compose application.

```kotlin
@Composable
fun App() {
    // Set current Koin instance to Compose context
    KoinContext() {

        MyScreen()
    }
}
```

:::info
Difference between `KoinAndroidContext` and `KoinContext`:
- `KoinAndroidContext` is looking into current Android app context for Koin instance
- `KoinContext` is looking into current GlobalContext for Koin instances
:::

:::note
If you get some `ClosedScopeException` from a Composable, either use `KoinContext` on your Composable or ensure to have proper Koin start configuration [with Android context](/docs/reference/koin-android/start.md#from-your-application-class)
:::

## Starting Koin with a Compose App - KoinApplication

The function `KoinApplication` helps to create Koin application instance, as a Composable:

```kotlin
@Composable
fun App() {
    KoinApplication(application = {
        modules(...)
    }) {
        
        // your screens here ...
        MyScreen()
    }
}
```

The `KoinApplication` function will handle start & stop of your Koin context, regarding the cycle of the Compose context. This function start and stop a new Koin application context.

:::info
In an Android Application, the `KoinApplication` will handle any need to stop/restart Koin context regarding configuration changes or drop of Activities.
:::

:::note
This replaces the use of the classic `startKoin` application function.
:::


### Compose Preview with Koin

The `KoinApplication` function is interesting to start dedicated context for preview. This can be also used to help with Compose preview:

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

While writing your composable function, you gain access to the following Koin API: `koinInject()`, to inject instance from Koin container

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


To keep aligned on the functional aspect of Jetpack Compose, the best writing approach is to inject instances directly into functions parameters. This way allow to have default implementation with Koin, but keep open to inject instances how you want.

```kotlin
@Composable
fun App(myService: MyService = koinInject()) {

}
```

### Injecting into a @Composable with Parameters

While you request a new dependency from Koin, you may need to inject parameters. To do this you can use `parameters` parameter of the `koinInject` function, with the `parametersOf()` function like this:

```kotlin
@Composable
fun App() {
    val myService = koinInject<MyService>(parameters = parametersOf("a_string"))
}
```

:::info
You can use parameters with lambda injection like `koinInject<MyService>{ parametersOf("a_string") }`, but this can have a performance impact if your recomposing a lot around. This version with lambda needs to unwrap your parameters on call, to help avoid remembering your parameters.

From version 4.0.2 of Koin, koinInject(Qualifier,Scope,ParametersHolder) is introduced to let you use parameters in the most efficient way
:::

## ViewModel for @Composable

The same way you have access to classical single/factory instances, you gain access to the following Koin ViewModel API:

* `koinViewModel()` - inject ViewModel instance
* `koinNavViewModel()` - inject ViewModel instance + Navigation arguments data (if you are using `Navigation` API)

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

:::note
Lazy API are not supported with updates of jetpack Compose
:::

### ViewModel and SavedStateHandle for @Composable

You can have a `SavedStateHandle` constructor parameter, it will be injected regarding the Compose environment (Navigation BackStack or ViewModel).
Either it's injected via ViewModel `CreationExtras` either via Navigation `BackStackEntry`:

```kotlin
// Setting objectId argument in Navhost
NavHost(
    navController,
    startDestination = "list"
) {
    composable("list") { backStackEntry ->
        //...
    }
    composable("detail/{objectId}") { backStackEntry ->
        val objectId = backStackEntry.arguments?.getString("objectId")?.toInt()
        DetailScreen(navController, objectId!!)
    }
}

// Injected Argument in ViewModel
class DetailViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        println("$this - objectId: ${savedStateHandle.get<String>("objectId")}")
    }
}
```

:::note
More details about SavedStateHandle injection difference: https://github.com/InsertKoinIO/koin/issues/1935#issuecomment-2362335705
:::

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