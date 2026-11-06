---
title: Troubleshooting
description: Find and fix Koin errors such as missing definitions, circular dependencies and closed scopes, with logging, compile-time checks and tests.
---

This page lists the errors Koin reports, what causes them and how to fix them. It also covers the tools to find a problem before it reaches users, and the patterns that lead to these errors. The messages are quoted as Koin prints them, so you can search the page for the text of your stack trace.

## Find problems early

Koin gives you three ways to see a configuration error, from the earliest to the latest.

- **Compile time**: with the [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin), a missing dependency or a dependency cycle fails the build. The error names the definition, the parameter and the module. See [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety) for every check and error code.
- **In the IDE**: the [Koin IDE plugin](/docs/tooling/ide-plugin) shows where each definition is declared, where it is used and what is missing, while you write code.
- **In tests**: without the Compiler Plugin, call `verify()` on your modules in a JVM unit test. It checks that each constructor parameter has a definition. See [Verify your configuration](/docs/reference/koin-test/verify).

```kotlin
class ModuleCheckTest {

    @Test
    fun checkKoinModules() {
        appModule.verify()
    }
}
```

## Logging

Koin logs nothing by default (`EmptyLogger`). Turn logging on to see each definition load and each instance resolution:

```kotlin
startKoin {
    printLogger(Level.DEBUG)
    modules(appModule)
}
```

| Level | What you see |
|-------|--------------|
| `Level.DEBUG` | Every resolution, with the scopes Koin searched. Use it to understand a failure |
| `Level.INFO` | Startup summary, such as `Started 42 definitions in 1.2 ms`. The default for `printLogger()` |
| `Level.WARNING` | Warnings, such as a definition that overrides another |
| `Level.ERROR` | Errors only |
| `Level.NONE` | Nothing |

On Android, use `androidLogger(Level.DEBUG)` to write to Logcat. See [Starting Koin](/docs/reference/koin-core/starting-koin) for the logger options.

`Level.DEBUG` slows down resolution. Keep it for debugging sessions, not for release builds.

## Runtime errors

### No definition found

```
NoDefinitionFoundException: No definition found for type 'com.example.UserRepository' on scope '['_root_']'.
Check or add definition for type 'com.example.UserRepository' in scope '_root_'.
```

Koin was asked for a type that no loaded module declares. Check, in this order:

