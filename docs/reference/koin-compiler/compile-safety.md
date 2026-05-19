---
title: Compile-Time Safety
---

The Koin Compiler Plugin validates your dependency graph at compile time — catching missing dependencies, qualifier mismatches, and broken call sites before your app runs.

This replaces runtime verification tools like `verify()` and `checkModules()`. If it compiles, it works.

## How It Works

The plugin validates your graph at three levels during compilation:

### A2 — Per-Module (Early Feedback)

Each module's definitions are checked against visible definitions: its own definitions, explicitly included modules, and `@Configuration` sibling modules.

```kotlin
@Module(includes = [DataModule::class])
@ComponentScan("app")
class AppModule
// Validates: definitions from AppModule + DataModule
```

Modules sharing a `@Configuration` label are mutually visible:

```kotlin
@Module @ComponentScan("core") @Configuration("prod")
class CoreModule  // provides Repository

@Module @ComponentScan("service") @Configuration("prod")
class ServiceModule  // Service(repo: Repository) → OK, visible from CoreModule
```

Different labels are isolated:

```kotlin
@Configuration("core")
class CoreModule

@Configuration("service")  // different label — CoreModule NOT visible
class ServiceModule         // Service(repo: Repository) → ERROR
```

**What A2 catches:**

- Missing dependencies
- Qualifier mismatches (`@Named("prod")` requested but only `@Named("test")` provided)
- Cross-scope violations
- `Lazy<T>` without `T` provided
- External deps not marked `@Provided`

### A3 — Full Graph (Complete Guarantee)

At `startKoin<T>()`, all modules from all sources are assembled and the complete graph is validated. Everything A2 couldn't see — cross-module dependencies, definitions from JARs — is checked here.

```kotlin
@KoinApplication(modules = [CoreModule::class, ServiceModule::class])
object MyApp

startKoin<MyApp> { }
// Validates: ALL definitions from CoreModule + ServiceModule combined
```

A3 also validates DSL definitions (`single<T>()`, `factory<T>()`, etc.) when they are part of the graph.

### A4 — Call-Site Validation

Every `koinViewModel<T>()`, `get<T>()`, `inject<T>()` call in your codebase is intercepted. The plugin captures the target type, file, line, and column — then checks that `T` exists in the assembled graph.

```kotlin
@Composable
fun UserScreen() {
    val viewModel: UserViewModel = koinViewModel()  // ← A4 validates this
}

class MyFragment : Fragment() {
    val service: PaymentService by inject()  // ← A4 validates this
}
```

If `UserViewModel` isn't in the graph → build error with exact file, line, and column.

**Cross-module call sites:** If a feature module calls `koinViewModel<T>()` but doesn't have visibility into the full graph, the plugin generates a call-site hint. When the app module compiles, it discovers these hints from dependency JARs and validates them against the complete graph.

## What Gets Validated

| Scenario | Result |
|----------|--------|
| Non-nullable param, no definition | **ERROR** |
| Nullable param (`T?`), no definition | OK — uses `getOrNull()` |
| Param with default value, no definition | OK — uses Kotlin default (when `skipDefaultValues=true`) |
| `@InjectedParam`, no definition | OK — provided at runtime via `parametersOf()` |
| `@Property("key")` param | OK — property injection (warns if no `@PropertyValue` default) |
| `List<T>` param | OK — `getAll()` returns empty list if none |
| `Lazy<T>`, no definition for `T` | **ERROR** — unwraps to validate inner type |
| `@Named("x")` param, no matching qualifier | **ERROR** — with hint if unqualified binding exists |
| Scoped dependency from wrong scope | **ERROR** |
| Default value param with `@Named` qualifier | **ERROR** — qualifier forces injection |
| `@Provided` type or parameter, no definition | OK — externally provided at runtime |
| `@ScopeId(name = "x")` param | OK — resolved from named scope at runtime |
| `Scope` type param | OK — scope receiver passed directly |
| Android framework type (e.g. `Context`) | OK — hardcoded whitelist |
| Circular dependency (A → B → A) | **ERROR** — detected during A2/A3 graph traversal |

