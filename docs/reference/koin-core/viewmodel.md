---
title: ViewModel
description: Declare AndroidX ViewModels in Koin for Android and Kotlin Multiplatform, with injected parameters, SavedStateHandle and ViewModel scopes.
---

This page covers how to declare a ViewModel in a Koin module, on every platform that supports AndroidX ViewModel (Android, iOS, desktop, web). It assumes you know [definitions](/docs/reference/koin-core/definitions). To retrieve a ViewModel, see the platform pages: [Android ViewModel](/docs/reference/koin-android/viewmodel) for `by viewModel()` in an Activity or Fragment, and [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel) for `koinViewModel()`.

## Dependencies

The declaration DSL is in `koin-core-viewmodel`, a multiplatform artifact:

```kotlin
// build.gradle.kts, commonMain
implementation("io.insert-koin:koin-core-viewmodel")
```

The injection APIs come with the platform artifacts: `io.insert-koin:koin-android` (it includes `koin-core-viewmodel`) and `io.insert-koin:koin-compose-viewmodel`. Versions come from the BOM: see [Gradle Setup](/docs/setup/gradle).

## Declaring a ViewModel

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

class UserViewModel(private val repository: UserRepository) : ViewModel()

val appModule = module {
    viewModel<UserViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

With the Compiler Plugin, `@KoinViewModel` is in `org.koin.core.annotation` and works in common code. The KSP processor used `org.koin.android.annotation.KoinViewModel`.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.viewModel

val appModule = module {
    viewModelOf(::UserViewModel)

    // or with the constructor call written out
    viewModel { UserViewModel(get()) }
}
```

</TabItem>
</Tabs>

A `viewModel` definition creates a new instance on each request, like a `factory`. The platform `ViewModelStore` (Activity, Fragment, navigation entry) keeps the instance, so the same owner gets the same ViewModel back, across configuration changes on Android. Do not declare a ViewModel as `single`: it would be shared by every screen and never cleared.

## Passing parameters

A ViewModel often needs a value known only at runtime, such as an item id. Mark the constructor parameter as an injected parameter:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class DetailViewModel(
    @InjectedParam val itemId: String,
    private val repository: DetailRepository,
) : ViewModel()

val appModule = module {
    viewModel<DetailViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@KoinViewModel
class DetailViewModel(
    @InjectedParam val itemId: String,
    private val repository: DetailRepository,
) : ViewModel()
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val appModule = module {
    viewModel { params ->
        DetailViewModel(itemId = params.get(), repository = get())
    }
}
```

`viewModelOf(::DetailViewModel)` also works: it reads parameters by type from the values passed by the caller.

</TabItem>
</Tabs>

The caller passes the value with `parametersOf`, whatever the declaration style:

```kotlin
// Android
val viewModel: DetailViewModel by viewModel { parametersOf("item_123") }

// Compose
val viewModel = koinViewModel<DetailViewModel> { parametersOf("item_123") }
```

Parameters are read once, when the ViewModel is created. If the same owner asks again with another value, it gets the existing instance. More on parameters in [Injected Parameters](/docs/reference/koin-core/injection-parameters).

## SavedStateHandle

Add a `SavedStateHandle` parameter to the constructor. Koin creates it from the ViewModel `CreationExtras`, with no declaration:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class ProfileViewModel(
    private val handle: SavedStateHandle,
    private val repository: UserRepository,
) : ViewModel() {
    val userId: String? = handle["userId"]
}

val appModule = module {
    viewModel<ProfileViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@KoinViewModel
class ProfileViewModel(
    private val handle: SavedStateHandle,
    private val repository: UserRepository,
) : ViewModel() {
    val userId: String? = handle["userId"]
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val appModule = module {
    viewModelOf(::ProfileViewModel)

    // or: get() resolves the SavedStateHandle
    viewModel { ProfileViewModel(get(), get()) }
}
```

</TabItem>
</Tabs>

On Android, navigation arguments and Activity extras are in the `SavedStateHandle`. Inject it in the constructor only. If the ViewModel owner has no `SavedStateRegistryOwner`, Koin fails with "Koin could not create a SavedStateHandle: the ViewModel's CreationExtras has no SavedStateRegistryOwner."

## ViewModel scope

A ViewModel can own a [scope](/docs/reference/koin-core/scopes): dependencies created with it and released when it is cleared. Declare them with the `viewModelScope` archetype:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val appModule = module {
    viewModelScope {
        scoped<UserCache>()
        viewModel<UserViewModel>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@ViewModelScope
class UserCache

@KoinViewModel
@ViewModelScope
class UserViewModel(private val cache: UserCache) : ViewModel()
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val appModule = module {
    viewModelScope {
        scopedOf(::UserCache)
        viewModelOf(::UserViewModel)
    }
}
```

</TabItem>
</Tabs>

Enable the option at start, or the ViewModel gets no scope:

```kotlin
startKoin {
    options(viewModelScopeFactory())
    modules(appModule)
}
```

`viewModelScope` and `viewModelScopeFactory()` require opting in to `@KoinViewModelScopeApi` and `@KoinExperimentalAPI`. A ViewModel that takes scoped dependencies in its constructor must be declared inside `viewModelScope { }`. Each ViewModel instance gets its own `UserCache`, closed when the ViewModel is cleared.

## Limits

- **One instance per owner**: injected parameters are ignored once the ViewModel exists for that owner. Use a different key or owner to get a second instance.
- **No `single` ViewModel**: a singleton ViewModel is never cleared and leaks its dependencies.
- **Experimental ViewModel scope**: `viewModelScope` is marked experimental and may change.

## Related

- [Android ViewModel](/docs/reference/koin-android/viewmodel): `by viewModel()`, `activityViewModel()`, navigation graph ViewModels.
- [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel): `koinViewModel()` and navigation ViewModels.
- [Injected Parameters](/docs/reference/koin-core/injection-parameters): `parametersOf` and `@InjectedParam`.
- [Scopes](/docs/reference/koin-core/scopes): scope archetypes and scope lifecycle.
- [Definitions](/docs/reference/koin-core/definitions): the other definition types.
