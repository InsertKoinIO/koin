---
title: ViewModel in Compose
---

# ViewModel in Compose

Koin provides several APIs for injecting ViewModels in Compose applications. This guide covers all ViewModel injection patterns.

:::info
For declaring ViewModels in modules, see [Core ViewModel](/docs/reference/koin-core/viewmodel). This page focuses on retrieving ViewModels in Compose.
:::

## Setup

```kotlin
// Compose Multiplatform (or Android)
implementation("io.insert-koin:koin-compose-viewmodel:$koin_version")

// Android convenience (includes koin-compose + koin-compose-viewmodel)
implementation("io.insert-koin:koin-androidx-compose:$koin_version")

// With Navigation integration
implementation("io.insert-koin:koin-compose-viewmodel-navigation:$koin_version")
```

:::info
All ViewModel APIs are in `koin-compose-viewmodel`. The `koin-androidx-compose` package includes it automatically.
:::

## Declaring ViewModels

### Compiler Plugin DSL

```kotlin
class UserViewModel(
    private val repository: UserRepository
) : ViewModel()

val appModule = module {
    viewModel<UserViewModel>()
}
```

### Annotations

```kotlin
@KoinViewModel
class UserViewModel(
    private val repository: UserRepository
) : ViewModel()
```

### Classic DSL

```kotlin
val appModule = module {
    viewModelOf(::UserViewModel)
    // or with lambda
    viewModel { UserViewModel(get()) }
}
```

## ViewModel Injection APIs

### koinViewModel() - Basic Injection

The primary API for injecting ViewModels in Compose:

```kotlin
@Composable
fun UserScreen() {
    val viewModel = koinViewModel<UserViewModel>()
    // Use viewModel...
}
```

**Best practice** - inject as default parameter for testability:

```kotlin
@Composable
fun UserScreen(
    viewModel: UserViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    // UI...
}
```

### koinNavViewModel() - With Navigation Arguments

When using Navigation Compose, use `koinNavViewModel()` to automatically receive navigation arguments via `SavedStateHandle`:

```kotlin
// Route with arguments
NavHost(navController, startDestination = "list") {
    composable("detail/{itemId}") { backStackEntry ->
        DetailScreen()
    }
}

// ViewModel receives arguments automatically
class DetailViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val itemId: String = savedStateHandle["itemId"] ?: ""
}

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = koinNavViewModel()
) {
    // viewModel.itemId is populated from navigation arguments
}
```

### koinActivityViewModel() - Activity-Scoped (Android)

Share a ViewModel across all Composables within the same Activity:

```kotlin
@Composable
fun ScreenA() {
    // Same instance across Activity
    val sharedVM = koinActivityViewModel<SharedViewModel>()
}

@Composable
fun ScreenB() {
    // Same instance as ScreenA
    val sharedVM = koinActivityViewModel<SharedViewModel>()
}
```

:::note
Available in `koin-androidx-compose` starting from version 4.1.
:::

### sharedKoinViewModel() - Navigation Graph Scoped

Share a ViewModel within a navigation graph (experimental):

```kotlin
navigation<Route.BookGraph>(startDestination = Route.BookList) {
    composable<Route.BookList> { backStackEntry ->
        val sharedVM = backStackEntry.sharedKoinViewModel<BookSharedViewModel>(navController)
        BookListScreen(sharedVM)
    }
    composable<Route.BookDetail> { backStackEntry ->
        // Same instance within BookGraph
        val sharedVM = backStackEntry.sharedKoinViewModel<BookSharedViewModel>(navController)
        BookDetailScreen(sharedVM)
    }
}
```

## ViewModel with Parameters

### Using @InjectedParam

Mark runtime parameters with `@InjectedParam`:

```kotlin
class DetailViewModel(
    @InjectedParam val itemId: String,
    private val repository: DetailRepository
) : ViewModel()

// Compiler Plugin DSL
val appModule = module {
    viewModel<DetailViewModel>()
}
```

Inject with parameters:

```kotlin
@Composable
fun DetailScreen(itemId: String) {
    val viewModel = koinViewModel<DetailViewModel> {
        parametersOf(itemId)
    }
}
```

### Classic DSL with Parameters

```kotlin
val appModule = module {
    viewModel { params ->
        DetailViewModel(
            itemId = params.get(),
            repository = get()
        )
    }
}
```

## SavedStateHandle

Koin automatically provides `SavedStateHandle` to ViewModels:

```kotlin
@KoinViewModel
class MyViewModel(
    private val handle: SavedStateHandle,
    private val repository: UserRepository
) : ViewModel() {
    // Access navigation arguments
    val userId: String? = handle["userId"]

    // Persist state across process death
    var query by handle.saveable { mutableStateOf("") }
}
```

```kotlin
val appModule = module {
    viewModel<MyViewModel>()  // SavedStateHandle injected automatically
}
```

:::info
`SavedStateHandle` is injected from either ViewModel `CreationExtras` or Navigation `BackStackEntry`, depending on context.
:::

## ViewModel Scope

Scope dependencies to ViewModel lifecycle using `viewModelScope`:

### Compiler Plugin DSL

```kotlin
val appModule = module {
    viewModelScope {
        scoped<UserCache>()
        scoped<UserRepository>()
        viewModel<UserViewModel>()
    }
}
```

### Annotations

```kotlin
@ViewModelScope
class UserCache

@ViewModelScope
class UserRepository(private val cache: UserCache)

@KoinViewModel
@ViewModelScope
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

### Classic DSL

```kotlin
val appModule = module {
    viewModelScope {
        scoped { UserCache() }
        scoped { UserRepository(get()) }
        viewModel { UserViewModel(get()) }
    }
}
```

## Quick Reference

| API | Use Case | Package |
|-----|----------|---------|
| `koinViewModel()` | Basic ViewModel injection | `koin-compose-viewmodel` |
| `koinNavViewModel()` | With Navigation arguments | `koin-compose-viewmodel-navigation` |
| `koinActivityViewModel()` | Shared across Activity (Android) | `koin-androidx-compose` |
| `sharedKoinViewModel()` | Shared within nav graph | `koin-compose-viewmodel-navigation` |

## Best Practices

1. **Inject as default parameters** - enables testing without Koin
   ```kotlin
   @Composable
   fun MyScreen(viewModel: MyViewModel = koinViewModel())
   ```

2. **Use koinNavViewModel() with Navigation** - automatic argument handling

3. **Prefer viewModelScope for ViewModel-specific dependencies** - clean lifecycle management

4. **Don't inject ViewModels in callbacks** - inject at Composable level
   ```kotlin
   // Bad
   Button(onClick = { val vm = koinViewModel<MyVM>() })

   // Good
   val vm = koinViewModel<MyVM>()
   Button(onClick = { vm.doSomething() })
   ```

## Next Steps

- **[Compose Lifecycle](/docs/reference/koin-compose/compose-lifecycle)** - State and recomposition
- **[Core ViewModel](/docs/reference/koin-core/viewmodel)** - ViewModel declaration DSL
- **[Android ViewModel](/docs/reference/koin-android/viewmodel)** - Android-specific features
