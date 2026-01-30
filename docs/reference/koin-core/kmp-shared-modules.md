---
title: Sharing Patterns
---

# Sharing Patterns

This guide covers patterns for organizing Koin modules in Kotlin Multiplatform projects.

:::info
For basic KMP setup, see [KMP Setup](/docs/reference/koin-core/kmp-setup). For definition types, see [Definitions](/docs/reference/koin-core/definitions).
:::

## The Shared Module Pattern

Create a common initialization function that can be extended by each platform:

```kotlin
// commonMain/kotlin/di/KoinHelper.kt
fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        includes(config)  // Platform-specific extensions
        modules(
            sharedModule,
            dataModule,
            domainModule,
            platformModule
        )
    }
}
```

## Module Organization

Examples use Compiler Plugin DSL. You can also use Annotations or Classic DSL.

### By Layer

```kotlin
// commonMain/kotlin/di/modules/

// Data layer
val dataModule = module {
    single<ApiClient>()
    single<UserRepository>()
    single<ProductRepository>()
}

// Domain layer
val domainModule = module {
    factory<GetUserUseCase>()
    factory<GetProductsUseCase>()
    factory<CreateOrderUseCase>()
}

// Shared module aggregates all
val sharedModule = module {
    includes(dataModule, domainModule)
}
```

### By Feature

```kotlin
// User feature
val userModule = module {
    single<UserRepository>()
    factory<GetUserUseCase>()
    factory<UpdateUserUseCase>()
}

// Product feature
val productModule = module {
    single<ProductRepository>()
    factory<GetProductsUseCase>()
    factory<SearchProductsUseCase>()
}

// Order feature
val orderModule = module {
    single<OrderRepository>()
    factory<CreateOrderUseCase>()
    factory<GetOrderHistoryUseCase>()
}
```

## Platform Extensions

Platform modules use Classic DSL with lambdas when custom construction logic is needed.

### Android Extension

```kotlin
// androidMain/kotlin/di/KoinAndroid.kt
fun initKoinAndroid(context: Context) {
    initKoin {
        androidContext(context)
        androidLogger()
        modules(androidModule)
    }
}

val androidModule = module {
    single<PlatformContext> { AndroidContext(get()) }
    single<FileStorage> { AndroidFileStorage(get()) }
    single<NetworkMonitor> { AndroidNetworkMonitor(get()) }
}
```

### iOS Extension

```kotlin
// iosMain/kotlin/di/KoinIos.kt
fun initKoinIos() {
    initKoin {
        modules(iosModule)
    }
}

val iosModule = module {
    single<PlatformContext> { IosContext() }
    single<FileStorage> { IosFileStorage() }
    single<NetworkMonitor> { IosNetworkMonitor() }
}
```

### Desktop Extension

```kotlin
// desktopMain/kotlin/di/KoinDesktop.kt
fun initKoinDesktop() {
    initKoin {
        printLogger()
        modules(desktopModule)
    }
}

val desktopModule = module {
    single<PlatformContext> { DesktopContext() }
    single<FileStorage> { DesktopFileStorage() }
}
```

## Expect/Actual Module Pattern

### Common Definition

```kotlin
// commonMain/kotlin/di/PlatformModule.kt
expect val platformModule: Module
```

### Platform Implementations

```kotlin
// androidMain
actual val platformModule = module {
    single<DatabaseDriver> { AndroidSqliteDriver(AppDatabase.Schema, get(), "app.db") }
    single<HttpEngine> { Android.create() }
    single<Settings> { AndroidSettings(get()) }
}

// iosMain
actual val platformModule = module {
    single<DatabaseDriver> { NativeSqliteDriver(AppDatabase.Schema, "app.db") }
    single<HttpEngine> { Darwin.create() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
}

// jsMain
actual val platformModule = module {
    single<DatabaseDriver> { JsSqliteDriver() }
    single<HttpEngine> { Js.create() }
    single<Settings> { LocalStorageSettings() }
}
```

## Compose Multiplatform

For multiplatform ViewModel, see [ViewModel](/docs/reference/koin-core/viewmodel).

### Shared ViewModel

```kotlin
// commonMain
@KoinViewModel
class UserViewModel(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<UserState>(UserState.Loading)
    val state = _state.asStateFlow()

    fun loadUser(id: String) {
        viewModelScope.launch {
            _state.value = UserState.Success(getUserUseCase(id))
        }
    }
}
```

### Shared UI

```kotlin
// commonMain
@Composable
fun UserScreen(
    viewModel: UserViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    when (val s = state) {
        is UserState.Loading -> LoadingIndicator()
        is UserState.Success -> UserContent(s.user)
        is UserState.Error -> ErrorMessage(s.message)
    }
}
```

## Testing

For KMP testing patterns, see [Testing](/docs/reference/koin-test/testing).

## Best Practices

1. **Single init function** - One `initKoin()` in commonMain
2. **Platform extensions via config** - Use `includes(config)` pattern
3. **Minimize platform modules** - Only truly platform-specific code
4. **Share ViewModel logic** - Business logic in commonMain
5. **Use expect/actual for factories** - Platform-specific instance creation
6. **Test in commonTest** - Most tests can be shared

## Next Steps

- **[KMP Setup](/docs/reference/koin-core/kmp-setup)** - Basic KMP configuration
- **[ViewModel](/docs/reference/koin-core/viewmodel)** - Multiplatform ViewModel
- **[Advanced Patterns](/docs/reference/koin-mp/kmp)** - Architecture patterns, testing, platform integration
- **[Koin for Compose](/docs/reference/koin-compose/compose)** - Compose Multiplatform
