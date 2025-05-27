---
title: Android ViewModel & Navigation
---

The `koin-android` Gradle module introduces a new `viewModel` DSL keyword that comes in complement of `single` and `factory`, to help declare a ViewModel
component and bind it to an Android Component lifecycle. The `viewModelOf` keyword is also available, to let you declare a ViewModel with its constructor.

```kotlin
val appModule = module {

    // ViewModel for Detail View
    viewModel { DetailViewModel(get(), get()) }

    // or directly with constructor
    viewModelOf(::DetailViewModel)

}
```

Your declared component must at least extends the `android.arch.lifecycle.ViewModel` class. You can specify how you inject the *constructor* of the class
and use the `get()` function to inject dependencies.

:::info
The `viewModel`/`viewModelOf` keyword helps to declare a factory instance of ViewModel. This instance will be handled by internal ViewModelFactory and reattach ViewModel instance if needed.
It also will let inject parameters.
:::


## Injecting your ViewModel

To inject a ViewModel in an `Activity`, `Fragment` or `Service` use:

* `by viewModel()` - lazy delegate property to inject a ViewModel into a property
* `getViewModel()` - directly get the ViewModel instance

```kotlin
class DetailActivity : AppCompatActivity() {

    // Lazy inject ViewModel
    val detailViewModel: DetailViewModel by viewModel()
}
```

:::note
ViewModel key is calculated against Key and/or Qualifier
:::

## Activity Shared ViewModel

One ViewModel instance can be shared between Fragments and their host Activity.

To inject a *shared* ViewModel in a `Fragment` use:

* `by activityViewModel()` - lazy delegate property to inject shared ViewModel instance into a property
* `getActivityViewModel()` - directly get the shared ViewModel instance

:::note
The `sharedViewModel` is deprecated in favor of `activityViewModel()` functions. The naming of this last one is more explicit.
:::

Just declare the ViewModel only once:

```kotlin
val weatherAppModule = module {

    // WeatherViewModel declaration for Weather View components
    viewModel { WeatherViewModel(get(), get()) }
}
```

Note: a qualifier for a ViewModel will be handled as a ViewModel's Tag

And reuse it in Activity and Fragments:

```kotlin
class WeatherActivity : AppCompatActivity() {

    /*
     * Declare WeatherViewModel with Koin and allow constructor dependency injection
     */
    private val weatherViewModel by viewModel<WeatherViewModel>()
}

class WeatherHeaderFragment : Fragment() {

    /*
     * Declare shared WeatherViewModel with WeatherActivity
     */
    private val weatherViewModel by activityViewModel<WeatherViewModel>()
}

class WeatherListFragment : Fragment() {

    /*
     * Declare shared WeatherViewModel with WeatherActivity
     */
    private val weatherViewModel by activityViewModel<WeatherViewModel>()
}
```

## Passing Parameters to the Constructor

The `viewModel` keyword API is compatible with injection parameters.

In the module:

```kotlin
val appModule = module {

    // ViewModel for Detail View with id as parameter injection
    viewModel { parameters -> DetailViewModel(id = parameters.get(), get(), get()) }
    // ViewModel for Detail View with id as parameter injection, resolved from graph
    viewModel { DetailViewModel(get(), get(), get()) }
    // or Constructor DSL
    viewModelOf(::DetailViewModel)
}
```


From the injection call site:

```kotlin
class DetailActivity : AppCompatActivity() {

    val id : String // id of the view

    // Lazy inject ViewModel with id parameter
    val detailViewModel: DetailViewModel by viewModel{ parametersOf(id)}
}
```

## SavedStateHandle Injection (3.3.0)

Add a new property typed `SavedStateHandle` to your constructor to handle your ViewModel state:

```kotlin
class MyStateVM(val handle: SavedStateHandle, val myService : MyService) : ViewModel()
```

In Koin module, just resolve it with `get()` or with parameters:

```kotlin
viewModel { MyStateVM(get(), get()) }
```

or with Constructor DSL:

```kotlin
viewModelOf(::MyStateVM)
```

To inject a *state* ViewModel in a `Activity`,`Fragment` use:

* `by viewModel()` - lazy delegate property to inject state ViewModel instance into a property
* `getViewModel()` - directly get the state ViewModel instance


```kotlin
class DetailActivity : AppCompatActivity() {

    // MyStateVM viewModel injected with SavedStateHandle
    val myStateVM: MyStateVM by viewModel()
}
```

:::info
All `stateViewModel` functions are deprecated. You can just use the regular `viewModel` function to inject a SavedStateHandle
:::

## Navigation Graph ViewModel

You can scope a ViewModel instance to your Navigation graph. Just retrieve with `by koinNavGraphViewModel()`. You just need your graph id.

```kotlin
class NavFragment : Fragment() {

    val mainViewModel: NavViewModel by koinNavGraphViewModel(R.id.my_graph)

}
```

## ViewModel Scope API

see all API to be used for ViewModel and Scopes: [ViewModel Scope](/docs/reference/koin-android/scope.md#viewmodel-scope-since-354)

## ViewModel Generic API

Koin provides some "under the hood" API to directly tweak your ViewModel instance. The available functions are `viewModelForClass` for `ComponentActivity` and `Fragment`:

```kotlin
ComponentActivity.viewModelForClass(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    owner: ViewModelStoreOwner = this,
    state: BundleDefinition? = null,
    key: String? = null,
    parameters: ParametersDefinition? = null,
): Lazy<T>
```

:::note
This function is still using `state: BundleDefinition`, but will convert it to `CreationExtras`
:::

Note that you can have access to the top level function, callable from anywhere:

```kotlin
fun <T : ViewModel> getLazyViewModelForClass(
    clazz: KClass<T>,
    owner: ViewModelStoreOwner,
    scope: Scope = GlobalContext.get().scopeRegistry.rootScope,
    qualifier: Qualifier? = null,
    state: BundleDefinition? = null,
    key: String? = null,
    parameters: ParametersDefinition? = null,
): Lazy<T>
```

## ViewModel API - Java Compat 

Java compatibility must be added to your dependencies:

```groovy
// Java Compatibility
implementation "io.insert-koin:koin-android-compat:$koin_version"
```

You can inject the ViewModel instance to your Java codebase by using `viewModel()` or `getViewModel()` static functions from `ViewModelCompat`:


```kotlin
@JvmOverloads
@JvmStatic
@MainThread
fun <T : ViewModel> getViewModel(
    owner: ViewModelStoreOwner,
    clazz: Class<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null
)
```