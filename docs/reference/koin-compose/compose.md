---
title: Koin for Compose
description: Start Koin in a Jetpack Compose or Compose Multiplatform app and inject dependencies in Composables with koinInject.
---

This page covers the Compose packages, the two ways to start Koin in a Compose app, and `koinInject()` to retrieve dependencies in a Composable. It works the same on Jetpack Compose (Android) and Compose Multiplatform. You should know how to declare [definitions](/docs/reference/koin-core/definitions) and [modules](/docs/reference/koin-core/modules).

## Packages

| Package | Contains | Platforms |
|---------|----------|-----------|
| `io.insert-koin:koin-compose` | `KoinApplication`, `koinInject`, scope and module APIs | Multiplatform |
| `io.insert-koin:koin-compose-viewmodel` | `koinViewModel`, `koinActivityViewModel` (Android) | Multiplatform |
| `io.insert-koin:koin-compose-viewmodel-navigation` | `sharedKoinViewModel`, `KoinNavigationScope`, `navigationScope` | Multiplatform |
| `io.insert-koin:koin-compose-navigation3` | Navigation 3 entries | Multiplatform |
| `io.insert-koin:koin-androidx-compose` | `koin-compose` + `koin-compose-viewmodel`, plus `KoinActivityScope` and `KoinFragmentScope` | Android |
| `io.insert-koin:koin-androidx-compose-navigation` | Deprecated `koinNavViewModel` only | Android |

All Compose APIs live in the multiplatform packages. `koin-androidx-compose` is a convenience artifact for Android projects:

```kotlin
// Android app
dependencies {
    implementation("io.insert-koin:koin-androidx-compose:$koin_version")
}

// Compose Multiplatform, in commonMain
commonMain.dependencies {
    implementation("io.insert-koin:koin-compose:$koin_version")
    implementation("io.insert-koin:koin-compose-viewmodel:$koin_version")
}
```

For the BOM and version catalogs, see [Gradle Setup](/docs/setup/gradle).

## Starting Koin

You have two options. Pick one per app.

### Start outside Compose

Start Koin where your platform starts: the `Application` class on Android, the shared `initKoin()` function in a KMP project (see [KMP Setup](/docs/reference/koin-core/kmp-setup)). Compose then uses the running Koin instance with no extra setup:

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}

@Composable
fun App() {
    val repository = koinInject<UserRepository>()
}
```

Use this option when code outside Compose (a `Service`, a `WorkManager` worker, iOS Swift code) also needs Koin, because that code runs without any Composable.

### Start from a Composable with KoinApplication

`KoinApplication` starts Koin from the root Composable and provides it to all children:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    KoinApplication(
        configuration = koinConfiguration { modules(appModule) }
    ) {
        MainScreen()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// di/Koin.kt
import org.koin.core.annotation.KoinApplication

@KoinApplication
object KoinApp
```

The annotation and the Composable are both named `KoinApplication`, so keep them in separate files.

```kotlin
// App.kt
import org.koin.compose.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration

@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration<KoinApp>()) {
        MainScreen()
    }
}
```

`koinConfiguration<KoinApp>()` loads the modules of the `@KoinApplication` class. Pass a lambda to add options: `koinConfiguration<KoinApp> { printLogger() }`.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    KoinApplication(
        configuration = koinConfiguration { modules(appModule) }
    ) {
        MainScreen()
    }
}
```

</TabItem>
</Tabs>

What `KoinApplication` does for you:

- **Platform setup**: on Android it calls `androidContext()` with the application context and sets up `androidLogger()`. On other platforms it sets up `printLogger()`. Change the level with the `logLevel` parameter (default `Level.INFO`).
- **Existing instance**: if Koin is already started, `KoinApplication` attaches to the running instance and ignores its own configuration.
- **Lifecycle**: Koin is not stopped when `KoinApplication` leaves the composition, so it survives an Android configuration change or Activity recreation. It is stopped only if the composition is abandoned.

`koinConfiguration { }` builds a reusable [KoinConfiguration](/docs/reference/koin-core/starting-koin), so you can share the same configuration between `KoinApplication`, `startKoin` and tests.

### Deprecated entry points

| API | Status | Use instead |
|-----|--------|-------------|
| `KoinApplication(application = { ... })` | Deprecated | `KoinApplication(configuration = koinConfiguration { ... })` |
| `KoinMultiplatformApplication(config)` | Deprecated | `KoinApplication(configuration)` |
| `KoinContext { }` | Deprecated, not needed | Nothing. Compose finds the instance started with `startKoin` |
| `KoinAndroidContext { }` | Deprecated, not needed | Nothing |

## Injecting with koinInject

`koinInject()` resolves a dependency from the current Koin scope (the root scope, unless a [Compose scope](/docs/reference/koin-compose/compose-scopes) is active):

```kotlin
import org.koin.compose.koinInject

