---
title: Scopes
description: Bind instances to a lifecycle shorter than the app, such as a user session or a screen, and release them when the scope closes.
---

A scope is a container for instances that live longer than a `factory` but shorter than a `single`. This page covers declaring scopes and scoped definitions, creating and closing scope instances, linking scopes, and scope archetypes. It assumes you know [definitions](/docs/reference/koin-core/definitions).

Platform APIs build on this page: [Android Scopes](/docs/reference/koin-android/scope) (`ScopeActivity`, `AndroidScopeComponent`, `activityScope()`), [Scopes in Compose](/docs/reference/koin-compose/compose-scopes) and [Ktor Request Scopes](/docs/reference/koin-ktor/ktor-scopes).

## How scopes work

A scope has two parts:

- **Scope declaration**: a section in a module, identified by a qualifier (a type or a name). It lists the definitions that belong to the scope.
- **Scope instance**: created at runtime from a declaration, with a unique scope id. Each scope instance holds its own copy of every `scoped` definition, until you close it.

| Definition | Instance |
|------------|----------|
| `single` | One for the whole container |
| `factory` | A new one on each request |
| `scoped` | One per scope instance, released when the scope instance closes |

Use a scope when several objects must share the same instance for a bounded time (a login session, a checkout flow, a screen), and that instance must be dropped afterwards.

## Declaring a scope

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val sessionModule = module {
    // scope identified by a type
    scope<UserSession> {
        scoped<SessionData>()
        scoped<CartRepository>()
        factory<CheckoutPresenter>()
    }

    // scope identified by a name
    scope(named("checkout")) {
        scoped<CheckoutNavigator>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

Tag the class with `@Scope` to say which scope it belongs to, and with `@Scoped` to declare it as a scoped definition. You need both:

```kotlin
@Scope(UserSession::class)
@Scoped
class SessionData

@Scope(UserSession::class)
@Scoped
class CartRepository(private val sessionData: SessionData)

// scope identified by a name
@Scope(name = "checkout")
@Scoped
class CheckoutNavigator
```

`@Scope(UserSession::class)` is the same as `scope<UserSession> { }`, and `@Scope(name = "checkout")` the same as `scope(named("checkout")) { }`. Both annotations also work on functions in a `@Module` class.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.dsl.module
import org.koin.core.module.dsl.*

val sessionModule = module {
    scope<UserSession> {
        scopedOf(::SessionData)
        scopedOf(::CartRepository)
        factoryOf(::CheckoutPresenter)
    }

    scope(named("checkout")) {
        scoped { CheckoutNavigator() }
    }
}
```

</TabItem>
</Tabs>

A `factory` inside a scope section creates a new instance on each request, but can depend on the scoped instances of that scope.

Use a type as scope qualifier when the scope is tied to one class (`UserSession`, `MainActivity`). Use a name when no class fits. `scope(named<MyQualifier>())` takes a type qualifier.

## Creating and closing a scope

Create a scope instance from the `Koin` instance, with an id and the scope qualifier. This happens where the lifecycle starts and ends, typically at an entry point (login and logout screens, a request handler):

```kotlin
val koin = KoinPlatform.getKoin()

// on login
val scope = koin.createScope<UserSession>(userId)
val cart: CartRepository = scope.get()

// on logout
koin.getScopeOrNull(userId)?.close()
```

The scoped classes themselves (`CartRepository`, `SessionData`) use constructor injection like any other definition.

| Function | Behavior |
|----------|----------|
| `createScope<T>(scopeId)` | Creates a scope instance for the type qualifier `T` |
| `createScope(scopeId, qualifier)` | Creates a scope instance for any qualifier, for example `named("checkout")` |
| `getOrCreateScope(scopeId, qualifier)` | Returns the open scope with this id, or creates it |
| `getScope(scopeId)` | Returns the open scope. Throws `ScopeNotCreatedException` ("No scope found for id '...'") if there is none |
| `getScopeOrNull(scopeId)` | Returns the open scope, or `null` |
| `deleteScope(scopeId)` | Removes the scope instance from the registry |

The scope id must be unique among open scopes. Two scope instances of the same declaration hold different instances:

```kotlin
val first = getKoin().createScope<UserSession>("user_1")
val second = getKoin().createScope<UserSession>("user_2")

first.get<SessionData>() !== second.get<SessionData>() // true
```

When you call `scope.close()`:

1. Koin calls the `onClose` callback of each scoped instance, then releases them.
2. The scope is removed from the registry.
3. Any later `get()` on it throws `ClosedScopeException` ("Scope '...' is closed"). Check `scope.closed` if you are not sure.

```kotlin
val sessionModule = module {
    scope<UserSession> {
        scoped<SessionData>() onClose { it?.clear() }
    }
}
```

To run code when the scope itself closes, register a `ScopeCallback` with `scope.registerCallback()`.

:::warning
A scope you create by hand stays open until you close it. Every instance it holds stays in memory. Close it at the end of its lifecycle (logout, end of flow). On Android, `activityScope()` and `fragmentScope()` close the scope for you: see [Android Scopes](/docs/reference/koin-android/scope).
:::

## Scope components

A class that owns a scope lifecycle can implement `KoinScopeComponent`. Its `get()` and `inject()` calls then resolve from its scope. This is an advanced pattern for the scope owner only: the definitions inside the scope still receive their dependencies through the constructor.

```kotlin
class UserSession : KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }

    // resolved from the UserSession scope
    val cart: CartRepository by inject()

    fun close() {
        scope.close()
    }
}
```

`createScope(this)` uses the class type as scope qualifier, an id built from the class name and hash code, and `this` as scope source. `newScope()` and `getOrCreateScope()` are lazy delegates for the same purpose: `override val scope by newScope()`.

## Resolution inside a scope

From a scope instance, Koin looks for a definition in this order:

1. The definitions of the scope (or of its archetype, see below).
2. The scope source.
3. The linked scopes, in order. The root scope, with all `single` and `factory` definitions, is always linked last.

So a scoped class can depend on singletons and on other scoped classes of the same scope:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class CartRepository(
    private val api: ApiService,        // single, from the root scope
    private val sessionData: SessionData // scoped, from the same scope
)

val sessionModule = module {
    single<ApiService>()
    scope<UserSession> {
        scoped<SessionData>()
        scoped<CartRepository>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class ApiService

@Scope(UserSession::class)
@Scoped
class SessionData

@Scope(UserSession::class)
@Scoped
class CartRepository(
    private val api: ApiService,         // single, from the root scope
    private val sessionData: SessionData // scoped, from the same scope
)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val sessionModule = module {
    singleOf(::ApiService)
    scope<UserSession> {
        scopedOf(::SessionData)
        scopedOf(::CartRepository)
    }
}
```

