---
title: Injecting Android ViewModel
---

The `koin-android` Gradle module introduces a new `viewModel` DSL keyword that comes in complement of `single` and `factory`, to help declare a ViewModel
component and bind it to an Android Component lifecycle.

```kotlin
val appModule = module {

    // ViewModel for Detail View
    viewModel { DetailViewModel(get(), get()) }

}
```

Your declared component must at least extends the `android.arch.lifecycle.ViewModel` class. You can specify how you inject the *constructor* of the class
and use the `get()` function to inject dependencies.

:::info
- The `viewModel` keyword helps declaring a factory instance of ViewModel. This instance will be handled by internal ViewModelFactory and reattach ViewModel instance if needed.

- The `viewModel` keyword can also let you use the injection parameters.
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

## Shared ViewModel

One ViewModel instance can be shared between Fragments and their host Activity.

To inject a *shared* ViewModel in a `Fragment` use:

* `by sharedViewModel()` - lazy delegate property to inject shared ViewModel instance into a property
* `getSharedViewModel()` - directly get the shared ViewModel instance

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
    private val weatherViewModel by sharedViewModel<WeatherViewModel>()
}

class WeatherListFragment : Fragment() {

    /*
     * Declare shared WeatherViewModel with WeatherActivity
     */
    private val weatherViewModel by sharedViewModel<WeatherViewModel>()
}
```

## Passing Parameters to Constructor

the `viewModel` keyword and injection API is compatible with injection parameters.

In the module:

```kotlin
val appModule = module {

    // ViewModel for Detail View with id as parameter injection
    viewModel { parameters -> DetailViewModel(id = parameters.get(), get(), get()) }
}
```

or even In the module:

```kotlin
val appModule = module {

    // ViewModel for Detail View with id as parameter injection, resolved from graph
    viewModel { DetailViewModel(get(), get(), get()) }
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

## State Handle Injection

Add a new property typed `SavedStateHandle` to your constructor to handle your ViewModel state:

```kotlin
class MyStateVM(val handle: SavedStateHandle, val myService : MyService) : ViewModel()
```

In Koin module, just resolve it with `get()` or by parameters:

```kotlin
viewModel { MyStateVM(get(), get()) }
// or
viewModel { params -> MyStateVM(params.get(), get()) }
```

Just call your ViewModel:

```kotlin
val myStateVM: MyStateVM by viewModel()
```

## Bundle Injection

Passing `SavedStateHandle` to your ViewModel can also help you pass Bundle arguments. Just declare your ViewModel:

```kotlin
class MyStateVM(val handle: SavedStateHandle, val myService : MyService) : ViewModel()
```

```kotlin
// in your module
viewModel { params -> MyStateVM(params.get(), get()) }
```

Just call your ViewModel with following `stateViewModel` function and `state` parameter:

```kotlin
val myStateVM: MyStateVM by stateViewModel( state = { Bundle(...) })
```

## Navigation Graph ViewModel (3.1.3)

You can scope a ViewModel instance to your Navigation graph. Just retrieve with `by koinNavGraphViewModel()`. You just need your graph Id.

```kotlin
class NavFragment : Fragment() {

    val mainViewModel: NavViewModel by koinNavGraphViewModel(R.id.my_graph)

}
```

## ViewModel API - Java Compat 

Java compatibility must be added to your dependencies:

```groovy
// Java Compatibility
implementation "io.insert-koin:koin-android-compat:$koin_version"
```

You can inject the ViewModel instance to your Java codebase by using `viewModel()` or `getViewModel` static functions from `ViewModelCompat`:

```java
// import viewModel
import static org.koin.android.compat.ViewModelCompat.viewModel;

// import getViewModel  
import static org.koin.android.compat.ViewModelCompat.getViewModel;

public class JavaActivity extends AppCompatActivity {

    // lazy ViewModel
    private final Lazy<DetailViewModel> viewModelLazy = viewModel(this, DetailViewModel.class);

    // directly get the ViewModel instance
    private final DetailViewModel viewModel = getViewModel(this, DetailViewModel.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        //...
    }
}
```

:::note 
ViewModel API is accessible from Koin & Scope instances. But also from [`ViewModelStoreOwner` class](https://github.com/InsertKoinIO/koin/tree/master/koin-projects/koin-androidx-viewmodel/src/main/java/org/koin/androidx/viewmodel/ext/android)
:::

:::info 
The Activity sharing its ViewModel injects it with `by viewModel()` or `getViewModel()`. Fragments are reusing  the shared ViewModel with `by sharedViewModel()`.
:::

For your Java Fragment, must be used `sharedViewModel` or `getSharedViewModel` from `SharedViewModelCompat`.

```java
// import sharedViewModel static function
import org.koin.android.compat.SharedViewModelCompat;

public class JavaFragment extends Fragment {

    private final Lazy<WeatherViewModel> viewModel = sharedViewModel(this, WeatherViewModel.class);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //...
    }
}
```