## Safety with Annotations

Annotate your classes, organize them in modules, and the compiler validates everything:

```kotlin
@Singleton
class Database

@Singleton
class UserRepository(private val db: Database)

@KoinViewModel
class UserViewModel(private val repo: UserRepository) : ViewModel()

@Module
@ComponentScan("com.myapp")
class AppModule
```

The plugin discovers annotated classes via `@ComponentScan`, validates each module's definitions at A2, and validates the full graph at A3 when you declare your application entry point:

```kotlin
@KoinApplication(modules = [AppModule::class])
object MyApp

startKoin<MyApp> { }  // ← triggers A3 full graph validation
```

**Top-level functions** are also supported. Annotated top-level functions are discovered by `@ComponentScan` and validated like class definitions:

```kotlin
@Singleton
fun provideDatabase(): DatabaseService = PostgresDatabase()

@Factory
fun provideCache(db: DatabaseService): CacheService = RedisCache(db)
// ← validated: DatabaseService exists
```

Use `@Configuration` labels to organize modules into groups that are validated together:

```kotlin
@Module @ComponentScan("core") @Configuration("prod")
class CoreModule

@Module @ComponentScan("feature") @Configuration("prod")
class FeatureModule  // can see CoreModule's definitions
```

## Safety with DSL

The compiler plugin also validates DSL definitions. When you write `single<T>()`, `factory<T>()`, or `viewModel<T>()`, the plugin intercepts the call, auto-wires the constructor, and validates all parameters:

```kotlin
val appModule = module {
    single<Database>()
    single<UserRepository>()       // ← validated: Database exists
    viewModel<UserViewModel>()     // ← validated: UserRepository exists
}
```

No manual `get()` calls needed — the plugin generates them and validates them at the same time.

The `create(::T)` function is also validated. It calls a function reference (typically a builder function, but can also be a constructor) and validates all its parameters:

```kotlin
fun buildUserRepository(db: Database): UserRepository = UserRepository(db)

val appModule = module {
    scope<UserSession> {
        scoped { create(::buildUserRepository) }  // ← validated: Database exists
    }
}
```

DSL definitions participate in A3 validation (full graph) and A4 validation (call sites). If you use `startKoin { modules(appModule) }`, the plugin validates all DSL definitions against the assembled graph.

## Both Styles Together

You can mix annotations and DSL in the same project. Both are collected into the same validation graph:

```kotlin
// Annotations
@Singleton class Database

// DSL
val featureModule = module {
    single<UserRepository>()  // ← validated: Database from annotations is visible
}
```

## Error Messages

Errors report the missing type, which definition needs it, and in which module:

```
[Koin] Missing dependency: Repository
  required by: Service (parameter 'repo')
  in module: ServiceModule
```

When a binding exists with a different qualifier, a hint is shown:

```
[Koin] Missing dependency: NetworkClient (qualifier: @Named("http"))
  required by: ApiService (parameter 'client')
  in module: AppModule
  Hint: Found NetworkClient without qualifier — did you mean to add @Named("http")?
```

Call-site errors include exact location:

```
[Koin] Missing definition: com.app.UserRepository
  resolved by: koinViewModel<UserViewModel>()
  No matching definition found in any declared module.
  → file: UserScreen.kt, line: 12, column: 5
```

## Forbidden Definitions

Some return types can never be resolved meaningfully through Koin and are rejected at compile time:

### KOIN-D007: `@Factory` returning a suspend `fun interface`

A `@Factory` that returns a type extending a suspend `fun interface` cannot be invoked through Koin's synchronous `get<T>()` API. The plugin blocks this at compile time.

```kotlin
fun interface AsyncTask { suspend operator fun invoke(): Result }

@Factory
fun provideTask(): AsyncTask = AsyncTask { ... }
// KOIN-D007 — ERROR: @Factory return types cannot extend a suspend fun interface
```

Refactor to a regular interface, or expose the suspend operation through a class with a suspend method.

## Generic DSL Types