</TabItem>
</Tabs>

The reverse does not work: a `single` cannot depend on a `scoped` definition, and `get<SessionData>()` from the root scope fails with `NoDefinitionFoundException`. Resolve scoped instances from their scope instance.

### Scope source

The scope source is the object that created the scope, passed as `source` in `createScope(scopeId, qualifier, source)`. A scoped definition can ask for it by type, like any other dependency:

```kotlin
class SessionLogger(private val session: UserSession)

val sessionModule = module {
    scope<UserSession> {
        scoped<SessionLogger>() // receives the UserSession that created the scope
    }
}
```

The source is only resolved for unqualified requests, and it is released when the scope closes.

### Injecting the scope

A scoped definition can receive the `Scope` itself as a constructor parameter, to resolve more instances later or to close it.

### Instances created outside Koin

`scope.declare(instance)` adds an object you already have to one scope instance, without a definition in a module:

```kotlin
val scope = getKoin().createScope<UserSession>(userId)
scope.declare(UserProfile(userId, name))
```

## Resolving from another scope

### Linking scopes

`linkTo` adds another scope to the resolution chain. The linked scope is searched after the current one:

```kotlin
val sessionScope = getKoin().createScope<UserSession>(userId)
val checkoutScope = getKoin().createScope("checkout_$userId", named("checkout"))

checkoutScope.linkTo(sessionScope)

// resolved from checkoutScope first, then sessionScope, then root
val cart: CartRepository = checkoutScope.get()
```

`unlink(scope)` removes the link. The root scope cannot be linked to anything.

### Pointing at a scope id

With annotations, `@ScopeId` resolves one constructor parameter from the scope instance with a given id, or a given type:

```kotlin
@Factory
class OrderSummary(
    @ScopeId(name = "checkout_main") val navigator: CheckoutNavigator
)

@Factory
class CartBadge(
    @ScopeId(UserSession::class) val cart: CartRepository
)
```

