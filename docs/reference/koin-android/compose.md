---
title: Injecting in Jetpack Compose
---

This page describe how you can inject your dependencies for your Jetpack Compose app - https://developer.android.com/jetpack/compose

## Injecting into a @Composable

While writing your composable function, you gain access to the following Koin API:

* `get()` - fetch instance from Koin container
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
    val myService = get<MyService>()
}
```

:::note 
To keep aligned on the functional aspect of Jetpack Compose, the best writing approach is to inject instances directly into functions properties. This way allow to have default implementation with Koin, but keep open to inject instances how you want.
:::

```kotlin
@Composable
fun App(myService: MyService = get()) {
}
```

## ViewModel for @Composable

The same way you have access to classical single/factory instances, you gain access to the following Koin ViewModel API:

* `getViewModel()` or `koinViewModel()` - fetch instance

For a module that declares a 'MyViewModel' component:

```kotlin
module {
    viewModel { MyViewModel() }
}
```

We can get your instance like that:

```kotlin
@Composable
fun App() {
    val vm = getViewModel<MyViewModel>()
}
```

You can specify the `owner` & `scope`: 

```kotlin
inline fun <reified T : ViewModel> getViewModel(
    qualifier: Qualifier? = null,
    owner: ViewModelOwner = getComposeViewModelOwner(),
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    noinline parameters: ParametersDefinition? = null,
)
```

Where ViewModelOwner can be used to pass a StoreOwner (like navigation):

```kotlin
@Composable
fun getComposeViewModelOwner(): ViewModelOwner {
    return ViewModelOwner.from(
        LocalViewModelStoreOwner.current!!,
        LocalSavedStateRegistryOwner.current
    )
}
```

:::warning
Lazy API is not supported with updates of jetpack Compose 1.1+
:::