Runtime Koin resolves definitions on the **erased raw class** — type parameters are not part of the lookup key. Compile-safety honours that: a `get<Box<X>>()` call is validated against any `Box<*>` provider in the graph, and two `single<Box<A>>()` / `single<Box<B>>()` declarations collide (same raw class, no qualifier).

```kotlin
class Box<T>(val value: T)

val appModule = module {
    single { Box(42) }   // registered as Box (raw)
}

koin.get<Box<Int>>()    // → returns the single Box registration
koin.get<Box<String>>() // → returns the same registration (erasure)
```

Validating on the raw class also avoids a Kotlin/Native klib signature mangling failure that used to crash iOS builds when a DSL definition carried an unsubstituted type parameter.

### Discriminating generic instances: type qualifier on the generic parameter

The idiomatic pattern when multiple instances of the same generic class must coexist is to register a **concrete wrapper type** and use a **type qualifier derived from the generic parameter** — `named<T>()`. This is what `koin-compose-navigation3` does internally to key each navigation route to its route type:

```kotlin
inline fun <reified T : Any> Module.navigation(
    noinline definition: @Composable Scope.(T) -> Unit,
): KoinDefinition<EntryProviderInstaller> {
    // Register a CONCRETE type (EntryProviderInstaller),
    // discriminated by a type qualifier derived from the generic param T.
    return _singleInstanceFactory<EntryProviderInstaller>(named<T>(), { ... })
}
```

Used on both sides:

```kotlin
// Declaration — T is a concrete type (HomeRoute, SettingsRoute, ...)
module {
    navigation<HomeRoute> { route -> HomeScreen() }
    navigation<SettingsRoute> { route -> SettingsScreen() }
}

// Resolution — same type qualifier keys the lookup
koin.get<EntryProviderInstaller>(named<HomeRoute>())
```

`named<T>()` produces a type qualifier from the reified `T`, so each generic instantiation gets a stable, distinct qualifier. Runtime Koin matches on (raw class + qualifier), which reintroduces the discrimination that type erasure removes.

Prefer this pattern over `single<Box<X>>()` directly whenever you need to distinguish generic instantiations.

## Scope Parameter Injection

Parameters of type `org.koin.core.scope.Scope` are automatically injected with the scope receiver — no annotation needed. Validation is skipped since injecting the scope enables dynamic lookups.

```kotlin
@Scoped
class ScopedService(val scope: Scope) {
    fun dynamicLookup() = scope.get<SomeDep>()
}
// Generates: ScopedService(scope)  — passes the scope receiver directly
```

## Named Scope Resolution: `@ScopeId`

Use `@ScopeId` to resolve a dependency from a named Koin scope instead of the current scope. Validation is skipped since the scope is resolved at runtime.

```kotlin
@Factory
class ProfileService(@ScopeId(name = "user_session") val session: UserSession)
// Generates: ProfileService(scope.getScope("user_session").get<UserSession>())
```

`@ScopeId` supports two forms:

| Form | Example | Scope ID |
|------|---------|----------|
| String name | `@ScopeId(name = "user_session")` | `"user_session"` |
| Type reference | `@ScopeId(UserSessionScope::class)` | FQ class name |

## Property Validation

`@Property("key")` parameters are resolved from Koin properties (set via `properties()` at startup). The plugin warns at compile time when no `@PropertyValue("key")` default exists:

```kotlin
@PropertyValue("api.timeout")
val defaultTimeout = 30

@Factory
class ApiClient(@Property("api.timeout") val timeout: Int)
// OK — @PropertyValue("api.timeout") provides compile-time default

@Factory
class Other(@Property("missing.key") val value: String)
// WARNING — no @PropertyValue("missing.key") found
// (still compiles — property may be provided at runtime)
```

## External Types: `@Provided`

Some types are provided by the platform or by external frameworks at runtime and are never declared as Koin definitions. Mark them with `@Provided` to skip validation.

`@Provided` works on both **classes** (all usages skip validation) and **parameters** (only that parameter is skipped):

```kotlin
// On a class — all usages of this type skip validation
@Provided
class SavedStateHandle

// On a parameter — only this parameter skips validation
@Singleton
class MyViewModel(@Provided val handle: SavedStateHandle)
```

