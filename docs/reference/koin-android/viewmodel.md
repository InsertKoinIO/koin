---
title: Android ViewModel
description: Retrieve ViewModels in Activities and Fragments with by viewModel(), share them with activityViewModel(), scope them to a navigation graph, or use them from Java.
---

This page covers the Android APIs to retrieve a ViewModel in an Activity or a Fragment: `by viewModel()`, `activityViewModel()`, navigation graph ViewModels and the Java API. How to declare a ViewModel, pass it parameters and use `SavedStateHandle` is the same on every platform: see [ViewModel](/docs/reference/koin-core/viewmodel). For Compose, see [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel).

The retrieval code below is the same whichever style you use to declare the ViewModel (`viewModel<UserViewModel>()`, `@KoinViewModel` or `viewModelOf(::UserViewModel)`).

## In an Activity

```kotlin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class UserActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadUser()

        // eager alternative, inside a function
        val sameViewModel: UserViewModel = getViewModel()
    }
}
```

`by viewModel()` is lazy: the ViewModel is created or retrieved on first access. Both functions go through the Activity `ViewModelStore`, so after a rotation you get the same instance back. Never retrieve a ViewModel with `by inject()` or `get()`: you would get a new, unmanaged instance each time.

These functions run on the main thread (`@MainThread`). Read the property in or after `onCreate()`.

## In a Fragment

```kotlin
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserDetailFragment : Fragment() {

    // owned by this Fragment
    private val detailViewModel: UserDetailViewModel by viewModel()

    // owned by the host Activity, shared with it and its other Fragments
    private val userViewModel: UserViewModel by activityViewModel()
}
```

| Function | Owner of the ViewModel | Eager version |
|----------|------------------------|---------------|
| `by viewModel()` | The Fragment | `getViewModel()` |
| `by activityViewModel()` | The host Activity | `getActivityViewModel()` |

### Sharing between Fragments

Fragments that use `activityViewModel()` for the same class get the same instance, and so does the Activity with `by viewModel()`:

```kotlin
class WeatherActivity : AppCompatActivity() {
    private val weatherViewModel: WeatherViewModel by viewModel()
}

class WeatherHeaderFragment : Fragment() {
    private val weatherViewModel: WeatherViewModel by activityViewModel()
}

class WeatherListFragment : Fragment() {
    private val weatherViewModel: WeatherViewModel by activityViewModel()
}
```

### Another owner

`viewModel()` takes an `ownerProducer`. Use it to share a ViewModel between child Fragments of the same parent:

```kotlin
class ChildFragment : Fragment() {
    private val parentViewModel: ParentViewModel by viewModel(ownerProducer = { requireParentFragment() })
}
```

## Parameters and qualifiers

Pass values to the ViewModel with a `parametersOf` lambda, and pick a definition with a qualifier:

```kotlin
class ItemActivity : AppCompatActivity() {

    private val itemId: String by lazy { intent.getStringExtra("ITEM_ID").orEmpty() }

    private val viewModel: ItemViewModel by viewModel { parametersOf(itemId) }
    private val draftViewModel: EditorViewModel by viewModel(qualifier = named("draft"))
}
```

How to declare the parameter on the ViewModel side (`@InjectedParam`, `params.get()`) is in [ViewModel](/docs/reference/koin-core/viewmodel). Parameters are used only when the ViewModel is first created: after a rotation, the ViewModel is retrieved from the store and the new parameters are ignored.

With a qualifier, Koin stores the ViewModel under its own key. Two qualifiers on the same class give two separate instances in the same owner.

## SavedStateHandle

Add a `SavedStateHandle` parameter to the ViewModel constructor and Koin provides it. Declaration is covered in [ViewModel](/docs/reference/koin-core/viewmodel). On Android, the handle is built from the owner `CreationExtras`: it contains the Activity intent extras or the Fragment arguments. Pass `extrasProducer` to `viewModel()` if you need other extras.

The `stateViewModel()` functions from older Koin versions are removed. Use `viewModel()`.

## Navigation graph ViewModel

With the `io.insert-koin:koin-androidx-navigation` artifact, scope a ViewModel to a Navigation component graph:

```kotlin
import org.koin.androidx.navigation.koinNavGraphViewModel

class CheckoutStepFragment : Fragment() {
    private val checkoutViewModel: CheckoutViewModel by koinNavGraphViewModel(R.id.checkout_graph)
}
```

The ViewModel is owned by the back stack entry of `R.id.checkout_graph`:

- Created: the first time a Fragment of the graph reads the property.
- Shared: every Fragment in the graph that asks for it gets the same instance.
- Cleared: when the graph is popped from the back stack.

`koinNavGraphViewModel()` takes the same `qualifier`, `extrasProducer` and `parameters` arguments as `viewModel()`. For Navigation Compose, see [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel).

## ViewModels and Android scopes

When an Activity or Fragment has a Koin scope (`ScopeActivity`, `AndroidScopeComponent`), `by viewModel()` resolves the ViewModel from that scope. The ViewModel can then receive dependencies scoped to the Activity.

:::warning
Don't inject Activity-scoped or Fragment-scoped instances into a ViewModel. The ViewModel survives a rotation, the Activity scope does not: the ViewModel keeps a reference to closed objects and leaks the old Activity. For instances that live as long as the ViewModel, use the ViewModel scope (see [Android Scopes](/docs/reference/koin-android/scope#viewmodel-scope)).
:::

## Lower-level API

`viewModelForClass()` takes a `KClass` instead of a reified type. Use it when the class is only known at runtime:

```kotlin
val viewModel: UserViewModel by viewModelForClass(
    clazz = UserViewModel::class,
    owner = this,
    key = "user_42",
    parameters = { parametersOf("42") },
)
```

`key` lets you keep several instances of the same class in one owner. Unlike `viewModel()`, it starts from empty `CreationExtras`: pass a `state` argument if the ViewModel needs a `SavedStateHandle`.

## Java

Add the `io.insert-koin:koin-android-compat` artifact and use the static functions of `ViewModelCompat`:

```java
import kotlin.Lazy;
import org.koin.android.compat.ViewModelCompat;

public class UserJavaActivity extends AppCompatActivity {

    private final Lazy<UserViewModel> viewModel =
        ViewModelCompat.viewModel(this, UserViewModel.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModel vm = ViewModelCompat.getViewModel(this, UserViewModel.class);
    }
}
```

`SharedViewModelCompat` gives the host Activity ViewModel from a Fragment, and `ScopeCompat` resolves from a given scope. `ViewModelCompat` and `SharedViewModelCompat` always resolve from the root scope, never from an Activity scope.

## Related

- [ViewModel](/docs/reference/koin-core/viewmodel): declare ViewModels, parameters and `SavedStateHandle` on every platform.
- [Android Scopes](/docs/reference/koin-android/scope): Activity, Fragment and ViewModel scopes.
- [Android Entry Points](/docs/reference/koin-android/entry-points): inject other dependencies in Activities and Fragments.
- [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel): `koinViewModel()` in Composables.
- [Unit Testing](/docs/reference/koin-test/testing): test ViewModels with Koin.
