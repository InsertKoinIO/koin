---
title: Verifying your Configuration
description: Check in a JVM test that every definition's dependencies are declared with verify(), and know when the Compiler Plugin makes it unnecessary.
---

This page covers `verify()`, the test API that checks your Koin configuration without running it, and the deprecated `checkModules()`. It also explains how both relate to the compile-time checks of the Koin Compiler Plugin, so you know whether you still need them. You should know how to write a [unit test with Koin](/docs/reference/koin-test/testing).

## Do you still need verify()?

The [Koin Compiler Plugin](/docs/reference/koin-compiler/compile-safety) checks the full dependency graph at build time: missing definitions, qualifier mismatches, scope errors and `get()` / `inject()` call sites. When it covers your project, a `verify()` test adds nothing and you can delete it.

You still need `verify()` when:

- No Compiler Plugin: your project uses the Constructor or Classic DSL without the plugin. (The deprecated KSP processor for annotations has its own `KOIN_CONFIG_CHECK` option.)
- Graph not known at build time: the plugin can't resolve module lists built with `if` / `when` or varargs. It reports `KOIN-W003` and skips the check for that entry point.
- Library modules: a library or feature module without its own `startKoin` / `@KoinApplication` gets no missing-dependency check until an app assembles it. A `verify()` test in the library checks it on its own.
- Lazy modules: you load part of the graph with [lazy modules](/docs/reference/koin-core/lazy-modules) and want one test over the full configuration (see [Modules and lazy modules together](#modules-and-lazy-modules-together)).

## verify()

`verify()` is an extension on `Module`, available on the JVM in `koin-test`. It reads the constructor of each definition's class and checks that every parameter type has a definition in the module or in the modules it includes. It runs no definition and needs no mock.

```kotlin
val appModule = module {
    includes(dataModule, networkModule)
    viewModel<UserViewModel>()
}
```

```kotlin
import org.koin.test.verify.verify

class AppModuleCheck {

    @Test
    fun checkKoinModule() {
        appModule.verify()
    }
}
```

On a missing dependency, `verify()` throws `MissingKoinDefinitionException` and prints a suggested fix. On a dependency cycle between two constructors, it throws `CircularInjectionException`.

To check several modules that are not linked with `includes()`, use `listOf(dataModule, uiModule).verifyAll()`.

### What verify() accepts

A constructor parameter passes the check when it is:

- Declared: a definition for its type exists in the module tree.
- Optional: the parameter has a default value. `verify()` prints a warning if no definition exists.
- Wrapped: a `Lazy<T>` or `List<T>` whose `T` is declared.
- Whitelisted: its type is in `extraTypes`, or is a basic type (`String`, `Int`, `Long`, `Double`).
- Injected or provided: the parameter is annotated with `@InjectedParam` or `@Provided`, or declared with `injections`.

`verify()` reads constructors only. It does not check qualifiers, properties or `get()` calls written inside a definition lambda.

### Types provided at runtime

Some types are never declared in a module: framework objects, or values created by the platform. Add them to `extraTypes`:

```kotlin
appModule.verify(
    extraTypes = listOf(HttpClientEngine::class, Clock::class)
)
```

On Android, use `androidVerify()` from `koin-android-test`. It adds `Context`, `Activity`, `Fragment`, `Application`, `SavedStateHandle` and `WorkerParameters` for you:

```kotlin
import org.koin.android.test.verify.androidVerify

@Test
fun checkKoinModule() {
    appModule.androidVerify()
}
```

### Injected parameters

A parameter passed with `parametersOf()` has no definition, so `verify()` reports it as missing. Declare it with `injections`:

```kotlin
class UserPresenter(val userId: String, val repository: UserRepository)

val presenterModule = module {
    factory { (userId: String) -> UserPresenter(userId, get()) }
}

@Test
fun checkKoinModule() {
    presenterModule.verify(
        injections = injectedParameters(
            definition<UserPresenter>(String::class)
        )
    )
}
```

Or annotate the constructor parameter. `@InjectedParam` marks a value passed with `parametersOf()`, `@Provided` marks a type supplied outside Koin's modules:

```kotlin
class UserPresenter(@InjectedParam val userId: String, val repository: UserRepository)

class SyncWorker(@Provided val params: WorkerParameters, val repository: UserRepository)
```

Both annotations come from `koin-core-annotations` and are the same ones the Compiler Plugin reads (see [Injected Parameters](/docs/reference/koin-core/injection-parameters)).

### Modules and lazy modules together

`koin-test-coroutines` adds `verify()` on a `ModuleConfiguration`, which checks regular and [lazy modules](/docs/reference/koin-core/lazy-modules) as one graph:

```kotlin
val configuration = moduleConfiguration {
    modules(coreModule)
    lazyModules(featureModule)
}

@Test
fun checkConfiguration() {
    configuration.verify()
}
```

`moduleConfiguration` and this `verify()` are experimental (`@KoinExperimentalAPI`).

## checkModules() (deprecated)

:::warning
`checkModules()`, `checkKoinModules()` and their DSL are deprecated. Migrate to `verify()`, or to the Compiler Plugin.
:::

Unlike `verify()`, `checkModules()` starts the modules and creates every definition, so it needs values for everything resolved at runtime. You may find it in older projects:

```kotlin
@Test
fun checkAllModules() {
    koinApplication {
        modules(appModule)
        checkModules {
            withInstance<Context>()
            withParameter<UserPresenter> { "user-1" }
            withProperty("api_url", "http://localhost")
        }
    }
}

// or
@Test
fun checkAllModules() {
    checkKoinModules(listOf(appModule))
}
```

The check DSL:

- `withInstance(value)`: add `value` to the graph. `withInstance<T>()` adds a mock of `T` and needs a `MockProviderRule` (see [Mocking with declareMock](/docs/reference/koin-test/testing#mocking-with-declaremock)).
- `withParameter<T> { value }`: the injected parameter used to create `T`. `withParameters<T> { parametersOf(a, b) }` for several.
- `withProperty(key, value)`: add a Koin property.
- `withScopeLink(scope, targetScope)`: [link](/docs/reference/koin-core/scopes) two scopes for the check.

To migrate to `verify()`: remove `withInstance` for framework types (use `extraTypes` or `androidVerify()`), and replace `withParameter` with `injections` or `@InjectedParam`.

## Related

- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): what the Compiler Plugin checks at build time.
- [Unit Testing](/docs/reference/koin-test/testing): start Koin in tests, override and mock definitions.
- [Injected Parameters](/docs/reference/koin-core/injection-parameters): `parametersOf` and `@InjectedParam`.
- [Lazy Modules](/docs/reference/koin-core/lazy-modules): load part of the graph in the background.
- [Troubleshooting](/docs/reference/koin-core/troubleshooting): runtime errors such as `NoDefinitionFoundException`.
