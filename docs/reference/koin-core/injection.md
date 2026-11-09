---
title: Injection
description: Retrieve instances outside of constructors with get() and by inject(), lazily or eagerly, optionally nullable, and get all instances of a type.
---

Most of your classes receive their dependencies through their constructor, and Koin fills them (see [Constructor parameters](/docs/reference/koin-core/definitions#constructor-parameters)). Some code can't: an Android `Activity`, a `main()` function, a class created by a framework. This page covers the functions that retrieve instances from Koin in that code: `get()`, `inject()`, their nullable variants, and `getAll()`.

## Constructor injection first

Use the functions on this page only where Koin can't create the class:

```kotlin
// Koin creates this class: dependencies come through the constructor
class UserService(
    private val repository: UserRepository,
    private val validator: UserValidator,
)

// Android creates this class: retrieve dependencies with inject()
class UserActivity : AppCompatActivity() {
    private val userService: UserService by inject()
}
```

A class with constructor parameters can be tested by passing fakes, with no Koin container. A class that calls `get()` needs a running container in its tests. Never call `get()` in a default parameter value (`class UserService(val repository: UserRepository = get())`): declare the class in a module instead.

## Lazy and eager retrieval

Retrieval code is the same in every style:

```kotlin
class UserActivity : AppCompatActivity() {

    // lazy: resolved on first access of the property
    private val userService: UserService by inject()

    // eager: resolved now, when the property is initialized
    private val analytics: Analytics = get()
}
```

| Function | Returns | Resolved |
|----------|---------|----------|
| `by inject<T>()` | `Lazy<T>` used as a property delegate | On first access |
| `get<T>()` | `T` | Immediately |

Use `by inject()` for properties. The class can be created before Koin has everything ready (an `Activity` field is initialized before `onCreate()`), and an instance that is never used is never created. Use `get()` inside a function body, when you need the value now.

`inject()` takes a `mode` parameter, a Kotlin `LazyThreadSafetyMode`. The default is `SYNCHRONIZED` on the JVM.

## Where get() and inject() are available

You can call `get()` and `by inject()` from:

- Android classes: `Activity`, `Fragment`, `Service` and other `ComponentCallbacks`, with no extra interface. See [Android Entry Points](/docs/reference/koin-android/entry-points).
- Any class that implements `KoinComponent`. See [KoinComponent](/docs/reference/koin-core/koin-component).
- A `Koin` instance: `KoinPlatform.getKoin().get<UserService>()` uses the global container, `koinApp.koin.get<UserService>()` an isolated one.
- Inside a definition (Classic DSL): `get()`, `inject()`, `getOrNull()`, `injectOrNull()` and `getAll()` resolve from the current scope.

Platform APIs follow the same rules with their own names:

- Compose: `koinInject()` and `koinViewModel()`. See [Koin for Compose](/docs/reference/koin-compose/compose).
- Android ViewModel: `by viewModel()` and `by activityViewModel()`. See [Android ViewModel](/docs/reference/koin-android/viewmodel).
- Ktor: `by inject()` in `Application`, `Route` and `ApplicationCall`. See [Koin for Ktor](/docs/reference/koin-ktor/ktor).

## Optional dependencies

When a definition may be missing, use the nullable variants. They return `null` instead of throwing `NoDefinitionFoundException`:

```kotlin
val koin = KoinPlatform.getKoin()

val tracker: Tracker? = koin.getOrNull()
val crashReporter: CrashReporter? by koin.injectOrNull()
```

`getOrNull()` and `injectOrNull()` exist on `Koin`, on `Scope` and inside definitions. The `KoinComponent` interface provides `get()` and `inject()` only: call `getKoin().getOrNull<T>()` there.

For a constructor parameter, declare it nullable (`tracker: Tracker?`) and Koin passes `null` when no definition exists. See [Constructor parameters](/docs/reference/koin-core/definitions#constructor-parameters).

If Koin itself may not be started, for example in a library, `KoinPlatform.getKoinOrNull()` returns `null` instead of throwing.

## All instances of a type

`getAll<T>()` returns every instance bound to the type `T`, across all definitions:

```kotlin
val koin = KoinPlatform.getKoin()

val interceptors: List<Interceptor> = koin.getAll()
```

The list contains each definition whose main type or bound type is `T`, with or without qualifier. It is empty when there is none. For a constructor parameter, a `List<Interceptor>` parameter does the same. Collection patterns are covered in [Advanced Patterns](/docs/reference/koin-core/advanced-patterns).

## Qualifiers and parameters at the call site

Every retrieval function takes a qualifier and a parameter block:

```kotlin
val localDb: Database by inject(named("local"))

val presenter: UserPresenter = get { parametersOf(userId) }

val detail: DetailPresenter = get(named("compact")) { parametersOf(itemId) }
```

- Qualifier: picks one definition among several of the same type. See [Qualifiers](/docs/reference/koin-core/qualifiers).
- Parameters: values only the caller knows, passed to the definition. See [Injected Parameters](/docs/reference/koin-core/injection-parameters).

With `by inject()`, the parameter block runs on first access, so it can read properties that are set after construction.

## When resolution fails

`get()` throws `NoDefinitionFoundException` when no definition matches the type and qualifier. With `by inject()`, the exception comes on first access of the property, not where you declared it. With the Compiler Plugin, `get()` and `inject()` calls are also checked at build time against the graph of your start call.

For common errors and how to read Koin's messages, see [Troubleshooting](/docs/reference/koin-core/troubleshooting).

## Related

- [Definitions](/docs/reference/koin-core/definitions): constructor injection and how parameters are resolved.
- [KoinComponent](/docs/reference/koin-core/koin-component): give any class access to `get()` and `inject()`.
- [Android Entry Points](/docs/reference/koin-android/entry-points): inject in Activity, Fragment, Service, Receiver and View.
- [Koin for Compose](/docs/reference/koin-compose/compose): `koinInject()` and other Compose APIs.
- [Scopes](/docs/reference/koin-core/scopes): retrieve instances from a scope instead of the root container.
