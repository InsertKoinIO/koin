---
title: Koin for Compose
---

# Koin for Compose

Koin provides full support for Jetpack Compose and Compose Multiplatform applications with dedicated packages for dependency injection.

## Packages Overview

| Package | Use Case |
|---------|----------|
| `koin-compose` | Base Compose API (multiplatform) |
| `koin-compose-viewmodel` | ViewModel injection (multiplatform) |
| `koin-compose-viewmodel-navigation` | ViewModel + Navigation 2.x |
| `koin-compose-navigation3` | Navigation 3 integration (multiplatform) |
| `koin-androidx-compose` | Android convenience (includes koin-compose + koin-compose-viewmodel) |

:::info
All Compose APIs are defined in `koin-compose` and `koin-compose-viewmodel`. The `koin-androidx-compose` package is a convenience wrapper that includes both for Android projects.
:::

### Which Package Should I Use?

**For Android-only projects:**
```kotlin
// Option 1: Android convenience package (includes koin-compose + koin-compose-viewmodel)
implementation("io.insert-koin:koin-androidx-compose:$koin_version")

// Option 2: Use multiplatform packages directly
implementation("io.insert-koin:koin-compose:$koin_version")
implementation("io.insert-koin:koin-compose-viewmodel:$koin_version")

// Optional: Navigation integration
implementation("io.insert-koin:koin-androidx-compose-navigation:$koin_version")
```

**For Compose Multiplatform projects:**
```kotlin
commonMain.dependencies {
    implementation("io.insert-koin:koin-compose:$koin_version")
    implementation("io.insert-koin:koin-compose-viewmodel:$koin_version")

    // Optional: Navigation integration
    implementation("io.insert-koin:koin-compose-viewmodel-navigation:$koin_version")
}
```

## Platform Support

| Platform | Compose Type | Status |
|----------|-------------|--------|
| Android | Jetpack Compose | Full support |
| iOS | Compose Multiplatform | Full support |
| Desktop | Compose Desktop | Full support |
| Web | Compose for Web | Experimental |

## Starting Koin

### Option 1: startKoin (Android only or External Setup)

Initialize Koin outside Compose for full control:

```kotlin
// Android Application class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(appModule)
        }
    }
}

// Compose UI uses Koin automatically
@Composable
fun App() {
    val viewModel = koinViewModel<MyViewModel>()
}
```

**Use when:** You need full control over Koin lifecycle, custom configuration, or integration with other frameworks.

### Option 2: KoinApplication (Compose-Managed)

Let Compose handle Koin setup automatically:

```kotlin
@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration {
        modules(appModule)
    }) {
        MyScreen()
    }
}
```

**Advantages:**
- No external setup required (no Application class needed)
- Android Context injected automatically
- Handles start/stop based on composition lifecycle
- Manages configuration changes on Android

**Use when:** You want the simplest setup with less control.

Automatically injects `androidContext` and `androidLogger` on Android.

:::note
`KoinMultiplatformApplication` is deprecated. Use `KoinApplication` with `koinConfiguration` instead.
:::

## Basic Injection

### koinInject() - Get Dependencies

Inject any Koin-managed dependency:

```kotlin
@Composable
fun UserScreen() {
    val repository = koinInject<UserRepository>()
    // Use repository...
}
```

**Best practice** - inject as default parameter:

```kotlin
@Composable
fun UserScreen(
    repository: UserRepository = koinInject()
) {
    // Testable without Koin
}
```

### koinViewModel() - Get ViewModels

Inject ViewModels with proper lifecycle management:

```kotlin
@Composable
fun UserScreen() {
    val viewModel = koinViewModel<UserViewModel>()
    val state by viewModel.state.collectAsState()
}
```

:::info
See [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel) for all ViewModel APIs.
:::

### With Parameters

Pass runtime parameters:

```kotlin
@Composable
fun DetailScreen(itemId: String) {
    val viewModel = koinViewModel<DetailViewModel> {
        parametersOf(itemId)
    }
}
```

For better performance with frequent recomposition:

```kotlin
@Composable
fun DetailScreen(itemId: String) {
    val viewModel = koinViewModel<DetailViewModel>(
        parameters = parametersOf(itemId)
    )
}
```

## Defining Modules

### Compiler Plugin DSL

```kotlin
val appModule = module {
    single<UserRepository>()
    viewModel<UserViewModel>()
}
```

### Annotations

```kotlin
@Singleton
class UserRepository

@KoinViewModel
class UserViewModel(
    private val repository: UserRepository
) : ViewModel()
```

### Classic DSL

```kotlin
val appModule = module {
    singleOf(::UserRepository)
    viewModelOf(::UserViewModel)
}
```

## Quick Reference

| Function | Purpose |
|----------|---------|
| `koinInject<T>()` | Inject any dependency |
| `koinViewModel<T>()` | Inject ViewModel |
| `koinNavViewModel<T>()` | ViewModel with Navigation args |
| `koinActivityViewModel<T>()` | Activity-scoped ViewModel (Android) |
| `rememberKoinModules()` | Load modules with composition |
| `KoinScope {}` | Create scoped context |

## Documentation

| Topic | Description |
|-------|-------------|
| **[ViewModel](/docs/reference/koin-compose/compose-viewmodel)** | All ViewModel injection APIs |
| **[Lifecycle & State](/docs/reference/koin-compose/compose-lifecycle)** | Recomposition, state, side effects |
| **[Dynamic Modules](/docs/reference/koin-compose/compose-modules)** | rememberKoinModules, lazy loading |
| **[Scopes](/docs/reference/koin-compose/compose-scopes)** | KoinScope, KoinNavigationScope, UnboundKoinScope |
| **[Testing](/docs/reference/koin-compose/compose-testing)** | Previews, unit tests |
| **[Isolated Context](/docs/reference/koin-compose/isolated-context)** | SDK isolation |
| **[Navigation 3](/docs/reference/koin-compose/navigation3)** | Type-safe navigation (multiplatform) |

## Related

- **[Core ViewModel](/docs/reference/koin-core/viewmodel)** - ViewModel declaration DSL
- **[Android ViewModel](/docs/reference/koin-android/viewmodel)** - Android-specific features
- **[KMP Setup](/docs/reference/koin-core/kmp-setup)** - Multiplatform configuration
