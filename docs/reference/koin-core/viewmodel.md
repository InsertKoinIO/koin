---
title: ViewModel
---

Koin provides multiplatform ViewModel support through the `koin-core-viewmodel` module. This allows you to declare and inject [AndroidX ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) instances across all Kotlin Multiplatform targets.

## Setup

Add the core ViewModel dependency:

```kotlin
// build.gradle.kts (commonMain)
implementation("io.insert-koin:koin-core-viewmodel:$koin_version")
```

For platform-specific injection APIs, add:

```kotlin
// Android
implementation("io.insert-koin:koin-android:$koin_version")

// Compose Multiplatform
implementation("io.insert-koin:koin-compose-viewmodel:$koin_version")
```

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
    // With constructor reference
    viewModelOf(::UserViewModel)

    // With lambda
    viewModel { UserViewModel(get()) }
}
```

## ViewModel with Parameters

Pass parameters at injection time using `@InjectedParam`:

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

## ViewModel Scope

ViewModels that need their own scoped dependencies use the `viewModelScope` archetype. Dependencies declared inside `viewModelScope` are tied to the ViewModel's lifecycle.

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
class UserViewModel(
    private val repository: UserRepository
) : ViewModel()
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

:::info
Dependencies inside `viewModelScope` are created when the ViewModel is first accessed and destroyed when the ViewModel is cleared.
:::

## Injecting ViewModels

### In Compose (Multiplatform)

Use `koinViewModel()` in Composable functions:

```kotlin
@Composable
fun UserScreen() {
    val viewModel = koinViewModel<UserViewModel>()
    // or with parameters
    val detailVM = koinViewModel<DetailViewModel> { parametersOf("item_123") }
}
```

### In Android

Use `by viewModel()` delegate in Activity or Fragment:

```kotlin
class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModel()

    // With parameters
    private val detailVM: DetailViewModel by viewModel { parametersOf("item_123") }
}
```

## SavedStateHandle

Add `SavedStateHandle` to your ViewModel constructor - Koin injects it automatically:

```kotlin
@KoinViewModel
class MyViewModel(
    private val handle: SavedStateHandle,
    private val repository: UserRepository
) : ViewModel() {

    val userId: String? = handle["userId"]
}
```

```kotlin
val appModule = module {
    viewModel<MyViewModel>()  // Compiler Plugin DSL
    // or
    viewModelOf(::MyViewModel)  // Classic DSL
}
```

## Quick Reference

| Approach | Module Declaration | Scope Declaration |
|----------|-------------------|-------------------|
| Compiler Plugin DSL | `viewModel<MyVM>()` | `viewModelScope { viewModel<MyVM>() }` |
| Annotations | `@KoinViewModel` | `@KoinViewModel @ViewModelScope` |
| Classic DSL | `viewModelOf(::MyVM)` | `viewModelScope { viewModelOf(::MyVM) }` |

| Platform | Injection API |
|----------|---------------|
| Compose | `koinViewModel<MyVM>()` |
| Android | `by viewModel()` |

## Platform-Specific Features

- **Android**: See [Android ViewModel](/docs/reference/koin-android/viewmodel) for Activity/Fragment sharing, Navigation Graph scoping
- **Compose**: See [Compose ViewModel](/docs/reference/koin-compose/compose#viewmodel-for-composable) for Compose-specific APIs

## Next Steps

- **[Scopes](/docs/reference/koin-core/scopes)** - Core scope concepts
- **[Android ViewModel](/docs/reference/koin-android/viewmodel)** - Android-specific features
- **[Compose](/docs/reference/koin-compose/compose)** - Compose Multiplatform integration
