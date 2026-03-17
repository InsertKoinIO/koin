---
title: Android ViewModel
---

This page covers Android-specific ViewModel features. For core ViewModel DSL and multiplatform support, see [ViewModel](/docs/reference/koin-core/viewmodel).

## Overview

[ViewModels](https://developer.android.com/topic/libraries/architecture/viewmodel) are architecture components designed to survive configuration changes and manage UI-related data. Koin provides special support for ViewModels with lifecycle-aware injection.

### Key Concepts

- **Survives Configuration Changes** - ViewModels persist through rotations and theme changes
- **Scoped to Lifecycle** - Tied to Activity, Fragment, or Navigation Graph lifecycle
- **Lazy Creation** - Created only when first accessed
- **Shared Instances** - Can be shared between Fragments and their host Activity

:::info
**Multiplatform ViewModel** - Koin ViewModel DSL is fully multiplatform via `koin-core-viewmodel`. For Compose Multiplatform, see [Compose ViewModel](/docs/reference/koin-compose/compose#viewmodel-for-composable).
:::

### ViewModel Scope Limitations

:::warning
**Important:** ViewModels are created against the root Koin scope and **cannot access** Activity or Fragment scoped dependencies. This prevents memory leaks as ViewModels outlive Activities and Fragments.

**Need scoped dependencies in ViewModel?** Use [ViewModel Scope](/docs/reference/koin-core/scopes#viewmodel-scope) to create a dedicated scope tied to your ViewModel's lifecycle.
:::

## Declaring ViewModels

### Compiler Plugin DSL

```kotlin
val appModule = module {
    viewModel<DetailViewModel>()
    viewModel<UserViewModel>()
}
```

### Annotations

```kotlin
@KoinViewModel
class DetailViewModel(
    private val repository: DetailRepository
) : ViewModel()

@KoinViewModel
class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel()
```

### Classic DSL

```kotlin
val appModule = module {
    // With constructor reference
    viewModelOf(::DetailViewModel)

    // With lambda
    viewModel { DetailViewModel(get()) }
}
```

## Injecting ViewModels

In `Activity`, `Fragment` or `Service`, use:

* `by viewModel()` - lazy delegate property
* `getViewModel()` - eager fetch

```kotlin
class DetailActivity : AppCompatActivity() {

    // Lazy inject ViewModel
    private val viewModel: DetailViewModel by viewModel()

    // Or eager
    // private val viewModel: DetailViewModel = getViewModel()
}
```

## Shared ViewModel (Activity)

Share a ViewModel between Fragments and their host Activity:

* `by activityViewModel()` - lazy delegate for shared ViewModel
* `getActivityViewModel()` - eager fetch

```kotlin
class WeatherActivity : AppCompatActivity() {
    private val weatherViewModel: WeatherViewModel by viewModel()
}

class WeatherHeaderFragment : Fragment() {
    // Shared with Activity
    private val weatherViewModel: WeatherViewModel by activityViewModel()
}

class WeatherListFragment : Fragment() {
    // Same instance as WeatherHeaderFragment
    private val weatherViewModel: WeatherViewModel by activityViewModel()
}
```

## Passing Parameters

### Compiler Plugin DSL

```kotlin
class DetailViewModel(
    @InjectedParam val itemId: String,
    private val repository: DetailRepository
) : ViewModel()

val appModule = module {
    viewModel<DetailViewModel>()
}
```

### Annotations

```kotlin
@KoinViewModel
class DetailViewModel(
    @InjectedParam val itemId: String,
    private val repository: DetailRepository
) : ViewModel()
```

### Classic DSL

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

### Injection Site

```kotlin
class DetailActivity : AppCompatActivity() {

    private val itemId: String by lazy { intent.getStringExtra("ITEM_ID")!! }

    // Pass parameter at injection
    private val viewModel: DetailViewModel by viewModel { parametersOf(itemId) }
}
```

## SavedStateHandle

Add `SavedStateHandle` to your ViewModel constructor - Koin injects it automatically:

### Annotations

```kotlin
@KoinViewModel
class MyStateViewModel(
    private val handle: SavedStateHandle,
    private val repository: MyRepository
) : ViewModel()
```

### DSL

```kotlin
class MyStateViewModel(
    private val handle: SavedStateHandle,
    private val repository: MyRepository
) : ViewModel()

val appModule = module {
    viewModel<MyStateViewModel>()  // Compiler Plugin DSL
    // or
    viewModelOf(::MyStateViewModel)  // Classic DSL
}
```

### Usage

```kotlin
class DetailActivity : AppCompatActivity() {
    // SavedStateHandle automatically injected
    private val viewModel: MyStateViewModel by viewModel()
}
```

:::info
All `stateViewModel` functions are deprecated. Use the regular `viewModel` function - `SavedStateHandle` is injected automatically.
:::

## Navigation Graph ViewModel

Scope a ViewModel to a Navigation graph:

```kotlin
class NavFragment : Fragment() {

    // Scoped to navigation graph
    private val navViewModel: NavViewModel by koinNavGraphViewModel(R.id.my_graph)
}
```

The ViewModel is:
- Created when first fragment in graph accesses it
- Shared across all fragments in the graph
- Destroyed when navigation graph is popped

## ViewModel with Scoped Dependencies

If your ViewModel needs its own scoped dependencies, use [ViewModel Scope](/docs/reference/koin-core/scopes#viewmodel-scope):

```kotlin
val appModule = module {
    viewModelScope {
        scoped<UserCache>()
        scoped<UserRepository>()
        viewModel<UserViewModel>()
    }
}
```

```kotlin
@ViewModelScope
class UserCache

@ViewModelScope
class UserRepository(private val cache: UserCache)

@KoinViewModel
@ViewModelScope
class UserViewModel(
    private val repository: UserRepository
) : ViewModel()
```

## ViewModel Generic API

For advanced use cases, Koin provides lower-level APIs:

```kotlin
// From ComponentActivity or Fragment
val viewModel = viewModelForClass(
    clazz = MyViewModel::class,
    qualifier = null,
    owner = this,
    key = null,
    parameters = { parametersOf("param") }
)
```

## Java Compatibility

Add the compat dependency:

```groovy
implementation "io.insert-koin:koin-android-compat:$koin_version"
```

Use `ViewModelCompat` static methods:

```java
MyViewModel viewModel = ViewModelCompat.getViewModel(this, MyViewModel.class);
```

## Quick Reference

| Action | Code |
|--------|------|
| Declare ViewModel | `viewModel<MyVM>()` / `@KoinViewModel` |
| Inject in Activity/Fragment | `by viewModel()` |
| Share with Activity | `by activityViewModel()` |
| Pass parameters | `by viewModel { parametersOf(id) }` |
| Navigation graph scope | `by koinNavGraphViewModel(R.id.graph)` |
| With SavedStateHandle | Just add to constructor |

## Next Steps

- **[Core ViewModel](/docs/reference/koin-core/viewmodel)** - Multiplatform ViewModel DSL
- **[Scopes](/docs/reference/koin-core/scopes#viewmodel-scope)** - ViewModel Scope for scoped dependencies
- **[Testing](/docs/reference/koin-test/testing)** - Testing ViewModels
- **[Compose](/docs/reference/koin-compose/compose)** - ViewModels in Compose
