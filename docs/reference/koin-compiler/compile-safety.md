---
title: Compile-Time Safety
description: What the Koin Compiler Plugin checks at build time, the diagnostics it reports, how to declare external types with @Provided, and its limits.
---

The Koin Compiler Plugin checks your dependency graph while your code compiles. A missing definition, a wrong qualifier or a circular dependency fails the build, with the file and line, instead of crashing at runtime. This page covers what is checked, the exact diagnostics, and the cases the plugin cannot check. It assumes the plugin is installed (see [Compiler Plugin Setup](/docs/setup/compiler-plugin)).

Compile-time safety is enabled by default (`compileSafety = true`, see [Compiler Plugin Options](/docs/reference/koin-annotations/options)).

## Where the checks run

The plugin checks at two levels.

**Full graph**: at each Koin entry point, the plugin assembles every module that the entry point loads, from your sources and from dependency JARs, and checks every definition against that complete graph.

**Call sites**: every `get<T>()`, `inject<T>()`, `koinInject<T>()`, `koinViewModel<T>()`, `by viewModel()` and similar call is checked: `T` must exist in the assembled graph. If a feature module makes the call but cannot see the full graph, the plugin leaves a hint in the compiled module. The app module reads these hints from its dependencies and checks them when it compiles.

Recognized entry points:

| Entry point | Example |
|-------------|---------|
| `startKoin` | `startKoin<MyApp>()`, `startKoin { modules(appModule) }` |
| `koinApplication` | `koinApplication { modules(appModule) }` |
| `koinConfiguration` | `koinConfiguration<MyApp>()` |
| `@KoinApplication` | `@KoinApplication object MyApp` |
| `withConfiguration<T>()` | `startKoin { withConfiguration<MyApp>() }` |
| Ktor `install(Koin)` | `install(Koin) { modules(appModule) }` |

`modules(...)` accepts lists the plugin can follow: `listOf(...)`, `+`, `.toList()`, a top-level `val`, or a function returning `List<Module>`:

```kotlin
val dataModules = listOf(databaseModule, networkModule)
fun featureModules(): List<Module> = listOf(loginModule, profileModule)

startKoin {
    modules(dataModules + featureModules())
}
```

A module declared as a function (`fun dataModule(): Module = module { ... }`) is followed like a top-level `val`.

:::note
A Gradle module with no entry point of its own (a library, most feature modules) gets no missing-dependency diagnostics in its own compilation. A module checked alone cannot know how the app will wire it. Its definitions are checked when the app module assembles the graph. The plugin says so in the build output:

```
[Koin] compile-safety validation skipped — no Koin entry point in this compilation.
```
:::

## What is checked

| Case | Result |
|------|--------|
| Non-nullable parameter, no definition | Error |
| Nullable parameter (`T?`), no definition | OK, resolved with `getOrNull()` |
| Parameter with a default value, no definition | OK, uses the Kotlin default (with `skipDefaultValues = true`) |
| Parameter with a default value and a qualifier (`@Named`) | Error, the qualifier forces resolution |
| `@InjectedParam` parameter | OK, passed at runtime with `parametersOf()` |
| `@Property("key")` parameter | OK. Warning if no `@PropertyValue("key")` default exists |
| `List<T>` parameter | OK, `getAll()` returns an empty list if nothing matches |
| `Lazy<T>`, no definition for `T` | Error, the inner type is checked |
| `@Named("x")` parameter, no definition with that qualifier | Error, with a hint if the type exists with another qualifier |
| Scoped dependency declared in another scope | Error |
| Circular dependency (A needs B, B needs A) | Error |
| `@Provided` type or parameter | OK, provided outside the graph |
| `@ScopeId` parameter | OK, resolved from a named scope at runtime |
| `Scope` parameter | OK, the current scope is passed |
| Android framework type (`Context`, `SavedStateHandle`, ...) | OK, built-in list |
| `get<T>()` / `inject<T>()` with no definition for `T` | Error |
| `get<T>()` without `parametersOf()` when `T` has `@InjectedParam` | Error |
| `parametersOf(...)` with the wrong count or types | Error |