- **Module not loaded**: the definition exists but its module is not in `modules(...)` or in an `includes(...)`.
- **Missing qualifier**: the definition has a qualifier and the request has none, or the reverse. The message then contains `and qualifier '...'`. See [Qualifiers](/docs/reference/koin-core/qualifiers).
- **Interface not bound**: the definition declares `UserRepositoryImpl` and the request asks for `UserRepository`. Bind the interface, see [Definitions](/docs/reference/koin-core/definitions#interface-binding).
- **Wrong scope**: the definition is `scoped` in a scope, and the request comes from another scope or from the root. The message lists the searched scopes. See [Scopes](/docs/reference/koin-core/scopes).

For `SavedStateHandle`, the message says instead that it is provided by the ViewModel `CreationExtras`. Resolve the ViewModel with `koinViewModel()` or `by viewModel()` and take `SavedStateHandle` as a constructor parameter. See [ViewModel](/docs/reference/koin-core/viewmodel).

### Could not create instance

```
InstanceCreationException: Could not create instance for '[Singleton: 'com.example.UserRepository']'
```

Koin found the definition, but creating the instance threw an exception. The real cause is the nested exception (`Caused by:`) in the stack trace. Often it is a `NoDefinitionFoundException` for one of the constructor parameters, or an exception thrown by the constructor itself.

### Already existing definition

```
DefinitionOverrideException: Already existing definition for [Singleton: 'com.example.ApiService'] at com.example.ApiService::_root_
```

Two modules declare the same type and qualifier, and overriding is off (`allowOverride(false)`). Remove the duplicate, give it a [qualifier](/docs/reference/koin-core/qualifiers), or mark the intended one with `.override()`. See [Definitions](/docs/reference/koin-core/definitions#overriding-a-definition).

### Scope errors

| Message | Exception | Cause |
|---------|-----------|-------|
| `Scope 'user_session' is closed` | `ClosedScopeException` | The scope was closed and something still asks it for an instance. Often a callback or a coroutine that outlives the screen |
| `No scope found for id 'user_session'` | `ScopeNotCreatedException` | `getScope(id)` was called before `createScope(id, ...)`. Use `getOrCreateScope` or create the scope first |
| `Scope with id 'user_session' is already created` | `ScopeAlreadyCreatedException` | `createScope` was called twice with the same id |
| `No value for scope 'user_session' in [Scoped: ...]` | `MissingScopeValueException` | A scoped value declared with `declare()` was not provided for this scope |
| `Activity should implement AndroidScopeComponent` | `IllegalStateException` | An Android scope API was used in an Activity that has no scope. See [Android Scopes](/docs/reference/koin-android/scope) |

See [Scopes](/docs/reference/koin-core/scopes) for the scope lifecycle.

### Injected parameters

| Message | Exception | Cause |
|---------|-----------|-------|
| `Can't get injected parameter #0 from DefinitionParameters[] for type 'kotlin.String'` | `NoParameterFoundException` | The definition reads a parameter that the caller did not pass with `parametersOf(...)` |
| `No value found for type 'kotlin.String'` | `DefinitionParameterException` | `params.get<T>()` found no parameter of that type |

See [Injected Parameters](/docs/reference/koin-core/injection-parameters).

### Start and properties

| Message | Exception | Cause |
|---------|-----------|-------|
| `KoinApplication has not been started` | `IllegalStateException` | Code used the global context (`KoinComponent`, `by inject()`) before `startKoin`. On Android, check that your `Application` class is declared in the manifest |
| `A Koin Application has already been started` | `KoinApplicationAlreadyStartedException` | `startKoin` was called twice. In tests, call `stopKoin()` after each test |
| `Can't resolve Context instance. Please use androidContext() function in your KoinApplication configuration.` | `MissingAndroidContextException` | A definition uses `androidContext()` and `startKoin` has no `androidContext(this)` |
| `Property 'api_url' not found` | `MissingPropertyException` | `getProperty("api_url")` with no such property and no default value |
| `No properties found for file '/koin.properties'` | `NoPropertyFileFoundException` | `fileProperties()` could not find the file |

### Compiler Plugin not applied

```
NotImplementedError: Error while using 'single<T>()'. The Koin Compiler plugin is missing. Please fix your setup to use 'io.insert-koin.compiler.plugin' Koin Gradle Plugin.
```

The code uses the Compiler Plugin DSL (`single<T>()`, `factory<T>()`, `startKoin<T>()`) but the plugin is not applied to this Gradle module. See [Compiler Plugin Setup](/docs/setup/compiler-plugin).

## Circular dependencies

A cycle happens when two classes need each other in their constructors:

```kotlin
class SessionManager(private val authService: AuthService)
class AuthService(private val sessionManager: SessionManager)
```

With the Compiler Plugin, the build fails:

```
[Koin][KOIN-D004] Circular dependency detected:
  com.example.AuthService → com.example.SessionManager → com.example.AuthService
  Break with Lazy<T> injection or refactor to remove the cycle.
```

With `verify()`, the test fails with `Circular injection between ... Fix your Koin configuration!`.

Without those checks, Koin does not detect the cycle. Each class resolves the other until the call stack overflows, and the app crashes with a `StackOverflowError`.

### Break the cycle with Lazy

Make one side lazy, so it is resolved on first use and not during construction:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class AuthService(private val sessionManager: Lazy<SessionManager>)

val authModule = module {
    single<SessionManager>()
    single<AuthService>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class SessionManager(private val authService: AuthService)

@Singleton
class AuthService(private val sessionManager: Lazy<SessionManager>)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
class AuthService(private val sessionManager: Lazy<SessionManager>)

val authModule = module {
    single { SessionManager(get()) }
    single { AuthService(inject()) }
}
```

</TabItem>
</Tabs>

`AuthService` must not read `sessionManager.value` in its constructor or `init` block, or the cycle comes back.

### Remove the cycle

A lazy parameter hides the problem but keeps the coupling. Most cycles mean that both classes share a responsibility. Move it to a third class that both depend on, or make one side depend on an interface that exposes only what it needs.

```kotlin
class TokenStore

class SessionManager(private val tokenStore: TokenStore)
class AuthService(private val tokenStore: TokenStore)
```

## Patterns that cause errors

**Resolving inside methods**: calling `get()` or `KoinComponent` inside a class body hides its dependencies. The Compiler Plugin cannot check them, and a missing definition fails only when the method runs. Prefer constructor parameters. See [KoinComponent](/docs/reference/koin-core/koin-component) for the cases where you need it.

```kotlin
// Hidden dependency, resolved late
class UserViewModel : ViewModel(), KoinComponent {
    fun loadUser() {
        val repository = get<UserRepository>()
    }
}

// Visible dependency, checked at build time
class UserViewModel(private val repository: UserRepository) : ViewModel()
```

**Creating dependencies yourself**: `private val api = ApiService()` inside a class bypasses Koin. The class can't be tested with a fake, and its dependencies are not checked.

**Qualifiers for different types**: a qualifier is only needed when several definitions provide the same type. `UserRepository` and `OrderRepository` are already different types.

**Work inside definitions**: a definition should create an object, not run logic. Side effects in a `single { }` lambda run at an unpredictable time (first use), and on the main thread for `createdAtStart`. Keep the work in the class, or move startup work to [Lazy Modules](/docs/reference/koin-core/lazy-modules).

**One module for the whole app**: a single large module is hard to override in tests and to load lazily. Split by feature or layer. See [Modules](/docs/reference/koin-core/modules).

**Scoped instances kept after close**: a reference to a scoped instance stored in a `single` or a static field keeps it alive after its scope closes, and later calls may hit `ClosedScopeException`. Inject scoped instances only into objects of the same scope or shorter.

:::tip
Some problems only show up on real devices: slow resolutions on startup, scopes that never close, crashes in rare paths. To see them in real user sessions, see [Production Monitoring](/docs/tooling/production-monitoring).
:::

## Related

- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): every build-time check and its error code.
- [Verify your configuration](/docs/reference/koin-test/verify): check modules in tests without the Compiler Plugin.
- [Starting Koin](/docs/reference/koin-core/starting-koin): logger and start options.
- [Scopes](/docs/reference/koin-core/scopes): scope lifecycle and closing.
- [IDE Plugin](/docs/tooling/ide-plugin): see your dependency graph while you code.