@Composable
fun UserScreen() {
    val repository = koinInject<UserRepository>()
}
```

Declare it as a default parameter. The Composable still works with Koin, and a test or a preview can pass its own instance:

```kotlin
@Composable
fun UserScreen(repository: UserRepository = koinInject()) {
    // ...
}
```

`koinInject()` wraps the resolution in `remember`, keyed on the qualifier, the scope and the parameters. A recomposition with the same keys returns the same object without resolving again.

### Qualifier and scope

```kotlin
@Composable
fun SyncScreen() {
    val localDb = koinInject<Database>(qualifier = named("local"))
    val cache = koinInject<SessionCache>(scope = sessionScope)
}
```

See [Qualifiers](/docs/reference/koin-core/qualifiers) for declaring named definitions.

### Parameters

Pass [injected parameters](/docs/reference/koin-core/injection-parameters) with a lambda, or with a `ParametersHolder`:

```kotlin
@Composable
fun UserDetail(userId: String) {
    // lambda form
    val presenter = koinInject<UserPresenter> { parametersOf(userId) }

    // holder form
    val presenter2 = koinInject<UserPresenter>(parametersHolder = parametersOf(userId))
}
```

With the lambda form, Koin invokes the lambda on every recomposition to compare the values. The holder form skips that step, which the Koin sources recommend for performance. Parameters are compared by value: when `userId` changes, `koinInject()` resolves a new instance.

## Accessing Koin directly

| Function | Returns |
|----------|---------|
| `getKoin()` | The `Koin` instance of the current composition |
| `currentKoinScope()` | The scope `koinInject()` uses by default |

Both are Composable functions in `org.koin.compose`. Prefer `koinInject()` in UI code.

## Previews

Compose previews do not run your `Application` class. Use `KoinApplicationPreview` to give a preview its own Koin instance: see [Compose Testing](/docs/reference/koin-compose/compose-testing).

## API summary

| API | Purpose | Page |
|-----|---------|------|
| `KoinApplication(configuration) { }` | Start Koin from Compose | This page |
| `koinInject<T>()` | Resolve any dependency | This page |
| `koinViewModel<T>()` | Resolve a ViewModel | [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel) |
| `KoinScope`, `KoinNavigationScope` | Scope bound to a Composable or a destination | [Scopes in Compose](/docs/reference/koin-compose/compose-scopes) |
| `rememberKoinModules { }` | Load modules with a Composable | [Dynamic Modules](/docs/reference/koin-compose/compose-modules) |
| `KoinIsolatedContext` | Use an isolated Koin instance | [Isolated Context](/docs/reference/koin-compose/isolated-context) |
| `navigation<T> { }`, `koinEntryProvider()` | Navigation 3 entries | [Navigation 3](/docs/reference/koin-compose/navigation3) |

## Related

- [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel): `koinViewModel()` and navigation-scoped ViewModels.
- [Lifecycle and State](/docs/reference/koin-compose/compose-lifecycle): how injected objects behave across recomposition.
- [KMP Setup](/docs/reference/koin-core/kmp-setup): start Koin in a Compose Multiplatform project.
- [Android Start](/docs/reference/koin-android/start): `androidContext()` and the Android `Application` class.
- [Compose Testing](/docs/reference/koin-compose/compose-testing): previews and UI tests.
