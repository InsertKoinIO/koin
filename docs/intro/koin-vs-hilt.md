---
title: Koin vs Hilt
description: A factual comparison of Koin and Hilt/Dagger. How each builds the graph, how the code looks, where each one is stronger, and how concepts map.
---

This page compares Koin with Hilt (and Dagger, which Hilt is built on) for Android developers who know one and want to understand the other. It covers how each framework builds the dependency graph, what the code looks like, and where each one has the advantage. To move a project from Hilt to Koin, see [Migrating from Hilt](/docs/reference/koin-android/hilt-migration).

Koin samples below use annotations, because they are closest to Hilt. The same definitions can be written with the Koin DSL (see [Choosing your style](/docs/intro/choosing-your-style)).

## How each one builds the graph

**Hilt** generates the dependency graph as Java source code at build time, with annotation processing (KSP or kapt). Every binding, component and scope is known when the app compiles. At runtime, the app calls generated factories directly, with no lookup.

**Koin** keeps the graph in a runtime container. Definitions are registered when Koin starts, and instances are resolved by type when requested. With the [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin), the graph assembled at `startKoin` is also checked at build time, and the plugin generates the constructor wiring. Without the plugin, a missing definition is found at runtime or in a [verification test](/docs/reference/koin-test/verify).

This difference explains most of the tradeoffs below.

## At a glance

| Aspect | Koin | Hilt |
|--------|------|------|
| Graph built | At runtime, checked at build time with the Compiler Plugin | At build time, generated code |
| Missing binding detected | Build time with the Compiler Plugin, otherwise at runtime or in a test | Build time, always |
| Build tooling | Kotlin compiler plugin, optional with the Constructor or Classic DSL | KSP or kapt, Hilt Gradle plugin |
| Platforms | Kotlin Multiplatform: Android, iOS, desktop, web, JVM server | Android (Dagger alone runs on the JVM) |
| Languages | Kotlin | Java and Kotlin |
| Load or unload modules at runtime | Yes (`loadKoinModules`, `unloadKoinModules`, lazy modules) | No |
| Component hierarchy | Root container plus scopes you declare, with Android scope archetypes | Fixed hierarchy of standard components (`SingletonComponent`, `ActivityComponent`, ...) |
| Android entry points | `by inject()`, `by viewModel()` in any Activity or Fragment | `@AndroidEntryPoint` on each class |
| Jetpack integration | ViewModel, WorkManager, Navigation, Compose, through Koin libraries | ViewModel, WorkManager, Navigation, Compose, through AndroidX Hilt libraries |

## Where Hilt is stronger

- **Complete graph at build time**: Hilt generates the whole graph, so every binding is verified on every build, for every module and scope. The Koin Compiler Plugin validates the graph at entry points, but modules loaded at runtime and library modules alone are outside its view.
- **No runtime lookup**: generated factories are called directly. Koin resolves each request through its container, which costs a lookup per resolution and registration work at startup.
- **Java support**: Dagger and Hilt work in Java codebases. Koin's API is designed for Kotlin.
- **Ecosystem and guidance**: Hilt is the DI library documented in the official Android guides, and many AndroidX libraries and samples ship Hilt integration first.
- **Detailed errors**: a Dagger error shows the full path from the entry point to the missing binding.

## Where Koin is stronger

- **Kotlin Multiplatform**: the same modules run on Android, iOS, desktop, web and Ktor servers. Hilt is Android only.
- **Runtime flexibility**: modules can be loaded, unloaded or loaded in the background after start, which fits feature flags, dynamic features and plugin architectures.
- **Less setup**: no annotation processor, no generated sources, no `@AndroidEntryPoint`. With the Constructor or Classic DSL, the build plugin is optional.
- **Less declaration code**: constructors need no `@Inject`, and interfaces are bound to their implementation without a separate `@Binds` module.
- **Your choice of style**: DSL or annotations, which can be mixed.

## Code side by side

### A class with dependencies

Koin:

```kotlin
@Singleton
class UserRepositoryImpl(private val api: ApiService) : UserRepository
```

Koin binds `UserRepositoryImpl` to `UserRepository` because it implements it.

Hilt:

```kotlin
@Singleton
class UserRepositoryImpl @Inject constructor(private val api: ApiService) : UserRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
```

### Third-party classes

Koin:

```kotlin
@Module
class NetworkModule {
    @Singleton
    fun retrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.example.com")
        .build()

    @Singleton
    fun apiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
```

Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun retrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.example.com")
        .build()

    @Provides
    @Singleton
    fun apiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
```

The two are close. Koin needs no `@InstallIn` because the module is not tied to a component, and no `@Provides` because `@Singleton` on a function already declares a definition.

### ViewModel

Koin:

```kotlin
@KoinViewModel
class UserViewModel(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()

// Activity or Fragment
val viewModel: UserViewModel by viewModel()

// Compose
val viewModel: UserViewModel = koinViewModel()
```

Hilt:

```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()

// Activity or Fragment annotated with @AndroidEntryPoint
val viewModel: UserViewModel by viewModels()

// Compose
val viewModel: UserViewModel = hiltViewModel()
```

### Several Gradle modules

Koin:

```kotlin
// feature-auth
@Module
@ComponentScan
@Configuration
class AuthModule

// app
@KoinApplication
class MyApp
```

Hilt:

```kotlin
// feature-auth
@Module
@InstallIn(SingletonComponent::class)
class AuthModule { ... }

// app
@HiltAndroidApp
class MyApp : Application()
```

Both discover feature modules on the classpath: Koin through `@Configuration`, Hilt through `@InstallIn`. In Koin, `@ComponentScan` also collects every annotated class of the package, so a feature module often needs no provider functions.

## Concept mapping

| Hilt | Koin |
|------|------|
| `@HiltAndroidApp` | `@KoinApplication` with `startKoin<MyApp>()`, or `startKoin { }` |
| `@AndroidEntryPoint` | Not needed: `by inject()` and `by viewModel()` work in any Activity or Fragment |
| `@Inject constructor` | Not needed: the constructor is read by the Compiler Plugin |
| `@Binds` | Automatic for annotations, `bind` in the DSL |
| `@Provides` | `@Singleton` or `@Factory` on a function, or a DSL definition |
| `@InstallIn(SingletonComponent::class)` | `@Configuration` |
| `@HiltViewModel` | `@KoinViewModel` |
| `@Named` | `@Named` |
| `@ActivityScoped`, `@ViewModelScoped` | `@ActivityScope`, `@ViewModelScope` |
| `@AssistedInject` | `@InjectedParam` |

The migration guide has the full mapping, a step-by-step process, and how to run both frameworks side by side during the move: see [Migrating from Hilt](/docs/reference/koin-android/hilt-migration).

## Related

- [Migrating from Hilt](/docs/reference/koin-android/hilt-migration): move a project step by step.
- [JSR-330 Compatibility](/docs/reference/koin-android/jsr330): keep `javax.inject` annotations.
- [What is Koin?](/docs/intro/what-is-koin): Koin's runtime and compile-time model.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): what Koin checks at build time.
- [Choosing your style](/docs/intro/choosing-your-style): DSL or annotations.
