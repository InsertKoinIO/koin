---
title: Koin for Jetpack Compose and Compose Multiplatform
---

This page describe how you can inject your dependencies for your [Android Jetpack Compose](https://developer.android.com/jetpack/compose) or your [Multiplaform Compose](https://www.jetbrains.com/lp/compose-mpp/) apps.


## Koin Compose Multiplatform vs Koin Android Jetpack Compose

Since mid-2024, Compose applications can be done with the Koin Multiplatform API. All APIs are identical between Koin Jetpack Compose (koin-androidx-compose) and Koin Compose Multiplatform (koin-compose).

### What Koin package for Compose?

For a pure Android app that uses only the Android Jetpack Compose API, use the following packages:
- `koin-androidx-compose` - to unlock Compose base API + Compose ViewModel API
- `koin-androidx-compose-navigation` - Compose ViewModel API with Navigation API integration

For an Android/Multiplatform app, use the following packages:
- `koin-compose` - Compose base API
- `koin-compose-viewmodel` - Compose ViewModel API
- `koin-compose-viewmodel-navigation` - Compose ViewModel API with Navigation API integration

## Starting over an existing Koin context

By using the `startKoin` function previous to your Compose application, your application is ready to welcome Koin injection. Nothing is required anymore to setup your Koin context with Compose.

:::note
`KoinContext` and `KoinAndroidContext` are deprecated
:::


## Starting Koin with a Compose App - KoinApplication
If you don't have access to a space where you can run the `startKoin` function, you can relay on Compose and Koin to start your Koin configuration.

The compose function `KoinApplication` helps to create a Koin application instance, as a Composable:

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

The `KoinApplication` function will handle the start and stop of your Koin context, regarding the cycle of the Compose context. This function starts and stops a new Koin application context.

:::info
In an Android Application, the `KoinApplication` will handle any need to stop/restart Koin context regarding configuration changes or drop of Activities.
:::

:::note
(Experimental API)
You can use the `KoinMultiplatformApplication` to replace a multiplatform entry point: it's the same as `KoinApplication` but injects automatically `androidContext` and `androidLogger` for you.
:::

## Compose Preview with KoinApplicationPreview

The `KoinApplicationPreview` compose function is dedicated to preview a Composable:

```kotlin
@Preview(name = "1 - Pixel 2 XL", device = Devices.PIXEL_2_XL, locale = "en")
@Preview(name = "2 - Pixel 5", device = Devices.PIXEL_5, locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "3 - Pixel 7 ", device = Devices.PIXEL_7, locale = "ru", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun previewVMComposable(){
    KoinApplicationPreview(application = { modules(appModule) }) {
        ViewModelComposable()
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

We can get your instance in the function parameters:

```kotlin
@Composable
fun App(vm : MyViewModel = koinViewModel()) {

}
```

:::note
Lazy API are not supported with updates of Jetpack Compose
:::

### Shared Activity ViewModel (4.1 - Android)

You can now use the `koinActivityViewModel()` to inject a ViewModel from the same ViewModel host: Activity.

```kotlin
@Composable
fun App() {
    // hold ViewModel instance at Activity level
    val vm = koinActivityViewModel<MyViewModel>()
}
```

### ViewModel and SavedStateHandle for @Composable

You can have a `SavedStateHandle` constructor parameter, which will be injected regarding the Compose environment (Navigation BackStack or ViewModel).
Either it's injected via ViewModel `CreationExtras` or via Navigation `BackStackEntry`:

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

### Shared ViewModel and Navigation (Experimental)

Koin Compose Naviation has now a `NavBackEntry.sharedKoinViewModel()` function, to allow to retrieve ViewModel already stored in current NavBackEntry. Inside your navigation part, just use `sharedKoinViewModel`:

```kotlin
navigation<Route.BookGraph>(
                startDestination = Route.BookList
            ) {
                composable<Route.BookList>(
                    exitTransition = { slideOutHorizontally() },
                    popEnterTransition = { slideInHorizontally() }
                ) {
                    // Use SharedViewModel here ...

                    val selectedBookViewModel =
                        it.sharedKoinViewModel<SelectedBookViewModel>(navController)
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