The rules for how each parameter type is resolved are on [Definitions](/docs/reference/koin-core/definitions#constructor-parameters).

## Checks per declaration style

Every style and DSL form is checked against the same graph. They can live in one project: a DSL definition can depend on an annotated class, and the other way around.

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val appModule = module {
    single<Database>()
    single<UserRepository>()      // checked: Database exists
    viewModel<UserViewModel>()    // checked: UserRepository exists
}
```

The plugin reads the constructor, generates the resolution calls and checks every parameter. `create(::function)` is checked the same way, on the function parameters:

```kotlin
fun buildUserRepository(db: Database): UserRepository = UserRepository(db)

val appModule = module {
    single { create(::buildUserRepository) }    // checked: Database exists
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class Database

@Singleton
class UserRepository(private val db: Database)    // checked: Database exists

@KoinViewModel
class UserViewModel(private val repo: UserRepository) : ViewModel()

@Module
@ComponentScan("com.myapp")
class AppModule

@KoinApplication(modules = [AppModule::class])
object MyApp
```

`startKoin<MyApp>()` is the entry point: the plugin checks every definition found by `@ComponentScan`. Annotated top-level functions are checked like classes:

```kotlin
@Factory
fun provideCache(db: Database): CacheService = RedisCache(db)    // checked: Database exists
```

Modules tagged with `@Configuration` are assembled with the `@KoinApplication` that requests their label, across Gradle modules, and checked together.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

Constructor references are checked like the Compiler Plugin DSL:

```kotlin
val appModule = module {
    singleOf(::Database)
    singleOf(::UserRepository)        // checked: Database exists
    viewModelOf(::UserViewModel)      // checked: UserRepository exists
    scope<UserSession> {
        scopedOf(::SessionCache)
    }
}
```

`singleOf`, `factoryOf`, `scopedOf`, `viewModelOf` and `new(::T)` keep their runtime behavior. The plugin adds the checks, it does not rewrite them.

For a lambda you write yourself, the plugin does not read the constructor. It checks the `get()`, `inject()` and `getOrNull()` calls inside the lambda, like any call site:

```kotlin
val appModule = module {
    single { UserRepository(get()) }  // checked: the get() call needs Database
}
```

</TabItem>
</Tabs>

:::warning
Since Compiler Plugin 1.2.0, `singleOf`, `factoryOf`, `scopedOf` and `viewModelOf` are checked. Code that relied on a missing dependency in these functions no longer compiles. The build was hiding a runtime crash.
:::

## Diagnostics

Every diagnostic has a stable code, printed as `[Koin][CODE]`. Search for the code or the first line of the message.

### Missing dependency (KOIN-D001)

A definition parameter has no definition in the graph:

```
[Koin][KOIN-D001] Missing dependency: com.myapp.Database
  required by: com.myapp.UserRepository (parameter 'db')
  in module: com.myapp.AppModule
```

For a DSL definition, `required by:` starts with `dsl:`. When the type exists with another qualifier, a hint follows:

```
[Koin][KOIN-D001] Missing dependency: com.myapp.NetworkClient qualified with @Named("http")
  required by: com.myapp.ApiService (parameter 'client')
  in module: com.myapp.AppModule
  Hint: Found similar binding: com.myapp.NetworkClient (no qualifier)
```

Fix: declare the missing definition, add the module that declares it to `modules()` or `includes()`, or fix the qualifier (see [Qualifiers](/docs/reference/koin-core/qualifiers)).

### Missing definition at a call site (KOIN-D002, KOIN-D003)

A `get<T>()`, `inject<T>()` or `koinViewModel<T>()` call asks for a type that no loaded module declares:

```
[Koin][KOIN-D002] Missing definition: com.myapp.Tracker
  resolved by: get<Tracker>()
  No matching definition found in any loaded module.
  Check your declaration with Annotation or DSL.
```

`KOIN-D003` is the same error for a call made in a dependency module, reported when the app module compiles. Its second line reads `required by a call site in a dependency module`.

### Circular dependency (KOIN-D004)

```
[Koin][KOIN-D004] Circular dependency detected:
  com.myapp.A → com.myapp.B → com.myapp.A
  Break with Lazy<T> injection or refactor to remove the cycle.
```

A `Lazy<T>`, nullable, `List<T>`, `@InjectedParam`, `@Provided`, `@Property`, `@ScopeId` or default-valued parameter does not count as an edge in the cycle. Changing one parameter to `Lazy<T>` breaks the cycle. See [Troubleshooting](/docs/reference/koin-core/troubleshooting) for ways to remove it.

### Injected parameters (KOIN-D005, KOIN-D006)

The definition declares `@InjectedParam` parameters, and the call site does not pass them:

```
[Koin][KOIN-D006] com.myapp.Greeter requires 1 injected param(s) but get<Greeter>() has no parametersOf(...) at the call site.
  Expected @InjectedParam slots: name: kotlin.String
  Pass them via parametersOf(...) inside the trailing lambda.
```

`KOIN-D005` reports a `parametersOf(...)` with the wrong number of arguments or the wrong types (`Mismatched parametersOf(...) for ...`). Both checks only run when the call contains a `parametersOf(...)` the plugin can read. See [Injected Parameters](/docs/reference/koin-core/injection-parameters).

### Unsupported suspend binding (KOIN-D007)

A definition whose type extends a suspend function type is rejected, because Koin cannot resolve it at runtime:

```kotlin
fun interface SetUsernameUseCase : suspend (String) -> Unit

@Factory
fun provideSetUsernameUseCase(repository: UserRepository): SetUsernameUseCase =
    SetUsernameUseCase(repository::setUsername)
```

```
[Koin][KOIN-D007] Unsupported binding: com.myapp.SetUsernameUseCase extends kotlin.coroutines.SuspendFunction1
  Suspend function injection is not yet supported by Koin runtime.
  Remove the @Single / @Factory / @Scoped registration, or refactor the binding type to not extend a suspend function.
  Tracked at https://github.com/InsertKoinIO/koin-compiler-plugin/issues/16
```

Fix: use a regular interface with a `suspend` method, or a class.

### All codes

| Code | Severity | Message starts with |
|------|----------|---------------------|
| `KOIN-D001` | Error | `Missing dependency:` |
| `KOIN-D002` | Error | `Missing definition:` (call site in this module) |
| `KOIN-D003` | Error | `Missing definition:` (call site in a dependency module) |
| `KOIN-D004` | Error | `Circular dependency detected:` |
| `KOIN-D005` | Error | `Mismatched parametersOf(...) for` |
| `KOIN-D006` | Error | `<Type> requires N injected param(s) but` |
| `KOIN-D007` | Error | `Unsupported binding:` |
| `KOIN-D008` | Error | `Module id collision:` (two module names encode to the same hint name: rename one) |
| `KOIN-W001` | Warning | `Module '<name>' is not loaded at startKoin` |
| `KOIN-W003` | Warning | `Graph not verifiable at compile time:` |
| `KOIN-W004` | Warning | `Custom @Configuration label '<label>' requested by` |
| `KOIN-W005` | Warning | `Definition '<name>' in dependency module` |
| `KOIN-W006` | Warning | `<definition>'s bind(...) argument could not be resolved` |
| `KOIN-A001` | Error | `@KoinViewModel definition '<name>' cannot be generated` (add `io.insert-koin:koin-core-viewmodel`) |
| `KOIN-A002` | Error | `@KoinWorker definition '<name>' cannot be generated` (add `io.insert-koin:koin-android-workmanager`) |
| `KOIN-A003` | Error | `Cannot generate <Module>.module()` (add `io.insert-koin:koin-core`) |
| `KOIN-S001` | Error | `create(::<Type>) must be the only instruction in the lambda` (see [unsafeDslChecks](/docs/reference/koin-annotations/options#unsafedslchecks)) |
| `KOIN-P001` | Warning | `Missing @PropertyValue default:` |
| `KOIN-M001` | Warning | `@Monitor: Kotzilla SDK not found on classpath` |

The warnings describe what the plugin could not check. `KOIN-W003`, `KOIN-W004` and `KOIN-W005` are covered in [Limits](#limits). `KOIN-W006` means a `bind`/`binds` argument is not a literal `X::class` (or a literal array or list of them): use a literal.

## External types: `@Provided`

Some types reach your graph from outside Koin's declarations: a platform object, an SDK instance, a definition added by hand at runtime. Mark them `@Provided` so the plugin does not report them as missing:

```kotlin
// on a class: every use of this type is skipped
@Provided
class FirebaseAnalytics

// on a parameter: only this parameter is skipped
@Singleton
class PaymentProcessor(@Provided val gateway: PaymentGateway)
```

`@Provided` (`org.koin.core.annotation`) works with every declaration style: annotate the class or the constructor parameter, even when the definition is in a DSL module. It only changes the compile-time check. At runtime the type is still resolved with `get()`, and fails if nobody declared it. Prefer the parameter form: on a class, it turns off the check for every use of that type.

These types are always accepted, without `@Provided`:

- `android.content.Context`
- `android.app.Application`
- `android.app.Activity`
- `androidx.activity.ComponentActivity`
- `androidx.fragment.app.Fragment`
- `androidx.lifecycle.SavedStateHandle`
- `androidx.work.WorkerParameters`

### Modules loaded at runtime

A module loaded with `loadKoinModules(...)` after `startKoin` is not part of the graph the plugin assembles. The plugin cannot know when, or if, that call runs. Types that only come from such a module must be `@Provided`:

```kotlin
val featureModule = module { single<Repository>() }   // loaded later
val appModule = module { single<Service>() }          // Service(@Provided val repo: Repository)

startKoin { modules(appModule) }
loadKoinModules(featureModule)
```

The plugin still reports the runtime-loaded module as unreachable with `KOIN-W001`. Adding it to `modules()` would defeat on-demand loading, so you can ignore this warning (it fails the build only with `allWarningsAsErrors`).

## Special parameters

**Scope**: a parameter of type `org.koin.core.scope.Scope` receives the current scope. It is not checked, since the code resolves types from it dynamically.

```kotlin
@Scoped
class ScopedService(val scope: Scope) {
    fun load() = scope.get<SomeDependency>()
}
```

**@ScopeId**: the parameter is resolved from another scope, by name (`@ScopeId(name = "user_session")`) or by type (`@ScopeId(UserSessionScope::class)`, the scope ID is the full class name). It is not checked, since that scope exists only at runtime. See [Scopes](/docs/reference/koin-core/scopes).

**@Property**: the parameter reads a Koin property. If no `@PropertyValue("key")` default is declared, the build gets a warning, and the property must be set at startup:

```kotlin
@PropertyValue("api.timeout")
val defaultTimeout = 30

@Factory
class ApiClient(@Property("api.timeout") val timeout: Int)    // OK

@Factory
class Other(@Property("missing.key") val value: String)
// [Koin][KOIN-P001] Missing @PropertyValue default: "missing.key" ...
```

**Default values**: with `skipDefaultValues = true` (default), a parameter with a Kotlin default value uses it and is not checked. See [skipDefaultValues](/docs/reference/koin-annotations/options#skipdefaultvalues).

## Generic types

Koin resolves definitions on the erased class: type arguments are not part of the lookup key. The plugin follows the same rule. `get<Box<Int>>()` is checked against any `Box` definition, and `single<Box<A>>()` and `single<Box<B>>()` are the same definition, so the second overrides the first.

To keep several instances of one generic type, give each one a type qualifier derived from its type argument with `named<T>()`. The pattern is described in [Advanced Patterns](/docs/reference/koin-core/advanced-patterns).

## Incremental builds

The full-graph check runs when the aggregator module (the one with the entry point) compiles. Kotlin incremental compilation can skip that module after a change elsewhere, for example inside a `module { }` lambda body. The plugin turns on `strictSafety` in modules where it detects an entry point, so the check runs on every build. Details and escape hatch: [strictSafety](/docs/reference/koin-annotations/options#strictsafety).

## Limits

- **Dynamic module lists**: `modules(if (debug) a else b)`, `modules(*list)` or a list built at runtime cannot be followed. The plugin reports `KOIN-W003` and skips the full-graph check for that entry point. Pass modules directly to keep the check.
- **Hand-written lambdas and cycles**: a `single { A(get()) }` lambda adds no edge to the cycle check, so a circular dependency through hand-written lambdas is not detected. Use `single<T>()`, `singleOf(::T)` or `create(::T)` where cycles matter.
- **Custom configuration labels**: modules tagged with a label other than `"default"` are discovered at most one `implementation` dependency away (`KOIN-W004`). Add a direct dependency, or list the module in `@KoinApplication(modules = [...])`.
- **Function definitions far away**: a `@Module` function definition, or a qualified top-level function, in a module two or more `implementation` hops away is not visible to the check (`KOIN-W005`). It still works at runtime. Add a direct dependency to check it.
- **Runtime-loaded modules**: see [Modules loaded at runtime](#modules-loaded-at-runtime).

## Replacing verify() and checkModules()

With compile-time safety on, the build checks what `verify()` and `checkModules()` tests check, on every compilation. You can remove those tests for graphs the plugin fully checks. Keep them for graphs the plugin reports as not verifiable (`KOIN-W003`) or that use runtime-loaded modules. See [Verifying modules](/docs/reference/koin-test/verify).

## Related

- [Compiler Plugin Options](/docs/reference/koin-annotations/options): `compileSafety`, `strictSafety`, `skipDefaultValues` and the other options.
- [Koin Compiler Plugin](/docs/intro/koin-compiler-plugin): what the plugin does and how it works.
- [Definitions](/docs/reference/koin-core/definitions): how constructor parameters are resolved.
- [Troubleshooting](/docs/reference/koin-core/troubleshooting): runtime errors, the logger and circular dependencies.
- [IDE Plugin](/docs/tooling/ide-plugin): see the graph and navigate definitions in the IDE.
- [Playground apps](https://github.com/InsertKoinIO/koin-compiler-plugin/tree/main/playground-apps): sample apps with annotations (`app-annotations/`) and the DSL (`app-dsl/`).
