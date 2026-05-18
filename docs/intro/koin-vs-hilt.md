---
title: Koin vs Hilt/Dagger
---

# Koin vs Hilt/Dagger

This page compares Koin with Hilt and Dagger to help you understand the differences and decide which framework fits your needs.

:::info
Koin supports both **DSL and Annotations** - choose what fits your team. Both are first-class citizens, equally powerful, and powered by the same Compiler Plugin. This comparison shows annotation examples for fair comparison with Hilt, but Koin's DSL offers equivalent functionality with even less boilerplate.
:::

## Philosophy Differences

| Aspect | Koin | Hilt/Dagger |
|--------|------|-------------|
| **Learning curve** | Minutes to learn | Hours/days to master |
| **Code complexity** | Simple DSL or annotations | Complex annotation rules |
| **Debugging** | Clear errors, no generated code maze | Generated code can be hard to trace |
| **Setup** | One plugin, minimal config | Multiple annotations, strict rules |
| **Compile safety** | ✅ With Compiler Plugin | ✅ Always |
| **Runtime flexibility** | ✅ Dynamic features | ❌ Static only |

## Annotation Comparison

Even annotations are simpler in Koin:

| Task | Koin | Hilt |
|------|------|------|
| **Singleton** | `@Singleton class MyService` | `@Singleton class MyService @Inject constructor(...)` |
| **Interface binding** | Automatic | Requires `@Binds` in abstract module |
| **Component scanning** | `@ComponentScan("package")` | Not available |
| **Module discovery** | `@Configuration` - auto-discovered | Manual `@InstallIn` per module |
| **Provide 3rd party** | `@Singleton fun provide()` | `@Provides` in `@Module` + `@InstallIn` |
| **ViewModel** | `@KoinViewModel class MyVM` | `@HiltViewModel class MyVM @Inject constructor` |

## Code Comparison

### Simple Singleton

**Koin:**
```kotlin
@Singleton
class MyRepository(val api: ApiService)

@Module
@ComponentScan("com.app")
class AppModule
```

**Hilt:**
```kotlin
@Singleton
class MyRepository @Inject constructor(val api: ApiService)

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindRepository(impl: MyRepository): Repository
}
```

### Interface Binding

**Koin - Automatic:**
```kotlin
@Singleton
class UserRepositoryImpl(val db: Database) : UserRepository

// That's it! Koin automatically binds to UserRepository interface
```

**Hilt - Requires explicit binding:**
```kotlin
@Singleton
class UserRepositoryImpl @Inject constructor(val db: Database) : UserRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
```

### Multi-Module Apps

**Koin - Modules auto-discovered:**
```kotlin
// feature/auth/AuthModule.kt
@Module
@ComponentScan
@Configuration  // Auto-discovered!
class AuthModule

// feature/profile/ProfileModule.kt
@Module
@ComponentScan
@Configuration  // Auto-discovered!
class ProfileModule

// app/MyApp.kt
@KoinApplication  // No need to list modules
class MyApp
```

**Hilt - Must manually install each module:**
```kotlin
// feature/auth/AuthModule.kt
@Module
@InstallIn(SingletonComponent::class)
class AuthModule { ... }

// feature/profile/ProfileModule.kt
@Module
@InstallIn(SingletonComponent::class)
class ProfileModule { ... }

// app/MyApp.kt
@HiltAndroidApp
class MyApp  // Still need correct @InstallIn everywhere
```

### ViewModel

**Koin:**
```kotlin
@KoinViewModel
class UserViewModel(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()

// In Activity/Fragment
val viewModel: UserViewModel by viewModel()

// In Compose
val viewModel: UserViewModel = koinViewModel()
```

**Hilt:**
```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel()

// In Activity/Fragment
val viewModel: UserViewModel by viewModels()

// In Compose
val viewModel: UserViewModel = hiltViewModel()
```

### Providing Third-Party Libraries

**Koin:**
```kotlin
@Module
class NetworkModule {
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.example.com")
        .build()

    @Singleton
    fun provideApi(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
```