**When to use `@Provided`:**

- **Android framework types** not in the whitelist — e.g., custom Android services
- **Third-party SDK types** injected externally — e.g., Firebase, analytics SDKs
- **Cross-module types from non-Koin modules** — when a dependency comes from a library that doesn't use Koin
- **Test doubles** — when replacing real implementations in test configurations
- **Types provided by hand** — `androidContext()`, manual `single { }` registrations

```kotlin
// External SDK — not managed by Koin
@Singleton
class AnalyticsService(@Provided val firebaseAnalytics: FirebaseAnalytics)

// Cross-module: provided by another team's module at runtime
@Factory
class PaymentProcessor(@Provided val paymentGateway: PaymentGateway)
```

**Common Android framework types are automatically whitelisted** and don't need `@Provided`:

- `android.content.Context`
- `android.app.Application`
- `android.app.Activity`
- `androidx.fragment.app.Fragment`
- `androidx.lifecycle.SavedStateHandle`
- `androidx.work.WorkerParameters`

## Default Values and skipDefaultValues

When `skipDefaultValues` is enabled (default), parameters with Kotlin default values use the default instead of being resolved from the DI container:

```kotlin
// With skipDefaultValues = true (default):
@Singleton
class ServiceWithDefault(val timeout: Int = 5000)
// → uses Kotlin default (5000), not DI resolution

// Nullable parameters are still injected:
@Singleton
class Service(val dep: Dependency? = null)
// → uses getOrNull() from DI

// Annotated parameters always use DI regardless of defaults:
@Singleton
class Service(@Named("custom") val name: String = "fallback")
// → resolves from DI with @Named("custom") qualifier

// Mixed: some from DI, some from defaults
@Singleton
class ApiClient(
    val repo: UserRepository,                        // → resolved from DI
    val timeout: Int = 30_000,                       // → uses Kotlin default
    @Property("api_url") val url: String = "https://api.example.com"  // → resolved from DI (annotated)
)
```

Set `skipDefaultValues = false` to always inject all parameters from the DI container, ignoring Kotlin default values.

## Configuration

Compile-time safety is enabled by default. To disable it:

```kotlin
koinCompiler {
    compileSafety = false  // Disable compile-time safety checks
}
```

Other related options:

```kotlin
koinCompiler {
    compileSafety = true       // Compile-time dependency validation (default: true)
    strictSafety = true        // Force aggregator's safety pass to re-run on every build
                               // (default: auto-detected on modules with startKoin / @KoinApplication)
    skipDefaultValues = true   // Skip injection for params with default values (default: true)
    unsafeDslChecks = true     // Validate create() is only instruction in lambda (default: true)
}
```

:::info Incremental compilation & `strictSafety`
The full-graph pass (A3) only runs in the aggregator's `compileKotlin`. Kotlin's incremental compilation under K2 doesn't track DSL changes inside `module { }` lambda bodies, nor classes newly added to `@ComponentScan` packages — so the aggregator can be marked UP-TO-DATE even when the graph changed. The plugin auto-enables [`strictSafety`](/docs/reference/koin-annotations/options#strictsafety) on detected aggregator modules to force A3 to re-run; library and feature modules stay fully incremental.
:::

## Migrating from verify() / checkModules()

The compiler plugin replaces runtime verification. You can remove your verification tests:

| Before | After |
|--------|-------|
| `module.verify()` in test | Compiler plugin (automatic) |
| `checkModules()` in test | Compiler plugin (automatic) |
| Runtime verification | Compile-time verification |
| Manual test setup | No test code needed |

The compiler validates on every build — no test code required.

## See Also

- **[Compiler Plugin Options](/docs/reference/koin-annotations/options)** - All configuration options
- **[Compiler Plugin Setup](/docs/setup/compiler-plugin)** - Installation guide
- **[Starting with Annotations](/docs/reference/koin-annotations/start)** - Getting started
- **[Playground Apps](https://github.com/InsertKoinIO/koin-compiler-plugin/tree/main/playground-apps)** - Complete reference apps with both annotations (`app-annotations/`) and DSL (`app-dsl/`) approaches
