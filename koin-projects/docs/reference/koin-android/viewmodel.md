
The `koin-android-viewmodel` project is dedicated to bring Android Architecture ViewModel features.

## ViewModel DSL

The `koin-android-viewmodel` introduces a new `viewModel` DSL keyword that comes in complement of `single` and `factory`, to help declare a ViewModel
component and bind it to an Android Component lifecycle.

```kotlin
val appModule = module {

    // ViewModel for Detail View
    viewModel { DetailViewModel(get(), get()) }

}
```

Your declared component must at least extends the `android.arch.lifecycle.ViewModel` class. You can specify how you inject the *constructor* of the class
and use the `get()` function to inject dependencies.

?> The `viewModel` keyword helps declaring a factory instance of ViewModel. This instance will be handled by internal ViewModelFactory and reattach ViewModel instance if needed.

?> The `viewModel` keyword can also let you use the injection parameters.

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


In a similar way, in Java you can inject the ViewModel instance, but using `viewModel()` or `getViewModel` static functions from `ViewModelCompat`:

```java
// import viewModel
import static org.koin.android.viewmodel.compat.ViewModelCompat.viewModel;

// import getViewModel
import static org.koin.android.viewmodel.compat.ViewModelCompat.getViewModel;

public class JavaActivity extends AppCompatActivity {

    // lazy ViewModel
    private Lazy<DetailViewModel> viewModelLazy = viewModel(this, DetailViewModel.class);

    // directly get the ViewModel instance
    private DetailViewModel viewModel = getViewModel(this, DetailViewModel.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        //...
    }
}
```

> ViewModel API is accessible from Koin & Scope instances. But also from [`ViewModelStoreOwner` class](https://github.com/InsertKoinIO/koin/tree/master/koin-projects/koin-androidx-viewmodel/src/main/java/org/koin/androidx/viewmodel/ext/android)

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

?> The Activity sharing its ViewModel injects it with `by viewModel()` or `getViewModel()`. Fragments are reusing  the shared ViewModel with `by sharedViewModel()`.

For your Java Fragment, must be used `sharedViewModel` or `getSharedViewModel` from `SharedViewModelCompat`.

```java
// import sharedViewModel static function
import static org.koin.android.viewmodel.compat.SharedViewModelCompat.sharedViewModel;

public class JavaFragment extends Fragment {

    private Lazy<WeatherViewModel> viewModel = sharedViewModel(this, WeatherViewModel.class);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //...
    }
}
```


## ViewModel and injection parameters

the `viewModel` keyword and injection API is compatible with injection parameters.

In the module:

```kotlin
val appModule = module {

    // ViewModel for Detail View with id as parameter injection
    viewModel { (id : String) -> DetailViewModel(id, get(), get()) }
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

## ViewModel and State Bundle

in `koin-androidx-viewmodel:2.1.0-alpha-10` we reviewed a cleaner way to deal with state bundle for your `ViewModel`.

Add a new property typed `SavedStateHandle` to your constructor:

```kotlin
class MyStateVM(val handle: SavedStateHandle, val myService : MyService) : ViewModel()
```

In Koin module, use parameter injection:

```kotlin
viewModel { (handle: SavedStateHandle) -> MyStateVM(handle, get()) }
```

!> `SavedStateHandle` argument is always inserted as the first parameters

In Your Activity/Fragment, use the `getStateViewModel()` or `by stateViewModel()` API to reach your StateViewModel:

```kotlin
val myStateVM: MyStateVM by stateViewModel()
```

You can even pass a bundle as state argument:

```kotlin
val myStateVM: MyStateVM by stateViewModel(bundle = myBundle)
```


> StateViewModel API is accessible from Koin & Scope instances. But also from [`SavedStateRegistryOwner` class](https://github.com/InsertKoinIO/koin/tree/master/koin-projects/koin-androidx-viewmodel/src/main/java/org/koin/androidx/viewmodel/ext/android)