**Hilt:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.example.com")
        .build()

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
```

## Dynamic Features: Koin's Unique Advantage

Koin is **runtime-based but performant and compile-safe**. This enables dynamic features that Hilt cannot offer:

| Dynamic Feature | Koin | Hilt |
|-----------------|------|------|
| Load modules at runtime | ✅ `loadKoinModules()` | ❌ Not possible |
| Unload modules | ✅ `unloadKoinModules()` | ❌ Not possible |
| Lazy background loading | ✅ `lazyModules()` | ❌ Not possible |
| Feature flag injection | ✅ Easy | ⚠️ Complex workarounds |
| Plugin architectures | ✅ Natural fit | ❌ Very difficult |
| A/B test implementations | ✅ Runtime swap | ⚠️ Compile-time only |
| Dynamic configuration | ✅ Supported | ❌ No, Must recompile |

### Example: Dynamic Module Loading

```kotlin
// KOIN - Dynamic module loading
if (userHasPremium) {
    loadKoinModules(premiumFeatureModule)
}

// Later, if subscription expires
unloadKoinModules(premiumFeatureModule)

// Lazy loading for faster startup
startKoin {
    modules(coreModule)
    lazyModules(
        analyticsModule,  // Loaded in background
        heavyFeatureModule
    )
}
```

**This is impossible with Hilt** - all dependencies are wired at compile time.

### Example: Feature Flags

```kotlin
// KOIN - Switch implementations at runtime
val featureModule = module {
    if (FeatureFlags.useNewApi) {
        single<ApiService> { NewApiService() }
    } else {
        single<ApiService> { LegacyApiService() }
    }
}

// Or dynamically
fun updateApiImplementation(useNew: Boolean) {
    unloadKoinModules(apiModule)
    loadKoinModules(if (useNew) newApiModule else legacyApiModule)
}
```

## Setup Comparison

### Koin Setup

See the **[Compiler Plugin Setup Guide](/docs/setup/compiler-plugin)** for detailed instructions.

### Hilt Setup

```kotlin
// settings.gradle.kts
plugins {
    id("com.google.dagger.hilt.android") version "2.x" apply false
}

// app/build.gradle.kts
plugins {
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.x")
    ksp("com.google.dagger:hilt-compiler:2.x")
}
```

## Error Messages

### Koin

```
org.koin.core.error.NoBeanDefFoundException:
No definition found for class 'com.app.UserRepository'.
Check your module definitions.
```

Clear, points directly to the issue.

### Hilt/Dagger

```
error: [Dagger/MissingBinding] com.app.UserRepository cannot be provided
without an @Inject constructor or an @Provides-annotated method.
com.app.UserRepository is injected at
    com.app.UserService(repository)
com.app.UserService is injected at
    com.app.UserActivity.service
com.app.UserActivity is injected at
    dagger.hilt.android.internal.managers.ActivityComponentManager.inject
```

Longer, requires understanding the component graph.

## When to Choose Each

### Choose Koin When:

- You value **productivity and simplicity**
- You need **runtime flexibility** (dynamic modules, feature flags)
- You're building **Kotlin Multiplatform** apps
- Your team wants to **learn quickly**
- You prefer **less boilerplate**
- You want **easier debugging**

### Choose Hilt When:

- Your team **already knows Dagger**
- You need **Google-first ecosystem** compatibility
- You require **Dagger's specific features**

## Migration from Hilt to Koin

If you're considering migration:

### Concept Mapping

| Hilt | Koin |
|------|------|
| `@HiltAndroidApp` | `@KoinApplication` and `startKoin<T> { }` |
| `@AndroidEntryPoint` | `by inject()` |
| `@HiltViewModel` with `by viewModels()` | `@KoinViewModel` with `by viewModel()` |
| `@Inject constructor` | Just constructor (auto-detected) |
| `@Binds` | Automatic or `bind` |
| `@InstallIn(SingletonComponent)` | `@Configuration` |
| `@Provides` on a function | `@Factory` on a function |

### Gradual Migration

You can migrate incrementally:

1. Add Koin to your project
2. Migrate one feature module at a time
3. Both DI frameworks can coexist during transition (Koin can scan target packages with `@ComponentScan`)
4. Remove Hilt when migration is complete

See [Migrating from Hilt](/docs/migration/from-hilt) for detailed steps.

## Summary

**Koin: Simple AND Powerful**

- **Compile-time safety** like Hilt (with Compiler Plugin)
- **DSL or Annotations** - both equally powerful, your choice
- **Simplicity and productivity** that Hilt can't match
- **Dynamic runtime features** that Hilt can't do

You don't have to choose between safety and simplicity. With Koin, you get both.

## Next Steps

- **[What is Koin?](/docs/intro/what-is-koin)** - Learn more about Koin
- **[Setup Guide](/docs/setup/gradle)** - Add Koin to your project
- **[Migrating from Hilt](/docs/migration/from-hilt)** - Step-by-step migration guide