`@ScopeId(name = "checkout_main")` is the same as `getScope("checkout_main").get<CheckoutNavigator>()` in the DSL. With a type, the scope id is the fully qualified class name (`"com.example.UserSession"`). The scope instance with this id must exist when the definition is resolved, or `getScope()` throws `ScopeNotCreatedException`.

## Scope archetypes

A scope declared with a type (`scope<MainActivity>`) only applies to that type. A **scope archetype** is a generic scope declaration shared by every component of a kind: every ViewModel, every Activity, every Fragment. You declare the definitions once, and each component gets its own scope instance.

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val appModule = module {
    viewModelScope {
        scoped<UserCache>()
        viewModel<UserViewModel>()
    }

    // Android only, koin-android
    activityScope {
        scoped<ActivityPresenter>()
    }
    fragmentScope {
        scoped<FragmentPresenter>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

An archetype annotation replaces `@Scope` and `@Scoped`:

```kotlin
import org.koin.core.annotation.ViewModelScope
import org.koin.android.annotation.ActivityScope
import org.koin.android.annotation.FragmentScope

@ViewModelScope
class UserCache(private val api: ApiService)

@ActivityScope
class ActivityPresenter

@FragmentScope
class FragmentPresenter
```

Archetype annotations also work on functions in a `@Module` class.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val appModule = module {
    viewModelScope {
        scopedOf(::UserCache)
        viewModelOf(::UserViewModel)
    }

    // Android only, koin-android
    activityScope {
        scopedOf(::ActivityPresenter)
    }
    fragmentScope {
        scopedOf(::FragmentPresenter)
    }
}
```

</TabItem>
</Tabs>

| Archetype | DSL | Annotation | Closed when | Platform |
|-----------|-----|------------|-------------|----------|
| ViewModel | `viewModelScope { }` | `@ViewModelScope` | The ViewModel is cleared | All (`koin-core-viewmodel`) |
| Activity | `activityScope { }` | `@ActivityScope` | The Activity is destroyed | Android |
| Activity retained | `activityRetainedScope { }` | `@ActivityRetainedScope` | The Activity finishes (survives configuration changes) | Android |
| Fragment | `fragmentScope { }` | `@FragmentScope` | The Fragment is destroyed | Android |

The archetype only declares the definitions. The component opens its scope instance at runtime: for Android, with the `activityScope()` and `fragmentScope()` delegates described in [Android Scopes](/docs/reference/koin-android/scope).

The ViewModel archetype needs the `viewModelScopeFactory()` option at start, so Koin creates a scope for each ViewModel. It is marked `@KoinViewModelScopeApi` and `@KoinExperimentalAPI`:

```kotlin
startKoin {
    options(viewModelScopeFactory())
    modules(appModule)
}
```

ViewModels that take scoped dependencies in their constructor must be declared inside `viewModelScope { }`. Details on ViewModels are in [ViewModel](/docs/reference/koin-core/viewmodel).

:::tip
A scope that is never closed keeps its instances alive. To see open scopes and leaked instances in real user sessions, see [Production Monitoring](/docs/tooling/production-monitoring).
:::

## Limits

- **Manual lifecycle**: outside the archetypes and platform helpers, you create and close scopes yourself. Koin does not detect a forgotten `close()`.
- **No access from the root**: `single` definitions and the root scope cannot see `scoped` definitions.
- **Closed is final**: a closed scope instance cannot be reopened. Create a new one with a new id.
- **Unique ids**: creating a scope with the id of an open scope fails. Use `getOrCreateScope` if the scope may already exist.

## Related

- [Android Scopes](/docs/reference/koin-android/scope): `ScopeActivity`, `AndroidScopeComponent`, `activityScope()` and `fragmentScope()`.
- [Scopes in Compose](/docs/reference/koin-compose/compose-scopes): `KoinScope`, `rememberKoinScope` and navigation scopes.
- [Ktor Request Scopes](/docs/reference/koin-ktor/ktor-scopes): one scope per HTTP request.
- [ViewModel](/docs/reference/koin-core/viewmodel): ViewModel declaration and the ViewModel scope.
- [Definitions](/docs/reference/koin-core/definitions): `single`, `factory`, `scoped` and `onClose`.
- [DSL Reference](/docs/reference/koin-core/dsl): every scope DSL keyword.
