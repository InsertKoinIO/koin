---
title: ViewModel in Compose
description: Retrieve ViewModels in Composables with koinViewModel, pass parameters, and share a ViewModel across a navigation graph or an Activity.
---

This page covers the Compose functions that retrieve a ViewModel: `koinViewModel()`, `sharedKoinViewModel()` and `koinActivityViewModel()`. Declaring a ViewModel in a module, with parameters, `SavedStateHandle` or a ViewModel scope, is the same on every platform: see [ViewModel](/docs/reference/koin-core/viewmodel).

All functions on this page are in `io.insert-koin:koin-compose-viewmodel`, except `sharedKoinViewModel()`, which is in `io.insert-koin:koin-compose-viewmodel-navigation`. See [Koin for Compose](/docs/reference/koin-compose/compose) for the packages.

## koinViewModel

```kotlin
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserScreen(viewModel: UserViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    // ...
}
```

`koinViewModel()` gets the ViewModel from the current `ViewModelStoreOwner` (`LocalViewModelStoreOwner`), and creates it with Koin the first time. The instance lives as long as that owner: an Activity, a Fragment, or a navigation destination.

Declaring it as a default parameter keeps the Composable testable: a test or a preview passes its own instance.

`koinViewModel()` is a Composable function. Call it in the body of a Composable, not in a callback such as `onClick`.

### Parameters

| Parameter | Default | Use |
|-----------|---------|-----|
| `qualifier` | `null` | Pick a [named](/docs/reference/koin-core/qualifiers) ViewModel definition |
| `viewModelStoreOwner` | `LocalViewModelStoreOwner.current` | Store the ViewModel in another owner |
| `key` | `null` | Keep several instances of the same class in one owner |
| `extras` | Extras of the owner | `CreationExtras` used to build `SavedStateHandle` |
| `scope` | `currentKoinScope()` | Koin scope used to resolve the ViewModel |
| `parameters` | `null` | Lambda returning [injected parameters](/docs/reference/koin-core/injection-parameters) |

### Passing parameters

```kotlin
@Composable
fun DetailScreen(itemId: String) {
    val viewModel = koinViewModel<DetailViewModel> { parametersOf(itemId) }
}
```

Parameters are used only when the ViewModel is created. If the same owner already holds a `DetailViewModel`, you get that instance back, even with a different `itemId`. When one owner must hold one ViewModel per value, add a `key`:

```kotlin
@Composable
fun DetailScreen(itemId: String) {
    val viewModel = koinViewModel<DetailViewModel>(key = itemId) {
        parametersOf(itemId)
    }
}
```

With Navigation Compose, each destination has its own owner, so you don't need a `key` for screens on the back stack.

## With Navigation Compose

Inside a `composable { }` destination, the current `ViewModelStoreOwner` is the `NavBackStackEntry`. `koinViewModel()` creates one ViewModel per destination, cleared when the destination is popped. Its `SavedStateHandle` holds the navigation arguments:

```kotlin
@Serializable
data class DetailRoute(val itemId: String)

class DetailViewModel(handle: SavedStateHandle) : ViewModel() {
    val itemId: String = handle.toRoute<DetailRoute>().itemId
}

NavHost(navController, startDestination = HomeRoute) {
    composable<DetailRoute> {
        DetailScreen(viewModel = koinViewModel())
    }
}
```

Koin injects `SavedStateHandle` for you: declare it as a constructor parameter. See [ViewModel](/docs/reference/koin-core/viewmodel) for the declaration side.

:::warning
`koinNavViewModel()` is deprecated, in both `koin-compose-viewmodel-navigation` and `koin-androidx-compose-navigation`. Replace it with `koinViewModel()` and the same parameters: navigation arguments reach `SavedStateHandle` the same way.
:::

### Sharing a ViewModel in a navigation graph

`sharedKoinViewModel()` stores the ViewModel in the back stack entry of the parent graph. Every destination of that graph gets the same instance:

```kotlin
import org.koin.compose.viewmodel.sharedKoinViewModel

navigation<BookGraph>(startDestination = BookList) {
    composable<BookList> { entry ->
        val shared = entry.sharedKoinViewModel<BookSharedViewModel>(navController)
        BookListScreen(shared)
    }
    composable<BookDetail> { entry ->
        val shared = entry.sharedKoinViewModel<BookSharedViewModel>(navController)
        BookDetailScreen(shared)
    }
}
```

The parent graph is `destination.parent?.route` by default. Pass `navGraphRoute` to target another graph. If the destination has no parent graph, `sharedKoinViewModel()` falls back to `koinViewModel()`.

## Activity-wide ViewModel (Android)

`koinActivityViewModel()` stores the ViewModel in the current `ComponentActivity`. Every Composable of that Activity gets the same instance, whatever navigation destination it is in:

```kotlin
import org.koin.compose.viewmodel.koinActivityViewModel

@Composable
fun CartBadge() {
    val cart = koinActivityViewModel<CartViewModel>()
}

@Composable
fun CartScreen() {
    val cart = koinActivityViewModel<CartViewModel>() // same instance as in CartBadge
}
```

It is available on Android only, and fails if `LocalActivity` is not a `ComponentActivity`. It takes the same `qualifier`, `key`, `scope` and `parameters` arguments as `koinViewModel()`.

## Scoped dependencies

A ViewModel can own a scope for its dependencies (`viewModelScope { }` or `@ViewModelScope`). Nothing changes on the Compose side: `koinViewModel()` creates the ViewModel and its scope. The declaration is described in [ViewModel](/docs/reference/koin-core/viewmodel).

## API summary

| API | Owner of the instance | Package |
|-----|-----------------------|---------|
| `koinViewModel()` | `LocalViewModelStoreOwner` (Activity, Fragment, destination) | `koin-compose-viewmodel` |
| `NavBackStackEntry.sharedKoinViewModel()` | Parent navigation graph entry | `koin-compose-viewmodel-navigation` |
| `koinActivityViewModel()` | Current `ComponentActivity` (Android) | `koin-compose-viewmodel` |
| `koinNavViewModel()` | Deprecated, use `koinViewModel()` | `koin-compose-viewmodel-navigation` |

## Related

- [ViewModel](/docs/reference/koin-core/viewmodel): declare ViewModels, parameters, `SavedStateHandle`, ViewModel scope.
- [Android ViewModel](/docs/reference/koin-android/viewmodel): `by viewModel()` in Activities and Fragments.
- [Navigation 3](/docs/reference/koin-compose/navigation3): ViewModels with Navigation 3 entries.
- [Lifecycle and State](/docs/reference/koin-compose/compose-lifecycle): what lives how long in Compose.
- [Compose Testing](/docs/reference/koin-compose/compose-testing): test Composables that use `koinViewModel()`.
