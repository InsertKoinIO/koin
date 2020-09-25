
This page describe how you can inject your dependencies for your Jetpack Compose app (https://developer.android.com/jetpack/compose)

## Injecting into a @Composable

While writing your composable function, you gain access to the following Koin API:

* `get()` - fetch instance from Koin container
* `by inject()` - lazy evaluated instance from Koin container

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
    // or val myService by inject<MyService>()
}
```

Koin Global instance is also accessible with `getKoin()`:

```kotlin
@Composable
fun App() {
    val koin = getKoin()
}
```

## ViewModel for @Composable

The same way you have access to classical single/factory instances, you gain access to the following Koin ViewModel API:

* `getViewModel()` - fetch instance
* `by viewModel()` - lazy evaluated

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
    // or val vm by viewModel<MyViewModel>()
}
```




