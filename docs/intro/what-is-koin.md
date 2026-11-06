---
title: What is Koin?
description: What Koin is, how it works at runtime and at compile time, which platforms it supports, and what the project is made of.
---

Koin is a dependency injection framework for Kotlin. This page explains how it works, what it costs and what it gives you, and how the project is organized. If dependency injection is new to you, read [What is Dependency Injection?](/docs/intro/what-is-dependency-injection) first.

## How Koin works

You describe your classes in modules, start Koin once, and ask for instances where you need them:

```kotlin
class ApiService
class UserRepository(private val api: ApiService)

val appModule = module {
    single<ApiService>()
    single<UserRepository>()
}

fun main() {
    startKoin {
        modules(appModule)
    }
}
```

Two parts work together:

- **Runtime container**: `startKoin` loads your modules into a container. Each definition is a Kotlin function that builds one type. When something asks for a `UserRepository`, Koin runs that function, resolves its constructor parameters from the container, and keeps or discards the instance depending on the definition type (`single`, `factory`, `scoped`). Koin does not build objects by reflection.
- **Compile-time checks (optional)**: the [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin) is a Kotlin compiler plugin. It reads your constructors to generate the wiring, then checks at build time that every dependency of the assembled graph is declared. A missing definition becomes a build error instead of a crash.

[Core Concepts](/docs/intro/core-concepts) explains modules, definitions, scopes and injection with one running example.

## What the runtime model costs and gives

Because the graph lives in a runtime container, Koin can change it while the app runs:

- **Dynamic modules**: load and unload modules after start with `loadKoinModules()` and `unloadKoinModules()`, for feature flags or on-demand features. See [Starting Koin](/docs/reference/koin-core/starting-koin).
- **Lazy modules**: load part of the graph in the background to reduce startup work. See [Lazy Modules](/docs/reference/koin-core/lazy-modules).
- **Scopes created at runtime**: open and close scopes for a session, a screen or a request. See [Scopes](/docs/reference/koin-core/scopes).
- **No generated sources**: nothing to commit, nothing to configure per Kotlin Multiplatform target.

The same model has costs you should know:

- **Startup work**: modules are registered when Koin starts, and every lookup goes through the container. For most apps this is small, but eager definitions (`createdAtStart`) run on the thread that starts Koin.
- **Runtime errors without the plugin**: with the Constructor or Classic DSL and no plugin, a missing definition is only found when the code runs. You can catch it in a unit test with [`verify()`](/docs/reference/koin-test/verify), or at build time with the Compiler Plugin.
- **Limits of compile-time checks**: the plugin validates the graph assembled at a Koin entry point such as `startKoin`. Modules loaded later with `loadKoinModules()` can't be proven at build time, and a library module with no entry point is only checked when an app assembles it. See [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety).
- **Kotlin version coupling**: the Compiler Plugin depends on compiler internals, so each plugin release is verified on a list of Kotlin versions. See [Compiler Plugin Setup](/docs/setup/compiler-plugin).

## Constructor injection and service locator

Your classes receive their dependencies through the constructor. Koin only retrieves an instance where it cannot create the object itself, at an entry point:

```kotlin
// Koin creates these: dependencies come through the constructor
class UserRepository(private val api: UserApi)
class UserViewModel(private val repository: UserRepository) : ViewModel()

// Android creates the Activity: it retrieves its ViewModel from Koin
class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModel()
}
```

The dependency is visible in the signature, and you can test `UserRepository` and `UserViewModel` without Koin. Retrieval functions such as `by inject()`, `by viewModel()` or `koinInject()` belong to entry points: an `Activity`, a `Fragment`, a root Composable, `main()`, a Ktor route, a test. [What is Dependency Injection?](/docs/intro/what-is-dependency-injection) compares the two patterns, and [Retrieving Dependencies](/docs/reference/koin-core/injection) covers `get()` and `inject()`.

## Two styles, one container

| Style | Example | Needs |
|-------|---------|-------|
| Compiler Plugin DSL | `single<UserRepository>()` | Koin Compiler Plugin |
| Annotations | `@Singleton class UserRepository(...)` | Koin Compiler Plugin, `koin-annotations` |
| Constructor DSL | `singleOf(::UserRepository)` | Nothing extra (the plugin adds compile-time checks) |
| Classic DSL | `single { UserRepository(get()) }` | Nothing extra (the plugin adds compile-time checks) |

Annotations are one style; the Compiler Plugin, Constructor and Classic DSL are the three forms of the other, the Koin DSL. All produce the same definitions in the same container. Pick one style per project. [Choosing your style](/docs/intro/choosing-your-style) compares them and gives a default.

## Annotations status

The `koin-annotations` library (`@Singleton`, `@Factory`, `@KoinViewModel`, `@Module`, `@ComponentScan`) is part of the main Koin project: same repository, same version, same BOM. It is not deprecated. What is deprecated is `koin-ksp-compiler`, the KSP processor that used to read these annotations. The Koin Compiler Plugin replaces it, with the same annotations. See [Migrating from KSP to the Compiler Plugin](/docs/migration/from-ksp-to-compiler-plugin).

## Platforms

Koin is a Kotlin Multiplatform library. The core runs on every Kotlin target, and integration libraries add platform APIs:

| Platform | Main artifact | Start here |
|----------|---------------|------------|
| Kotlin/JVM | `koin-core` | [Kotlin tutorial](/docs/quickstart/kotlin) |
| Android | `koin-android` | [Start Koin on Android](/docs/reference/koin-android/start) |
| Jetpack Compose, Compose Multiplatform | `koin-compose`, `koin-compose-viewmodel` | [Koin for Compose](/docs/reference/koin-compose/compose) |
| Kotlin Multiplatform (iOS, desktop, web, native) | `koin-core` | [Kotlin Multiplatform Setup](/docs/reference/koin-core/kmp-setup) |
| Ktor server | `koin-ktor` | [Koin for Ktor](/docs/reference/koin-ktor/ktor) |

The full artifact list is in [Gradle Setup](/docs/setup/gradle).

## The Koin project

Koin is open source under the Apache 2.0 license. It is made of:

- **Koin libraries**: core, Android, Compose, Ktor, test and annotations, all released under one version. Source: [InsertKoinIO/koin](https://github.com/InsertKoinIO/koin).
- **Koin Compiler Plugin**: the Kotlin compiler plugin and its Gradle plugin, with its own version. Source: [InsertKoinIO/koin-compiler-plugin](https://github.com/InsertKoinIO/koin-compiler-plugin).
- **Koin IDE Plugin**: navigation and checks in Android Studio and IntelliJ IDEA. See [IDE Plugin](/docs/tooling/ide-plugin).
- **Samples**: runnable apps for each tutorial in [InsertKoinIO/koin-getting-started](https://github.com/InsertKoinIO/koin-getting-started).

If you know Hilt or Dagger, [Koin vs Hilt](/docs/intro/koin-vs-hilt) compares the two approaches.

## Related

- [Core Concepts](/docs/intro/core-concepts): the mental model, with one example built step by step.
- [Choosing your style](/docs/intro/choosing-your-style): which declaration style to use.
- [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin): what happens at build time.
- [Setup](/docs/setup): add Koin to your project.
- [Tutorials](/docs/quickstart/kotlin): build a first app.
